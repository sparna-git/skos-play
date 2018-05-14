package fr.sparna.rdf.rdf4j.toolkit.query;

import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;

/**
 * 
 * @author Thomas Francart
 *
 */
public abstract class SelfTupleQueryHelper extends AbstractTupleQueryResultHandler implements TupleQueryHelperIfc {
	
	protected SparqlOperationIfc operation;

	public SelfTupleQueryHelper() {
		super();
	}

	public SelfTupleQueryHelper(SparqlOperationIfc operation) {
		super();
		this.operation = operation;
	}

	@Override
	public SparqlOperationIfc getOperation() {
		return operation;
	}

	@Override
	public TupleQueryResultHandler getHandler() {
		return this;
	}

		
}
