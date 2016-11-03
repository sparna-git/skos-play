package fr.sparna.rdf.sesame.toolkit.reader;

import org.eclipse.rdf4j.query.BindingSet;

/**
 * An abstraction for objects capable of reading a key and its associated value from a SPARQL
 * query result binding.
 * @author Thomas Francart
 *
 * @param <Key>
 * @param <Value>
 */
public interface KeyValueBindingSetReaderIfc<Key, Value> {

	/**
	 * Reads the key from the binding set
	 * @param binding
	 * @return
	 */
	public Key readKey(BindingSet binding);
	
	/**
	 * Reads the value from the binding set
	 * @param binding
	 * @return
	 */
	public Value readValue(BindingSet binding);
	
}
