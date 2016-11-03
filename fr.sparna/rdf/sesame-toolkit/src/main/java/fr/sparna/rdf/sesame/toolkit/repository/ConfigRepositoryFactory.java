package fr.sparna.rdf.sesame.toolkit.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ModelFactory;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.FileUtil;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryConnectionDoorman;

/**
 * Provides a Sesame repository by initializing it from a Sesame config file.
 * A Sesame config file describes the Sail to use, and the various parameters for
 * the sail. A Sesame config file is described in RDF.
 * 
 * This uses a ConfigProviderIfc to read the config file.
 * 
 * @author Thomas Francart
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
					Model model = new LinkedHashModelFactory().createEmptyModel();
					model.addAll(s);
					config.parse(model, null);				

					// add the config to the repository manager
					log.debug("Adding repository config for repository "+this.repositoryName);
					rm.addRepositoryConfig(new RepositoryConfig(this.repositoryName, config));	
				} finally {
					RepositoryConnectionDoorman.closeQuietly(connection);
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
