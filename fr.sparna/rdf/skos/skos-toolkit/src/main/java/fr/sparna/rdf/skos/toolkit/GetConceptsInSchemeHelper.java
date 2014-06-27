package fr.sparna.rdf.skos.toolkit;

import java.net.URI;
import java.util.HashMap;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

/**
 * Queries for the concepts in a given concept scheme (or in the entire repository), 
 * with their prefLabel in a given language. Results are _not_ordered and should be ordered with a Collator.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetConceptsInSchemeHelper extends SelectSparqlHelperBase {

	/**
	 * @param lang				a 2-letters ISO-code of the language to read labels in.
	 * @param conceptSchemeURI 	the URI of the concept scheme to read concepts from (can be null to read all concepts)
	 */
	public GetConceptsInSchemeHelper(String lang, final URI conceptSchemeURI) {
		super(
				new QueryBuilder(lang, conceptSchemeURI),
				new HashMap<String, Object>() {{
					// is a concept scheme URI was given, bind it to a URI
					if(conceptSchemeURI != null) {
						put("scheme", conceptSchemeURI);
					}
				}}		
		);
	}
	
	/**
	 * Same as this(lang, null)
	 * @param lang				a 2-letters ISO-code of the language to read concepts in.
	 */
	public GetConceptsInSchemeHelper(String lang) {
		this(lang, null);
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("concept");
		Literal prefLabel = (Literal)binding.getValue("label");
		this.handleConcept(concept, prefLabel);
	}
	
	protected abstract void handleConcept(Resource concept, Literal prefLabel)
	throws TupleQueryResultHandlerException;
	
	public static class QueryBuilder implements SparqlQueryBuilderIfc {

		private String lang = null;
		private URI conceptScheme = null;

		/**
		 * @param lang 				2-letter ISO-code of a language to select labels in
		 * @param conceptScheme		optionnal URI of a concept scheme to select labels in
		 */
		public QueryBuilder(String lang, URI conceptScheme) {
			this.lang = lang;
			this.conceptScheme = conceptScheme;
		}
		
		/**
		 * Same as this(lang, null)
		 * @param lang 				2-letter ISO-code of a language to select labels in
		 */
		public QueryBuilder(String lang) {
			this(lang, null);
		}

		@Override
		public String getSPARQL() {
			String sparql = "" +
					"SELECT ?concept ?label"+"\n" +
					"WHERE {"+"\n" +
					"	?concept a <"+SKOS.CONCEPT+"> ." +
					((this.conceptScheme != null)?"?concept <"+SKOS.IN_SCHEME+"> ?scheme . ":"") +
					"	{ " +
					"		{ " +
							"   ?concept <"+SKOS.PREF_LABEL+"> ?label FILTER(langMatches(lang(?label), '"+this.lang+"')) " +
							" }" +
							" UNION {" +
							// il faut qu'on ait au moins un critere positif sinon ca ne fonctionne pas
							"	?concept a <"+SKOS.CONCEPT+"> . " +
							"	FILTER NOT EXISTS { ?concept <"+SKOS.PREF_LABEL+"> ?nopref . FILTER(langMatches(lang(?nopref), '"+this.lang+"')) }" +
							"   BIND(str(?concept) as ?label)" +
							" }" +
					"	}" +
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
		GetConceptsInSchemeHelper helper = new GetConceptsInSchemeHelper(
				"fr",
				URI.create("http://www.test.fr/skos/_scheme")
		) {
			
			@Override
			protected void handleConcept(
					Resource concept,
					Literal label
			) throws TupleQueryResultHandlerException {
				System.out.println(concept.stringValue()+" : "+label.getLabel());
			}
		};
		Perform.on(r).select(helper);
	}
	
}
