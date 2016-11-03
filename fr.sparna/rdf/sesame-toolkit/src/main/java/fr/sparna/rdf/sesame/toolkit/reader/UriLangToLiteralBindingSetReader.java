package fr.sparna.rdf.sesame.toolkit.reader;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;

/**
 * An implementation of KeyValueBindingSetReaderIfc that reads a UriLang as the key and a Literal
 * as a Value. Typically used to read data properties in a given specific language.
 * 
 * @author Thomas Francart
 *
 */
public class UriLangToLiteralBindingSetReader implements KeyValueBindingSetReaderIfc<UriLang, Literal> {

	protected String uriVarName;
	protected String langVarName;
	protected String valueVarName;
	
	public UriLangToLiteralBindingSetReader(String uriVarName, String langVarName, String valueVarName) {
		super();
		this.uriVarName = uriVarName;
		this.langVarName = langVarName;
		this.valueVarName = valueVarName;
	}

	@Override
	public UriLang readKey(BindingSet binding) {
		Value k = binding.getValue(this.uriVarName);
		Value l = binding.getValue(this.langVarName);
		if(!(k instanceof URI)) {
			throw new IllegalArgumentException("Value "+k+" is not a URI, check your SPARQL query");
		}
		if(!(l instanceof Literal)) {
			throw new IllegalArgumentException("Value "+l+" is not a Literal, check your SPARQL query");
		}
		return new UriLang((URI)k, l.stringValue());
	}

	@Override
	public Literal readValue(BindingSet binding) {
		Value v = binding.getValue(this.valueVarName);
		if(!(v instanceof Literal)) {
			throw new IllegalArgumentException("Value "+v+" is not a Literal, check your SPARQL query");
		}
		return (Literal)v;
	}

	public String getValueVarName() {
		return valueVarName;
	}

	public void setValueVarName(String valueVarName) {
		this.valueVarName = valueVarName;
	}

	public String getUriVarName() {
		return uriVarName;
	}

	public void setUriVarName(String uriVarName) {
		this.uriVarName = uriVarName;
	}

	public String getLangVarName() {
		return langVarName;
	}

	public void setLangVarName(String langVarName) {
		this.langVarName = langVarName;
	}
	
}
