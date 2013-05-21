package fr.sparna.rdf.sesame.toolkit.query;

import org.openrdf.query.TupleQueryResultHandler;

/**
 * An object that associates a SPARQL query (an instance of a {@link SPARQLQueryIfc}) and a
 * <code>TupleQueryResultHandler</code> capable of handling the results of the query. The {@link fr.sparna.rdf.sesame.toolkit.query.Perform}
 * takes this interface as a parameter of its <code>select</code> method.
 * 
 * @author Thomas Francart
 * 
 */
public interface SelectSPARQLHelperIfc {

	/**
	 * Returns a SPARQL query along with all its execution parameters
	 * 
	 * @return a {@link fr.sparna.rdf.sesame.toolkit.query.SPARQLQueryIfc SPARQLQueryIfc} containing the SPARQL String
	 * with all its parameters.
	 */
	public abstract SPARQLQueryIfc getQuery();

	/**
	 * Returns a handler capable of processing the results of the query returned by
	 * <code>getQuery()</code>.
	 * 
	 * @return a <code>TupleQueryResultHandler</code> that knows what to do with the results of the query
	 */
	public abstract TupleQueryResultHandler getHandler();

}