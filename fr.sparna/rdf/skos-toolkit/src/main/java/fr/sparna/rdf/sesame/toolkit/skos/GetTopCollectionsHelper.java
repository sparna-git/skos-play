package fr.sparna.rdf.sesame.toolkit.skos;

import java.util.HashMap;

import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

/**
 * Queries for the top collections of a given concept scheme, optionally ordered by their skos:prefLabels
 * in a given language.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetTopCollectionsHelper extends SelectSPARQLHelperBase {

	/**
	 * @param conceptSchemeURI 	the URI of the concept scheme to read top collections from, or null to read all top collections
	 * @param orderByLang		a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering.
	 */
	public GetTopCollectionsHelper(final java.net.URI conceptSchemeURI, String orderByLang) {
		super(
				new QueryBuilder(orderByLang, conceptSchemeURI != null),
				new HashMap<String, Object>() {{
					if(conceptSchemeURI != null) { put("scheme", conceptSchemeURI); }
				}}
		);
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource collection = (Resource)binding.getValue("collection");
		this.handleTopCollection(collection);
	}
	
	protected abstract void handleTopCollection(Resource collection)
	throws TupleQueryResultHandlerException;
	
	public static class QueryBuilder implements SPARQLQueryBuilderIfc {

		private String orderByLang = null;
		private boolean addInScheme = true;

		/**
		 * 
		 * @param orderByLang	2-letter ISO-code of a language, or null to build a query without ordering.
		 * @param addInScheme	true to add ?collection skos:inScheme ?scheme condition.
		 */
		public QueryBuilder(String orderByLang, boolean addInScheme) {
			this.orderByLang = orderByLang;
			this.addInScheme = addInScheme;
		}

		@Override
		public String getSPARQL() {
			String sparql = "" +
					"SELECT ?scheme ?collection"+"\n" +
					"WHERE {"+"\n" +
					"	?collection a <"+SKOS.COLLECTION+"> ."+
					((this.addInScheme)?"	?collection <"+SKOS.IN_SCHEME+"> ?scheme ."+"\n":"") +
					"	FILTER NOT EXISTS { ?parentCollection <"+SKOS.MEMBER+"> ?collection . }"+"\n" +
					((this.orderByLang != null)?
					"	OPTIONAL { ?collection <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(lang(?prefLabel) = '"+this.orderByLang+"')}"+"\n" +
					"}" +
					" ORDER BY ?prefLabel"
					:
					"}");
					
					return sparql;
		}		
	}
	
}
