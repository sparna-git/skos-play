package fr.sparna.rdf.rdf4j.toolkit.repository.init;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;

/**
 * Loads RDF from a URL. Optionally, if the program runs offline or if you want to ensure there is a default data if
 * the URL cannot be reached, you can set a local fallback path. The local JVM resource referred to by this path will
 * be loaded if the initial URL cannot be loaded.
 * 
 * @author Thomas Francart
 */
public class LoadFromUrl extends AbstractLoadOperation implements Consumer<RepositoryConnection> {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// URL to load from, associated with optional path to a classpath-accessible fallback resource
	protected Map<URL, String> urls;
	// whether to automatically add the URL to the corresponding named graph
	protected boolean autoNamedGraph;
	// contentType to accept in the header - if null, Sesame provides a default behavior
	// typically some data are expecting precise content-type and fails if content-type is not one they know, like http://vocab.getty.edu/aat/300001280
	protected RDFFormat acceptContentType = null;
	
	protected String cacheDir;

	public LoadFromUrl(Map<URL, String> urls, boolean autoNamedGraph) {
		super();
		this.urls = urls;
		this.autoNamedGraph = autoNamedGraph;
	}
	
	public LoadFromUrl(List<URL> urls, boolean autoNamedGraph) {
		super();
		if(urls != null) {
			this.urls = new HashMap<URL, String>();
			for (URL url : urls) {
				this.urls.put(url, null);
			}
		}
		this.autoNamedGraph = autoNamedGraph;
	}
	
	public LoadFromUrl(URL url, boolean autoNamedGraph) {
		this(url, autoNamedGraph, null);
	}
	
	public LoadFromUrl(URL url, boolean autoNamedGraph, String localFallback) {
		this(new HashMap<URL, String>(Collections.singletonMap(url, localFallback)), autoNamedGraph);
	}	
	
	public LoadFromUrl(URL url) {
		this(url, false);
	}
	
	@Override
	public void accept(RepositoryConnection connection) {
		
		// return if urls is null
		if(this.urls == null) {
			return;
		}
		
		// create cache dir if needed and set autoNamedGraph
		if(cacheDir != null) {
			log.info("Working with a cacheDir, forcing autoNamedGraph to true");
			this.autoNamedGraph = true;
			
			File dir = new File(cacheDir);
			if(!dir.exists()) {
				dir.mkdirs();
			}
		}
		
		for (Map.Entry<URL, String> aUrlEntry : this.urls.entrySet()) {
			
			// set target graph according to autoNamedGraph flag
			if(this.autoNamedGraph) {
				try {
					this.targetGraph = SimpleValueFactory.getInstance().createIRI(aUrlEntry.getKey().toURI().toString());
				} catch (URISyntaxException e) {
					log.error("Unable to convert following URL to a URI to set it as named graph : '"+aUrlEntry.getKey()+"'", e);
					// move to the next one
					continue;
				}
			}
			
			try {
				// build cache file
				File cacheFile = toCacheFile(aUrlEntry.getKey());
				
				// look into cache
				if(cacheDir != null && cacheFile.exists()) {
					log.debug("Load url "+aUrlEntry.getKey().toString()+" from cache file "+cacheFile.getAbsolutePath());
					LoadFromFileOrDirectory load = new LoadFromFileOrDirectory(cacheFile.getAbsolutePath());
					load.setAutoNamedGraphs(false);
					load.setTargetGraph(this.targetGraph);
					load.setDefaultNamespace(this.defaultNamespace);
					load.accept(connection);
				} else {
					
					// fix for french DBPedia URIs
					URL urlToLoad = aUrlEntry.getKey();
					final String DBPEDIA_FR_NAMESPACE = "http://fr.dbpedia.org/resource/";
					if(urlToLoad.toString().startsWith(DBPEDIA_FR_NAMESPACE)) {
						log.debug("Detected a french DBPedia URL. Will turn it into direct n3 loading to avoid accented characters problems");
						urlToLoad = new URL("http://fr.dbpedia.org/data/"+urlToLoad.toString().substring(DBPEDIA_FR_NAMESPACE.length())+".rdf");
					}
					
					log.debug("Loading URL "+urlToLoad+"...");
					connection.add(
							urlToLoad,
							this.defaultNamespace,
							// NEVER EVER explicitly set the RDFFormat when loading from a URL.
							// Sesame can determine the appropriate parser based on the content type of the response if this parameter
							// is left to null
							// Rio.getParserFormatForFileName(aUrlEntry.getKey().toString(), RDFFormat.RDFXML),
							// null,
							this.acceptContentType,
							(this.targetGraph != null)?connection.getValueFactory().createIRI(this.targetGraph.toString()):null
					);
					
					if(cacheDir != null) {
						try {
							log.debug("Store in cache...");
							RepositoryWriter.writeToFile(cacheFile.getAbsolutePath(), connection, this.targetGraph);
						} catch (Exception e) {
							log.warn("Unable to write to cache "+cacheFile.getAbsolutePath()+" for url "+aUrlEntry.getKey());
							e.printStackTrace();
						}	
					}
				}
			} catch (IOException e) {
				String message = "Cannot open stream of URL '"+aUrlEntry.getKey()+"', cause : "+e.getMessage();
				log.error(message);
				// look in the fallback value
				if(aUrlEntry.getValue() != null) {
					log.info("Will attempt to load local resource fallback : '"+aUrlEntry.getValue()+"'");
					InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(aUrlEntry.getValue());
					if(resourceAsStream != null) {
						LoadFromStream lfs = new LoadFromStream(
								resourceAsStream,
								Rio.getParserFormatForFileName(aUrlEntry.getValue()).orElse(RDFFormat.RDFXML),
								this.defaultNamespace
						);
						lfs.setTargetGraph(this.targetGraph);
						lfs.accept(connection);
					}
				}
			}
		}	
	}
	
