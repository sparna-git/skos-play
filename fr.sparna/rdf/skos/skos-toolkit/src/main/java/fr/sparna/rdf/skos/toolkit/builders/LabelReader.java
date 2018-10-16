package fr.sparna.rdf.skos.toolkit.builders;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.reader.IriLang;
import fr.sparna.rdf.rdf4j.toolkit.reader.IriLangBindingSetGenerator;
import fr.sparna.rdf.rdf4j.toolkit.reader.IriLangToLiteralBindingSetParser;
import fr.sparna.rdf.rdf4j.toolkit.reader.KeyValueReader;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.skos.toolkit.SKOS;


/**
 * Queries for the skos:prefLabel, skos:altLabels and skos:hiddenLabels of a concept.
 * 
 * @author Thomas Francart
 */
public class LabelReader extends KeyValueReader<IriLang, Literal> {
	

	
	public LabelReader(
			Set<String> literalTypes,
			String lang,
			String conceptScheme
	) {
		super(
				new QuerySupplier(literalTypes, lang, conceptScheme).get(),
				new IriLangBindingSetGenerator("concept", "dummy"),
				new IriLangToLiteralBindingSetParser("concept", "lang", "label")
		);
	}
	
	public LabelReader(
			String literalType,
			String lang,
			String conceptScheme
	) {
		super(
				new QuerySupplier(Collections.singleton(literalType), lang, conceptScheme).get(),
				new IriLangBindingSetGenerator("concept", "dummy"),
				new IriLangToLiteralBindingSetParser("concept", "lang", "label")
		);
	}
	
	public LabelReader(
			Set<String> literalTypes
	) {
		this(literalTypes, null, null);
	}
	
	public LabelReader(
			String literalType
	) {
		this(literalType, null, null);
	}

	public static class QuerySupplier implements Supplier<String> {
		
		private Set<String> literalTypes = null;
		private String lang;
		private String conceptScheme;
		
		public QuerySupplier(Set<String> literalTypes, String lang, String conceptScheme) {
			super();
			this.literalTypes = literalTypes;
			this.lang = lang;
			this.conceptScheme = conceptScheme;
		}

		@Override
		public String get() {
			String literalTypesCriteria = "";
			for (String aType : literalTypes) {
				literalTypesCriteria += "<"+aType+">"+"|";
			}
			literalTypesCriteria = literalTypesCriteria.substring(0, (literalTypesCriteria.length()-"|".length()));
			
			String sparql = "" +
			"SELECT ?concept (lang(?label) AS ?lang) ?label"+"\n" +
			"WHERE {"+"\n" +
			"   ?concept a <"+SKOS.CONCEPT+"> ."+"\n"+
			"	?concept "+literalTypesCriteria+" ?label ."+"\n"+
			((conceptScheme != null)?"   ?concept <"+SKOS.IN_SCHEME+"> <"+ conceptScheme +"> ."+"\n":"")+
			((lang != null)?"   FILTER(lang(?label) = '"+lang+"')"+"\n":"")+
			"}";
			
			return sparql;
		}
		
	}
	
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"C-1-pref\"@fr; skos:altLabel \"A-1-alt\"@fr ." +
				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-pref\"@fr ." +
				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:prefLabel \"D-3-pref\"@fr ."
		);
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(ch.qos.logback.classic.Level.INFO);
	    ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("fr.sparna.rdf")).setLevel(ch.qos.logback.classic.Level.TRACE);
		
		LabelReader me = new LabelReader(SKOS.PREF_LABEL, "fr", null);
		IriLang key = new IriLang("http://www.test.fr/skos/_1", "fr");
		
		try(RepositoryConnection connection = r.getConnection()) {
			List<Literal> result = me.read(key, connection);
			System.out.println(result.size());
			if(result.size() > 0) {
				System.out.println(result.get(0));
			}
		}
		
	}
	
}
