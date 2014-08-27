package fr.sparna.rdf.skos.toolkit;

import java.util.HashMap;

import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

/**
 * Queries for the top concepts of a given concept scheme, optionally ordered by their skos:prefLabels
 * in a given language.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetTopConceptsHelper extends SelectSparqlHelperBase {

	/**
	 * @param conceptSchemeURI 	the URI of the concept scheme to read top concepts from
	 * @param orderByLang		a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering.
	 */
	public GetTopConceptsHelper(final java.net.URI conceptSchemeURI, String orderByLang) {
		super(
				new QueryBuilder(orderByLang),
				new HashMap<String, Object>() {{
					put("scheme", conceptSchemeURI);
				}}		
		);
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource top = (Resource)binding.getValue("top");
		this.handleTopConcept(top);
	}
	
	protected abstract void handleTopConcept(Resource top)
	throws TupleQueryResultHandlerException;
	
	public static class QueryBuilder implements SparqlQueryBuilderIfc {

		private String orderByLang = null;		

		/**
		 * @param orderByLang an 2-letter ISO-code of a language, or null to build a query without ordering.
		 */
		public QueryBuilder(String orderByLang) {
			this.orderByLang = orderByLang;
		}

		@Override
		public String getSPARQL() {
			String sparql = "" +
					"SELECT DISTINCT ?scheme ?top"+"\n" +
					"WHERE {"+"\n" +
					"	?scheme <"+SKOS.HAS_TOP_CONCEPT+">|^<"+SKOS.TOP_CONCEPT_OF+"> ?top"+"\n" +
					((this.orderByLang != null)?
					"	OPTIONAL { ?top <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
					"}" +
					" ORDER BY ?prefLabel"
					:
					"}");
					
					return sparql;
		}		
	}
	
}
