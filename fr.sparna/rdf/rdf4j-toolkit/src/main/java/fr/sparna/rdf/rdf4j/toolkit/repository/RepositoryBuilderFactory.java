package fr.sparna.rdf.rdf4j.toolkit.repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromFileOrDirectory;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromUrl;

/**
 * A Supplier for RepositoryBuilder, from a simple String or a list of files/directory to load.
 * 
 * @author Thomas Francart
 *
 */
public class RepositoryBuilderFactory implements Supplier<RepositoryBuilder> {
	
	public static final String DEFAULT_REPOSITORY_SYSTEM_PROPERTY = "rdf4j.repository";
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected List<String> fileOrDirectoryOrURLs = new ArrayList<String>();
	
	protected Supplier<Repository> localRepositorySupplier; 
	
	public static RepositoryBuilderFactory fromSystemProperty() {
		return new RepositoryBuilderFactory(DEFAULT_REPOSITORY_SYSTEM_PROPERTY);
	}
	
	/**
	 * Creates a RepositoryBuilderFactory with a list of Strings that can be file path, directory paths, or URLs, and the
	 * original Supplier<Repository>.
	 * 
	 * @param fileOrDirectoryOrURLs
	 * @param localRepositoryFactory
	 */
	public RepositoryBuilderFactory(List<String> fileOrDirectoryOrURLs, Supplier<Repository> localRepositoryFactory) {
		super();
		this.fileOrDirectoryOrURLs = fileOrDirectoryOrURLs;
		this.localRepositorySupplier = localRepositoryFactory;
	}
	
	/**
	 * Creates a RepositoryBuilderFactory with a single String that can be file path, directory paths, or URLs, and the
	 * original Supplier<Repository>.
	 * 
	 * @param fileOrDirectoryOrURL
	 * @param localRepositoryFactory
	 */
	public RepositoryBuilderFactory(String fileOrDirectoryOrURL, Supplier<Repository> localRepositoryFactory) {
		this(Collections.singletonList(fileOrDirectoryOrURL), localRepositoryFactory);
	}
	
	/**
	 * Creates a RepositoryBuilderFactory with a list of Strings that can be file path, directory paths, or URLs,
	 * and a default LocalMemoryRepositorySupplier.
	 * 
	 * @param fileOrDirectoryOrURLs
	 */
	public RepositoryBuilderFactory(List<String> fileOrDirectoryOrURLs) {
		this(fileOrDirectoryOrURLs, new LocalMemoryRepositorySupplier());
	}
	
	/**
	 * Creates a RepositoryBuilderFactory with a single String that can be file path, directory paths, or URLs, 
	 * and a default LocalMemoryRepositorySupplier.
	 * 
	 * @param fileOrDirectoryOrURL
	 */
	public RepositoryBuilderFactory(String fileOrDirectoryOrURL) {
		this(Collections.singletonList(fileOrDirectoryOrURL), new LocalMemoryRepositorySupplier());
	}
	
	/**
	 * Creates the RepositoryBuilder. Each String can be :
	 * <ul>
	 *   <li>The name of a System property from which the actual value will be read if it exists</li>
	 *   <li>A URL if it starts with 'http'. In this case if it ends with a known RDF extension it will first be attempted to be loaded as an RDF file, 
	 *   otherwise it will be interpreted as the URL of a SPARQL endpoint;
	 *   </li>
	 *   <li>A URL if it starts with 'jdbc:virtuoso', in this case a VirtuosoReflectionRepositoryFactory will be used as the source Supplier<Repository></li>
	 *   <li>The path to a file or directory or classpath resource</li>
	 * </ul>
	 */
	public RepositoryBuilder get() {
		List<Consumer<RepositoryConnection>> operations = new ArrayList<Consumer<RepositoryConnection>>();
		Supplier<Repository> repositorySupplier = localRepositorySupplier;
		
		if(fileOrDirectoryOrURLs != null && fileOrDirectoryOrURLs.size() == 1) {
			String value = fileOrDirectoryOrURLs.get(0);

			// try with a system property
			String pValue = System.getProperty(value);
			if(pValue != null) {
				value = pValue;
			}
			
			// try with a URL
			URL url = null;
			if(value.startsWith("http")) {
				try {
					url = new URL(value);
					log.debug(value+" is a valid URL");
				} catch (MalformedURLException e) {
					log.debug(value+" is not a valid URL. It will be interpreted as a file or directory path.");
				}
			}
			
			
			if(url != null) {
				if(Rio.getParserFormatForFileName(url.toString()).isPresent()) {
					// looks like a file we can parse, let's parse it
					log.debug(value+" can be parsed using an available parser");
					operations.add(new LoadFromUrl(url));
				} else {
					// does not look like a file we can parse, try to ping it to see if it is a endpoint
					log.debug(value+" cannot be parsed using availble parser, will try to ping for a SPARQL endpoint...");
					Repository r = new EndpointRepositorySupplier(value).get();
					try(RepositoryConnection connection = r.getConnection()) {
						if(Perform.on(connection).ping()) {
							log.debug("Ping was successfull, will consider it like a SPARQL endpoint");
							repositorySupplier = new EndpointRepositorySupplier(value, url.toString().contains("r"));
						} else {
							log.debug("Ping was NOT successfull, will stick to loading a URL");
							operations.add(new LoadFromUrl(url));
						}
					} catch (Exception e) {
						// oups, something bad happened, will stick to a URL
						e.printStackTrace();
						log.debug("Oups, an exception happened ("+e.getMessage()+", see stacktrace),  will stick to loading a URL");
						operations.add(new LoadFromUrl(url));
					}
				}
			} else if(value.startsWith("jdbc:virtuoso")){
				log.debug(value+" is a virtuoso jdbc connection");
				repositorySupplier = new VirtuosoReflectionRepositoryFactory(value);				
			} else {
				log.debug(value+" will try to be loaded from a file, directory or classpath resource");
				operations.add(new LoadFromFileOrDirectory(this.fileOrDirectoryOrURLs));
			}

		} else {
			// if more than one arg, consider they are necessarily files or directories
			operations.add(new LoadFromFileOrDirectory(this.fileOrDirectoryOrURLs));
		}
		
		RepositoryBuilder repositoryBuilder = new RepositoryBuilder(repositorySupplier, operations);
		return repositoryBuilder;
	}
	
	/**
	 * Test if given URL can be the URL of a SPARQL endpoint, or a URL pointing to a file
	 * @param url
	 * @return
	 */
	public static boolean isEndpointURL(String url) {
		// 1. test if a parser is available for that file extension.
		if(Rio.getParserFormatForFileName(url.toString()) != null) {
			return false;
		} 
		
		// 2. if not, try to ping the URL		
		Repository r = new EndpointRepositorySupplier(url).get();
		try(RepositoryConnection connection = r.getConnection()) {
			if(Perform.on(connection).ping()) {
				return true;
			}
		} catch (Exception e) {
			// exception : it is not a endpoint
			return false;
		}

		
		return false;
	}
	
	public void addFileOrDirectoryOrURL(String fileOrDirectoryOrURL) {
		this.fileOrDirectoryOrURLs.add(fileOrDirectoryOrURL);
	}
	
	public List<String> getFileOrDirectoryOrURLs() {
		return fileOrDirectoryOrURLs;
	}

	public void setFileOrDirectoryOrURLs(List<String> fileOrDirectoryOrURLs) {
		this.fileOrDirectoryOrURLs = fileOrDirectoryOrURLs;
	}
	
}
