package fr.sparna.rdf.skos.xls2skos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.rdf4j.model.IRI;

public class ColumnHeader {
	
	public static final String PARAMETER_SEPARATOR = "separator";

	/**
	 * the full orignal value of the header, as a raw string
	 */
	private String originalValue;
	/**
	 * The language parameter of the header, e.g. "skos:prefLabel@en"
	 */
	private Optional<String> language;
	/**
	 * The datatype parameter of the header, e.g. "dct:created^^xsd:date"
	 */
	private Optional<IRI> datatype;
	/**
	 * The IRI of the property of that column, e.g. for "skos:prefLabel@en", the property is "http://www.w3.org/2004/02/skos/core#prefLabel"
	 */
	private IRI property;
	/**
	 * The property part of the header, as declared, e.g. for "dct:created^^xsd:date", the declared property is "dct:created"
	 */
	private String declaredProperty;
	/**
	 * Whether the column declares an inverse flag, e.g. "^skos:member"
	 */
	private boolean inverse = false;
	/**
	 * Additionnal parameters on the header, e.g. "skos:altLabel(separator=",")"
	 */
	private Map<String, String> parameters = new HashMap<String, String>();
	
	public ColumnHeader(String originalValue) {
		this.originalValue = originalValue;
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


	public void setLanguage(Optional<String> language) {
		this.language = language;
	}

	public void setDatatype(Optional<IRI> datatype) {
		this.datatype = datatype;
	}

	public void setProperty(IRI property) {
		this.property = property;
	}

	public void setDeclaredProperty(String declaredProperty) {
		this.declaredProperty = declaredProperty;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "ColumnHeader [originalValue=" + originalValue + ", language=" + language + ", datatype=" + datatype
				+ ", property=" + property + ", declaredProperty=" + declaredProperty + ", inverse=" + inverse
				+ ", parameters=" + parameters + "]";
	}
	
	
	
}
