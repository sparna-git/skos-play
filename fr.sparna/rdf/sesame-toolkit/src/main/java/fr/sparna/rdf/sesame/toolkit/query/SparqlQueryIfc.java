package fr.sparna.rdf.sesame.toolkit.query;

import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * Defines a SPARQL query (as a String), along with its bindings, default graphs, named graphs,
 * and includeInferred parameters (everything needed to execute the query).
 *  
 * @author Thomas Francart
 *
 */
public interface SparqlQueryIfc {
	
	/**
	 * Returns a SPARQL query to be executed by a {@link Perform}
	 * 
	 * @return The SPARQL query
	 */
	public String getSPARQL();
	
	/**
	 * Returns the bindings to be set on the returned SPARQL query. For exemple, if the method
	 * getSPARQL() returns the query "CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}", one could bind the "s" variables
	 * by returning "new HashMap(){{put("s","http://www.exemple.com/ontology#123456");}};"
	 * 
	 * <p>Bindings values are interpreted as follow :
	 * <ul>
	 *   <li>If the value of the binding is an instance of org.eclipse.rdf4j.model.Value (either a Literal, a URI or a BNode)
	 *   then it is passed as is to the query binding</li>
	 *   <li>If the value of the binding is an instance of java.net.URI or java.net.URL, an org.eclipse.rdf4j.model.URI will be
	 *   constructed from it and passed to the query binding</li>
	 *   <li>In every other cases the toString() method will be called on the binding value and an org.eclipse.rdf4j.model.Literal
	 *   instance will be constructed from it and passed to the query binding.</li>
	 * </ul>
	 * 
	 * @return the bindings that will be set on the SPARQL query
	 */
	public Map<String, Object> getBindings();
	
	/**
	 * Tells if the sparql query should be executed by including the inferred statements or not.
	 * <p />If this returns null, the default behavior of the {@link Perform} will be used.
	 * 
	 * @return true if this query will use inferred statements
	 */
	public Boolean isIncludeInferred();
	
	/**
	 * Returns the set of URIs (as java.net.URI) that will constitute the default graph in which the query
	 * will be executed. Return null to specify no default graph.
	 * <p />If this returns null, the default behavior of the {@link Perform} will be used.
	 * 
	 * @return a Set of java.net.URI specifying the default graph
	 */
	public Set<URI> getDefaultGraphs();
	
	/**
	 * Returns the set of URIs (as java.net.URI) that will be the named graphs of the query.
	 * Return null to specify no named graphs.
	 * 
	 * <p />If this returns null, the default behavior of the {@link Perform} will be used.
	 * 
	 * @return a Set of java.net.URI specifying the named graphs
	 */
	public Set<URI> getNamedGraphs();
}