	// creer le chemin vers le fichier de cache en escapant les caracteres de l'URL
	private File toCacheFile(URL url) {
		return new File(this.cacheDir+"/"+url.toString().replaceAll("[^0-9a-zA-Z\\.]", "_")+".ttl");
	}
	
	public boolean isAutoNamedGraph() {
		return autoNamedGraph;
	}

	public void setAutoNamedGraph(boolean autoNamedGraph) {
		this.autoNamedGraph = autoNamedGraph;
	}

	public Map<URL, String> getUrls() {
		return urls;
	}

	public void setUrls(Map<URL, String> urls) {
		this.urls = urls;
	}
	
	/**
	 * Convenience method
	 * @param urls
	 */
	public void setUrls(List<URL> urls) {
		this.urls = new HashMap<URL, String>();
		for (URL url : urls) {
			this.urls.put(url, null);
		}
	}

	public RDFFormat getAcceptContentType() {
		return acceptContentType;
	}

	public void setAcceptContentType(RDFFormat acceptContentType) {
		this.acceptContentType = acceptContentType;
	}

	public String getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
		System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		System.setProperty("org.slf4j.simpleLogger.log.fr.sparna.rdf","trace");
		
//		URL url = new URL("http://fr.dbpedia.org/data/Aérodrome.n3");
//		InputStream is = url.openStream();
//		String s = InputStreamUtil.readToString(is, "UTF-8");
//		System.out.println(s);
//		
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
//		r.getConnection().add(
//				new URL("http://fr.dbpedia.org/data/Aérodrome.n3"),
//				null,
//				RDFFormat.N3
//		);
		
//		r.getConnection().add(r.getValueFactory().createStatement(
//				r.getValueFactory().createURI("http://fr.dbpedia.org/resource/Aérodrome"), RDFS.LABEL, r.getValueFactory().createLiteral("toto"))
//		);
		

		
//		LoadFromUrl lfu = new LoadFromUrl(new URL("http://prefix.cc/popular/all.file.vann"));
//		lfu.execute(r);
//		
//		Perform.on(r).select(
//				new SelectSparqlHelper(
//						"PREFIX vann:<http://purl.org/vocab/vann/> SELECT ?prefix ?uri WHERE { ?x vann:preferredNamespacePrefix ?prefix . ?x vann:preferredNamespaceUri ?uri }",
//						new DebugHandler(System.out)
//				)
//		);
		
		LoadFromUrl me = new LoadFromUrl(
				// Arrays.asList(new URL[] { new URL("http://vocab.getty.edu/aat/300001280"), new URL("http://fr.dbpedia.org/resource/Abri"), new URL("http://fr.dbpedia.org/resource/Acad%C3%A9mie") }),
				// Arrays.asList(new URL[] { new URL("http://fr.dbpedia.org/resource/A%C3%A9rodrome") }),
				Arrays.asList(new URL[] { new URL("http://fr.dbpedia.org/resource/Aérodrome") }),
				true
		);
		me.setAcceptContentType(RDFFormat.N3);
		try(RepositoryConnection connection = r.getConnection()) {
			me.accept(connection);
			fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter.writeToFile("/home/thomas/test.ttl", connection);
		}
		
	}
	
}
