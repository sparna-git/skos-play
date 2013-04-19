package fr.sparna.commons.lang;

import java.text.Normalizer;

public class StringUtil {

	/**
	 * Remove diacritics from the input string
	 * 
	 * @param source
	 * @return
	 */
	public static String withoutAccents(String source) {
		return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
	}
	
}
