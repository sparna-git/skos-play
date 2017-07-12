package fr.sparna.rdf.rdf4j.toolkit.repository.init;

import java.util.function.Consumer;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 * Defines common properties for operations that loads data in the repository, typically
 * the target graph in which to load the data, and a default namespace to use when parsing
 * RDF.
 * 
 * @author Thomas Francart
 *
 */
public abstract class AbstractLoadOperation implements Consumer<RepositoryConnection> {

	protected IRI targetGraph;
	protected String defaultNamespace = RDF.NAMESPACE;
	
	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	/**
	 * Sets default namespace to use to parse the data. Default is RDF.NAMESPACE
	 * @param defaultNamespace
	 */
	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}	
	
	public IRI getTargetGraph() {
		return targetGraph;
	}

	/**
	 * Sets the target graph in which data will be loaded.
	 * 
	 * @param targetGraph
	 */
	public void setTargetGraph(IRI targetGraph) {
		this.targetGraph = targetGraph;
	}
	
}
