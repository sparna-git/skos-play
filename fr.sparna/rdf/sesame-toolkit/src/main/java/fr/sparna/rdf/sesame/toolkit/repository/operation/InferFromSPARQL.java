package fr.sparna.rdf.sesame.toolkit.repository.operation;

import org.openrdf.repository.Repository;

/**
 * A special kind of LoadFromSPARQL operation where the SPARQL queries are executed on the source repository
 * 
 * @author Thomas Francart
 *
 */
public class InferFromSPARQL extends LoadFromSPARQL implements RepositoryOperationIfc {
	
	public InferFromSPARQL(String sparqlPath) {
		super(null, sparqlPath);
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
