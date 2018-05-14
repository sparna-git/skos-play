package fr.sparna.rdf.rdf4j.toolkit.query;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

/**
 * Associates the path of a query in a resource file, an optional Collection<Binding>, and this (abstract) RDFHandler.
 * Subclasses must implement <code>getResourcePath()</code> to provide the path to the resource to read the SPARQL query from,
 * and the methods of RDFHandler (typically <code>handleStatement</code>).
 * 
 * @author Thomas Francart
 *
 */
public abstract class ResourceGraphQueryHelper extends AbstractRDFHandler implements GraphQueryHelperIfc {

	private BindingSet bindingSet;
	
	public ResourceGraphQueryHelper() {
		super();
	}

	public ResourceGraphQueryHelper(BindingSet bindingSet) {
		super();
		this.bindingSet = bindingSet;
	}
	
	public abstract String getResourcePath();

	@Override
	public SparqlOperationIfc getOperation() {
		return new SimpleSparqlOperation(SimpleQueryReader.fromResource(this.getResourcePath()), this.bindingSet);
	}

	@Override
	public RDFHandler getHandler() {
		return this;
	}
	
	/**
	 * A helper method to set the bindings on this instance that returns this instance, that allows
	 * to create a new instance and pass it bindings in a single statement.
	 * 
	 * @param bindings	The bindings to set
	 * @return this
	 */
	public ResourceGraphQueryHelper withBindings(BindingSet bindingSet) {
		this.setBindingSet(bindingSet);
		return this;
	}

	public BindingSet getBindingSet() {
		return bindingSet;
	}

	public void setBindingSet(BindingSet bindingSet) {
		this.bindingSet = bindingSet;
	}
	
}
