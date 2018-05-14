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
 * Queries for the top collections of a given concept scheme, optionally ordered by their skos:prefLabels
 * in a given language.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetTopCollectionsHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param conceptSchemeURI 	the URI of the concept scheme to read top collections from, or null to read all top collections
	 * @param orderByLang		a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering.
	 */
	public GetTopCollectionsHelper(final IRI conceptSchemeIri, String orderByLang) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(orderByLang, conceptSchemeIri != null))
				.withBinding(
						(conceptSchemeIri != null)
						?new SimpleBinding("scheme", conceptSchemeIri)
						:null
				)
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
	
	public static class QuerySupplier implements Supplier<String> {

		private String orderByLang = null;
		private boolean addInScheme = true;

		/**
		 * 
		 * @param orderByLang	2-letter ISO-code of a language, or null to build a query without ordering.
		 * @param addInScheme	true to add ?collection skos:inScheme ?scheme condition.
		 */
		public QuerySupplier(String orderByLang, boolean addInScheme) {
			this.orderByLang = orderByLang;
			this.addInScheme = addInScheme;
		}

		@Override
		public String get() {
			String sparql = "" +
					"SELECT ?scheme ?collection"+"\n" +
					"WHERE {"+"\n" +
					"	?collection a <"+SKOS.COLLECTION+"> ."+
					((this.addInScheme)?"	?collection <"+SKOS.IN_SCHEME+"> ?scheme ."+"\n":"") +
					"	FILTER NOT EXISTS { ?parentCollection <"+SKOS.MEMBER+"> ?collection . }"+"\n" +
					((this.orderByLang != null)?
					"	OPTIONAL { ?collection <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
					"}" +
					" ORDER BY ?prefLabel"
					:
					"}");
					
					return sparql;
		}		
	}
	
}
