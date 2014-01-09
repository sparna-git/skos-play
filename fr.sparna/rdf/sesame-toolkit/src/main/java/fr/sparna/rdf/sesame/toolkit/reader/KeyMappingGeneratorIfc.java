package fr.sparna.rdf.sesame.toolkit.reader;

import java.util.Map;

/**
 * An abstraction for objects capable of generating a SPARQL query mapping from
 * a search key.
 * 
 * @author Thomas Francart
 *
 * @param <Key> the key from which to generate a mapping
 */
public interface KeyMappingGeneratorIfc<Key> {

	public Map<String, Object> generate(Key key);
	
}
