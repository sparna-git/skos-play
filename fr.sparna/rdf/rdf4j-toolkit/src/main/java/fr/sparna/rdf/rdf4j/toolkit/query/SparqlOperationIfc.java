package fr.sparna.rdf.rdf4j.toolkit.query;

import java.util.Collection;

import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.Dataset;

/**
 * Encapsulates a SPARQL operation (query or update) as a String, along with its bindings, the include inferred flag,
 * and the dataset definition on which to execute the query.
 *  
 * @author Thomas Francart
 *
 */
public interface SparqlOperationIfc {
	
	/**
	 * Returns a SPARQL query to be executed by a {@link Perform}
	 * 
	 * @return The SPARQL query
	 */
	public String getSPARQL();
	
	/**
	 * Returns the bindings to be set on the returned SPARQL query.
	 * 
	 * @return the bindings that will be set on the SPARQL query
	 */
	public Collection<Binding> getBindings();
	
	/**
	 * Tells if the sparql query should be executed by including the inferred statements or not.
	 * <p />If this returns null, the default behavior of the {@link Perform} will be used.
	 * 
	 * @return true if this query will use inferred statements
	 */
	public Boolean isIncludeInferred();
	
	/**
	 * The Dataset on which to execute the query (defines the default graph, default insert graph, and named graphs)
	 */
	public Dataset getDataset();

}
