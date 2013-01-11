package fr.sparna.rdf.sesame.toolkit.repository;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.inferencer.fc.DirectTypeHierarchyInferencer;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Creates a simple in-memory Repository. Use FactoryConfiguration to activate RDFS inference
 * if needed.
 * 
 * @author Thomas Francart
 *
 */
public class LocalMemoryRepositoryFactory implements RepositoryFactoryIfc {
	
	public enum FactoryConfiguration {
		NO_INFERENCE,
		RDFS_AWARE,
		RDFS_WITH_DIRECT_TYPE_AWARE
	}
	
	private FactoryConfiguration configuration;

	/**
	 * Constructs a Factory with the default NO_INFERENCE configuration flag
	 */
	public LocalMemoryRepositoryFactory() {
		this(FactoryConfiguration.NO_INFERENCE);
	}
	
	/**
	 * Constructs a Factory with the given configuration flag; flags are defined as an
	 * enum in this class.
	 * 
	 * @param configuration	The <code>FactoryConfiguration</code> to use
	 */
	public LocalMemoryRepositoryFactory(FactoryConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public Repository createNewRepository() throws RepositoryFactoryException {
		// init repository
		Repository repository = null;
		try {
			
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
		} catch (RepositoryException e) {
			throw new RepositoryFactoryException("Repository config exception", e);
		}
		return repository;	
	}

	public FactoryConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(FactoryConfiguration configuration) {
		this.configuration = configuration;
	}
}
