package fr.sparna.rdf.skos.toolkit.builders;

import java.util.Collections;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.reader.KeyMappingGeneratorIfc;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueBindingSetReaderIfc;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueReader;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueSparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.reader.UriLang;
import fr.sparna.rdf.sesame.toolkit.reader.UriLangMappingGenerator;
import fr.sparna.rdf.sesame.toolkit.reader.UriLangToLiteralBindingSetReader;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.skos.toolkit.SKOS;


/**
 * Queries for the skos:prefLabel, skos:altLabels and skos:hiddenLabels of a concept.
 * 
 * @author Thomas Francart
 */
public class GetLabels extends KeyValueSparqlQueryBuilder<UriLang, Literal> {
	
	private List<String> literalTypes = null;
	private String lang;
	private String conceptScheme;
	
	public GetLabels(
			List<String> literalTypes,
			String lang,
			String conceptScheme
	) {
		this.literalTypes = literalTypes;
		this.lang = lang;
		this.conceptScheme = conceptScheme;
	}
	
	public GetLabels(
			String literalType,
			String lang,
			String conceptScheme
	) {
		this.literalTypes = Collections.singletonList(literalType);
		this.lang = lang;
		this.conceptScheme = conceptScheme;
	}
	
	
	public GetLabels(
			List<String> literalTypes
	) {
		this(literalTypes, null, null);
	}
	
	public GetLabels(
			String literalType
	) {
		this(literalType, null, null);
	}

	@Override
	public String getSPARQL() {
		String literalTypesCriteria = "";
		for (String aType : this.literalTypes) {
			literalTypesCriteria += "<"+aType+">"+"|";
		}
		literalTypesCriteria = literalTypesCriteria.substring(0, (literalTypesCriteria.length()-"|".length()));
		
		String sparql = "" +
		"SELECT ?concept (lang(?label) AS ?lang) ?label"+"\n" +
		"WHERE {"+"\n" +
		"   ?concept a <"+SKOS.CONCEPT+"> ."+"\n"+
		"	?concept "+literalTypesCriteria+" ?label ."+"\n"+
		((this.conceptScheme != null)?"   ?concept <"+SKOS.IN_SCHEME+"> <"+ this.conceptScheme +"> ."+"\n":"")+
		((this.lang != null)?"   FILTER(lang(?label) = '"+this.lang+"')"+"\n":"")+
		"}";
		
		return sparql;
	}

	@Override
	public KeyMappingGeneratorIfc<UriLang> getKeyMappingGenerator() {
		return new UriLangMappingGenerator("concept", "dummy");
	}

	@Override
	public KeyValueBindingSetReaderIfc<UriLang, Literal> getKeyValueBindingSetReader() {
		return new UriLangToLiteralBindingSetReader("concept", "lang", "label");
	}
	
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"C-1-pref\"@fr; skos:altLabel \"A-1-alt\"@fr ." +
				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-pref\"@fr ." +
				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:prefLabel \"D-3-pref\"@fr ."
		);
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		GetLabels me = new GetLabels(SKOS.PREF_LABEL, "fr", null);
		UriLang key = new UriLang("http://www.test.fr/skos/_1", "fr");
		
		SparqlQuery query = new SparqlQuery(me, me.getKeyMappingGenerator().generate(key));
		System.out.println(query);
		
		KeyValueReader<UriLang, Literal> reader = new KeyValueReader<UriLang, Literal>(
				r,
				me
		);
		
		List<Literal> result = reader.read(key);
		System.out.println(result.size());
		if(result.size() > 0) {
			System.out.println(result.get(0));
		}
		
		
	}
	
}
