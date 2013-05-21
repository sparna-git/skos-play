package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.File;
import java.util.List;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLQueryIfc;

/**
 * A special kind of LoadFromSPARQL operation where the SPARQL queries are executed on the source repository
 * 
 * @author Thomas Francart
 *
 */
public class InferFromSPARQL extends LoadFromSPARQL implements RepositoryOperationIfc {
	
	/**
	 * Shortcut to execute and load a single inference query
	 * 
	 * @param query			The query to execute
	 */
	public InferFromSPARQL(SPARQLQueryIfc query) {
		super(null, query);
	}
	
	/**
	 * Will execute and load the given list of SPARQL queries on the repository
	 * 
	 * @param sparqlQueries		The list of SPARQL queries to execute
	 */
	public InferFromSPARQL(List<SPARQLQueryIfc> sparqlQueries) {
		super(null, sparqlQueries);
	}
	
	public InferFromSPARQL(File directory) {
		super(null, directory);
	}

	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {
		// sets the source repository as being the one we initialize - same source and same target
		this.sourceRepository = repository;
		// execute on the same target repository
		super.execute(repository);
	}

}
