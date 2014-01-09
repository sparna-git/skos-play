package fr.sparna.rdf.sesame.toolkit.query;

import org.openrdf.query.resultio.BooleanQueryResultWriter;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public class BooleanSparqlHelper implements BooleanSparqlHelperIfc {

	protected SparqlQueryIfc query;
	protected BooleanQueryResultWriter writer;
	
	
	public BooleanSparqlHelper(
			SparqlQueryIfc query,
			BooleanQueryResultWriter writer
	) {
		super();
		this.query = query;
		this.writer = writer;
	}
	
	public BooleanSparqlHelper(
			SparqlQueryBuilderIfc builder,
			BooleanQueryResultWriter writer
	) {
		this(new SparqlQuery(builder), writer);
	}

	public BooleanSparqlHelper(
			String sparql,
			BooleanQueryResultWriter writer
	) {
		this(new SparqlQuery(sparql), writer);
	}

	@Override
	public SparqlQueryIfc getQuery() {
		return query;
	}
	
	public void setQuery(SparqlQueryIfc query) {
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
