package fr.sparna.rdf.sesame.toolkit.skos;

import java.net.URI;
import java.util.HashMap;

import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

/**
 * Return the list of all broader concepts of a given concept, optionally ordered by their label in a given language.
 * Note : a concept may have multiple broaders in the case of poly-hierarchy.
 * <p>The conceptURI can be null, in which case this helper will return all pairs [?concept;?broader].
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetBroadersHelper extends SelectSPARQLHelperBase {

	/**
	 * @param conceptURI URI of the concept for which we want the broaders (optionnaly null to get all pairs [?concept;?broader]
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetBroadersHelper(final URI conceptURI, String orderByLang) {
		super(
				new QueryBuilder(orderByLang),
				new HashMap<String, Object>() {{
					// si concept est null la variable ne sera pas bindee et la query
					// remontera TOUS les narrowers de tous les concepts
					if(conceptURI != null) {
						put("concept", conceptURI);
					}
				}}
		);
	}

	/**
	 * Process the bindings and calls <code>handleBroaderConcept</code> with each tuple [concept;broader]
	 */
	@Override
	public void handleSolution(BindingSet binding) throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("concept");
		Resource broader = (Resource)binding.getValue("broader");
		this.handleBroaderConcept(concept, broader);
	}

	/**
	 * Called for each tuple [concept;broader], potentially ordered by the label
	 * of the broader in a given language
	 * 
	 * @param concept	URI of a concept
	 * @param broader	URI of a broader of this concept
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleBroaderConcept(Resource concept, Resource broader)
	throws TupleQueryResultHandlerException;

	/**
	 * Builds a SPARQL Query that fetch the <code>?broader</code> concepts of a <code>?concept</code> variable.
	 * If the <code>?concept</code> variable is bound to a URI, this will fetch the <code>?broader</code>s of this <code>?concept</code>.
	 * If it is not bound, this will fetch all the tuples [concept;broader] in the graph.
	 * 
	 * Optionally orders the result on the label of the <code>?broader</code>s in a given language
	 * 
	 * @author Thomas Francart
	 */
	public static class QueryBuilder implements SPARQLQueryBuilderIfc {

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
					"SELECT DISTINCT ?concept ?broader"+"\n" +
					"WHERE {"+"\n" +
					"	?concept <"+SKOS.BROADER+">|^<"+SKOS.NARROWER+"> ?broader "+"\n" +
					((this.orderByLang != null)?
							"	OPTIONAL { ?broader <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(lang(?prefLabel) = '"+this.orderByLang+"')}"+"\n" +
							"}" +
							"ORDER BY ?prefLabel"
							:
							"}");

			return sparql;
		}		
	}

}
