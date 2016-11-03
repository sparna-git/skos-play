package fr.sparna.rdf.sesame.toolkit.query;

import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResultHandlerException;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

/**
 * Base implementation of a SelectSparqlHelperIfc that implements both <code>TupleQueryResultHandler</code>
 * and {@link SparqlQueryIfc}, and returns itself in the <code>getQuery()</code> and <code>getHandler()</code>
 * methods.
 * 
 * <p>This extends {@link SparqlQuery} to implement all the query methods, and implements the methods of
 * <code>TupleQueryResultHandler</code> in a default implementation that does nothing. The only method to
 * implement for the subclasses is <code>handleSolution</code>.
 * 
 * <p>This allows to write subclasses this way :
 * <code><pre>
 * public class MyHelper extends SelectSparqlHelperBase {
 * 	
 * 	public MyHelper(\/* your parameters here \/*) {
 *		// call any super constructor
 *		// super(SparqlQueryBuilder);
 *		// super(String);
 *		// super(SparqlQueryBuilder, HashMap<String, Value>);
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
public abstract class SelectSparqlHelperBase extends SparqlQuery implements SelectSparqlHelperIfc, TupleQueryResultHandler, SparqlQueryIfc {
	
	public SelectSparqlHelperBase(SparqlQueryBuilderIfc builder, Map<String, Object> bindings) {
		super(builder, bindings);
	}

	public SelectSparqlHelperBase(SparqlQueryBuilderIfc builder) {
		super(builder);
	}

	public SelectSparqlHelperBase(String sparql, Map<String, Object> bindings) {
		super(sparql, bindings);
	}

	public SelectSparqlHelperBase(String sparql) {
		super(sparql);
	}

	@Override
	public SparqlQueryIfc getQuery() {
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
	public void handleBoolean(boolean arg0) throws QueryResultHandlerException {
		// base implementation does nothing
	}

	@Override
	public void handleLinks(List<String> arg0)
	throws QueryResultHandlerException {
		// base implementation does nothing
	}

	@Override
	public abstract void handleSolution(BindingSet bindingSet)
	throws TupleQueryResultHandlerException;

}
