package fr.sparna.commons.lang;

public interface Filter<T> {

	/**
	 * Test if the input passes the filter or not;
	 * 
	 * @param input
	 * @return
	 */
	public boolean filter(T input);
	
}
