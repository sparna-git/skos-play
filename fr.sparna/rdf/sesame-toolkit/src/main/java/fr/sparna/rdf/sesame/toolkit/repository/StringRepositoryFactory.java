package fr.sparna.rdf.sesame.toolkit.repository;

import java.net.MalformedURLException;
import java.net.URL;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.handler.DebugHandler;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;

/**
 * Tries to determine automatically the implementation of the repository to use based on a String parameter.
 * <p/>The sequence is as follow :
 * <ul>
 *   <li />if the String can be parsed as a valid URL 
 *   (<code>new URL(fileOrDirectoryOrURL) does not return an exception</code>), then a
 *   <code>EndpointRepositoryFactory</code> is used and the repository creation is delegated to it.
 *   <li />else, a <code>LocalMemoryRepositoryFactory</code> will be used with a <code>LoadFromFile</code> operation
 *   initialized with the given String
 * </ul>
 * 
 * @author Thomas Francart
 *
 */
public class StringRepositoryFactory extends RepositoryBuilder {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected String fileOrDirectoryOrURL;

	public StringRepositoryFactory(String fileOrDirectoryOrURL) {
		super();
		this.fileOrDirectoryOrURL = fileOrDirectoryOrURL;
		
		URL url = null;
		try {
			url = new URL(this.fileOrDirectoryOrURL);
		} catch (MalformedURLException e) {
			log.debug(this.fileOrDirectoryOrURL+" is not a valid URL. It will be interpreted as a file or directory path.");
		}
		
		if(url != null) {
			this.setRepositoryFactory(new EndpointRepositoryFactory(this.fileOrDirectoryOrURL));

//			// test if endpoint answers a simple query. Otherwise we consider the URL as the URL of an RDF file
//			try {
//				Repository temp = this.createNewRepository();
//				
//				try {
//					Perform.on(temp).select(new SelectSPARQLHelper("SELECT DISTINCT ?type WHERE { <http://www.this.uri.does.not.exists> a ?type }", new ResourceListHandler()));
//					log.debug("Querying endpoint "+this.fileOrDirectoryOrURL+" succeeded. Will initialize an EndpointRepository.");
//					// nothing more, we're all set.
//				} catch (Exception e) {
//					// catching every exception (in particular if we give a URL wit protocol file://... we get an exception from commons.http
//					log.debug("Failed when querying endpoint "+this.fileOrDirectoryOrURL+". Will try to load remote URL in a local memory repository.");
//					// TODO : if endpoint is indeed up but querying fails for some reason, we are here and we don't know
//					this.setRepositoryFactory(new LocalMemoryRepositoryFactory());
//					this.addOperation(new LoadFromURL(url));
//				}
//				
//			} catch (RepositoryFactoryException e) {
//				log.debug("Failed when attempting to initialize endpoint "+this.fileOrDirectoryOrURL+" is not a valid URL. It will be interpreted as an RDF file URL.");
//				this.setRepositoryFactory(new LocalMemoryRepositoryFactory());
//				this.addOperation(new LoadFromURL(url));
//			}
		} else {
			this.setRepositoryFactory(new LocalMemoryRepositoryFactory());
			this.addOperation(new LoadFromFileOrDirectory(this.fileOrDirectoryOrURL));
		}
	}
	
	public String getFileOrDirectoryOrURL() {
		return fileOrDirectoryOrURL;
	}

	public void setFileOrDirectoryOrURL(String fileOrDirectoryOrURL) {
		this.fileOrDirectoryOrURL = fileOrDirectoryOrURL;
	}

	
	public static void main(String[] args) throws Exception {
		// StringRepositoryFactory factory = new StringRepositoryFactory("/home/thomas/workspace/datalift/geo2012.ttl");
		// StringRepositoryFactory factory = new StringRepositoryFactory("http://lov.okfn.org/endpoint/lov");
		// StringRepositoryFactory factory = new StringRepositoryFactory("http://axel.deri.ie/teaching/SemWebTech_2009/testdata/foaf.ttl");
		// StringRepositoryFactory factory = new StringRepositoryFactory("file:///home/thomas/workspace/datalift/geo2012.ttl");
		
		// Repository r = factory.createNewRepository();
		// Perform.on(r).select(new SelectSPARQLHelper("SELECT DISTINCT ?type WHERE { ?s a ?type }", new DebugHandler()));
	}
	
}
