package fr.sparna.rdf.sesame.toolkit.query;

import org.openrdf.rio.RDFHandler;

/**
 * An object that associates a SPARQL query (an instance of a {@link SPARQLQueryIfc}) and a
 * <code>RDFHandler</code> capable of handling the results of the query. The {@link fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter}</code>
 * takes this interface as a parameter of its <code>executeConstruct</code> method.
 * 
 * @author Thomas Francart
 * 
 */
public interface ConstructSPARQLHelperIfc {

	/**
	 * Returns a SPARQL query along with all its execution parameters
	 * 
	 * @return a SPARQL query with additionnal parameters
	 */
	public abstract SPARQLQueryIfc getQuery();

	/**
	 * Returns a handler capable of processing the results of the query returned by
	 * <code>getQuery()</code>.
	 * 
	 * @return an RDFHandler that knows what to do with the results of the query
	 */
	public abstract RDFHandler getHandler();

}