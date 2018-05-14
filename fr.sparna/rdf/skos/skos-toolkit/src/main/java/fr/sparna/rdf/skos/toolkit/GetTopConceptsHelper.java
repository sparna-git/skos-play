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
 * Queries for the top concepts of a given concept scheme, optionally ordered by their skos:prefLabels
 * in a given language.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetTopConceptsHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param conceptSchemeURI 	the URI of the concept scheme to read top concepts from
	 * @param orderByLang		a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering.
	 */
	public GetTopConceptsHelper(final IRI conceptSchemeIri, String orderByLang) {
		super(				
				new SimpleSparqlOperation(new QuerySupplier(orderByLang))
				.withBinding(
						(conceptSchemeIri != null)
						?new SimpleBinding("scheme", conceptSchemeIri)
						:null
				)	
		);
	}

	/**
	 * @param orderByLang		a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering.
	 */
	public GetTopConceptsHelper(String orderByLang) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(orderByLang))		
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
