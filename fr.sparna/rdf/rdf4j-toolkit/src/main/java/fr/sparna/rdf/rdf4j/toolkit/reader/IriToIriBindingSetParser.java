package fr.sparna.rdf.rdf4j.toolkit.reader;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;

/**
 * An implementation of BindingSetReaderIfc that reads a IRI as the key and a IRI
 * as a Value. Typically used to read object properties.
 * 
 * @author Thomas Francart
 *
 */
public class IriToIriBindingSetParser implements BindingSetParserIfc<IRI, IRI> {

	protected String keyVarName;
	protected String valueVarName;
	
	public IriToIriBindingSetParser(String keyVarName, String valueVarName) {
		super();
		this.keyVarName = keyVarName;
		this.valueVarName = valueVarName;
	}

	@Override
	public IRI parseKey(BindingSet binding) {
		Value v = binding.getValue(this.keyVarName);
		if(!(v instanceof IRI)) {
			throw new IllegalArgumentException("Value "+v+" is not a URI, check your SPARQL query");
		}
		return (IRI)v;
	}

	@Override
	public IRI parseValue(BindingSet binding) {
		Value v = binding.getValue(this.valueVarName);
		if(!(v instanceof IRI)) {
			throw new IllegalArgumentException("Value "+v+" is not a URI, check your SPARQL query");
		}
		return (IRI)v;
	}

	public String getKeyVarName() {
		return keyVarName;
	}

	public void setKeyVarName(String keyVarName) {
		this.keyVarName = keyVarName;
	}

	public String getValueVarName() {
		return valueVarName;
	}

	public void setValueVarName(String valueVarName) {
		this.valueVarName = valueVarName;
	}
	
}
