package fr.sparna.rdf.skos.toolkit;

import java.net.URI;
import java.util.HashMap;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

/**
 * Queries for alignements in a given concept scheme (or in
 * the entire repository).
 * Results are _not_ordered.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetAlignmentsInSchemeHelper extends SelectSparqlHelperBase {

	/**
	 * @param conceptSchemeURI 	the URI of the concept scheme to read labels from (can be null to read all labels)
	 */
	public GetAlignmentsInSchemeHelper(final URI conceptSchemeURI) {
		super(
				new QueryBuilder(conceptSchemeURI),
				new HashMap<String, Object>() {{
					// is a concept scheme URI was given, bind it to a URI
					if(conceptSchemeURI != null) {
						put("scheme", conceptSchemeURI);
					}
				}}
		);
	}
	
	/**
	 * Same as this(null)
	 */
	public GetAlignmentsInSchemeHelper() {
		this(null);
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept1 = (Resource)binding.getValue("concept1");
		Resource alignementType = (Resource)binding.getValue("align");
		Resource concept2 = (Resource)binding.getValue("concept2");
		
		this.handleAlignment(concept1, alignementType, concept2);
	}
	
	
	protected abstract void handleAlignment(Resource concept, Resource alignementType, Resource targetConcept)
	throws TupleQueryResultHandlerException;
	
	
	public static class QueryBuilder implements SparqlQueryBuilderIfc {

		private URI conceptScheme = null;

		/**
		 * @param conceptScheme		optionnal URI of a concept scheme to select labels in
		 */
		public QueryBuilder(URI conceptScheme) {
			this.conceptScheme = conceptScheme;
		}
		
		/**
		 * Same as this(null)
		 */
		public QueryBuilder() {
			this(null);
		}

		@Override
		public String getSPARQL() {
			String sparql = "" +
					"SELECT DISTINCT ?concept1 ?align ?concept2 "+"\n" +
					"WHERE {"+"\n" +
					"	?concept1 a <"+SKOS.CONCEPT+"> ."+"\n" +
					((this.conceptScheme != null)?"?concept1 <"+SKOS.IN_SCHEME+"> ?scheme . ":"")+"\n" +
					" ?concept1 ?align ?concept2 . "+"\n" +
					" VALUES ?align { <"+SKOS.EXACT_MATCH+"> <"+SKOS.CLOSE_MATCH+"> <"+SKOS.RELATED_MATCH+"> <"+SKOS.BROAD_MATCH+"> <"+SKOS.NARROW_MATCH+"> }"+"\n" +
					"}";
					return sparql;
		}		
	}
	
	public static void main(String... args) throws Exception {
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
//		Repository r = RepositoryBuilder.fromRdf(
//				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
//				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
//				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:exactMatch <http://eurovoc.europa.eu/1>; skos:closeMatch <http://eurovoc.europa.eu/10119> ." +
//				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-fr\"@fr ."
//		);
		
		Repository r = RepositoryBuilder.fromString("/home/thomas/workspace/skosplay/test-alignements");
		
		GetAlignmentsInSchemeHelper helper = new GetAlignmentsInSchemeHelper(
				null
		) {

			@Override
			protected void handleAlignment(
					Resource concept,
					Resource alignementType,
					Resource targetConcept)
					throws TupleQueryResultHandlerException {
				System.out.println(concept+" | "+alignementType+" | "+targetConcept);
			}

			
		};
		Perform.on(r).select(helper);
	}
	
}
