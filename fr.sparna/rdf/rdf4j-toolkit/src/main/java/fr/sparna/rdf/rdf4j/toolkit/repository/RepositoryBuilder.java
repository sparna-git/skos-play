package fr.sparna.rdf.rdf4j.toolkit.repository;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromString;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromUrl;
import fr.sparna.rdf.rdf4j.toolkit.util.Namespaces;

/**
 * Supplies a Repository from a source Supplier<Repository>, and a list of Consumer<RepositoryConnection>
 * that will be called to e.g. load data in the repository.
 * 
 * @author Thomas Francart
 */
public class RepositoryBuilder implements Supplier<Repository> {
	
	

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private boolean autoRegisterNamespaces = true;
	
	private Supplier<Repository> repositorySupplier;
	private List<Consumer<RepositoryConnection>> operations;
	
	/**
	 * Creates a RepositoryBuilder from another Supplier<Repository> and the list of operations that will act on it.
	 * @param repositorySupplier
	 * @param initOperations
	 */
	public RepositoryBuilder(Supplier<Repository> repositorySupplier, List<Consumer<RepositoryConnection>> initOperations) {
		super();
		this.repositorySupplier = repositorySupplier;
		this.operations = initOperations;
	}
	
	/**
	 * Convenience constructor to build with a single operation
	 * 
	 * @param repositoryFactory
	 * @param anOperation
	 */
	public RepositoryBuilder(Supplier<Repository> repositoryFactory, Consumer<RepositoryConnection> anOperation) {
		this(repositoryFactory, new ArrayList<Consumer<RepositoryConnection>>(Collections.singletonList(anOperation)));
	}	
	
	/**
	 * Creates a RepositoryBuilder with a Supplier<Repository> and no init operations.
	 * @param repositoryFactory
	 */
	public RepositoryBuilder(Supplier<Repository> repositoryFactory) {
		this(repositoryFactory, (List<Consumer<RepositoryConnection>>)null);
	}
	
	/**
	 * Creates a RepositoryBuilder with a default LocalMemoryRepositoryFactory and no init operations.
	 */
	public RepositoryBuilder() {
		this(new LocalMemoryRepositorySupplier());
	}
	
	/**
	 * Creates a RepositoryBuilder with a default LocalMemoryRepositoryFactory and a single operation
	 */
	public RepositoryBuilder(Consumer<RepositoryConnection> operation) {
		this(new LocalMemoryRepositorySupplier(), operation);
	}
	
	/**
	 * Attemps to load the given URL in a local memory repository.
	 *  
	 * @param url
	 * @return
	 * @throws RepositoryFactoryException
	 */
	public static Repository fromURL(URL url) {
		RepositoryBuilder builder = new RepositoryBuilder(
				new LocalMemoryRepositorySupplier(),
				// true : use default fallback
				new LoadFromUrl(url, false, url.getFile().substring(1))
				);
		return builder.get();
	}
	
	/**
	 * Builds a repository loaded with the provided RDF data as a String.
	 * This relies on the LoadFromString operation.
	 * 
	 * @param rdf a String containing the rdf data to load
	 * @return
	 * @throws RepositoryFactoryException
	 */
	public static Repository fromRdf(String rdf) {
		RepositoryBuilder builder = new RepositoryBuilder(new LocalMemoryRepositorySupplier(), new LoadFromString(rdf));
		return builder.get();
	}
	
	@Override
	public Repository get() {
		// create an initialized repository
		Repository repository = repositorySupplier.get();
		
		// triggers the operations if any - to load data in the repository if needed.
		if(this.operations != null) {
			log.debug("Found init operations - will now run "+this.operations.size()+" operations...");
			try(RepositoryConnection connection = repository.getConnection()) {
				for (Consumer<RepositoryConnection> anOperation : this.operations) {
					log.debug("Running operation "+anOperation.getClass().getCanonicalName());
					anOperation.accept(connection);
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
	public void addOperation(Consumer<RepositoryConnection> operation) {
		if(this.operations == null) {
			this.operations = new ArrayList<Consumer<RepositoryConnection>>(Collections.singletonList(operation));
		} else {
			this.operations.add(operation);
		}
	}	

	public boolean isAutoRegisterNamespaces() {
		return autoRegisterNamespaces;
	}

	public void setAutoRegisterNamespaces(boolean autoRegisterNamespaces) {
		this.autoRegisterNamespaces = autoRegisterNamespaces;
	}

}
