package fr.sparna.rdf.rdf4j.toolkit.query;

import java.util.List;

import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

public class PingSparqlHelper extends AbstractTupleQueryResultHandler implements TupleQueryHelperIfc {

	public static final String PING_QUERY = "SELECT ?x WHERE { <http://this.is> <http://a.ping> ?x }";
	
	protected boolean pinged = false;

	@Override
	public SparqlOperationIfc getQuery() {
		return new SimpleSparqlOperation(PING_QUERY);
	}

	@Override
	public TupleQueryResultHandler getHandler() {
		return this;
	}

	@Override
	public void startQueryResult(List<String> arg0)
	throws TupleQueryResultHandlerException {
		pinged = true;
	}

	@Override
	public void handleSolution(BindingSet bindingSet)
	throws TupleQueryResultHandlerException {
		// nothing
	}

	public boolean isPinged() {
		return pinged;
	}

}
