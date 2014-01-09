package fr.sparna.rdf.sesame.toolkit.iterator;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import fr.sparna.commons.lang.ClosableIterator;

public class TupleQueryResultResourceIterator implements ClosableIterator<Resource> {
	
	protected TupleQueryResult tqr;
	
	public TupleQueryResultResourceIterator(TupleQueryResult tqr) {
		super();
		this.tqr = tqr;
		
		// check only one variable in the result
		try {
			if(tqr.getBindingNames().size() > 1) {
				throw new IllegalArgumentException("TupleQueryResult has more than 1 variable (it has "+tqr.getBindingNames().size()+")");
			}
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		try {
			return tqr.hasNext();
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Resource next() {		
		try {
			Value v = tqr.next().getValue(tqr.getBindingNames().get(0));
			if(!(v instanceof Resource)) {
				throw new IllegalArgumentException("Value "+v+" is not a Resource.");
			}
			
			return (Resource)v;
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		try {
			this.tqr.remove();
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			this.tqr.close();
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}
	
}
