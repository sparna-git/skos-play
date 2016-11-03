package fr.sparna.rdf.sesame.toolkit.reader;

import java.util.Iterator;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;

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
	
	public UriToLiteralBindingSetReader(String keyVarName) {
		this(keyVarName, null);
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
		
		if(this.valueVarName == null) {
			// determnie value varName automagically
			for (Iterator<String> i = binding.getBindingNames().iterator(); i.hasNext();) {
				String aName = i.next();
				if(!aName.equals(this.keyVarName)) {
					if(valueVarName != null) {
						throw new IllegalArgumentException("SPARQL query result has more than 2 binding variables, "+this.keyVarName+", "+valueVarName+", "+aName+" (maybe others)");
					}
					valueVarName = aName;
				}
			}
			
			if(valueVarName == null) {
				throw new IllegalArgumentException("Cannot find value variable name when examining query binding names. Make sure the query returns 2 variables, "+this.keyVarName+" and another one.");
			}
		}
		
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
