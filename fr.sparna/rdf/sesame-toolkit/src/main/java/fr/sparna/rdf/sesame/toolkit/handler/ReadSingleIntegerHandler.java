package fr.sparna.rdf.sesame.toolkit.handler;

import org.openrdf.model.Literal;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

/**
 * Reads a query result consisting of a single line with a single integer (typically a COUNT query).
 * 
 * @author Thomas Francart
 */
public class ReadSingleIntegerHandler extends ReadSingleValueHandler {
	
	@Override
	public void handleSolution(BindingSet bs)
	throws TupleQueryResultHandlerException {
		super.handleSolution(bs);
		
		if(this.result instanceof org.openrdf.model.Resource) {
			// oups, a Resource, we expect a literal
			throw new TupleQueryResultHandlerException(this.getClass().getSimpleName()+" can only read query results with a literal result.");
		}
	}

	public Integer getResultIntValue() {
		return (getResult() != null)?((Literal)getResult()).intValue():null;
	}

}
