package fr.sparna.rdf.sesame.toolkit.handler;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 * Counts the number of statements returned.
 * Resets the statement count at the beginning of each result if the resetCount flag is activated 
 * 
 * @author Thomas Francart
 *
 */
public class CountStatementHandler implements RDFHandler {

	private int statementCount;
	private boolean resetCount = false;	
	
	public CountStatementHandler(boolean resetCount) {
		super();
		this.resetCount = resetCount;
	}
	
	public CountStatementHandler() {
		super();
	}

	@Override
	public void endRDF() throws RDFHandlerException {
	}

	@Override
	public void handleComment(String arg0) throws RDFHandlerException {
	}

	@Override
	public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
	}

	@Override
	public void handleStatement(Statement arg0) throws RDFHandlerException {
		++this.statementCount;
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		if(this.resetCount) {
			// resets statement count
			this.statementCount = 0;
		}
	}

	public int getStatementCount() {
		return statementCount;
	}
}
