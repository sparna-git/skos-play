package fr.sparna.rdf.rdf4j.toolkit.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerBase;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

/**
 * Reads a query result consisting of a single value (Resource or Literal) possibly with multiple lines in the result set
 * 
 * @author Thomas Francart
 */
public class ReadValueListHandler extends AbstractTupleQueryResultHandler implements TupleQueryResultHandler {

	protected List<Value> result = null;
	protected String bindingName = null;

	@Override
	public void startQueryResult(List<String> bindingNames)
	throws TupleQueryResultHandlerException {	
		// if more than 1 binding name, we have a problem
		if(bindingNames.size() > 1) {
			throw new TupleQueryResultHandlerException(this.getClass().getSimpleName()+" can only read query results with a single binding.");
		}
		
		// keep track of binding name
		this.bindingName = bindingNames.get(0);
		
		// re-init result
		this.result = new ArrayList<Value>();
	}
	
	@Override
	public void handleSolution(BindingSet bs)
	throws TupleQueryResultHandlerException {
		Value v = bs.getValue(this.bindingName);
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
