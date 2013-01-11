package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.net.URI;

import org.openrdf.model.vocabulary.RDF;

/**
 * Defines common properties for operations that loads data in the repository, typically
 * the target graph in which to load the data, and a default namespace to use when parsing
 * RDF.
 * 
 * @author Thomas Francart
 *
 */
public abstract class AbstractLoadOperation implements RepositoryOperationIfc {

	protected URI targetGraph;
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
	
	public URI getTargetGraph() {
		return targetGraph;
	}

	/**
	 * Sets the target graph in which data will be loaded.
	 * 
	 * @param targetGraph
	 */
	public void setTargetGraph(URI targetGraph) {
		this.targetGraph = targetGraph;
	}
	
}
