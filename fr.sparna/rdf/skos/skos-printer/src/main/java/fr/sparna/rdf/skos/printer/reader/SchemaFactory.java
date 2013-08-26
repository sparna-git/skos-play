package fr.sparna.rdf.skos.printer.reader;

import fr.sparna.rdf.skos.printer.schema.Att;
import fr.sparna.rdf.skos.printer.schema.CellType;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.ListItem;
import fr.sparna.rdf.skos.printer.schema.Ref;
import fr.sparna.rdf.skos.printer.schema.RowType;
import fr.sparna.rdf.skos.printer.schema.TypedString;


public class SchemaFactory {

	public static TypedString createTypedString(String s, String type) {
		TypedString str = new TypedString();
		str.setValue(s);
		if(type != null) {
			str.setType(type);
		}
		return str;
	}
	
	public static TypedString createTypedString(String s) {
		return createTypedString(s, null);
	}
	
	public static Att createAtt(String value, String attType, String valueType) {
		Att a = new Att();
		a.setStr(createTypedString(value, valueType));
		a.setType(attType);
		return a;
	}
	
	/**
	 * Créé une Entry complète
	 * 
	 * @param blockId
	 * @param uri
	 * @param label
	 * @param labelType
	 * @return
	 */
	public static ConceptBlock createConceptBlock(String blockId, String uri, String label, String labelType) {
		ConceptBlock e = new ConceptBlock();
		// utilisation de Namespaces pour raccourcir l'URI
		// not anymore to have full URIs in HTML display and geenrate outgoing links
		// e.setConcept(Namespaces.getInstance().shorten(uri));
		e.setUri(uri);
		e.setLabel(createTypedString(label, labelType));
		e.setId(blockId);
		return e;
	}
	
	/**
	 * Créé une Entry avec seulement un prefLabel
	 * 
	 * @param uri
	 * @param prefLabel
	 * @return
	 */
	public static ConceptBlock createConceptBlock(String entryId, String uri, String prefLabel) {
		return createConceptBlock(entryId, uri, prefLabel, "pref");
	}
	
	public static Ref createRef(String entryRef, String conceptUri, String prefLabel, String refType, String labelType) {
		Ref r = new Ref();
		r.setRefId(entryRef);
		r.setType(refType);
		r.setLabel(createTypedString(prefLabel, labelType));
		r.setUri(conceptUri);
		return r;
	}
	
	/**
	 * Créé une référence vers une autre entry sans préciser le type du label de la référence
	 * 
	 * @param entryRef
	 * @param uri
	 * @param prefLabel
	 * @param refType
	 * @return
	 */
	public static Ref createRef(String entryRef, String uri, String prefLabel, String refType) {
		return createRef(entryRef, uri, prefLabel, refType, null);
	}
	
	public static ListItem createListItem(Object o) {
		ListItem li = new ListItem();
		li.setAny(o);
		return li;
	}
	
	public static CellType createCell(Object o) {
		CellType c = new CellType();
		c.setAny(o);
		return c;
	}
	
	public static RowType createRow(Object...objects) {
		RowType row = new RowType();
		for (Object object : objects) {
			row.getCell().add(createCell(object));
		}
		return row;
	}

}
