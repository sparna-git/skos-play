package fr.sparna.rdf.sesame.toolkit.query;

import org.eclipse.rdf4j.rio.RDFHandler;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public class ConstructSparqlHelper implements ConstructSparqlHelperIfc {

	protected SparqlQueryIfc query;
	protected RDFHandler handler;
	
	public ConstructSparqlHelper(
			SparqlQueryIfc query,
			RDFHandler handler
	) {
		super();
		this.query = query;
		this.handler = handler;
	}
	
	public ConstructSparqlHelper(
			SparqlQueryBuilderIfc builder,
			RDFHandler handler
	) {
		this(new SparqlQuery(builder), handler);
	}
	
	public ConstructSparqlHelper(
			String sparql,
			RDFHandler handler
	) {
		this(new SparqlQuery(sparql), handler);
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.sesame.toolkit.query.ConstructSparqlHelperIfc#getQuery()
	 */
	@Override
	public SparqlQueryIfc getQuery() {
		return query;
	}
	
	public void setQuery(SparqlQueryIfc query) {
		this.query = query;
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.sesame.toolkit.query.ConstructSparqlHelperIfc#getHandler()
	 */
	@Override
	public RDFHandler getHandler() {
		return handler;
	}
	
	public void setHandler(RDFHandler handler) {
		this.handler = handler;
	}
}
