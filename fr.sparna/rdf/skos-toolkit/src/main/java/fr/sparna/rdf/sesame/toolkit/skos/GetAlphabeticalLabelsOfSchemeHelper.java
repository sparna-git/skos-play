package fr.sparna.rdf.sesame.toolkit.skos;

import java.net.URI;
import java.util.HashMap;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.repository.DefaultRepositoryFactory;

/**
 * Queries for the labels (pref and alt) of concepts in a given concept scheme (or in
 * the entire repository), ordered by label, in a given language.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetAlphabeticalLabelsOfSchemeHelper extends SelectSPARQLHelperBase {

	/**
	 * @param lang				a 2-letters ISO-code of the language to read labels in.
	 * @param conceptSchemeURI 	the URI of the concept scheme to read labels from (can be null to read all labels)
	 */
	public GetAlphabeticalLabelsOfSchemeHelper(String lang, final URI conceptSchemeURI) {
		super(
				new QueryBuilder(lang, conceptSchemeURI),
				new HashMap<String, Object>() {{
					put("scheme", conceptSchemeURI);
				}}		
		);
	}
	
	/**
	 * Same as this(lang, null)
	 * @param lang				a 2-letters ISO-code of the language to read labels in.
	 */
	public GetAlphabeticalLabelsOfSchemeHelper(String lang) {
		this(lang, null);
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Literal label = (Literal)binding.getValue("label");
		Literal prefLabel = (Literal)binding.getValue("prefLabel");
		Resource concept = (Resource)binding.getValue("concept");
		this.handleLabel(label, prefLabel, concept);
	}
	
	protected abstract void handleLabel(Literal label, Literal prefLabel, Resource concept)
	throws TupleQueryResultHandlerException;
	
	public static class QueryBuilder implements SPARQLQueryBuilderIfc {

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
					"SELECT ?label ?prefLabel ?concept"+"\n" +
					"WHERE {"+"\n" +
					"	?concept a <"+SKOS.CONCEPT+"> ." +
					((this.conceptScheme != null)?"?concept <"+SKOS.IN_SCHEME+"> ?scheme . ":"") +
					"	{ " +
					"		{ ?concept <"+SKOS.PREF_LABEL+"> ?label FILTER(lang(?label) = '"+this.lang+"') }" +
							"UNION" +
							"{" +
							"	?concept <"+SKOS.ALT_LABEL+"> ?label ." +
							"	?concept <"+SKOS.PREF_LABEL+"> ?prefLabel ." +
							"	FILTER(lang(?label) = '"+this.lang+"' && lang(?prefLabel) = '"+this.lang+"') " +
							" }" +
					"	}" +
					"}"+"\n" +
					" ORDER BY ?label";
					
					return sparql;
		}		
	}
	
	public static void main(String... args) throws Exception {
		Repository r = DefaultRepositoryFactory.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"C-1-pref\"@fr; skos:altLabel \"A-1-alt\"@fr ." +
				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-pref\"@fr ." +
				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:prefLabel \"D-3-pref\"@fr ."
		);
		GetAlphabeticalLabelsOfSchemeHelper helper = new GetAlphabeticalLabelsOfSchemeHelper(
				"fr",
				URI.create("http://www.test.fr/skos/_scheme")
		) {
			
			@Override
			protected void handleLabel(
					Literal label,
					Literal prefLabel,
					Resource concept
			) throws TupleQueryResultHandlerException {
				System.out.println(label.getLabel()+" / "+((prefLabel != null)?prefLabel.getLabel():"null")+" / "+concept.stringValue());
			}
		};
		SesameSPARQLExecuter.newExecuter(r).executeSelect(helper);
	}
	
}
