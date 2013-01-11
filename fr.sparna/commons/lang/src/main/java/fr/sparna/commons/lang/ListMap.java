package fr.sparna.commons.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMap<X, Y> extends HashMap<X, List<Y>> implements Map<X, List<Y>> {

	public void add(X key, Y value) {
		if(this.containsKey(key)) {
			this.get(key).add(value);
		} else {
			this.put(key, new ArrayList<Y>(Collections.singletonList(value)));
		}
	}
	
}
