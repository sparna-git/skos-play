package fr.sparna.rdf.sesame.toolkit.query;

import org.openrdf.rio.RDFHandler;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

public class ConstructSPARQLHelper implements ConstructSPARQLHelperIfc {

	protected SPARQLQueryIfc query;
	protected RDFHandler handler;
	
	public ConstructSPARQLHelper(
			SPARQLQueryIfc query,
			RDFHandler handler
	) {
		super();
		this.query = query;
		this.handler = handler;
	}
	
	public ConstructSPARQLHelper(
			SPARQLQueryBuilderIfc builder,
			RDFHandler handler
	) {
		this(new SPARQLQuery(builder), handler);
	}
	
	public ConstructSPARQLHelper(
			String sparql,
			RDFHandler handler
	) {
		this(new SPARQLQuery(sparql), handler);
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelperIfc#getQuery()
	 */
	@Override
	public SPARQLQueryIfc getQuery() {
		return query;
	}
	
	public void setQuery(SPARQLQueryIfc query) {
		this.query = query;
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelperIfc#getHandler()
	 */
	@Override
	public RDFHandler getHandler() {
		return handler;
	}
	
	public void setHandler(RDFHandler handler) {
		this.handler = handler;
	}
}
