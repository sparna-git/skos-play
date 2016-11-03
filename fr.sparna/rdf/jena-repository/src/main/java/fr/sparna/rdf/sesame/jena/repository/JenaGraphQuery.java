package fr.sparna.rdf.sesame.jena.repository;

import java.util.Map;

import org.openjena.jenasesame.util.Convert;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.impl.AbstractQuery;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * A Graph Query on a Jena Model.
 * 
 * @author Thomas Francart
 *
 */
public class JenaGraphQuery extends AbstractQuery implements GraphQuery {

	protected ValueFactory factory;
	protected Model model;
	protected String sparql;
	protected String baseURI;

	public JenaGraphQuery(Model model, ValueFactory factory, String sparql, String baseURI) {
		super();
		this.model = model;
		this.factory = factory;
		this.sparql = sparql;
		this.baseURI = baseURI;
	}
	
	/**
	 * Wraps the Model returned by the execution of the query in a {@link JenaGraphQueryResult}
	 * TODO : handle DESCRIBE by calling qexec.execDescribe()
	 */
	@Override
	public GraphQueryResult evaluate() throws QueryEvaluationException {
		Query query = QueryFactory.create(this.sparql, this.baseURI) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		Model result = qexec.execConstruct();
		return new JenaGraphQueryResult(result, factory, qexec);
	}

	/**
	 * TODO : handle DESCRIBE by calling qexec.execDescribe()
	 */
	@Override
	public void evaluate(RDFHandler handler) 
	throws QueryEvaluationException, RDFHandlerException {
		Query query = QueryFactory.create(this.sparql, this.baseURI) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		try {
			Model result = qexec.execConstruct() ;
			// notify of start
			handler.startRDF();
			// notify namespaces
			for (Map.Entry<String, String> anEntry : result.getNsPrefixMap().entrySet()) {
				handler.handleNamespace(anEntry.getKey(), anEntry.getValue());
			}
			
			// TODO : handler.handleComments ?
			
			// notify statements
			StmtIterator it = result.listStatements();
			while(it.hasNext()) {
				Statement jenaStmt = it.next();
				handler.handleStatement(Convert.statementToSesameStatement(factory, jenaStmt));
			}
			
		} finally {
			qexec.close() ;
			
			// notify of stop
			handler.endRDF();
		}
	}

}
