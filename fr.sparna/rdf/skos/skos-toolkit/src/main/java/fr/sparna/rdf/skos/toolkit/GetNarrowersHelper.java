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
 * Return the list of all narrower concepts of a given concept, optionally ordered by their label in a given language.
 * <p>The conceptURI can be null, in which case this helper will return all the pair [?concept;?narrower].
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetNarrowersHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param conceptURI URI of the concept for which we want the narrowers
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetNarrowersHelper(final IRI conceptIri, String orderByLang) {
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
	 * Process the bindings and calls <code>handleNarrowerConcept</code> with each tuple [concept;narrower]
	 */
	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("concept");
		Resource narrower = (Resource)binding.getValue("narrower");
		this.handleNarrowerConcept(concept, narrower);
	}
	
	/**
	 * Called for each tuple [concept;narrower], potentially ordered by the label
	 * of the narrower in a given language
	 * 
	 * @param concept	URI of a concept
	 * @param narrower	URI of a narrower of this concept
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleNarrowerConcept(Resource concept, Resource narrower)
	throws TupleQueryResultHandlerException;
	
	/**
	 * Builds a SPARQL Query that fetch the <code>?narrower</code> concepts of a <code>?concept</code> variable.
	 * If the <code>?concept</code> variable is bound to a URI, this will fetch the <code>?narrower</code>s of this <code>?concept</code>.
	 * If it is not bound, this will fetch all the tuples [concept;narrower] in the graph.
	 * 
	 * Optionally orders the result on the label of the <code>?narrower</code>s in a given language
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
			"SELECT DISTINCT ?concept ?narrower"+"\n" +
			"WHERE {"+"\n" +
			"	?concept <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?narrower "+"\n" +
			((this.orderByLang != null)?
			"	OPTIONAL { ?narrower <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
			"}" +
			"ORDER BY ?prefLabel"
			:
			"}");
			
			return sparql;
		}		
	}
	
}
