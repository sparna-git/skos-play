package fr.sparna.rdf.skos.printer.reader;

import fr.sparna.rdf.sesame.toolkit.util.Namespaces;
import fr.sparna.rdf.skos.printer.schema.Att;
import fr.sparna.rdf.skos.printer.schema.Entry;
import fr.sparna.rdf.skos.printer.schema.Label;
import fr.sparna.rdf.skos.printer.schema.Ref;
import fr.sparna.rdf.skos.printer.schema.Str;



public class SchemaFactory {

	public static Str createStr(String s, String type) {
		Str str = new Str();
		str.setValue(s);
		if(type != null) {
			str.setType(type);
		}
		return str;
	}
	
	public static Str createStr(String s) {
		return createStr(s, null);
	}
	
	public static Att createAtt(String value, String attType, String valueType) {
		Att a = new Att();
		a.setStr(createStr(value, valueType));
		a.setType(attType);
		return a;
	}
	
	public static Label createLabel(String value, String labelType) {
		Label l = new Label();
		l.setStr(createStr(value, labelType));
		return l;
	}
	
	/**
	 * Créé une Entry complète
	 * 
	 * @param entryId
	 * @param uri
	 * @param label
	 * @param labelType
	 * @return
	 */
	public static Entry createEntry(String entryId, String uri, String label, String labelType) {
		Entry e = new Entry();
		// utilisation de Namespaces pour raccourcir l'URI
		// not anymore to have full URIs in HTML display and geenrate outgoing links
		// e.setConcept(Namespaces.getInstance().shorten(uri));
		e.setConcept(uri);
		e.setLabel(createLabel(label, labelType));
		e.setEntryId(entryId);
		return e;
	}
	
	/**
	 * Créé une Entry avec seulement un prefLabel
	 * 
	 * @param uri
	 * @param prefLabel
	 * @return
	 */
	public static Entry createEntry(String entryId, String uri, String prefLabel) {
		return createEntry(entryId, uri, prefLabel, "pref");
	}
	
	public static Ref createRef(String entryRef, String conceptUri, String prefLabel, String refType, String labelType) {
		Ref r = new Ref();
		r.setEntryRef(entryRef);
		r.setType(refType);
		// cette entry n'aura pas d'entryId
		r.setEntry(createEntry(null, conceptUri, prefLabel, labelType));
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
}
