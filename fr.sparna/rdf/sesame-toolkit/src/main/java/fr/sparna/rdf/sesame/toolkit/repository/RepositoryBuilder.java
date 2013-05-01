package fr.sparna.rdf.sesame.toolkit.repository;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromString;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromURL;
import fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationException;
import fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationIfc;
import fr.sparna.rdf.sesame.toolkit.util.Namespaces;

/**
 * Wraps another <code>RepositoryFactoryIfc</code> to create a new Repository, and executes
 * <code>RepositoryOperationIfc</code>s on it afterwards.
 * 
 * @author Thomas Francart
 */
public class RepositoryBuilder implements RepositoryFactoryIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// listeners
	private List<RepositoryOperationIfc> operations;
	
	private RepositoryFactoryIfc repositoryFactory;
	
	private boolean autoRegisterNamespaces = true;
	
	public RepositoryBuilder(RepositoryFactoryIfc repositoryFactory, List<RepositoryOperationIfc> operations) {
		super();
		this.repositoryFactory = repositoryFactory;
		this.operations = operations;
	}
	
	/**
	 * Convenience constructor to build with a single operation
	 * 
	 * @param repositoryFactory
	 * @param anOperation
	 */
	public RepositoryBuilder(RepositoryFactoryIfc repositoryFactory, RepositoryOperationIfc anOperation) {
		this(repositoryFactory, new ArrayList<RepositoryOperationIfc>(Collections.singletonList(anOperation)));
	}	
	
	public RepositoryBuilder(RepositoryFactoryIfc repositoryFactory) {
		this(repositoryFactory, (List<RepositoryOperationIfc>)null);
	}
	
	/**
	 * Creates a RepositoryBuilder with a default LocalMemoryRepositoryFactory
	 */
	public RepositoryBuilder() {
		this(new LocalMemoryRepositoryFactory());
	}
	
	/**
	 * Shortcut to a StringRepositoryFactory.
	 * 
	 * @param fileOrDirectoryOrEndpointURL
	 * @return
	 * @throws RepositoryFactoryException
	 */
	public static Repository fromString(String fileOrDirectoryOrEndpointURL) 
	throws RepositoryFactoryException {
		RepositoryBuilder builder = new RepositoryBuilder(new StringRepositoryFactory(fileOrDirectoryOrEndpointURL));
		return builder.createNewRepository();
	}

	/**
	 * Builds a repository loaded with the provided RDF data as a String.
	 * This relies on the LoadFromString operation.
	 * 
	 * @param rdf a String containing the rdf data to load
	 * @return
	 * @throws RepositoryFactoryException
	 */
	public static Repository fromRdf(String rdf) 
	throws RepositoryFactoryException {
		RepositoryBuilder f = new RepositoryBuilder(new LocalMemoryRepositoryFactory(), new LoadFromString(rdf));
		return f.createNewRepository();
	}
	
	/**
	 * Attemps to load the given URL in a local memory repository.
	 *  
	 * @param url
	 * @return
	 * @throws RepositoryFactoryException
	 */
	public static Repository fromURL(URL url) throws RepositoryFactoryException {
		RepositoryBuilder builder = new RepositoryBuilder(
				new LocalMemoryRepositoryFactory(),
				// true : use default fallback
				new LoadFromURL(url, true)
				);
		return builder.createNewRepository();
	}

	@Override
	public Repository createNewRepository()
	throws RepositoryFactoryException {
		// create an initialized repository
		Repository repository = repositoryFactory.createNewRepository();
		
		// triggers the operations if any - to load data in the repository if needed.
		if(this.operations != null) {
			log.debug("Found init operations - will now run "+this.operations.size()+" operations...");
			for (RepositoryOperationIfc anOperation : this.operations) {
				try {
					log.debug("Running operation "+anOperation.getClass().getCanonicalName());
					anOperation.execute(repository);
				} catch (RepositoryOperationException e) {
					throw new RepositoryFactoryException(e);
				}
			}
		}
		
		if(autoRegisterNamespaces) {
			// register Namespaces globally
			Namespaces.getInstance().withRepository(repository);
		}
		
		return repository;
	}

	/**
	 * Adds a single operation to the list of operations of this builder
	 * 
	 * @param operation	The operation to add
	 */
	public void addOperation(RepositoryOperationIfc operation) {
		if(this.operations == null) {
			this.operations = new ArrayList<RepositoryOperationIfc>(Arrays.asList(new RepositoryOperationIfc[] {operation}));
		} else {
			this.operations.add(operation);
		}
	}	
	
	public List<RepositoryOperationIfc> getOperations() {
		return operations;
	}

	public void setOperations(List<RepositoryOperationIfc> operations) {
		this.operations = operations;
	}

	public RepositoryFactoryIfc getRepositoryFactory() {
		return repositoryFactory;
	}

	public void setRepositoryFactory(RepositoryFactoryIfc repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public boolean isAutoRegisterNamespaces() {
		return autoRegisterNamespaces;
	}

	public void setAutoRegisterNamespaces(boolean autoRegisterNamespaces) {
		this.autoRegisterNamespaces = autoRegisterNamespaces;
	}

}
