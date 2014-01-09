package fr.sparna.rdf.sesame.toolkit.query;

import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public abstract class ConstructSparqlHelperBase extends SparqlQuery implements ConstructSparqlHelperIfc, RDFHandler, SparqlQueryIfc {

	public ConstructSparqlHelperBase(SparqlQueryBuilderIfc builder, Map<String, Object> bindings) {
		super(builder, bindings);
	}

	public ConstructSparqlHelperBase(SparqlQueryBuilderIfc builder) {
		super(builder);
	}

	public ConstructSparqlHelperBase(String sparql, Map<String, Object> bindings) {
		super(sparql, bindings);
	}

	public ConstructSparqlHelperBase(String sparql) {
		super(sparql);
	}

	@Override
	public SparqlQueryIfc getQuery() {
		return this;
	}

	@Override
	public RDFHandler getHandler() {
		return this;
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		// base implements does nothing
	}

	@Override
	public void handleComment(String comment) throws RDFHandlerException {
		// base implements does nothing
	}

	@Override
	public void handleNamespace(String key, String value) throws RDFHandlerException {
		// base implements does nothing
	}

	@Override
	public abstract void handleStatement(Statement s) throws RDFHandlerException;

	@Override
	public void startRDF() throws RDFHandlerException {
		// base implements does nothing
	}

	
	
}
