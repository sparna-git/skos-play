package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.IOException;
import java.io.InputStream;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import fr.sparna.rdf.sesame.toolkit.util.RepositoryConnectionDoorman;

/**
 * Read and load data from an InputStream, that can be a resource in the classpath, a URL, etc.
 * The format of the RDF data to be read must be passed along with the stream.
 * 
 * @author Thomas Francart
 *
 */
public class LoadFromStream extends AbstractLoadOperation implements RepositoryOperationIfc {

	// stream to load from
	private InputStream stream;
	// format to use
	private RDFFormat format;

	/**
	 * Specifies the default namespace for the data to be read. By default it is RDF.NAMESPACE.
	 * 
	 * @param stream
	 * @param format
	 * @param defaultNamespace
	 */
	public LoadFromStream(InputStream stream, RDFFormat format, String defaultNamespace) {
		super();
		this.stream = stream;
		this.format = format;
		this.defaultNamespace = defaultNamespace;
	}
	
	/**
	 * Loads from the given stream using the given RDF format, using RDF.NAMESPACE as the default namespace.
	 * 
	 * @param stream
	 * @param format
	 */
	public LoadFromStream(InputStream stream, RDFFormat format) {
		this(stream, format, RDF.NAMESPACE);
	}
	
	/**
	 * Loads the given classpath resource relative the given owner, using the default RDF.NAMESPACE as the namespace
	 * 
	 * @param owner
	 * @param resource
	 */
	public LoadFromStream(Object owner, String resource) {
		this(
				owner.getClass().getResourceAsStream(resource),
				RDFFormat.forFileName(resource, RDFFormat.RDFXML),
				RDF.NAMESPACE
		);
	}

	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {
		try {
			RepositoryConnection connection = repository.getConnection();
			try {
				connection.add(
						this.stream,
						this.defaultNamespace,
						this.format,
						(this.targetGraph != null)?repository.getValueFactory().createURI(this.targetGraph.toString()):null
				);
			} finally {
				RepositoryConnectionDoorman.closeQuietly(connection);
			}
		} catch (RDFParseException e) {
			throw new RepositoryOperationException("Error when parsing RDF. "+this.format.getName()+" was expected. Error was : "+e.getMessage(), e);
		} catch (IOException e) {
			throw new RepositoryOperationException("Cannot read from stream", e);
		} catch (RepositoryException e) {
			throw new RepositoryOperationException(e);
		}
	}

}
