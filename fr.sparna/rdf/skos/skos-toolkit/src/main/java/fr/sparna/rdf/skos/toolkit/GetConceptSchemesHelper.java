package fr.sparna.rdf.skos.toolkit;

import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

/**
 * Returns the list of all concept schemes, optionally ordered by their label in a given language.
 * 
 * @author Thomas Francart
 */
public abstract class GetConceptSchemesHelper extends SelectSparqlHelperBase {

	/**
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetConceptSchemesHelper(String orderByLang) {
		super(new QueryBuilder(orderByLang));
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

	
	public static class QueryBuilder implements SparqlQueryBuilderIfc {

		private String orderByLang = null;

		public QueryBuilder(String orderByLang) {
			super();
			this.orderByLang = orderByLang;
		}

		@Override
		public String getSPARQL() {
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
