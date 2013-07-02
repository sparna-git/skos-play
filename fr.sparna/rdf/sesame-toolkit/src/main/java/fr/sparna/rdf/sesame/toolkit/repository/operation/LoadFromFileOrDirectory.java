package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read and load data from a file passed as a parameter. If the file is actually a
 * directory, it will try to recursively load all the files inside it.
 * 
 * @author Thomas Francart
 *
 */
public class LoadFromFileOrDirectory extends AbstractLoadOperation implements RepositoryOperationIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// les chemins vers les repertoires/dossiers a charger
	private List<String> rdfFiles;
	
	private boolean autoNamedGraphs = false;
	
	private int numberOfProcessedFiles = 0;
	

	/**
	 * Load the data from every file/directory referenced by the list of paths passed as parameters.
	 * @param rdfFileorDirectories
	 */
	public LoadFromFileOrDirectory(List<String> rdfFileorDirectories) {
		super();
		this.rdfFiles = rdfFileorDirectories;
	}
	
	/**
	 * Loads the data from a single file/directory given as parameter.
	 * @param rdfFileorDirectory
	 */
	public LoadFromFileOrDirectory(String rdfFileorDirectory) {
		this(Collections.singletonList(rdfFileorDirectory));
	}

	@Override
	public void execute(Repository repository) 
	throws RepositoryOperationException {
		if(this.rdfFiles != null) {
			for (String anRdf : this.rdfFiles ) {
				if(anRdf != null) {
					File anRdfFile = new File(anRdf);
					try {
						if(!anRdfFile.exists() && Thread.currentThread().getContextClassLoader().getResource(anRdf) != null) {
							log.debug("File "+anRdf+" was not found, but exists as a resource in classpath - will load from it.");
							try {
								repository.getConnection().add(
										Thread.currentThread().getContextClassLoader().getResource(anRdf),
										// TODO : ici mettre le namespace par defaut comme un parametre ?
										RDF.NAMESPACE,
										// on suppose que c'est du RDF/XML par defaut
										Rio.getParserFormatForFileName(anRdf, RDFFormat.RDFXML),
										(autoNamedGraphs)?repository.getValueFactory().createURI(anRdfFile.toURI().toString()):((this.targetGraph != null)?repository.getValueFactory().createURI(this.targetGraph.toString()):null)
								);
							} catch (RepositoryException e) {
								e.printStackTrace();
							}
							numberOfProcessedFiles++;
						} else {
							log.debug("Loading RDF from file or directory "+anRdf+" into repository...");
							RepositoryConnection connection = null;
							
							try {
								connection = repository.getConnection();
								connection.setAutoCommit(false);
								this.loadFileOrDirectory(new File(anRdf), connection, new File(anRdf).toURI());
							} catch (RepositoryException e) {
								e.printStackTrace();
							} finally {
								if(connection != null) {
									try {
										connection.commit();
										connection.close();
									} catch (RepositoryException e) {
										e.printStackTrace();
									}
								}
							}
						}						
					} catch (RDFParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} 
					log.debug("Done Loading RDF from "+anRdf+" into repository. Number of processed files : "+this.numberOfProcessedFiles);
				}
			}
		}
	}
	
	/**
	 * Charge le RDF contenu dans le fichier. Si le fichier est un répertoire, tente
	 * d'itérer sur tous les sous-fichiers et sous-répertoires.
	 * 
	 * @param aFileOrDirectory
	 * @param connection
	 * @param context
	 * @throws RDFParseException
	 * @throws RepositoryException
	 * @throws IOException
	 */
	private void loadFileOrDirectory(File aFileOrDirectory, RepositoryConnection connection, URI context)
	throws RDFParseException, RepositoryException, IOException {
		log.debug("Processing file "+aFileOrDirectory.getAbsolutePath()+"...");
		
		// don't process hidden files or directories
		if(aFileOrDirectory.isHidden()) {
			log.debug("Hidden file or directory - will not be processed.");
			return;
		}
		
		if(aFileOrDirectory.isDirectory()) {
			for (File f : aFileOrDirectory.listFiles()) {
				try {
					loadFileOrDirectory(f, connection, context);
				} catch (Exception e) {
					// on attrape l'exception et on passe au suivant
					e.printStackTrace();
				}
			}
		} else {
			try {
				connection.add(
						aFileOrDirectory,
						// TODO : mettre le namespace par defaut comme un parametre ?
						RDF.NAMESPACE,
						Rio.getParserFormatForFileName(aFileOrDirectory.getName(), RDFFormat.RDFXML),
						(autoNamedGraphs)?
								connection.getRepository().getValueFactory().createURI(context.toString())
								:((this.targetGraph != null)?connection.getRepository().getValueFactory().createURI(this.targetGraph.toString()):null)
				);
				numberOfProcessedFiles++;
			} catch (Exception e) {
				// on attrape l'exception et on la print - si on n'a que le finally, l'exception passe a la trappe
				e.printStackTrace();
			}
		}
	}	

	public List<String> getRdfFiles() {
		return rdfFiles;
	}

	public void setRdfFiles(List<String> rdfFiles) {
		this.rdfFiles = rdfFiles;
	}

	/**
	 * Returns the total number of files that were loaded by this DataInjector. This allows to test
	 * that at least one file has been found in the directory for exemple.
	 * 
	 * @return the number of files processed by this instance 
	 */
	public int getNumberOfProcessedFiles() {
		return numberOfProcessedFiles;
	}

	public boolean isAutoNamedGraphs() {
		return autoNamedGraphs;
	}

	/**
	 * Sets whether this data injector should store every loaded file in a separate named graph
	 * built from the file path. Default is false.
	 * @param namedGraphAware
	 */
	public void setAutoNamedGraphs(boolean namedGraphAware) {
		this.autoNamedGraphs = namedGraphAware;
	}

}
