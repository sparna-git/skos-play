package fr.sparna.rdf.skos.printer.reader;

import fr.sparna.rdf.skos.printer.schema.Att;
import fr.sparna.rdf.skos.printer.schema.CellType;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.IndexEntry;
import fr.sparna.rdf.skos.printer.schema.Label;
import fr.sparna.rdf.skos.printer.schema.Link;
import fr.sparna.rdf.skos.printer.schema.ListItem;
import fr.sparna.rdf.skos.printer.schema.RowType;
import fr.sparna.rdf.skos.printer.schema.StyledString;


public class SchemaFactory {

	public static StyledString createStyledString(String s, String style) {
		StyledString str = new StyledString();
		str.setValue(s);
		if(style != null) {
			str.setStyle(style);
		}
		return str;
	}
	
	public static StyledString createStyledString(String s) {
		return createStyledString(s, null);
	}
	
	public static Link createLink(String entryRef, String conceptUri, String label, String style) {
		Link l = new Link();
		l.setRefId(entryRef);
		l.setUri(conceptUri);
		if(style != null) {
			l.setStyle(style);
		}
		l.setValue(label);
		return l;
	}
	
	public static Att createAtt(String value, String attType, String valueType) {
		Att a = new Att();
		a.setStr(createStyledString(value, valueType));
		a.setType(attType);
		return a;
	}
	
	public static Att createAttLink(String entryRef, String conceptUri, String label, String attType, String style) {
		Att a = new Att();
		a.setType(attType);
		a.setLink(createLink(entryRef, conceptUri, label, style));
		return a;
	}
	
	/**
	 * Créé une référence vers une autre entry sans préciser de style
	 * 
	 * @param entryRef
	 * @param uri
	 * @param prefLabel
	 * @param refType
	 * @return
	 */
	public static Att createAttLink(String entryRef, String uri, String prefLabel, String refType) {
		return createAttLink(entryRef, uri, prefLabel, refType, null);
	}
	
	public static Label createLabel(String label, String style) {
		Label l = new Label();
		l.setStr(createStyledString(label, style));
		return l;
	}
	
	public static Label createLabelLink(String entryRef, String conceptUri, String label, String style) {
		Label l = new Label();
		l.setLink(createLink(entryRef, conceptUri, label, style));
		return l;
	}
	
	public static ConceptBlock createConceptBlock(String blockId, String uri, Label l) {
		ConceptBlock e = new ConceptBlock();
		e.setId(blockId);
		e.setUri(uri);
		e.setLabel(l);
		return e;
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
	
	public static IndexEntry createIndexEntry(String entryId, String uri, Label label, String before, String keyLabel, String after) {
		IndexEntry entry = new IndexEntry();
		entry.setId(entryId);
		entry.setUri(uri);
		entry.setLabel(label);
		entry.setKey(keyLabel);
		entry.setBefore(before);
		entry.setAfter(after);
		return entry;
	}

}
