package fr.sparna.rdf.sesame.jena.repository;

import java.util.Map;

import org.openjena.jenasesame.util.Convert;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.impl.AbstractQuery;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * A Generic Query on a Jena Model.
 * 
 * @author Thomas Francart
 *
 */
public class JenaQuery extends AbstractQuery implements Query {

	protected ValueFactory factory;
	protected Model model;
	protected String sparql;
	protected String baseURI;

	public JenaQuery(Model model, ValueFactory factory, String sparql, String baseURI) {
		super();
		this.model = model;
		this.factory = factory;
		this.sparql = sparql;
		this.baseURI = baseURI;
	}
	


}
