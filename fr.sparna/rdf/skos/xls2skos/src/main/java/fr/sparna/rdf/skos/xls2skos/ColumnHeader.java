package fr.sparna.rdf.skos.xls2skos;

import java.util.Optional;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class ColumnHeader {

	private String originalValue;
	private Optional<String> language;
	private Optional<IRI> datatype;
	private IRI property;
	private String declaredProperty;
	private boolean inverse = false;
	
	private ColumnHeader(String originalValue) {
		this.originalValue = originalValue;
	}
	
	public static ColumnHeader parse(String value, PrefixManager pm) {
		if(value == null) {
			return null;
		}
		ColumnHeader h = new ColumnHeader(value);
		h.declaredProperty = parseDeclaredProperty(value);
		h.property = parseProperty(value, pm);
		h.language = parseLanguage(value);
		h.datatype = parseDatatype(value, pm);
		h.inverse = parseInverse(value);
		
		return h;
	}
	
	private static IRI parseProperty(String declaredProperty, PrefixManager pm) {
		try {
			if(pm.usesKnownPrefix(declaredProperty)) {
				return SimpleValueFactory.getInstance().createIRI(pm.uri(declaredProperty, false));
			} else {
				return SimpleValueFactory.getInstance().createIRI(declaredProperty);
			}
		} catch (Exception e) {
			return null;
		}		
	}
	
	private static String parseDeclaredProperty(String value) {
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

	public IRI getProperty() {
		return property;
	}

	public boolean isInverse() {
		return inverse;
	}

	public String getDeclaredProperty() {
		return declaredProperty;
	}
	
}
