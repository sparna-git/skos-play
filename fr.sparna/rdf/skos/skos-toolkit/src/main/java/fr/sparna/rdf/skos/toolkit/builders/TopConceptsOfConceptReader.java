package fr.sparna.rdf.skos.toolkit.builders;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;

import fr.sparna.rdf.rdf4j.toolkit.reader.IriBindingSetGenerator;
import fr.sparna.rdf.rdf4j.toolkit.reader.IriToIriBindingSetParser;
import fr.sparna.rdf.rdf4j.toolkit.reader.KeyValueReader;
import fr.sparna.rdf.skos.toolkit.SKOS;

/**
 * Queries for the top concepts of a given concept, optionally ordered by their skos:prefLabels
 * in a given language. A top Concept is a Concept that does not have a skos:broader or is not referenced by a skos:narrower.
 * 
 * @author Thomas Francart
 */
public class TopConceptsOfConceptReader extends KeyValueReader<IRI, IRI> {

	private static final String KEY_VAR_NAME = "concept";
	private static final String VALUE_VAR_NAME = "top";
	
			

	/**
	 * @param orderByLang an 2-letter ISO-code of a language, or null to build a query without ordering.
	 */
	public TopConceptsOfConceptReader(String orderByLang) {
		super(
				new QuerySupplier(orderByLang).get(),
				new IriBindingSetGenerator(KEY_VAR_NAME),
				new IriToIriBindingSetParser(KEY_VAR_NAME, VALUE_VAR_NAME)
		);
	}

	public static class QuerySupplier implements Supplier<String> {
	
		private String orderByLang = null;
		
		public QuerySupplier(String orderByLang) {
			super();
			this.orderByLang = orderByLang;
		}

		@Override
		public String get() {
	
			String sparql = "" +
					"SELECT DISTINCT ?"+KEY_VAR_NAME+" ?"+VALUE_VAR_NAME+""+"\n" +
					"WHERE {"+"\n" +
					"   { SELECT DISTINCT ?"+VALUE_VAR_NAME+" WHERE { ?"+KEY_VAR_NAME+" <"+SKOS.BROADER+">|^<"+SKOS.NARROWER+"> ?"+VALUE_VAR_NAME+".  FILTER NOT EXISTS { ?"+VALUE_VAR_NAME+" <"+SKOS.BROADER+">|^<"+SKOS.NARROWER+"> ?parent } } }"+"\n" +
					"	?"+KEY_VAR_NAME+" (<"+SKOS.BROADER+">|^<"+SKOS.NARROWER+">)+ ?"+VALUE_VAR_NAME+"\n" +
					((this.orderByLang != null)?
					"	OPTIONAL { ?"+VALUE_VAR_NAME+" <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(lang(?prefLabel) = '"+this.orderByLang+"')}"+"\n" +
					"}" +
					" ORDER BY ?prefLabel"
					:
					"}");		
			
			return sparql;
		}
	}

}
