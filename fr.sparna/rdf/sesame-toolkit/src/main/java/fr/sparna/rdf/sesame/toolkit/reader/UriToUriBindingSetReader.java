package fr.sparna.rdf.sesame.toolkit.reader;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

/**
 * An implementation of KeyValueBindingSetReaderIfc that reads a URI as the key and a URI
 * as a Value. Typically used to read object properties.
 * 
 * @author Thomas Francart
 *
 */
public class UriToUriBindingSetReader implements KeyValueBindingSetReaderIfc<URI, URI> {

	protected String keyVarName;
	protected String valueVarName;
	
	public UriToUriBindingSetReader(String keyVarName, String valueVarName) {
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
	public URI readValue(BindingSet binding) {
		Value v = binding.getValue(this.valueVarName);
		if(!(v instanceof URI)) {
			throw new IllegalArgumentException("Value "+v+" is not a URI, check your SPARQL query");
		}
		return (URI)v;
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
