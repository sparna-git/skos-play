package fr.sparna.rdf.sesame.toolkit.repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.repository.Repository;
import org.openrdf.rio.Rio;
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
			
			// try with a SPARQL endpoint 
			URL url = null;

			if(value.startsWith("http")) {
				try {
					url = new URL(value);
				} catch (MalformedURLException e) {
					log.debug(value+" is not a valid URL. It will be interpreted as a file or directory path.");
				}
			}
			
			
			if(url != null) {
				if(Rio.getParserFormatForFileName(url.toString()) != null) {
					// looks like a file we can parse, let's parse it
					this.setRepositoryFactory(this.localRepositoryFactory);
					this.addOperation(new LoadFromFileOrDirectory(this.fileOrDirectoryOrURLs));
				} else {
					// does not look like a file we can parse, try to ping it to see if it is a endpoint
					try {
						Repository r = new EndpointRepositoryFactory(value).createNewRepository();
						if(Perform.on(r).ping()) {
							this.setRepositoryFactory(new EndpointRepositoryFactory(value, url.toString().contains("openrdf-sesame")));
						} else {
							this.setRepositoryFactory(this.localRepositoryFactory);
							this.addOperation(new LoadFromUrl(url));
						}
					} catch (RepositoryFactoryException e) {
						// oups, something bad happened, will stick to a URL
						e.printStackTrace();
						this.setRepositoryFactory(this.localRepositoryFactory);
						this.addOperation(new LoadFromUrl(url));
					}
				}
			} else if(value.startsWith("jdbc:virtuoso")){
				this.setRepositoryFactory(new VirtuosoReflectionRepositoryFactory(value));				
			} else {
				this.setRepositoryFactory(this.localRepositoryFactory);
				this.addOperation(new LoadFromFileOrDirectory(this.fileOrDirectoryOrURLs));
			}

		} else {
			// if more than one arg, consider they are necessarily files or directories
			this.setRepositoryFactory(this.localRepositoryFactory);
			this.addOperation(new LoadFromFileOrDirectory(this.fileOrDirectoryOrURLs));
		}
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
