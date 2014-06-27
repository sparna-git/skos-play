package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import fr.sparna.rdf.skos.toolkit.SKOS;

public class SKOSTags {

	public static final String KEY_TOP_CONCEPT = "topConcept";
	
	public static String getString(String key) {
		return key;
	}
	
	public static String getString(URI uri) {
		return getStringForURI(uri.toString());
	}
	
	public static String getStringForURI(String uri) {
		return getString(uri.substring(SKOS.NAMESPACE.length()));
	}
	
}
