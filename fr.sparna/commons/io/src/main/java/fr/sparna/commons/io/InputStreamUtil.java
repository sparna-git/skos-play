package fr.sparna.commons.io;

import java.io.InputStream;

public class InputStreamUtil {

	/**
	 * Turns the given stream into String using the given charset
	 * 
	 * @param is
	 * @param charsetName
	 * @return
	 */
	public static String streamToString(InputStream is, String charsetName) {
		try {
	        return new java.util.Scanner(is, charsetName).useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "";
	    }
	}
	
	/**
	 * Turns the given stream into a String using the default platform charset
	 * 
	 * @param is
	 * @return
	 */
	public static String streamToString(InputStream is) {
		try {
	        return new java.util.Scanner(is).useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "";
	    }
	}
	
}
