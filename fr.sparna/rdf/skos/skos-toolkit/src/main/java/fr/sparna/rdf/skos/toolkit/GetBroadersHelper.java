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
 * Return the list of all broader concepts of a given concept, optionally ordered by their label in a given language.
 * Note : a concept may have multiple broaders in the case of poly-hierarchy.
 * <p>The conceptURI can be null, in which case this helper will return all pairs [?concept;?broader].
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetBroadersHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param conceptURI URI of the concept for which we want the broaders (optionnaly null to get all pairs [?concept;?broader]
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetBroadersHelper(final IRI conceptIri, String orderByLang) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(orderByLang))
				.withBinding(
						(conceptIri != null)
						?new SimpleBinding("concept", conceptIri)
						:null
				)
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
					"SELECT DISTINCT ?concept ?broader"+"\n" +
					"WHERE {"+"\n" +
					"	?concept <"+SKOS.BROADER+">|^<"+SKOS.NARROWER+"> ?broader "+"\n" +
					((this.orderByLang != null)?
							"	OPTIONAL { ?broader <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
							"}" +
							"ORDER BY ?prefLabel"
							:
							"}");

			return sparql;
		}		
	}

}
