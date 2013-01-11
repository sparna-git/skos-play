package fr.sparna.rdf.sesame.jena.repository;

import java.util.List;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * A TupleQueryResult wrapping a Jena ResultSet
 * 
 * @author Thomas Francart
 *
 */
public class JenaTupleQueryResult implements TupleQueryResult {

	protected ResultSet rs;
	protected QueryExecution qexec;
	protected ValueFactory factory;

	public JenaTupleQueryResult(ResultSet rs, QueryExecution qexec, ValueFactory factory) {
		super();
		this.rs = rs;
		this.qexec = qexec;
		this.factory = factory;
	}

	@Override
	public void close() throws QueryEvaluationException {
		qexec.close();
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		return rs.hasNext();
	}

	@Override
	public BindingSet next() throws QueryEvaluationException {
		return new JenaBindingSet(rs.next(), factory);
	}

	@Override
	public void remove() throws QueryEvaluationException {
		rs.remove();
	}

	@Override
	public List<String> getBindingNames() {
		return rs.getResultVars();
	}
	
	
}
