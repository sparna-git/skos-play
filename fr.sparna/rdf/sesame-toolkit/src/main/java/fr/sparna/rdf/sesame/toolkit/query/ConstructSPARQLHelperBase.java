package fr.sparna.rdf.sesame.toolkit.query;

import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

public abstract class ConstructSPARQLHelperBase extends SPARQLQuery implements ConstructSPARQLHelperIfc, RDFHandler, SPARQLQueryIfc {

	public ConstructSPARQLHelperBase(SPARQLQueryBuilderIfc builder, Map<String, Value> bindings) {
		super(builder, bindings);
	}

	public ConstructSPARQLHelperBase(SPARQLQueryBuilderIfc builder) {
		super(builder);
	}

	public ConstructSPARQLHelperBase(String sparql, Map<String, Value> bindings) {
		super(sparql, bindings);
	}

	public ConstructSPARQLHelperBase(String sparql) {
		super(sparql);
	}

	@Override
	public SPARQLQueryIfc getQuery() {
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
