package fr.sparna.rdf.sesame.toolkit.repository;

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

}
