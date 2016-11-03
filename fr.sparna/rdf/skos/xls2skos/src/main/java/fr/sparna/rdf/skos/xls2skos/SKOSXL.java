package fr.sparna.rdf.skos.xls2skos;

import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

class SKOSXL {
	public static String NAMESPACE = "http://www.w3.org/2008/05/skos-xl#";
	public static URI PREF_LABEL = SimpleValueFactory.getInstance().createURI(NAMESPACE+"prefLabel");
	public static URI ALT_LABEL = SimpleValueFactory.getInstance().createURI(NAMESPACE+"altLabel");
	public static URI HIDDEN_LABEL = SimpleValueFactory.getInstance().createURI(NAMESPACE+"hiddenLabel");
	public static URI LABEL = SimpleValueFactory.getInstance().createURI(NAMESPACE+"Label");
	public static URI LITERAL_FORM = SimpleValueFactory.getInstance().createURI(NAMESPACE+"literalForm");
}