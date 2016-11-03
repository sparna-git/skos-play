package fr.sparna.rdf.skos.toolkit.builders;

import org.eclipse.rdf4j.model.URI;

import fr.sparna.rdf.sesame.toolkit.reader.KeyMappingGeneratorIfc;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueBindingSetReaderIfc;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueSparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.reader.UriKeyMappingGenerator;
import fr.sparna.rdf.sesame.toolkit.reader.UriToUriBindingSetReader;
import fr.sparna.rdf.skos.toolkit.SKOS;

/**
 * Queries for the top concepts of a given concept, optionally ordered by their skos:prefLabels
 * in a given language. A top Concept is a Concept that does not have a skos:broader or is not referenced by a skos:narrower.
 * 
 * @author Thomas Francart
 */
public class GetTopConceptsOfConcept extends KeyValueSparqlQueryBuilder<URI, URI> {

	private static final String KEY_VAR_NAME = "concept";
	private static final String VALUE_VAR_NAME = "top";
	
	private String orderByLang = null;		

	/**
	 * @param orderByLang an 2-letter ISO-code of a language, or null to build a query without ordering.
	 */
	public GetTopConceptsOfConcept(String orderByLang) {
		this.orderByLang = orderByLang;
	}

	@Override
	public String getSPARQL() {
		String sparql = "" +
				"SELECT DISTINCT ?"+KEY_VAR_NAME+" ?"+VALUE_VAR_NAME+""+"\n" +
				"WHERE {"+"\n" +
				"	?"+KEY_VAR_NAME+" (<"+SKOS.BROADER+">|^<"+SKOS.NARROWER+">)+ ?"+VALUE_VAR_NAME+"\n" +
				"	FILTER NOT EXISTS { ?"+VALUE_VAR_NAME+" <"+SKOS.BROADER+">|^<"+SKOS.NARROWER+"> ?parent }"+"\n" +
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
