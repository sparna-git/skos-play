package fr.sparna.rdf.sesame.toolkit.repository;

import org.eclipse.rdf4j.repository.Repository;

/**
 * Creates and returns initialized Sesame repositories.
 * <p />The method <code>init()</code> is supposed to be already called on the created repository
 * <p />Returned repositories can be loaded with data or not depending on the implementation.
 * 
 * @author Thomas Francart
 *
 */
public interface RepositoryFactoryIfc {
	
	/**
	 * Creates and returns a new initialised <code>Repository</code>. Each call to this method
	 * will create a new instance of <code>Repository</code>.
	 * 
	 * @return a new initialized <code>Repository</code> instance
	 */
	public Repository createNewRepository() throws RepositoryFactoryException;
	
}
