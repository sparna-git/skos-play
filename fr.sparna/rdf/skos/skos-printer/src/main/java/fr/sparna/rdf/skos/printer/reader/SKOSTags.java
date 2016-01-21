package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;

import fr.sparna.rdf.skos.toolkit.SKOS;

public class SKOSTags {
	
	public static String getString(String key) {
		return key;
	}
	
	public static String getString(URI uri) {
		return getStringForURI(uri.toString());
	}
	
	public static String getStringForURI(String uri) {
		if(uri.startsWith(SKOS.NAMESPACE)) {
			return getString(uri.substring(SKOS.NAMESPACE.length()));
		} else if(uri.startsWith(SKOSPLAY.NAMESPACE)) {
			return getString(uri.substring(SKOSPLAY.NAMESPACE.length()));
		} else {
			// never happens
			return uri;
		}
	}
	
}
