package fr.sparna.rdf.skos.toolkit.builders;

import org.openrdf.model.URI;

import fr.sparna.rdf.sesame.toolkit.reader.KeyMappingGeneratorIfc;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueBindingSetReaderIfc;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueSparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.reader.UriKeyMappingGenerator;
import fr.sparna.rdf.sesame.toolkit.reader.UriToUriBindingSetReader;
import fr.sparna.rdf.skos.toolkit.SKOS;

/**
 * Queries for the collections of a given concept, optionally ordered by their skos:prefLabels
 * in a given language. The collection references the Concept with a skos:member
 * 
 * @author Thomas Francart
 */
public class GetCollectionsOfConcept extends KeyValueSparqlQueryBuilder<URI, URI> {

	private static final String KEY_VAR_NAME = "concept";
	private static final String VALUE_VAR_NAME = "collection";
	
	private String orderByLang = null;		

	/**
	 * @param orderByLang an 2-letter ISO-code of a language, or null to build a query without ordering.
	 */
	public GetCollectionsOfConcept(String orderByLang) {
		this.orderByLang = orderByLang;
	}

	@Override
	public String getSPARQL() {
		String sparql = "" +
				"SELECT DISTINCT ?"+KEY_VAR_NAME+" ?"+VALUE_VAR_NAME+""+"\n" +
				"WHERE {"+"\n" +
				"	?"+VALUE_VAR_NAME+" <"+SKOS.MEMBER+"> ?"+KEY_VAR_NAME+"\n" +
				((this.orderByLang != null)?
				"	OPTIONAL { ?"+VALUE_VAR_NAME+" <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(lang(?prefLabel) = '"+this.orderByLang+"')}"+"\n" +
				"}" +
				" ORDER BY ?prefLabel"
				:
				"}");
				
		return sparql;
	}

	@Override
	public KeyMappingGeneratorIfc<URI> getKeyMappingGenerator() {
		return new UriKeyMappingGenerator(KEY_VAR_NAME);
	}

	@Override
	public KeyValueBindingSetReaderIfc<URI, URI> getKeyValueBindingSetReader() {
		return new UriToUriBindingSetReader(KEY_VAR_NAME, VALUE_VAR_NAME);
	}
	
}
