package fr.sparna.rdf.sesame.toolkit.query;

import org.openrdf.query.resultio.BooleanQueryResultWriter;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

public class BooleanSPARQLHelper implements BooleanSPARQLHelperIfc {

	protected SPARQLQueryIfc query;
	protected BooleanQueryResultWriter writer;
	
	
	public BooleanSPARQLHelper(
			SPARQLQueryIfc query,
			BooleanQueryResultWriter writer
	) {
		super();
		this.query = query;
		this.writer = writer;
	}
	
	public BooleanSPARQLHelper(
			SPARQLQueryBuilderIfc builder,
			BooleanQueryResultWriter writer
	) {
		this(new SPARQLQuery(builder), writer);
	}

	public BooleanSPARQLHelper(
			String sparql,
			BooleanQueryResultWriter writer
	) {
		this(new SPARQLQuery(sparql), writer);
	}

	@Override
	public SPARQLQueryIfc getQuery() {
		return query;
	}
	
	public void setQuery(SPARQLQueryIfc query) {
		this.query = query;
	}
	
	@Override
	public BooleanQueryResultWriter getWriter() {
		return writer;
	}
	
	public void setWriter(BooleanQueryResultWriter writer) {
		this.writer = writer;
	}	
}
