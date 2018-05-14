package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;

/**
 * Return the list of all languages used on pref, alt and hidden labels in a thesaurus
 * 
 * @author Thomas Francart
 */
public abstract class GetLanguagesHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param conceptURI URI of the concept for which we want the broaders (optionnaly null to get all pairs [?concept;?broader]
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetLanguagesHelper() {
		super(
				new SimpleSparqlOperation(new QuerySupplier())
		);
	}

	/**
	 * Process the bindings and calls <code>handleLang</code> with each solution
	 */
	@Override
	public void handleSolution(BindingSet binding) throws TupleQueryResultHandlerException {
		Literal lang = (Literal)binding.getValue("lang");
		this.handleLang(lang);
	}

	/**
	 * Called for each language found
	 * 
	 * @param lang	a language to handle
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleLang(Literal lang)
	throws TupleQueryResultHandlerException;

	/**
	 * Builds a SPARQL Query that fetch the language information of pref, alt and hidden labels
	 * 
	 * @author Thomas Francart
	 */
	public static class QuerySupplier implements Supplier<String> {

		public QuerySupplier() { }

		@Override
		public String get() {
			String sparql = "" +
					"SELECT DISTINCT (lang(?label) AS ?lang)"+"\n" +
					"WHERE {"+"\n" +
					"	?concept a <"+SKOS.CONCEPT+"> . " +
					"	?concept ?labelProperty ?label ." +
					"	FILTER (" +
					"		?labelProperty = <"+SKOS.PREF_LABEL+">" +
					"		||" +
					"		?labelProperty = <"+SKOS.ALT_LABEL+">" +
					"		||" +
					"		?labelProperty = <"+SKOS.HIDDEN_LABEL+">" +
					"	)"+
					"}";

			return sparql;
		}		
	}
	
	public static void main(String...strings) throws Exception {
		Repository r = RepositoryBuilder.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"C-1-pref\"@fr; skos:altLabel \"A-1-alt\"@en ." +
				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-pref\"@fr ." +
				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:hiddenLabel \"D-3-pref\"@de ."
		);
		
		try(RepositoryConnection c = r.getConnection()) {
			Perform.on(c).select(new GetLanguagesHelper() {
				@Override
				protected void handleLang(Literal lang)
				throws TupleQueryResultHandlerException {
					System.out.println(lang.stringValue());
				}
			});
		}
	}

}
