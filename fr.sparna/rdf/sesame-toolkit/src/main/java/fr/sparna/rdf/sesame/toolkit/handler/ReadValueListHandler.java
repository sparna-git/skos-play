package fr.sparna.rdf.sesame.toolkit.handler;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;

/**
 * Reads a query result consisting of a single value (Resource or Literal) possibly with multiple lines in the result set
 * 
 * @author Thomas Francart
 */
public class ReadValueListHandler extends TupleQueryResultHandlerBase implements TupleQueryResultHandler {

	protected List<Value> result = null;

	@Override
	public void startQueryResult(List<String> bindingNames)
	throws TupleQueryResultHandlerException {	
		// if more than 1 binding name, we have a problem
		if(bindingNames.size() > 1) {
			throw new TupleQueryResultHandlerException(this.getClass().getSimpleName()+" can only read query results with a single binding.");
		}
		
		// re-init result
		this.result = new ArrayList<Value>();
	}
	
	@Override
	public void handleSolution(BindingSet bs)
	throws TupleQueryResultHandlerException {
		Value v = bs.getValue(bs.getBindingNames().iterator().next());
		this.result.add(v);
	}

	/**
	 * Returns the result list from this query
	 * 
	 * @return
	 */
	public List<Value> getResult() {
		return result;
	}

}
