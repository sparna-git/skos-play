package fr.sparna.rdf.rdf4j.toolkit.reader;

import java.util.Iterator;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;

/**
 * An implementation of BindingSetReaderIfc that reads a IRI as the key and a Literal
 * as a Value. Typically used to read data properties.
 * 
 * @author Thomas Francart
 *
 */
public class IriToLiteralBindingSetParser implements BindingSetParserIfc<IRI, Literal> {

	protected String keyVarName;
	protected String valueVarName;
	
	public IriToLiteralBindingSetParser(String keyVarName, String valueVarName) {
		super();
		this.keyVarName = keyVarName;
		this.valueVarName = valueVarName;
	}
	
	/**
	 * Magic constructor with only the key variable name; the value variable name will be determined automatically by
	 * Examining the binding set variables. The query must have only 2 variables in the result set.
	 * 
	 * @param keyVarName
	 */
	public IriToLiteralBindingSetParser(String keyVarName) {
		this(keyVarName, null);
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
	public Literal parseValue(BindingSet binding) {
		
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
