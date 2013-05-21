package fr.sparna.rdf.sesame.toolkit.query;

import java.net.URI;
import java.util.Set;

/**
 * An extension of a {@link SPARQLQueryIfc} to add SPARQL 1.1 Update-specific parameters
 *  
 * @author Thomas Francart
 *
 */
public interface SPARQLUpdateIfc extends SPARQLQueryIfc {
	
	/**
	 * Returns the default graph in which insertion from this SPARQL query will be made (using
	 * the INSERT keyword).
	 * 
	 * <p />If this returns null, the default behavior of the {@link Perform} will be used.
	 * 
	 * @return a java.net.URI specifying the default graph to use for SPARQL INSERT operations
	 */
	public URI getDefaultInsertGraph();

	/**
	 * Returns the set of URIs (as java.net.URI) in which the deletions from this SPARQL query
	 * (using DELETE keyword) will be made. Return null to specify the default graph.
	 * 
	 * <p />If this returns null, the default behavior of the {@link Perform} will be used.
	 * 
	 * @return a Set of java.net.URI specifying the graphs in which SPARQL DELETE operations will take place
	 */
	public Set<URI> getDefaultRemoveGraphs();
}
