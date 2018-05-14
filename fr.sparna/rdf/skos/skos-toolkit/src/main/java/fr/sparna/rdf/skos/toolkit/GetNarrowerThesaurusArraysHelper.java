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
 * Return the list of all narrower thesaurus arrays of the given Concept. A narrower thesaurus array of concept X is
 * a skos:Collection that has skos:members concepts that are all direct narrowers of X.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetNarrowerThesaurusArraysHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param conceptURI URI of the concept for which we want the narrower thesaurus arrays
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetNarrowerThesaurusArraysHelper(final IRI conceptIri, String orderByLang) {
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
	 * Process the bindings and calls <code>handleNarrowerThesaurusArray</code> with each tuple [concept;array]
	 */
	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("concept");
		Resource array = (Resource)binding.getValue("array");
		this.handleNarrowerThesaurusArray(concept, array);
	}
	
	/**
	 * Called for each tuple [concept;array], potentially ordered by the label
	 * of the array in a given language
	 * 
	 * @param concept	URI of a concept
	 * @param narrower	URI of a narrower array
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleNarrowerThesaurusArray(Resource concept, Resource array)
	throws TupleQueryResultHandlerException;
	
	/**
	 * Builds a SPARQL Query that fetch the narrower <code>?array</code> of a <code>?concept</code> variable.
	 * 
	 * Optionally orders the result on the label of the <code>?array</code>s in a given language
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
			"SELECT DISTINCT ?concept ?array"+"\n" +
			"WHERE {"+"\n" +
			"	?concept <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?narrower . "+"\n" +
			"	?array <"+SKOS.MEMBER+"> ?narrower . " + "\n" +
			"	FILTER NOT EXISTS { "+"\n" +
			"     ?array <"+SKOS.MEMBER+"> ?other ." + "\n" +
			"     FILTER NOT EXISTS {" + "\n" +
			"	    ?concept <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?other . "+"\n" +
			"     } " + "\n" +
			"   } "+
			((this.orderByLang != null)?
			"	OPTIONAL { ?array <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
			"}" +
			"ORDER BY ?prefLabel"
			:
			"}");
			
			return sparql;
		}		
	}
	
}
