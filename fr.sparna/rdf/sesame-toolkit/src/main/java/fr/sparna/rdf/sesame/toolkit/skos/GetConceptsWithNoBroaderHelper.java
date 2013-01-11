package fr.sparna.rdf.sesame.toolkit.skos;

import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;


/**
 * Return the list of all concepts without a broader, optionally ordered by their label in a given language.
 * 
 * @author Thomas Francart
 */
public abstract class GetConceptsWithNoBroaderHelper extends SelectSPARQLHelperBase {
	
	/**
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetConceptsWithNoBroaderHelper(String orderByLang) {
		super(new QueryBuilder(orderByLang));
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource noBroader = (Resource)binding.getValue("concept");
		this.handleConceptWithNoBroader(noBroader);
	}
	
	protected abstract void handleConceptWithNoBroader(Resource noBroader)
	throws TupleQueryResultHandlerException;
	
	public static class QueryBuilder implements SPARQLQueryBuilderIfc {

		private String orderByLang = null;

		public QueryBuilder(String orderByLang) {
			super();
			this.orderByLang = orderByLang;
		}

		/**
		 * TODO : test also on narrowers
		 */
		@Override
		public String getSPARQL() {
			String sparql = "" +
					"SELECT ?concept"+"\n" +
					"WHERE {"+"\n" +
					"	?concept a <"+SKOS.CONCEPT+"> ."+"\n" +
					"	OPTIONAL { ?concept <"+SKOS.BROADER+"> ?broader }"+"\n"+
					"	FILTER(!bound(?broader))"+"\n"+
					(
							(this.orderByLang != null)?
							"	OPTIONAL { ?concept <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(lang(?prefLabel) = '"+this.orderByLang+"')}"+"\n" +
							"}" +
							" ORDER BY ?prefLabel"
							:
							"}"
					);
					
			return sparql;
		}

	}
	
}
