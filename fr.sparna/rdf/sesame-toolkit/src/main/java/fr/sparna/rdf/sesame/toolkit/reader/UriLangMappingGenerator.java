package fr.sparna.rdf.sesame.toolkit.reader;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of KeyMappingGeneratorIfc that maps a given URI and a given language
 * to 2 variable names.
 * 
 * @author Thomas Francart
 *
 */
public class UriLangMappingGenerator implements KeyMappingGeneratorIfc<UriLang> {

	protected String uriVarName;
	protected String langVarName;

	/**
	 * 
	 * @param uriVarName	the name of the query variable to which URI values will be bounded
	 * @param langVarName	the name of the query variable to which lang values will be bounded
	 */
	public UriLangMappingGenerator(String uriVarName, String langVarName) {
		super();
		this.uriVarName = uriVarName;
		this.langVarName = langVarName;
	}

	/**
	 * Maps the given variable to the given URI
	 */
	@Override
	public Map<String, Object> generate(final UriLang uriLang) {
		return new HashMap<String, Object>() {{
			// avoid setting a null mapping
			if(uriLang != null)  {
				put(uriVarName, uriLang.getUri());
				put(langVarName, uriLang.getLang());
			}
		}};
	}
		
}
