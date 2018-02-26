package fr.sparna.rdf.rdf4j.toolkit.repository;

import java.util.function.Supplier;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.inferencer.fc.DirectTypeHierarchyInferencer;
import org.eclipse.rdf4j.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * Supplies a simple in-memory Repository. Use FactoryConfiguration to activate RDFS inference
 * if needed.
 * 
 * @author Thomas Francart
 *
 */
public class LocalMemoryRepositorySupplier implements Supplier<Repository> {
	
	public enum FactoryConfiguration {
		NO_INFERENCE,
		RDFS_AWARE,
		RDFS_WITH_DIRECT_TYPE_AWARE
	}
	
	private FactoryConfiguration configuration;

	/**
	 * Constructs a Supplier with the default NO_INFERENCE configuration flag
	 */
	public LocalMemoryRepositorySupplier() {
		this(FactoryConfiguration.NO_INFERENCE);
	}
	
	/**
	 * Constructs a Supplierc with the given configuration flag; flags are defined as an
	 * enum in this class.
	 * 
	 * @param configuration	The <code>FactoryConfiguration</code> to use
	 */
	public LocalMemoryRepositorySupplier(FactoryConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	public Repository get() throws RepositoryException {
		// init repository
		Repository repository = null;
			
		switch(this.configuration) {
		case NO_INFERENCE : {
			repository = new SailRepository(new MemoryStore());
			break;
		}
		case RDFS_AWARE : {
			repository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
			break;
		}
		case RDFS_WITH_DIRECT_TYPE_AWARE : {
			repository = new SailRepository(new DirectTypeHierarchyInferencer(new MemoryStore()));
			break;
		}
		}
		
		repository.initialize();
		
		return repository;	
	}

	public FactoryConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(FactoryConfiguration configuration) {
		this.configuration = configuration;
	}
}
