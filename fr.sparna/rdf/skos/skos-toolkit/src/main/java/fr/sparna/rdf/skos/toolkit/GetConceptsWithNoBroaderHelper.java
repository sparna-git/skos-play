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
 * Return the list of all concepts without a broader, optionally ordered by their label in a given language.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetConceptsWithNoBroaderHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {
	
	/**
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetConceptsWithNoBroaderHelper(String orderByLang) {
		super(new SimpleSparqlOperation(new QuerySupplier(orderByLang, false)));
	}
	
	public GetConceptsWithNoBroaderHelper(String orderByLang, final IRI conceptSchemeIri) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(orderByLang, ((conceptSchemeIri != null)?true:false)))
				.withBinding(
						(conceptSchemeIri != null)
						?new SimpleBinding("additionalCriteriaObject", conceptSchemeIri)
						:null
				)
		);
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource noBroader = (Resource)binding.getValue("concept");
		this.handleConceptWithNoBroader(noBroader);
	}
	
	protected abstract void handleConceptWithNoBroader(Resource noBroader)
	throws TupleQueryResultHandlerException;
	
	public static class QuerySupplier implements Supplier<String> {

		private String orderByLang = null;
		private boolean additionalCriteria = false;

		public QuerySupplier(String orderByLang, boolean additionalCriteria) {
			super();
			this.orderByLang = orderByLang;
			this.additionalCriteria = additionalCriteria;
		}

		/**
		 * TODO : test also on narrowers
		 */
		@Override
		public String get() {
			String sparql = "" +
					"SELECT DISTINCT ?concept"+"\n" +
					"WHERE {"+"\n" +
					"	?concept a <"+SKOS.CONCEPT+"> ."+"\n" +
					"	"+((this.additionalCriteria)?"   ?concept ?additionalCriteriaPredicate ?additionalCriteriaObject .":"")+"\n" +
					"	OPTIONAL { ?concept <"+SKOS.BROADER+">|^<"+SKOS.NARROWER+"> ?broader }"+"\n"+
					"	FILTER(!bound(?broader))"+"\n"+
					(
							(this.orderByLang != null)?
							"	OPTIONAL { ?concept <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
							"}" +
							" ORDER BY ?prefLabel"
							:
							"}"
					);
					
			return sparql;
		}

	}
	
}
