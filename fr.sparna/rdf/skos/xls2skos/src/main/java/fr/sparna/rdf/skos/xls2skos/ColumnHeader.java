package fr.sparna.rdf.skos.xls2skos;

import java.util.Optional;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class ColumnHeader {

	private String originalValue;
	private Optional<String> language;
	private Optional<IRI> datatype;
	private String property;
	private boolean inverse = false;
	
	private ColumnHeader(String originalValue) {
		this.originalValue = originalValue;
	}
	
	public static ColumnHeader parse(String value, PrefixManager pm) {
		if(value == null) {
			return null;
		}
		ColumnHeader h = new ColumnHeader(value);
		h.property = parseProperty(value);
		h.language = parseLanguage(value);
		h.datatype = parseDatatype(value, pm);
		h.inverse = parseInverse(value);
		
		return h;
	}
	
	private static String parseProperty(String value) {
		String property = value;
		
		// remove inverse mark
		if(parseInverse(value)) {
			property = value.substring(1);
		}
		
		if(property.contains("@")) {
			property = property.substring(0, property.lastIndexOf('@'));
		}
		if(property.contains("^^")) {
			property = property.substring(0, property.lastIndexOf("^^"));
		}
		return property;
	}
	
	private static Optional<String> parseLanguage(String value) {
		if(value.contains("@")) {
			return Optional.of(value.substring(value.lastIndexOf('@')+1));
		}
		return Optional.empty();
	}
	
	private static Optional<IRI> parseDatatype(String value, PrefixManager pm) {
		if(value.contains("^^")) {
			String dt = value.substring(value.lastIndexOf("^^")+2);
			if(pm.usesKnownPrefix(dt)) {
				return Optional.of(SimpleValueFactory.getInstance().createIRI(pm.uri(dt, false)));
			} else if (dt.startsWith("<http")){
				return Optional.of(SimpleValueFactory.getInstance().createIRI(dt.substring(1, dt.length()-2)));
			} else {
				return Optional.of(SimpleValueFactory.getInstance().createIRI(dt));
			}
		}
		return Optional.empty();
	}
	
	private static boolean parseInverse(String value) {
		return value.startsWith("^");
	}

	public String getOriginalValue() {
		return originalValue;
	}

	public Optional<String> getLanguage() {
		return language;
	}

	public Optional<IRI> getDatatype() {
		return datatype;
	}

	public String getProperty() {
		return property;
	}

	public boolean isInverse() {
		return inverse;
	}
	
}
