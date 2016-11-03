package fr.sparna.rdf.sesame.jena.repository;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.impl.AbstractQuery;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * A Boolean Query on a Jena Model.
 * 
 * @author Thomas Francart
 *
 */
public class JenaBooleanQuery extends AbstractQuery implements BooleanQuery {

	protected Model model;
	protected String sparql;
	protected String baseURI;

	public JenaBooleanQuery(Model model, String sparql, String baseURI) {
		super();
		this.model = model;
		this.sparql = sparql;
		this.baseURI = baseURI;
	}

	@Override
	public boolean evaluate() throws QueryEvaluationException {
		Query query = QueryFactory.create(this.sparql, this.baseURI) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		try {
			return qexec.execAsk();
		} finally { 
			qexec.close() ;
		}
	}

}
