package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;

import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;

/**
 * Return the list of all members of a given Collection that have no broaders, optionally ordered by their label in a given language.
 * <p>The conceptURI can be null, in which case this helper will return all pairs [?collection;?topmember].
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetTopMembersHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param collectionURI URI of the collection for which we want the top members (optionnaly null to get all pairs [?collection;?topmember]
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetTopMembersHelper(final IRI collectionIri, String orderByLang) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(orderByLang))
				.withBinding(
						// si concept est null la variable ne sera pas bindee et la query
						// remontera TOUS les members de toutes les collections
						(collectionIri != null)
						?new SimpleBinding("collection", collectionIri)
						:null
				)	
		);
	}

	/**
	 * Process the bindings and calls <code>handleMember</code> with each tuple [concept;topmember]
	 */
	@Override
	public void handleSolution(BindingSet binding) throws TupleQueryResultHandlerException {
		Resource collection = (Resource)binding.getValue("collection");
		Resource member = (Resource)binding.getValue("topmember");
		this.handleMember(collection, member);
	}

	/**
	 * Called for each tuple [collection;topmember], potentially ordered by the label
	 * of the member in a given language
	 * 
	 * @param collection	URI of a collection
	 * @param member		URI of a member of that collection
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleMember(Resource collection, Resource topmember)
	throws TupleQueryResultHandlerException;

	/**
	 * Builds a SPARQL Query that fetch the <code>?topmember</code> of a <code>?collection</code> variable.
	 * If the <code>?collection</code> variable is bound to a URI, this will fetch the <code>?topmember</code>s of this <code>?collection</code>.
	 * If it is not bound, this will fetch all the tuples [collection;topmember] in the graph.
	 * 
	 * Optionally orders the result on the label of the <code>?topmember</code>s in a given language
	 * 
	 * @author Thomas Francart
	 */
	public static class QuerySupplier implements Supplier<String> {

		private String orderByLang = null;		

		/**
		 * @param orderByLang an 2-letter ISO-code of a language, or null to build a query without ordering.
		 */
		public QuerySupplier(String orderByLang) {
			this.orderByLang = orderByLang;
		}

		@Override
		public String get() {
			String sparql = "" +
					"SELECT DISTINCT ?collection ?topmember"+"\n" +
					"WHERE {"+"\n" +
					"	?collection <"+SKOS.MEMBER+"> ?topmember ."+"\n" +
					"	FILTER NOT EXISTS { ?topmember <"+SKOS.BROADER+">|^<"+SKOS.NARROWER+"> ?broader }"+"\n" +
					((this.orderByLang != null)?
					"	OPTIONAL { ?topmember <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
					"}" +"\n" +
					"ORDER BY ?prefLabel"+"\n"
					:
					"}");
			return sparql;
		}		
	}

}
