package fr.sparna.rdf.sesame.toolkit.reader;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.model.URI;

/**
 * An implementation of KeyMappingGeneratorIfc that maps a given URI to a given variable name.
 * 
 * @author Thomas Francart
 *
 */
public class UriKeyMappingGenerator implements KeyMappingGeneratorIfc<URI> {

	protected String varName;

	/**
	 * 
	 * @param varName the name of the query variable to which values will be bounded
	 */
	public UriKeyMappingGenerator(String varName) {
		super();
		this.varName = varName;
	}

	/**
	 * Maps the given variable to the given URI
	 */
	@Override
	public Map<String, Object> generate(final URI key) {
		return new HashMap<String, Object>() {{
			// avoid setting a null mapping
			if(key != null) put(varName, key);
		}};
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}
	
}
