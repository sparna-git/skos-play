package fr.sparna.rdf.rdf4j.toolkit.reader;


import java.util.function.Function;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.sparql.query.SPARQLQueryBindingSet;

/**
 * An implementation of KeyMappingGeneratorIfc that maps a given URI to a given variable name.
 * 
 * @author Thomas Francart
 *
 */
public class IriBindingSetGenerator implements Function<IRI, BindingSet> {

	protected String varName;

	/**
	 * 
	 * @param varName the name of the query variable to which values will be bounded
	 */
	public IriBindingSetGenerator(String varName) {
		super();
		this.varName = varName;
	}

	/**
	 * Maps the given variable to the given IRI
	 */
	@Override
	public BindingSet apply(final IRI key) {
		SPARQLQueryBindingSet bs = new SPARQLQueryBindingSet();
		if(key != null) bs.addBinding(varName, key);
		return bs;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}
	
}
