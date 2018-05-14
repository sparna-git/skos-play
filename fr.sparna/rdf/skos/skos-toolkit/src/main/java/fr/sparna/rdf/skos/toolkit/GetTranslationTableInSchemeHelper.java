package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;

/**
 * Queries for the prefLabels of concepts in a given concept scheme (or in
 * the entire repository), in two given language.
 * Results are _not_ordered and should be ordered with a Collator.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetTranslationTableInSchemeHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param lang1				a 2-letters ISO-code of a language to read labels in.
	 * @param lang2				a 2-letters ISO-code of a language to read labels in.
	 * @param conceptSchemeURI 	the URI of the concept scheme to read labels from (can be null to read all labels)
	 */
	public GetTranslationTableInSchemeHelper(String lang1, String lang2, final IRI conceptSchemeIri) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(lang1, lang2, conceptSchemeIri))
				.withBinding(
						(conceptSchemeIri != null)
						?new SimpleBinding("scheme", conceptSchemeIri)
						:null
				)
		);
	}
	
	/**
	 * Same as this(lang1, lang2, null)
	 * @param lang1				2-letter ISO-code of a language to select labels in
	 * @param lang2				2-letter ISO-code of a language to select labels in
	 */
	public GetTranslationTableInSchemeHelper(String lang1, String lang2) {
		this(lang1, lang2, null);
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("concept");
		Literal label1 = (Literal)binding.getValue("label1");
		Literal label2 = (Literal)binding.getValue("label2");
		
		this.handleTranslation(concept, label1, label2);
	}
	
	protected abstract void handleTranslation(Resource concept, Literal label1, Literal label2)
	throws TupleQueryResultHandlerException;
	
	public static class QuerySupplier implements Supplier<String> {

		private String lang1 = null;
		private String lang2 = null;
		private IRI conceptScheme = null;

		/**
		 * @param lang1				2-letter ISO-code of a language to select labels in
		 * @param lang2				2-letter ISO-code of a language to select labels in
		 * @param conceptScheme		optionnal URI of a concept scheme to select labels in
		 */
		public QuerySupplier(String lang1, String lang2, IRI conceptScheme) {
			this.lang1 = lang1;
			this.lang2 = lang2;
			this.conceptScheme = conceptScheme;
		}
		
		/**
		 * Same as this(lang1, lang2, null)
		 * @param lang1				2-letter ISO-code of a language to select labels in
		 * @param lang2				2-letter ISO-code of a language to select labels in
		 */
		public QuerySupplier(String lang1, String lang2) {
			this(lang1, lang2, null);
		}

		@Override
		public String get() {
			String sparql = "" +
					"SELECT ?concept ?label1 ?label2 "+"\n" +
					"WHERE {"+"\n" +
					"	?concept a <"+SKOS.CONCEPT+"> ."+"\n" +
					((this.conceptScheme != null)?"?concept <"+SKOS.IN_SCHEME+"> ?scheme . ":"")+"\n" +
					" OPTIONAL { ?concept <"+SKOS.PREF_LABEL+"> ?label1 FILTER(langMatches(lang(?label1), '"+this.lang1+"')) }"+"\n" +
					" OPTIONAL { ?concept <"+SKOS.PREF_LABEL+"> ?label2 FILTER(langMatches(lang(?label2), '"+this.lang2+"')) }"+"\n" +
					"}";
					return sparql;
		}		
	}
	
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"A-1-fr\"@fr; skos:prefLabel \"A-1-en\"@en ." +
				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-fr\"@fr ."
		);
		GetTranslationTableInSchemeHelper helper = new GetTranslationTableInSchemeHelper(
				"fr",
				"en",
				SimpleValueFactory.getInstance().createIRI("http://www.test.fr/skos/_scheme")
		) {

			@Override
			protected void handleTranslation(
					Resource concept,
					Literal label1,
					Literal label2
			) throws TupleQueryResultHandlerException {
				System.out.println(concept.stringValue()+" : "+(label1 != null ? label1.stringValue() : "")+" | "+(label2 != null ? label2.stringValue() : ""));
			}
			
		};
		try(RepositoryConnection c = r.getConnection()) {
			Perform.on(c).select(helper);
		}
	}
	
}
