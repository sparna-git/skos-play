package fr.sparna.rdf.sesame.jena.repository;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.impl.AbstractQuery;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * A Tuple Query on a Jena Model.
 * 
 * @author Thomas Francart
 *
 */
public class JenaTupleQuery extends AbstractQuery implements TupleQuery {

	protected ValueFactory factory;
	protected Model model;
	protected String sparql;
	protected String baseURI;

	public JenaTupleQuery(Model model, ValueFactory factory, String sparql, String baseURI) {
		super();
		this.model = model;
		this.factory = factory;
		this.sparql = sparql;
		this.baseURI = baseURI;
	}

	/**
	 * Wraps the ResultSet returned by the Model in a {@link JenaTupleQueryResult}
	 */
	@Override
	public TupleQueryResult evaluate() throws QueryEvaluationException {
		Query query = QueryFactory.create(this.sparql, this.baseURI) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;
		return new JenaTupleQueryResult(results, qexec, factory);		
	}

	/**
	 * Iterates on the ResultSet returned by the Model and pass each QuerySolution
	 * wrapped in a {@link JenaBindingSet} to the handler.
	 */
	@Override
	public void evaluate(TupleQueryResultHandler handler)
	throws QueryEvaluationException, TupleQueryResultHandlerException {
		Query query = QueryFactory.create(this.sparql, this.baseURI) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		try {
			ResultSet results = qexec.execSelect() ;
			while ( results.hasNext() ) {
				QuerySolution soln = results.nextSolution() ;
				handler.handleSolution(new JenaBindingSet(soln, factory));
			}
		} finally {
			qexec.close() ;
		}
	}	

}
