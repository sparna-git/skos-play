package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;

import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;

/**
 * Queries for alignements in a given concept scheme (or in
 * the entire repository).
 * Results are _not_ ordered.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetAlignmentsInSchemeHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param conceptSchemeURI 	the IRI of the concept scheme to read alignments from (can be null to read all alignments)
	 */
	public GetAlignmentsInSchemeHelper(final IRI conceptSchemeIRI) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(conceptSchemeIRI))
				.withBinding(
						(conceptSchemeIRI != null)
						?new SimpleBinding("scheme", conceptSchemeIRI)
						:null
				)
		);
	}	
	
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
	
	
	
	public static class QuerySupplier implements Supplier<String> {

		private boolean includeInScheme = false;

		/**
		 * @param includeInScheme		indicate if we want to generate a skos:inScheme criteria
		 */
		public QuerySupplier(boolean includeInScheme) {
			this.includeInScheme = includeInScheme;
		}
		
		/**
		 * @param conceptSchemeIRI		if non null, will insert the extra skos:inScheme criteria
		 */
		public QuerySupplier(IRI conceptSchemeIRI) {
			this(conceptSchemeIRI != null);
		}

		@Override
		public String get() {
			String sparql = "" +
					"SELECT DISTINCT ?concept1 ?align ?concept2 "+"\n" +
					"WHERE {"+"\n" +
					"	?concept1 a <"+SKOS.CONCEPT+"> ."+"\n" +
					((this.includeInScheme)?"?concept1 <"+SKOS.IN_SCHEME+"> ?scheme . ":"")+"\n" +
					" ?concept1 ?align ?concept2 . "+"\n" +
					" VALUES ?align { <"+SKOS.EXACT_MATCH+"> <"+SKOS.CLOSE_MATCH+"> <"+SKOS.RELATED_MATCH+"> <"+SKOS.BROAD_MATCH+"> <"+SKOS.NARROW_MATCH+"> }"+"\n" +
					"}";
					return sparql;
		}		
	}
	
}
