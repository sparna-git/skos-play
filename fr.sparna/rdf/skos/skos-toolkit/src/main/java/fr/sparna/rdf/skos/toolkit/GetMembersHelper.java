package fr.sparna.rdf.skos.toolkit;

import java.net.URI;
import java.util.HashMap;

import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

/**
 * Return the list of all members of a given Collection, optionally ordered by their label in a given language.
 * <p>The conceptURI can be null, in which case this helper will return all pairs [?collection;?member].
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetMembersHelper extends SelectSparqlHelperBase {

	/**
	 * @param collectionURI URI of the collection for which we want the memebers (optionnaly null to get all pairs [?collection;?member]
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetMembersHelper(final URI collectionURI, String orderByLang) {
		super(
				new QueryBuilder(orderByLang),
				new HashMap<String, Object>() {{
					// si concept est null la variable ne sera pas bindee et la query
					// remontera TOUS les members de toutes les collections
					if(collectionURI != null) {
						put("collection", collectionURI);
					}
				}}
		);
	}

	/**
	 * Process the bindings and calls <code>handleBroaderConcept</code> with each tuple [concept;broader]
	 */
	@Override
	public void handleSolution(BindingSet binding) throws TupleQueryResultHandlerException {
		Resource collection = (Resource)binding.getValue("collection");
		Resource member = (Resource)binding.getValue("member");
		this.handleMember(collection, member);
	}

	/**
	 * Called for each tuple [collection;member], potentially ordered by the label
	 * of the member in a given language
	 * 
	 * @param collection	URI of a collection
	 * @param member		URI of a member of that collection
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleMember(Resource collection, Resource member)
	throws TupleQueryResultHandlerException;

	/**
	 * Builds a SPARQL Query that fetch the <code>?member</code> of a <code>?collection</code> variable.
	 * If the <code>?collection</code> variable is bound to a URI, this will fetch the <code>?member</code>s of this <code>?collection</code>.
	 * If it is not bound, this will fetch all the tuples [collection;member] in the graph.
	 * 
	 * Optionally orders the result on the label of the <code>?member</code>s in a given language
	 * 
	 * @author Thomas Francart
	 */
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
					"SELECT DISTINCT ?collection ?member"+"\n" +
					" WHERE {"+"\n" +
					"	?collection <"+SKOS.MEMBER+"> ?member ."+"\n" +
					((this.orderByLang != null)?
					"	OPTIONAL { ?member <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(lang(?prefLabel) = '"+this.orderByLang+"')}"+"\n" +
					"}" +"\n" +
					"ORDER BY ?prefLabel"+"\n"
					:
					"}");
			return sparql;
		}		
	}

}
