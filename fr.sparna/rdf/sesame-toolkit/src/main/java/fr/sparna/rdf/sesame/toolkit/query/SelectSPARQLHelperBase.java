package fr.sparna.rdf.sesame.toolkit.query;

import java.util.List;
import java.util.Map;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

/**
 * Base implementation of a SelectSPARQLHelperIfc that implements both <code>TupleQueryResultHandler</code>
 * and {@link SPARQLQueryIfc}, and returns itself in the <code>getQuery()</code> and <code>getHandler()</code>
 * methods.
 * 
 * <p>This extends {@link SPARQLQuery} to implement all the query methods, and implements the methods of
 * <code>TupleQueryResultHandler</code> in a default implementation that does nothing. The only method to
 * implement for the subclasses is <code>handleSolution</code>.
 * 
 * <p>This allows to write subclasses this way :
 * <code><pre>
 * public class MyHelper extends SelectSPARQLHelperBase {
 * 	
 * 	public MyHelper(\/* your parameters here \/*) {
 *		// call any super constructor
 *		// super(SPARQLQueryBuilder);
 *		// super(String);
 *		// super(SPARQLQueryBuilder, HashMap<String, Value>);
 *		// super(String, HashMap<String, Value>);
 *	}
 *
 *	@Override
 *	public void handleSolution(BindingSet binding)
 *	throws TupleQueryResultHandlerException {
 *		// handle solution of the query
 *	}
 * }
 * </pre></code>
 * 
 * @author Thomas Francart
 *
 */
public abstract class SelectSPARQLHelperBase extends SPARQLQuery implements SelectSPARQLHelperIfc, TupleQueryResultHandler, SPARQLQueryIfc {
	
	public SelectSPARQLHelperBase(SPARQLQueryBuilderIfc builder, Map<String, Value> bindings) {
		super(builder, bindings);
	}

	public SelectSPARQLHelperBase(SPARQLQueryBuilderIfc builder) {
		super(builder);
	}

	public SelectSPARQLHelperBase(String sparql, Map<String, Value> bindings) {
		super(sparql, bindings);
	}

	public SelectSPARQLHelperBase(String sparql) {
		super(sparql);
	}

	@Override
	public SPARQLQueryIfc getQuery() {
		return this;
	}

	@Override
	public TupleQueryResultHandler getHandler() {
		return this;
	}
	
	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {
		// base implementation does nothing
	}

	@Override
	public void startQueryResult(List<String> arg0)
	throws TupleQueryResultHandlerException {
		// base implementation does nothing
	}

	@Override
	public abstract void handleSolution(BindingSet bindingSet)
	throws TupleQueryResultHandlerException;

}
