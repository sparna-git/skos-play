package fr.sparna.rdf.sesame.toolkit.query;

import org.openrdf.query.TupleQueryResultHandler;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public class SelectSparqlHelper implements SelectSparqlHelperIfc {

	protected SparqlQueryIfc query;
	protected TupleQueryResultHandler handler;
	
	
	public SelectSparqlHelper(
			SparqlQueryIfc query,
			TupleQueryResultHandler handler
	) {
		super();
		this.query = query;
		this.handler = handler;
	}
	
	public SelectSparqlHelper(
			SparqlQueryBuilderIfc builder,
			TupleQueryResultHandler handler
	) {
		this(new SparqlQuery(builder), handler);
	}

	public SelectSparqlHelper(
			String sparql,
			TupleQueryResultHandler handler
	) {
		this(new SparqlQuery(sparql), handler);
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperIfc#getQuery()
	 */
	@Override
	public SparqlQueryIfc getQuery() {
		return query;
	}
	
	public void setQuery(SparqlQueryIfc query) {
		this.query = query;
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperIfc#getHandler()
	 */
	@Override
	public TupleQueryResultHandler getHandler() {
		return handler;
	}
	
	public void setHandler(TupleQueryResultHandler handler) {
		this.handler = handler;
	}	
}
