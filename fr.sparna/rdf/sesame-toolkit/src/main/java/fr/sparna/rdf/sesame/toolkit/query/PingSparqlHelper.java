package fr.sparna.rdf.sesame.toolkit.query;

import java.util.List;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

public class PingSparqlHelper extends SelectSparqlHelperBase implements SelectSparqlHelperIfc {

	public static final String PING_QUERY = "SELECT ?x WHERE { <http://this.is> <http://a.ping> ?x }";
	
	protected boolean pinged = false;

	public PingSparqlHelper() {
		super(PING_QUERY);
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
