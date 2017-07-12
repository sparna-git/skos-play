package fr.sparna.rdf.rdf4j.toolkit.repository.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * Read and load data from an InputStream, that can be a resource in the classpath, a URL, etc.
 * The format of the RDF data to be read must be passed along with the stream.
 * 
 * @author Thomas Francart
 *
 */
public class LoadFromStream extends AbstractLoadOperation implements Consumer<RepositoryConnection> {

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
				Rio.getParserFormatForFileName(resource).orElse(RDFFormat.RDFXML),
				RDF.NAMESPACE
		);
	}

	@Override
	public void accept(RepositoryConnection connection) {
		try {
			connection.add(
					this.stream,
					this.defaultNamespace,
					this.format,
					(this.targetGraph != null)?this.targetGraph:null
			);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
