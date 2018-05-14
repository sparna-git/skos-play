package fr.sparna.rdf.rdf4j.toolkit.query;

import org.eclipse.rdf4j.query.TupleQueryResultHandler;

/**
 * An object that associates a SELECT SPARQL operation and a
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
	 * @return The SPARQL Operation
	 */
	public abstract SparqlOperationIfc getOperation();

	/**
	 * Returns a handler capable of processing the results of the query returned by
	 * <code>getQuery()</code>.
	 * 
	 * @return a <code>TupleQueryResultHandler</code> that knows what to do with the results of the query
	 */
	public abstract TupleQueryResultHandler getHandler();

}