package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import fr.sparna.rdf.skos.toolkit.SKOS;

public class SKOSTags {

	public static final String KEY_TOP_CONCEPT = "topConcept";
	
	private ResourceBundle bundle;

	/**
	 * Singleton
	 */
	private static Map<String, SKOSTags> instances = new HashMap<String, SKOSTags>();
	
	/**
	 * Private constructor
	 */
	private SKOSTags(String lang) {
		this.bundle = ResourceBundle.getBundle(
				"fr.sparna.rdf.skos.display.Tags",
				new Locale(lang),
				new fr.sparna.i18n.StrictResourceBundleControl()
		);
	}
	
	/**
	 * Singleton accessor
	 * @return
	 */
	public static SKOSTags getInstance(String lang) {
		if(!SKOSTags.instances.containsKey(lang)) {
			SKOSTags.instances.put(lang, new SKOSTags(lang));
		}
		return SKOSTags.instances.get(lang);
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}
	
	public String getString(String key) {
		String s = bundle.getString(key);
		return (s == null)?"??":s;
	}
	
	public String getString(URI uri) {
		return this.getStringForURI(uri.toString());
	}
	
	public String getStringForURI(String uri) {
		return this.getString(uri.substring(SKOS.NAMESPACE.length()));
	}
	
}
