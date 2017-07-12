package fr.sparna.rdf.rdf4j.toolkit.query;

import java.util.Collection;

import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;

/**
 * Associates the path of a query in a resource file, an optional Collection<Binding>, and this (abstract) TupleQueryResultHandler.
 * Subclasses must implement <code>getResourcePath()</code> to provide the path to the resource to read the SPARQL query from,
 * and the methodes of TupleQueryResultHandler (typically <code>handleSolution</code>).
 * 
 * @author Thomas Francart
 *
 */
public abstract class ResourceQueryAndResultHandler extends AbstractTupleQueryResultHandler implements TupleQueryHelperIfc {

	private Collection<Binding> bindings;
	
	public ResourceQueryAndResultHandler() {
		super();
	}

	public ResourceQueryAndResultHandler(Collection<Binding> bindings) {
		super();
		this.bindings = bindings;
	}
	
	public abstract String getResourcePath();

	@Override
	public SparqlOperationIfc getQuery() {
		return new SimpleSparqlOperation(SimpleQueryReader.fromResource(this.getResourcePath()), this.bindings);
		// as an attempt...
		// return new SimpleSparqlOperation(new SimpleQueryReader(this), this.bindings);
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
	public ResourceQueryAndResultHandler withBindings(Collection<Binding> bindings) {
		this.setBindings(bindings);
		return this;
	}

	public Collection<Binding> getBindings() {
		return bindings;
	}

	public void setBindings(Collection<Binding> bindings) {
		this.bindings = bindings;
	}
	
}
