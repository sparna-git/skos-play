package fr.sparna.rdf.skos.xls2skos;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

class SKOSXL {
	public static String NAMESPACE = "http://www.w3.org/2008/05/skos-xl#";
	public static IRI PREF_LABEL = SimpleValueFactory.getInstance().createIRI(NAMESPACE+"prefLabel");
	public static IRI ALT_LABEL = SimpleValueFactory.getInstance().createIRI(NAMESPACE+"altLabel");
	public static IRI HIDDEN_LABEL = SimpleValueFactory.getInstance().createIRI(NAMESPACE+"hiddenLabel");
	public static IRI LABEL = SimpleValueFactory.getInstance().createIRI(NAMESPACE+"Label");
	public static IRI LITERAL_FORM = SimpleValueFactory.getInstance().createIRI(NAMESPACE+"literalForm");
}