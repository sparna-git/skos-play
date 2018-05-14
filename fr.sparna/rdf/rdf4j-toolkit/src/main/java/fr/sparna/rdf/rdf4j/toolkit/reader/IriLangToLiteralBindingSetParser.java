package fr.sparna.rdf.rdf4j.toolkit.reader;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;

/**
 * An implementation of BindingSetParserIfc that reads a IriLang as the key and a Literal
 * as a Value. Typically used to read data properties in a given specific language.
 * 
 * @author Thomas Francart
 *
 */
public class IriLangToLiteralBindingSetParser implements BindingSetParserIfc<IriLang, Literal> {

	protected String uriVarName;
	protected String langVarName;
	protected String valueVarName;
	
	public IriLangToLiteralBindingSetParser(String uriVarName, String langVarName, String valueVarName) {
		super();
		this.uriVarName = uriVarName;
		this.langVarName = langVarName;
		this.valueVarName = valueVarName;
	}

	@Override
	public IriLang parseKey(BindingSet binding) {
		Value k = binding.getValue(this.uriVarName);
		Value l = binding.getValue(this.langVarName);
		if(!(k instanceof IRI)) {
			throw new IllegalArgumentException("Value "+k+" is not a IRI, check your SPARQL query");
		}
		if(!(l instanceof Literal)) {
			throw new IllegalArgumentException("Value "+l+" is not a Literal, check your SPARQL query");
		}
		return new IriLang((IRI)k, l.stringValue());
	}

	@Override
	public Literal parseValue(BindingSet binding) {
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
