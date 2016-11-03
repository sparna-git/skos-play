package fr.sparna.rdf.sesame.toolkit.repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromUrl;

/**
 * Tries to determine automatically the implementation of the repository to use based on a String parameter.
 * <p/>The sequence is as follow :
 * <ul>
 *   <li />if the String can be parsed as a valid URL 
 *   (<code>new URL(fileOrDirectoryOrURL) does not return an exception</code>), and the URL does not have a known RDF file extension, then a
 *   <code>EndpointRepositoryFactory</code> is used and the repository creation is delegated to it.
 *   <li />else, a <code>LocalMemoryRepositoryFactory</code> will be used with a <code>LoadFromFileOrDirectoryOrURL</code> operation
 *   initialized with the given String
 * </ul>
 * 
 * @author Thomas Francart
 *
 */
public class StringRepositoryFactory extends RepositoryBuilder {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected List<String> fileOrDirectoryOrURLs = new ArrayList<String>();
	
	protected RepositoryFactoryIfc localRepositoryFactory; 
	
	public StringRepositoryFactory(List<String> fileOrDirectoryOrURLs, RepositoryFactoryIfc localRepositoryFactory) {
		super();
		this.fileOrDirectoryOrURLs = fileOrDirectoryOrURLs;
		this.localRepositoryFactory = localRepositoryFactory;
		init();
	}
	
	public StringRepositoryFactory(List<String> fileOrDirectoryOrURLs) {
		this(fileOrDirectoryOrURLs, new LocalMemoryRepositoryFactory());
	}
	
	public StringRepositoryFactory(String fileOrDirectoryOrURL, RepositoryFactoryIfc localRepositoryFactory) {
		this(Collections.singletonList(fileOrDirectoryOrURL), localRepositoryFactory);
	}
	
	public StringRepositoryFactory(String fileOrDirectoryOrURL) {
		this(Collections.singletonList(fileOrDirectoryOrURL), new LocalMemoryRepositoryFactory());
	}
	
	protected void init() {
		if(fileOrDirectoryOrURLs != null && fileOrDirectoryOrURLs.size() == 1) {
			String value = fileOrDirectoryOrURLs.get(0);

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
				if(Rio.getParserFormatForFileName(url.toString()) != null) {
					// looks like a file we can parse, let's parse it
					log.debug(value+" can be parsed using an available parser");
					this.setRepositoryFactory(this.localRepositoryFactory);
					this.addOperation(new LoadFromUrl(url));
				} else {
					// does not look like a file we can parse, try to ping it to see if it is a endpoint
					try {
						log.debug(value+" cannot be parsed using availble parser, will try to ping for a SPARQL endpoint...");
						Repository r = new EndpointRepositoryFactory(value).createNewRepository();
						if(Perform.on(r).ping()) {
							log.debug("Ping was successfull, will consider it like a SPARQL endpoint");
							this.setRepositoryFactory(new EndpointRepositoryFactory(value, url.toString().contains("openrdf-sesame")));
						} else {
							log.debug("Ping was NOT successfull, will stick to loading a URL");
							this.setRepositoryFactory(this.localRepositoryFactory);
							this.addOperation(new LoadFromUrl(url));
						}
					} catch (RepositoryFactoryException e) {
						// oups, something bad happened, will stick to a URL
						e.printStackTrace();
						log.debug("Oups, an exception happened ("+e.getMessage()+", see stacktrace),  will stick to loading a URL");
						this.setRepositoryFactory(this.localRepositoryFactory);
						this.addOperation(new LoadFromUrl(url));
					}
				}
			} else if(value.startsWith("jdbc:virtuoso")){
				log.debug(value+" is a virtuoso jdbc connection");
				this.setRepositoryFactory(new VirtuosoReflectionRepositoryFactory(value));				
			} else {
				log.debug(value+" will try to be loaded from a file, directory or classpath resource");
				this.setRepositoryFactory(this.localRepositoryFactory);
				this.addOperation(new LoadFromFileOrDirectory(this.fileOrDirectoryOrURLs));
			}

		} else {
			// if more than one arg, consider they are necessarily files or directories
			this.setRepositoryFactory(this.localRepositoryFactory);
			this.addOperation(new LoadFromFileOrDirectory(this.fileOrDirectoryOrURLs));
		}
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
		try {
			Repository r = new EndpointRepositoryFactory(url).createNewRepository();
			if(Perform.on(r).ping()) {
				return true;
			}
		} catch (RepositoryFactoryException e) {
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

	@Deprecated
	public String getFileOrDirectoryOrURL() {
		return (fileOrDirectoryOrURLs != null && fileOrDirectoryOrURLs.size() > 0)?fileOrDirectoryOrURLs.get(0):null;
	}

	@Deprecated
	public void setFileOrDirectoryOrURL(String fileOrDirectoryOrURL) {
		this.fileOrDirectoryOrURLs = new ArrayList<String>();
		this.fileOrDirectoryOrURLs.add(fileOrDirectoryOrURL);
	}
	
}
