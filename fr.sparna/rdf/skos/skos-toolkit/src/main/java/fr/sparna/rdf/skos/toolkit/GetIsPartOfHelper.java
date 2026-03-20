package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;

import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;

/**
 * Return the links of all (concept schemes) related to this one with a dct:isPartOf
 * <p>The conceptURI can be null, in which case this helper will return all pairs [?conceptScheme;?isPartOf].
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetIsPartOfHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param conceptSchemeIri URI of the concept scheme for which we want the isPartOf (optionnaly null to get all pairs [?collection;?topmember]
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetIsPartOfHelper(final IRI conceptSchemeIri, String orderByLang) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(orderByLang))
				.withBinding(
						// si concept est null la variable ne sera pas bindee et la query
						// remontera TOUS les couple scheme/isPartOf
						(conceptSchemeIri != null)
						?new SimpleBinding("conceptScheme", conceptSchemeIri)
						:null
				)	
		);
	}

	/**
	 * Process the bindings and calls <code>handleMember</code> with each tuple [conceptScheme;topisPartOfmember]
	 */
	@Override
	public void handleSolution(BindingSet binding) throws TupleQueryResultHandlerException {
		Resource collection = (Resource)binding.getValue("conceptScheme");
		Resource member = (Resource)binding.getValue("isPartOf");
		this.handleIsPartOf(collection, member);
	}

	/**
	 * Called for each tuple [conceptScheme;isPartOf], potentially ordered by the label
	 * of the member in a given language
	 * 
	 * @param collection	URI of a conceptScheme
	 * @param member		URI of a (concept scheme) pointing with an isPartOf
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleIsPartOf(Resource conceptScheme, Resource isPartOf)
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
					"SELECT DISTINCT ?conceptScheme ?isPartOf"+"\n" +
					"WHERE {"+"\n" +
					"	?conceptScheme ^<"+DCTERMS.IS_PART_OF+"> ?isPartOf ."+"\n" +
					((this.orderByLang != null)?
					"	OPTIONAL { ?isPartOf <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
					"}" +"\n" +
					"ORDER BY ?prefLabel"+"\n"
					:
					"}");
			return sparql;
		}		
	}

}
