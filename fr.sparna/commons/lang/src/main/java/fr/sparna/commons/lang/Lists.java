package fr.sparna.commons.lang;

import java.util.ArrayList;
import java.util.List;

public final class Lists {

	public static <F,T> List<T> transform(
			List<F> fromIterable,
			Function<? super F,? extends T> function
	) {		
		ArrayList<T> result = new ArrayList<T>();
		
		for (F anF : fromIterable) {
			result.add(function.apply(anF));
		}
		
		return result;
	}
	
	public static <T> List<T> filter(
			List<T> fromIterable,
			Filter<? super T> filter
	) {
		ArrayList<T> result = new ArrayList<T>();
		
		for (T aT : fromIterable) {
			if(filter.filter(aT)) {
				result.add(aT);
			}
		}
		
		return result;
	}
	
}
