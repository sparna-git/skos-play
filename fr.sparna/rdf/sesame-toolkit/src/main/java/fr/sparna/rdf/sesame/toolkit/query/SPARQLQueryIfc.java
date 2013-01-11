package fr.sparna.rdf.sesame.toolkit.query;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Value;

/**
 * Defines a SPARQL query (as a String), along with its bindings, default graphs, named graphs,
 * and includeInferred parameters (everything needed to execute the query).
 *  
 * @author Thomas Francart
 *
 */
public interface SPARQLQueryIfc {
	
	/**
	 * Returns a SPARQL query to be executed by a {@link SesameSPARQLExecuter}
	 * 
	 * @return The SPARQL query
	 */
	public String getSPARQL();
	
	/**
	 * Returns the bindings to be set on the returned SPARQL query. For exemple, if the method
	 * getSPARQL() returns the query "CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}", one could bind the "s" variables
	 * by returning "new HashMap(){{put("s","http://www.exemple.com/ontology#123456");}};" 
	 * 
	 * @return the bindings that will be set on the SPARQL query
	 */
	public Map<String, Value> getBindings();
	
	/**
	 * Tells if the sparql query should be executed by including the inferred statements or not.
	 * <p />If this returns null, the default behavior of the {@link SesameSPARQLExecuter} will be used.
	 * 
	 * @return true if this query will use inferred statements
	 */
	public Boolean isIncludeInferred();
	
	/**
	 * Returns the set of URIs (as java.net.URI) that will constitute the default graph in which the query
	 * will be executed. Return null to specify no default graph.
	 * <p />If this returns null, the default behavior of the {@link SesameSPARQLExecuter} will be used.
	 * 
	 * @return a Set of java.net.URI specifying the default graph
	 */
	public Set<URI> getDefaultGraphs();
	
	/**
	 * Returns the set of URIs (as java.net.URI) that will be the named graphs of the query.
	 * Return null to specify no named graphs.
	 * 
	 * <p />If this returns null, the default behavior of the {@link SesameSPARQLExecuter} will be used.
	 * 
	 * @return a Set of java.net.URI specifying the named graphs
	 */
	public Set<URI> getNamedGraphs();
}
