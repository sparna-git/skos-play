package fr.sparna.rdf.rdf4j.toolkit.query;

import org.eclipse.rdf4j.query.TupleQueryResultHandler;

/**
 * An object that associates a SPARQL operation (query or pudate) and a
 * <code>TupleQueryResultHandler</code> capable of handling the results of the query. A {@link fr.sparna.rdf.rdf4j.toolkit.query.Perform}
 * takes this interface as a parameter of its <code>select</code> method.
 * 
 * @author Thomas Francart
 * 
 */
public interface TupleQueryHelperIfc {

	/**
	 * Returns a SPARQL query
	 * 
	 * @return The SPARQL String
	 */
	public abstract SparqlOperationIfc getQuery();

	/**
	 * Returns a handler capable of processing the results of the query returned by
	 * <code>getQuery()</code>.
	 * 
	 * @return a <code>TupleQueryResultHandler</code> that knows what to do with the results of the query
	 */
	public abstract TupleQueryResultHandler getHandler();

}