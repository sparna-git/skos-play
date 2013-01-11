package fr.sparna.rdf.sesame.toolkit.query;

import java.net.URI;
import java.util.Set;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

/**
 * Concrete implementation of SPARQLUpdateIfc that implements getters and setters
 * for defaultRemoveGraphs and defaultInsertGrap.
 * 
 * @author Thomas Francart
 *
 */
public class SPARQLUpdate extends SPARQLQuery implements SPARQLUpdateIfc {

	protected Set<URI> defaultRemoveGraphs = null;
	
	protected URI defaultInsertGraph = null;

	public SPARQLUpdate(SPARQLQueryBuilderIfc builder) {
		super(builder);
	}

	public SPARQLUpdate(String sparql) {
		super(sparql);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<URI> getDefaultRemoveGraphs() {
		return defaultRemoveGraphs;
	}

	public void setDefaultRemoveGraphs(Set<URI> defaultRemoveGraphs) {
		this.defaultRemoveGraphs = defaultRemoveGraphs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URI getDefaultInsertGraph() {
		return defaultInsertGraph;
	}

	public void setDefaultInsertGraph(URI defaultInsertGraph) {
		this.defaultInsertGraph = defaultInsertGraph;
	}
	
}
