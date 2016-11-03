package fr.sparna.rdf.sesame.jena.repository;

import java.util.Map;

import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryEvaluationException;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * A GraphQueryResult wrapping a Jena StmtIterator. This is a subclass
 * of JenaStatementIterator, that already wraps an StmtIterator.
 * 
 * @author Thomas Francart
 *
 */
public class JenaGraphQueryResult extends JenaStatementIterator implements GraphQueryResult {

	protected Model model;
	protected QueryExecution qExec;
	
	
	public JenaGraphQueryResult(
			Model model,
			ValueFactory factory,
			QueryExecution qExec
	) {
		super(model.listStatements(), factory);
		this.model = model;
		this.qExec = qExec;
	}

	@Override
	public void close() throws QueryEvaluationException {
		this.iterator.close();
		this.qExec.close();
		this.model.close();
	}

	@Override
	public Map<String, String> getNamespaces() {
		return model.getNsPrefixMap();
	}
	
}
