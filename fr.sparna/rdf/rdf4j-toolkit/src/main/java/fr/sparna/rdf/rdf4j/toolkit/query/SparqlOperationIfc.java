package fr.sparna.rdf.rdf4j.toolkit.query;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.Dataset;

/**
 * Defines a SPARQL query (as a String), along with its bindings, default graphs, named graphs,
 * and includeInferred parameters (everything needed to execute the query).
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
	 * 
	 */
	public Dataset getDataset();

}
