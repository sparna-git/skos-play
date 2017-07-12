package fr.sparna.rdf.rdf4j.toolkit.repository;

import java.util.function.Supplier;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

/**
 * Creates a Sesame Repository that connects to a SPARQL endpoint.
 * 
 * @author Thomas Francart
 */
public class EndpointRepositorySupplier implements Supplier<Repository> {

	private boolean isSesame = false;
	private String endpoint;

	public EndpointRepositorySupplier(String endpoint, boolean isSesame) {
		super();
		this.endpoint = endpoint;
		this.isSesame = isSesame;
	}
	
	public EndpointRepositorySupplier(String endpoint) {
		this(endpoint, false);
	}

	@Override
	public Repository get() {		
		Repository repository = (this.isSesame)?new HTTPRepository(this.endpoint):new SPARQLRepository(this.endpoint);
		repository.initialize();		
		return repository;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

}
