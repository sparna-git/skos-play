package fr.sparna.rdf.sesame.toolkit.repository;

import java.util.Map;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.rdf4j.http.client.util.HttpClientBuilders;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

/**
 * Creates a Sesame Repository that connects to a SPARQL endpoint.
 * 
 * @author Thomas Francart
 */
public class EndpointRepositoryFactory implements RepositoryFactoryIfc {

	private boolean isSesame = false;
	private Map<String, String> additionalHttpHeaders;
	private String endpoint;

	public EndpointRepositoryFactory(String endpoint, boolean isSesame) {
		super();
		this.endpoint = endpoint;
		this.isSesame = isSesame;
	}
	
	public EndpointRepositoryFactory(String endpoint) {
		this(endpoint, false);
	}

	@Override
	public Repository createNewRepository() throws RepositoryFactoryException {
		
		Repository repository = null;
		try {		
			repository = (this.isSesame)?new HTTPRepository(this.endpoint):new SPARQLRepository(this.endpoint);
			
			if(repository instanceof SPARQLRepository) {
				
				// set a custom Http Pooling Manager with a higher number of connection per route
				PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		        cm.setMaxTotal(100);
		        cm.setDefaultMaxPerRoute(20);
		        cm.closeExpiredConnections();
		        ((SPARQLRepository)repository).setHttpClient(HttpClientBuilder.create().setConnectionManager(cm).build());
				
				if(this.additionalHttpHeaders != null) {
					((SPARQLRepository)repository).setAdditionalHttpHeaders(additionalHttpHeaders);
				}				
			}
			
			repository.initialize();
		} catch (RepositoryException e) {
			throw new RepositoryFactoryException(e);
		}		
		
		return repository;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Map<String, String> getAdditionalHttpHeaders() {
		return additionalHttpHeaders;
	}

	public void setAdditionalHttpHeaders(Map<String, String> additionalHttpHeaders) {
		this.additionalHttpHeaders = additionalHttpHeaders;
	}

}
