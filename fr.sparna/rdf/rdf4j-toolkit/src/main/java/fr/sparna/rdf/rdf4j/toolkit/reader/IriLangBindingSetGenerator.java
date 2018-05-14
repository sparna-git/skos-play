package fr.sparna.rdf.rdf4j.toolkit.reader;

import java.util.function.Function;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.repository.sparql.query.SPARQLQueryBindingSet;

/**
 * An implementation of KeyMappingGeneratorIfc that maps a given URI and a given language
 * to 2 variable names.
 * 
 * @author Thomas Francart
 *
 */
public class IriLangBindingSetGenerator implements Function<IriLang, BindingSet> {

	protected String uriVarName;
	protected String langVarName;

	/**
	 * 
	 * @param uriVarName	the name of the query variable to which URI values will be bounded
	 * @param langVarName	the name of the query variable to which lang values will be bounded
	 */
	public IriLangBindingSetGenerator(String uriVarName, String langVarName) {
		super();
		this.uriVarName = uriVarName;
		this.langVarName = langVarName;
	}

	/**
	 * Maps the given variable to the given URI
	 */
	@Override
	public BindingSet apply(final IriLang iriLang) {
		SPARQLQueryBindingSet bs = new SPARQLQueryBindingSet();
		if(iriLang != null) {
			bs.addBinding(uriVarName, iriLang.getIri());
			bs.addBinding(langVarName, SimpleValueFactory.getInstance().createLiteral(iriLang.getLang()));
		}
		return bs;
	}
		
}
