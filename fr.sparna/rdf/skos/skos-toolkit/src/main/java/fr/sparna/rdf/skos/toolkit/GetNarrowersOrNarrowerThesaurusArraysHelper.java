package fr.sparna.rdf.skos.toolkit;

import java.net.URI;
import java.util.HashMap;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;


/**
 * Return the list of all narrower concepts and narrower thesaurus arrays of the given Concept. A narrower thesaurus array of concept X is
 * a skos:Collection that has skos:members concepts that are all direct narrowers of X.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetNarrowersOrNarrowerThesaurusArraysHelper extends SelectSparqlHelperBase implements SelectSparqlHelperIfc {

	/**
	 * @param conceptURI URI of the concept for which we want the narrower thesaurus arrays
	 * @param orderByLang a 2-letters ISO-code of a language to order the list on the labels of this language,
	 * or null to disable ordering. 
	 */
	public GetNarrowersOrNarrowerThesaurusArraysHelper(final URI conceptURI, String orderByLang) {
		super(
				new QueryBuilder(orderByLang),
				new HashMap<String, Object>() {{
					put("concept", conceptURI);
				}}
		);
	}
	
	/**
	 * Process the bindings and calls <code>handleNarrower</code> with each tuple [concept;narrower]
	 */
	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("concept");
		Resource narrower = (Resource)binding.getValue("narrower");
		this.handleNarrower(concept, narrower);
	}
	
	/**
	 * Called for each tuple [concept;narrower], potentially ordered by the label
	 * of the array in a given language
	 * 
	 * @param concept	URI of a concept
	 * @param narrower	URI of a narrower array
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleNarrower(Resource concept, Resource narrower)
	throws TupleQueryResultHandlerException;
	
	/**
	 * Builds a SPARQL Query that fetch the narrower <code>?narrower</code> of a <code>?concept</code> variable.
	 * 
	 * Optionally orders the result on the label of the <code>?array</code>s in a given language
	 * 
	 * @author Thomas Francart
	 */
	public static class QueryBuilder implements SparqlQueryBuilderIfc {

		private String orderByLang = null;		

		/**
		 * @param orderByLang an 2-letter ISO-code of a language, or null to build a query without ordering.
		 */
		public QueryBuilder(String orderByLang) {
			this.orderByLang = orderByLang;
		}

		@Override
		public String getSPARQL() {
			String sparql = "" +
			"SELECT DISTINCT ?concept ?narrower"+"\n" +
			"WHERE {"+"\n" +
			" { "+"\n" +
			"  { "+"\n" +
			// alternative that selects ThesaurusArrays
			"	?concept <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?narrowerConcept . "+"\n" +
			"	?narrower <"+SKOS.MEMBER+"> ?narrowerConcept . " + "\n" +
			"	FILTER NOT EXISTS { "+"\n" +
			"     ?narrower <"+SKOS.MEMBER+"> ?other ." + "\n" +
			"     FILTER NOT EXISTS {" + "\n" +
			"	    ?concept <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?other . "+"\n" +
			"     } " + "\n" +
			"   } " + "\n" +
			"  } " + "\n" +
			"  UNION " + "\n" +
			// alternative that selects narrower concepts NOT IN a ThesaurusArray
			"  { "+"\n" +
			"	?concept <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?narrower . "+"\n" +
			"   FILTER NOT EXISTS { "+"\n" +
			"	  ?collection <"+SKOS.MEMBER+"> ?narrower . " + "\n" +
			"	  FILTER NOT EXISTS { "+"\n" +
			"       ?collection <"+SKOS.MEMBER+"> ?other ." + "\n" +
			"       FILTER NOT EXISTS {" + "\n" +
			"	      ?concept <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?other . "+"\n" +
			"       } " + "\n" +
			"     } " + "\n" +
			"   }"+"\n" +
			"  } " + "\n" + // end second part of the UNION
			" } " + "\n" + // end wrapper around UNION
			((this.orderByLang != null)?
			"	OPTIONAL { ?array <"+SKOS.PREF_LABEL+"> ?prefLabel . FILTER(langMatches(lang(?prefLabel), '"+this.orderByLang+"'))}"+"\n" +
			"}" +
			"ORDER BY ?prefLabel"
			:
			"}");
			
			return sparql;
		}		
	}
	
}
