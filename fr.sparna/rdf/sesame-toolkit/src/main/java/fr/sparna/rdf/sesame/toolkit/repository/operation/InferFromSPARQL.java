package fr.sparna.rdf.sesame.toolkit.repository.operation;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

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
	 * @param repository	The repository to execute SPARQL queries on
	 * @param query			The query to execute
	 */
	public InferFromSPARQL(SPARQLQueryBuilderIfc query) {
		super(null, query);
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
