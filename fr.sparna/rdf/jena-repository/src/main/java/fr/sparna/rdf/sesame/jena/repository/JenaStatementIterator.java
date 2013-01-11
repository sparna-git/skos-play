package fr.sparna.rdf.sesame.jena.repository;

import info.aduna.iteration.CloseableIteration;

import java.util.Iterator;

import org.openjena.jenasesame.util.Convert;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryException;

import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * An Iterator<org.openrdf.model.Statement> wrapping a Jena StmtIterator
 * and doing the convertion between Jena Statement and Sesame Statement
 * at each iteration.
 * This is used to build a CloseableIteration<org.openrdf.model.Statement, RepositoryException>
 * easily from a Jena StmtIterator
 * 
 * @author Thomas Francart
 *
 */
public class JenaStatementIterator implements Iterator<Statement> {

	protected StmtIterator iterator;
	protected ValueFactory factory;
	
	public JenaStatementIterator(StmtIterator iterator, ValueFactory factory) {
		super();
		this.iterator = iterator;
		this.factory = factory;
	}

	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public Statement next() {
		com.hp.hpl.jena.rdf.model.Statement jenaStatement = this.iterator.next();
		return Convert.statementToSesameStatement(factory, jenaStatement);
	}

	@Override
	public void remove() {
		this.iterator.remove();
	}
	
	
}
