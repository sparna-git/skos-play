package fr.sparna.rdf.sesame.toolkit.handler;

import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;

/**
 * Reads a query result consisting of a single line with a single integer (typically a COUNT query).
 * 
 * @author Thomas Francart
 */
public class ReadSingleIntegerHandler extends TupleQueryResultHandlerBase implements TupleQueryResultHandler {

	private Integer result = null;

	@Override
	public void startQueryResult(List<String> bindingNames)
	throws TupleQueryResultHandlerException {	
		// if more than 1 binding name, we have a problem
		if(bindingNames.size() > 1) {
			throw new TupleQueryResultHandlerException(this.getClass().getSimpleName()+" can only read query results with a single binding.");
		}
		
		// re-init result
		this.result = null;
	}
	
	@Override
	public void handleSolution(BindingSet bs)
	throws TupleQueryResultHandlerException {
		if(this.result != null) {
			// oups, 2 result lines, this is unexpected
			throw new TupleQueryResultHandlerException(this.getClass().getSimpleName()+" can only read query results with a single result.");
		}
		Object o = bs.getValue(bs.getBindingNames().iterator().next());
		
		if(o instanceof org.openrdf.model.Resource) {
			// oups, a Value, we expect a literal
			throw new TupleQueryResultHandlerException(this.getClass().getSimpleName()+" can only read query results with a literal result.");
		} else {
			this.result = ((Literal)o).intValue();
		}
	}

	public Integer getResult() {
		return result;
	}

}
