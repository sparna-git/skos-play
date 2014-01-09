package fr.sparna.rdf.sesame.toolkit.reader;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

/**
 * An implementation of KeyValueBindingSetReaderIfc that reads a URI as the key and a Literal
 * as a Value. Typically used to read data properties.
 * 
 * @author Thomas Francart
 *
 */
public class UriToLiteralBindingSetReader implements KeyValueBindingSetReaderIfc<URI, Literal> {

	protected String keyVarName;
	protected String valueVarName;
	
	public UriToLiteralBindingSetReader(String keyVarName, String valueVarName) {
		super();
		this.keyVarName = keyVarName;
		this.valueVarName = valueVarName;
	}

	@Override
	public URI readKey(BindingSet binding) {
		Value v = binding.getValue(this.keyVarName);
		if(!(v instanceof URI)) {
			throw new IllegalArgumentException("Value "+v+" is not a URI, check your SPARQL query");
		}
		return (URI)v;
	}

	@Override
	public Literal readValue(BindingSet binding) {
		Value v = binding.getValue(this.valueVarName);
		if(!(v instanceof Literal)) {
			throw new IllegalArgumentException("Value "+v+" is not a Literal, check your SPARQL query");
		}
		return (Literal)v;
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
