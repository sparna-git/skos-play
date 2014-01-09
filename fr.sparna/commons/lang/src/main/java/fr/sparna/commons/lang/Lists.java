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
	
}
