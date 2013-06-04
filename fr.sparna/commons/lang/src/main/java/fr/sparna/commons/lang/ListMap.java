package fr.sparna.commons.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMap<X, Y> extends HashMap<X, List<Y>> implements Map<X, List<Y>> {

	/**
	 * Adds a value for a given key.
	 * 
	 * @param key
	 * @param value
	 */
	public void add(X key, Y value) {
		if(this.containsKey(key)) {
			this.get(key).add(value);
		} else {
			this.put(key, new ArrayList<Y>(Collections.singletonList(value)));
		}
	}
	
	/**
	 * Adds a list of values for a given key.
	 * 
	 * @param key		
	 * @param values	the values to add
	 */
	public void addAll(X key, List<Y> values) {
		if(this.containsKey(key)) {
			this.get(key).addAll(values);
		} else {
			this.put(key, new ArrayList<Y>(values));
		}
	}
	
	/**
	 * Clears the list associated with the given key
	 * @param key
	 */
	public void clear(X key) {
		this.put(key, new ArrayList<Y>());
	}
	
}
