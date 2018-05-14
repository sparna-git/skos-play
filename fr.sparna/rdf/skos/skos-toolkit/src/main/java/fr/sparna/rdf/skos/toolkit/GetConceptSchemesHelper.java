package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;

/**
 * Returns the list of all concept schemes, optionally ordered by their label in a given language.
 * 
 * @author Thomas Francart
 */
public abstract class GetConceptSchemesHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetConceptSchemesHelper(String orderByLang) {
		super(new SimpleSparqlOperation(new QuerySupplier(orderByLang)));
	}

	@Override
	public void handleSolution(BindingSet binding) throws TupleQueryResultHandlerException {
		Resource conceptScheme = (Resource)binding.getValue("conceptScheme");
		this.handleConceptScheme(conceptScheme);
	}

	/**
	 * To be implemented by subclasses.
	 * @param conceptScheme
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleConceptScheme(Resource conceptScheme) throws TupleQueryResultHandlerException;

	
	public static class QuerySupplier implements Supplier<String> {

		private String orderByLang = null;

		public QuerySupplier(String orderByLang) {
			super();
			this.orderByLang = orderByLang;
		}

		@Override
		public String get() {
			String sparql = "" +
					"SELECT ?conceptScheme"+"\n" +
					"WHERE {"+"\n" +
					"	?conceptScheme a <"+SKOS.CONCEPT_SCHEME+"> ."+"\n" +
					(
							(this.orderByLang != null)?
									"	OPTIONAL { ?conceptScheme <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
									"}" +
									" ORDER BY ?prefLabel"
									:
									"}"
					);

			return sparql;
		}

	}

}
