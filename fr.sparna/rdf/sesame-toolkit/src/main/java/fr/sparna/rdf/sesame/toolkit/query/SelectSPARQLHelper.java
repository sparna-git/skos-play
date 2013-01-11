package fr.sparna.rdf.sesame.toolkit.query;

import org.openrdf.query.TupleQueryResultHandler;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

public class SelectSPARQLHelper implements SelectSPARQLHelperIfc {

	protected SPARQLQueryIfc query;
	protected TupleQueryResultHandler handler;
	
	
	public SelectSPARQLHelper(
			SPARQLQueryIfc query,
			TupleQueryResultHandler handler
	) {
		super();
		this.query = query;
		this.handler = handler;
	}
	
	public SelectSPARQLHelper(
			SPARQLQueryBuilderIfc builder,
			TupleQueryResultHandler handler
	) {
		this(new SPARQLQuery(builder), handler);
	}

	public SelectSPARQLHelper(
			String sparql,
			TupleQueryResultHandler handler
	) {
		this(new SPARQLQuery(sparql), handler);
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperIfc#getQuery()
	 */
	@Override
	public SPARQLQueryIfc getQuery() {
		return query;
	}
	
	public void setQuery(SPARQLQueryIfc query) {
		this.query = query;
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperIfc#getHandler()
	 */
	@Override
	public TupleQueryResultHandler getHandler() {
		return handler;
	}
	
	public void setHandler(TupleQueryResultHandler handler) {
		this.handler = handler;
	}	
}
