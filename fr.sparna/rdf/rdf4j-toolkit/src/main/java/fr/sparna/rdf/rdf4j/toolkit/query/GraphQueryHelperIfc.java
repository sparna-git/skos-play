package fr.sparna.rdf.rdf4j.toolkit.query;

import org.eclipse.rdf4j.rio.RDFHandler;

/**
 * An object that associates a SPARQL query (an instance of a {@link SparqlOperationIfc}) and a
 * <code>RDFHandler</code> capable of handling the results of the query. The {@link fr.sparna.rdf.rdf4j.toolkit.query.Perform}</code>
 * takes this interface as a parameter of its <code>graph</code> method.
 * 
 * @author Thomas Francart
 * 
 */
public interface GraphQueryHelperIfc {

	/**
	 * Returns a SPARQL query along with all its execution parameters
	 * 
	 * @return a SPARQL query with additionnal parameters
	 */
	public abstract SparqlOperationIfc getQuery();

	/**
	 * Returns a handler capable of processing the results of the query returned by
	 * <code>getQuery()</code>.
	 * 
	 * @return an RDFHandler that knows what to do with the results of the query
	 */
	public abstract RDFHandler getHandler();

}