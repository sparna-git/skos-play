package fr.sparna.rdf.sesame.toolkit.repository.operation;

import org.openrdf.repository.Repository;



/**
 * Executes an operation on a Repository, typically to load data into it, coming from a file, a URL, an XML+XSL, etc.
 * depending on the implementation. <code>RepositoryOperationIfc</code>s can be given as parameters to an
 * {@link fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder InitializingRepositoryFactory} to
 * creates <code>Repository</code>s loaded with data automatically.
 * 
 * @author Thomas Francart
 *
 */
public interface RepositoryOperationIfc {

	/**
	 * Adds some data into the repository passed as a variable. Data can come from an RDF file,
	 * an XML file + an XSL file, remote data from a URL, or any other data source depending on
	 * the implementation.
	 * 
	 * @param repository the repository on which to apply the operation
	 * @throws RepositoryProviderException
	 */
	public void execute(Repository repository) throws RepositoryOperationException;
	
}
