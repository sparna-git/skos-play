package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.handler.DebugHandler;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.query.Perform;

/**
 * Loads RDF from a URL. Optionally, if the program runs offline or if you want to ensure there is a default data if
 * the URL cannot be reached, you can set a local fallback path. The local JVM resource referred to by this path will
 * be loaded if the initial URL cannot be loaded.
 * 
 * @author Thomas Francart
 */
public class LoadFromUrl extends AbstractLoadOperation implements RepositoryOperationIfc {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// URL to load from, associated with optional path to a classpath-accessible fallback resource
	protected Map<URL, String> urls;
	// whether to automatically add the URL to the corresponding named graph
	protected boolean autoNamedGraph;

	public LoadFromUrl(Map<URL, String> urls, boolean autoNamedGraph) {
		super();
		this.urls = urls;
		this.autoNamedGraph = autoNamedGraph;
	}
	
	public LoadFromUrl(List<URL> urls, boolean autoNamedGraph) {
		super();
		this.urls = new HashMap<URL, String>();
		for (URL url : urls) {
			this.urls.put(url, null);
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
	public void execute(Repository repository)
	throws RepositoryOperationException {
		
		// return if urls is null
		if(this.urls == null) {
			return;
		}
		
		for (Map.Entry<URL, String> aUrlEntry : this.urls.entrySet()) {
			
			// set target graph according to autoNamedGraph flag
			if(this.autoNamedGraph && this.targetGraph != null) {
				try {
					this.targetGraph = aUrlEntry.getKey().toURI();
				} catch (URISyntaxException e) {
					throw new RepositoryOperationException("Unable to convert following URL to a URI to set it as named graph : '"+aUrlEntry.getKey()+"'", e);
				}
			}
			
			try {
				log.debug("Loading URL "+aUrlEntry.getKey()+"...");
				repository.getConnection().add(
						aUrlEntry.getKey(),
						this.defaultNamespace,
						// NEVER EVER explicitly set the RDFFormat when loading from a URL.
						// Sesame can determine the appropriate parser based on the content type of the response if this parameter
						// is left to null
						// Rio.getParserFormatForFileName(url.toString(), RDFFormat.RDFXML),
						null,
						(this.targetGraph != null)?repository.getValueFactory().createURI(this.targetGraph.toString()):null
				);
			} catch (RDFParseException e) {
				throw new RepositoryOperationException("Error when parsing content at URL '"+aUrlEntry.getKey().toString()+"'", e);
			} catch (RepositoryException e) {
				throw new RepositoryOperationException("Error when adding content of URL '"+aUrlEntry.getKey().toString()+"'", e);
			} catch (IOException e) {
				log.info("Cannot open stream of URL '"+aUrlEntry.getKey()+"', cause : "+e.getMessage());
				// look in the fallback value
				if(aUrlEntry.getValue() != null) {
					log.info("Will attempt to load local resource fallback : '"+aUrlEntry.getValue()+"'");
					InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(aUrlEntry.getValue());
					if(resourceAsStream == null) {
						throw new RepositoryOperationException("Cannot find resource "+aUrlEntry.getValue());
					}
					LoadFromStream lfs = new LoadFromStream(
							resourceAsStream,
							Rio.getParserFormatForFileName(aUrlEntry.getValue(), RDFFormat.RDFXML),
							this.defaultNamespace
					);
					lfs.setTargetGraph(this.targetGraph);
					lfs.execute(repository);
				}
			}
		}
	
	}
	
	
	public boolean isAutoNamedGraph() {
		return autoNamedGraph;
	}

	public void setAutoNamedGraph(boolean autoNamedGraph) {
		this.autoNamedGraph = autoNamedGraph;
	}

	public static void main(String[] args) throws Exception {
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		LoadFromUrl lfu = new LoadFromUrl(new URL("http://prefix.cc/popular/all.file.vann"));
		lfu.execute(r);
		
		Perform.on(r).select(
				new SelectSparqlHelper(
						"PREFIX vann:<http://purl.org/vocab/vann/> SELECT ?prefix ?uri WHERE { ?x vann:preferredNamespacePrefix ?prefix . ?x vann:preferredNamespaceUri ?uri }",
						new DebugHandler(System.out)
				)
		);
	}
	
}
