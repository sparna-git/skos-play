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
 * Queries for the concepts with a given label, optionnally in a given lang and optionnaly in a given concept scheme
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetConceptsWithLabelHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param lang				a 2-letters ISO-code of the language to read labels in.
	 * @param conceptSchemeURI 	the URI of the concept scheme to read concepts from (can be null to read all concepts)
	 */
	public GetConceptsWithLabelHelper(String key, String lang, final IRI conceptSchemeIri) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(key, lang, conceptSchemeIri))
				.withBinding(
						(conceptSchemeIri != null)
						?new SimpleBinding("scheme", conceptSchemeIri)
						:null
				)
		);
	}
	
	/**
	 * Same as this(key, lang, null)
	 */
	public GetConceptsWithLabelHelper(String key, String lang) {
		this(key, lang, null);
	}

	/**
	 * Same as this(key, null, null)
	 */
	public GetConceptsWithLabelHelper(String key) {
		this(key, null, null);
	}
	
	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("concept");
		Literal prefLabel = (Literal)binding.getValue("prefLabel");
		this.handleConcept(concept, prefLabel);
	}
	
	protected abstract void handleConcept(Resource concept, Literal prefLabel)
	throws TupleQueryResultHandlerException;
	
	public static class QuerySupplier implements Supplier<String> {

		private String lang = null;
		private String key = null;
		private IRI conceptScheme = null;

		/**
		 * @param key 				key to search for
		 * @param lang 				2-letter ISO-code of a language to select labels in
		 * @param conceptScheme		optionnal URI of a concept scheme to select labels in
		 */
		public QuerySupplier(String key, String lang, IRI conceptScheme) {
			this.key = key;
			this.lang = lang;
			this.conceptScheme = conceptScheme;
		}
		
		/**
		 * Same as this(key, null, null)
		 */
		public QuerySupplier(String key) {
			this(key, null, null);
		}

		@Override
		public String get() {
			String SKOS_PATH = "<"+SKOS.PREF_LABEL+">|<"+SKOS.ALT_LABEL+">|<"+SKOS.HIDDEN_LABEL+">";
			String sparql = "" +
					"SELECT DISTINCT ?concept ?prefLabel"+"\n" +
					"WHERE {"+"\n" +
					"	?concept a <"+SKOS.CONCEPT+"> ." +
					((this.conceptScheme != null)?"?concept <"+SKOS.IN_SCHEME+"> ?scheme . ":"") +
					"   ?concept " + SKOS_PATH + " ?label . "+
					"   FILTER(LCASE(STR(?label)) = LCASE('"+key.replace("'", "\\'")+"'))"+
					((this.lang != null)?"   FILTER(langMatches(lang(?label), '"+this.lang+"'))":"")+
					"   ?concept <"+SKOS.PREF_LABEL+"> ?prefLabel . " +
					((this.lang != null)?"   FILTER(langMatches(lang(?prefLabel), '"+this.lang+"'))":"")+
					"}";
					
					return sparql;
		}		
	}
	
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"C-1-pref\"@fr; skos:altLabel \"A-1-alt\"@fr ." +
				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-pref\"@fr ." +
				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:prefLabel \"D-3 l'apostrophe\"@fr ."
		);
		GetConceptsWithLabelHelper helper = new GetConceptsWithLabelHelper(
				"l'apostrophe",
				null,
				SimpleValueFactory.getInstance().createIRI("http://www.test.fr/skos/_scheme")
		) {
			
			@Override
			protected void handleConcept(
					Resource concept,
					Literal label
			) throws TupleQueryResultHandlerException {
				System.out.println(concept.stringValue()+" : "+label.getLabel());
			}
		};
		try(RepositoryConnection c = r.getConnection()) {
			Perform.on(c).select(helper);
		}
	}
	
}
