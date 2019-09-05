package fr.sparna.rdf.skos.xls2skos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.hssf.util.CellReference;
import org.eclipse.rdf4j.model.IRI;

public class ColumnHeader {
	
	public static final String PARAMETER_SEPARATOR = "separator";
	public static final String PARAMETER_SUBJECT_COLUMN = "subjectColumn";
	public static final String PARAMETER_LOOKUP_COLUMN = "lookupColumn";
	public static final String PARAMETER_RECONCILE = "reconcile";
	public static final String PARAMETER_RECONCILE_ON = "reconcileOn";
	public static final String PARAMETER_ID = "id";

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
	/**
	 * The column id set by the "id" parameter
	 */
	private String id;
	/**
	 * The IRI of the type or ConceptScheme on which to reconcile, set by the "reconcileOn" parameter
	 */
	private IRI reconcileOn;
	/**
	 * The actual Excel column index corresponding to this header
	 */
	private short columnIndex;
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public short getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(short columnIndex) {
		this.columnIndex = columnIndex;
	}

	public IRI getReconcileOn() {
		return reconcileOn;
	}

	public void setReconcileOn(IRI reconcileOn) {
		this.reconcileOn = reconcileOn;
	}

	/**
	 * Finds the column index based on a column ID reference or an Excel column reference.
	 * Returns -1 if not found.
	 * 
	 * @param headers
	 * @param idRef
	 * @return
	 */
	public static short idRefToColumnIndex(List<ColumnHeader> headers, String idRef) {
		for (ColumnHeader header : headers) {
			if(header.getId() != null && header.getId().equals(idRef)) {
				return header.getColumnIndex();
			}
		}
		
		// if we haven't found the proper column id, try it as an Excel column reference
		if(idRef.length() <= 2) {
			short subjectColumnIndex = (short)CellReference.convertColStringToIndex(idRef);
			if(subjectColumnIndex != -1) {
				return subjectColumnIndex;
			}
		}

		return -1;
	}
	
	/**
	 * Finds the column index based on a reference that can be 
	 * either an ID reference or a property reference or an Excel column reference.
	 * Returns -1 if not found.
	 * 
	 * @param headers
	 * @param idOrPropertyRef the ID or property reference to search
	 * @return
	 */
	public static short idRefOrPropertyRefToColumnIndex(List<ColumnHeader> headers, String idOrPropertyRef) {
		for (ColumnHeader header : headers) {
			if(
					(header.getId() != null && header.getId().equals(idOrPropertyRef))
					||
					(header.getDeclaredProperty() != null && header.getDeclaredProperty().equals(idOrPropertyRef))
			) {
				return header.getColumnIndex();
			}
		}
		
		// if we haven't found the proper column id, try it as an Excel column reference
		if(idOrPropertyRef.length() <= 2) {
			short subjectColumnIndex = (short)CellReference.convertColStringToIndex(idOrPropertyRef);
			if(subjectColumnIndex != -1) {
				return subjectColumnIndex;
			}
		}
		
		return -1;
	}
	
	public static ColumnHeader findByColumnIndex(List<ColumnHeader> headers, int columnIndex) {
		for (ColumnHeader header : headers) {
			if(header.getColumnIndex() == columnIndex) {
				return header;
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		return "ColumnHeader [originalValue=" + originalValue + ", language=" + language + ", datatype=" + datatype
				+ ", property=" + property + ", declaredProperty=" + declaredProperty + ", inverse=" + inverse
				+ ", parameters=" + parameters + ", id=" + id + ", columnIndex=" + columnIndex + "]";
	}

	
	
	
	
}
