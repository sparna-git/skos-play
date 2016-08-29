package fr.sparna.rdf;

import java.util.Collection;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;



/**
 * Wraps a connection to handle transaction. This transaction does a commit every
 * [transactionSize] inserts. This saves _a lot_ of time compared to a connection
 * in antoCommit.
 * 
 * <p>Usage :
 * <code>
 * Repository r = ...;
 * RepositoryTransaction transaction = new RepositoryTransaction(r);
 * transaction.add(...);
 * transaction.add(...);
 * transaction.add(...);
 * transaction.remove(...);
 * transaction.commit();
 * </code>
 * 
 * @author Thomas Francart
 *
 */
public class RepositoryTransaction {

	private int transactionSize = 500;
	private RepositoryConnection connection;	
	
	private int currentTransactionCount = 0;
	
	
	public RepositoryTransaction(RepositoryConnection connection) throws RepositoryException {
		this.connection = connection;
		this.connection.setAutoCommit(false);
	}

	public void add(Collection<Statement> sts, Resource... r) throws RepositoryException {
		for (Statement statement : sts) {
			this.add(statement, r);
		}
		this.commit();
	}
	
	public void remove(Collection<Statement> sts) throws RepositoryException {
		for (Statement statement : sts) {
			this.remove(statement);
		}
		this.commit();
	}	
	
	public void add(Statement s, Resource... r) throws RepositoryException {
		connection.add(s,r);
		commitIfNecessary();
	}
	
	public void remove(Statement s) throws RepositoryException {
		connection.remove(s);
		commitIfNecessary();
	}
	
	private void commitIfNecessary() throws RepositoryException {
		this.currentTransactionCount++;
		if(currentTransactionCount == transactionSize) {
			this.commit();
		}		
	}
	
	public void commit() throws RepositoryException {
		this.connection.commit();
		this.currentTransactionCount = 0;		
	}

	public int getTransactionSize() {
		return transactionSize;
	}

	public void setTransactionSize(int transactionSize) {
		this.transactionSize = transactionSize;
	}

	public void closeQuietly() {
		this.connection.close();
	}
	
}
