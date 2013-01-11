package fr.sparna.rdf.sesame.toolkit.handler;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;

public class ResourceListHandler extends TupleQueryResultHandlerBase implements TupleQueryResultHandler {

	private List<Resource> resources;

	@Override
	public void startQueryResult(List<String> bindingNames) throws TupleQueryResultHandlerException {
		this.resources = new ArrayList<Resource>();
	}

	@Override
	public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
		if(bindingSet.getBindingNames().size() > 1) {
			throw new TupleQueryResultHandlerException("Only binding sets with a single binding name are accepted");
		}
		
		Value v = bindingSet.getValue(bindingSet.getBindingNames().toArray(new String[]{})[0]);
		
		if(v instanceof Literal) {
			throw new TupleQueryResultHandlerException("Literal values are not accepted");
		} else if (v instanceof Resource) {
			this.resources.add((Resource)v);
		} else {
			throw new TupleQueryResultHandlerException("Unexpected value type : "+v.getClass().getName());
		}
	}

	public List<Resource> getResources() {
		return resources;
	}
	
}
