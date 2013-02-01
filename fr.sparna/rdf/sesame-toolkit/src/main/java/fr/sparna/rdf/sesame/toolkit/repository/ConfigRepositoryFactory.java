package fr.sparna.rdf.sesame.toolkit.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.FileUtil;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryTransaction;

/**
 * Provides a Sesame repository by initializing it from a Sesame config file.
 * A Sesame config file describes the Sail to use, and the various parameters for
 * the sail. A Sesame config file is described in RDF.
 * 
 * This uses a ConfigProviderIfc to read the config file.
 * 
 * @author mondeca
 *
 */
public class ConfigRepositoryFactory implements RepositoryFactoryIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected static final String CONFIG_BASE_URI = "http://www.this_is_hardcoded.com";

	// le repertoire sous lequel toutes les config vont se retrouver
	protected String repositoriesRoot = ".sesame-toolkit-repositories";
	// le nom du repository a creer (va creer un repertoire sous repositoriesRoot)
	protected String repositoryName = "default";
	// l'objet qui va nous fournir la config Sesame a utiliser
	protected ConfigProviderIfc configProvider;
	// un boolean pour indiquer si on veut supprimer le repertoire au depart
	// si on ne le fait pas, le repository ne SERA PAS recree une deuxieme fois
	protected boolean cleanAtStartup = true;	

	public ConfigRepositoryFactory(ConfigProviderIfc configProvider) {
		super();
		this.configProvider = configProvider;
	}
	
	public ConfigRepositoryFactory() {
		super();
	}


	@Override
	public Repository createNewRepository() throws RepositoryFactoryException {
		try {
			File repositoriesRootFile = new File(repositoriesRoot);

			// clean le directory if needed
			if(this.cleanAtStartup) {
				log.debug("Cleaning file "+repositoriesRootFile+" recursively");
				FileUtil.deleteFileRecursive(repositoriesRootFile);
			}

			// init le manager de repositories sesame
			LocalRepositoryManager rm = new LocalRepositoryManager(repositoriesRootFile);
			rm.initialize();

			// si le repository n'existe pas deja...
			// (ce test est obligatoire sinon on a une exception si on essaie de recreer un repo qui existe deja)
			if(!rm.hasRepositoryConfig(this.repositoryName)) {
				SailRepositoryConfig config = new SailRepositoryConfig();

				// charger la config du repository
				Repository configRepository = new SailRepository(new MemoryStore());
				configRepository.initialize();

				RepositoryConnection connection = configRepository.getConnection();
				try {
					try {
						connection.add(
								this.configProvider.getConfigAsStream(),
								CONFIG_BASE_URI,
								this.configProvider.getConfigFormat()
						);
					} catch (RDFParseException e) {
						e.printStackTrace();
						throw new RepositoryException("Unable to parse provided config file",e);
					}  catch (IOException e) {
						e.printStackTrace();
						throw new RepositoryException("I/O exception when reading config stream",e);
					}

					// charger la config
					List<Statement> s = connection.getStatements(null, null, null, true).asList();
					config.parse(new GraphImpl(s), null);				

					// add the config to the repository manager
					log.debug("Adding repository config for repository "+this.repositoryName);
					rm.addRepositoryConfig(new RepositoryConfig(this.repositoryName, config));	
				} finally {
					RepositoryTransaction.closeQuietly(connection);
				}
			}

			// initialize the repository				
			Repository repo = rm.getRepository(this.repositoryName);
			log.debug("Initialize repository "+this.repositoryName+"...");
			repo.initialize();
			log.debug("Done initializing");
			
			return repo;
		} catch (RepositoryConfigException e) {
			throw new RepositoryFactoryException("Repository config exception",e);
		} catch (RepositoryException e) {
			throw new RepositoryFactoryException("Repository config exception",e);
		}
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getRepositoriesRoot() {
		return repositoriesRoot;
	}

	public void setRepositoriesRoot(String repositoriesRoot) {
		this.repositoriesRoot = repositoriesRoot;
	}

	public boolean isCleanAtStartup() {
		return cleanAtStartup;
	}

	public void setCleanAtStartup(boolean cleanAtStartup) {
		this.cleanAtStartup = cleanAtStartup;
	}

	public ConfigProviderIfc getConfigProvider() {
		return configProvider;
	}
	
	public void setConfigProvider(ConfigProviderIfc configProvider){
		this.configProvider = configProvider;
	}

}
