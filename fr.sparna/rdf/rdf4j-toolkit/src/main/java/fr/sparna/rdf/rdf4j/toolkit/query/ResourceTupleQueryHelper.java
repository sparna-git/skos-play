package fr.sparna.rdf.rdf4j.toolkit.query;

import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;

/**
 * Associates the path of a query in a resource file, an optional Collection<Binding>, and this (abstract) TupleQueryResultHandler.
 * Subclasses must implement <code>getResourcePath()</code> to provide the path to the resource to read the SPARQL query from,
 * and the methods of TupleQueryResultHandler (typically <code>handleSolution</code>).
 * 
 * @author Thomas Francart
 *
 */
public abstract class ResourceTupleQueryHelper extends AbstractTupleQueryResultHandler implements TupleQueryHelperIfc {

	private BindingSet bindings;
	
	public ResourceTupleQueryHelper() {
		super();
	}

	public ResourceTupleQueryHelper(BindingSet bindings) {
		super();
		this.bindings = bindings;
	}
	
	public abstract String getResourcePath();

	@Override
	public SparqlOperationIfc getOperation() {
		return new SimpleSparqlOperation(SimpleQueryReader.fromResource(this.getResourcePath()), this.bindings);
	}

	@Override
	public TupleQueryResultHandler getHandler() {
		return this;
	}
	
	/**
	 * A helper method to set the bindings on this instance that returns this instance, that allows
	 * to create a new instance and pass it bindings in a single statement.
	 * 
	 * @param bindings	The bindings to set
	 * @return this
	 */
	public ResourceTupleQueryHelper withBindingSet(BindingSet bindings) {
		this.setBindingSet(bindings);
		return this;
	}

	public BindingSet getBindingSet() {
		return bindings;
	}

	public void setBindingSet(BindingSet bindings) {
		this.bindings = bindings;
	}
	
}
