package fr.sparna.rdf.rdf4j.toolkit.repository.init;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read and load data from a file passed as a parameter. If the file is actually a
 * directory, it will try to recursively load all the files inside it.
 * 
 * @author Thomas Francart
 *
 */
public class LoadFromFileOrDirectory extends AbstractLoadOperation {

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
	public void accept(RepositoryConnection connection) {
		if(this.rdfFiles != null) {
			for (String anRdf : this.rdfFiles ) {
				if(anRdf != null) {
					File anRdfFile = new File(anRdf);
					try {
						if(!anRdfFile.exists() && this.getClass().getResource(anRdf) != null) {
							log.debug("File "+anRdf+" was not found, but exists as a resource in classpath - will load from it.");
							
							try {
								connection.add(
										this.getClass().getResourceAsStream(anRdf),
										// TODO : ici mettre le namespace par defaut comme un parametre ?
										RDF.NAMESPACE,
										// NOTE : if we leave the RDFFormat parameter to null, then Sesame will determine
										// a default format based on the file extension. The only difference is that it will
										// not default to RDF/XML
										// on suppose que c'est du RDF/XML par defaut
										Rio.getParserFormatForFileName(anRdf).orElse(RDFFormat.RDFXML),
										(autoNamedGraphs)?connection.getValueFactory().createIRI(anRdfFile.toURI().toString()):((this.targetGraph != null)?connection.getValueFactory().createIRI(this.targetGraph.toString()):null)
								);
							} catch (RepositoryException e) {
								e.printStackTrace();
							}
							numberOfProcessedFiles++;
						} else {
							log.debug("Loading RDF from file or directory "+anRdf+" into repository...");

							try {
								this.loadFileOrDirectory(new File(anRdf), connection, new File(anRdf).toURI());
							} catch (RepositoryException e) {
								e.printStackTrace();
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
		IRI graph = (autoNamedGraphs)?
				connection.getValueFactory().createIRI(context.toString())
				:((this.targetGraph != null)?connection.getValueFactory().createIRI(this.targetGraph.toString()):null);
				
		log.debug("Processing file "+aFileOrDirectory.getAbsolutePath()+" into graph "+graph+"...");
		
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
						Rio.getParserFormatForFileName(aFileOrDirectory.getName()).orElse(RDFFormat.RDFXML),
						(autoNamedGraphs)?
								connection.getValueFactory().createIRI(context.toString())
								:((this.targetGraph != null)?connection.getValueFactory().createIRI(this.targetGraph.toString()):null)
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
