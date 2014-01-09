package fr.sparna.commons.lang;

public interface Function<F, T> {

	/**
	 * Applies the function to an object of type F, resulting in an object of type T.
	 * 
	 * @param input
	 * @return
	 */
	public T apply(F input);
	
}
