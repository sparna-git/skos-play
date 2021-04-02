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
 * Return the broader concepts of the member of a given Collection. Used to determine if the Collection is a ThesaurusArray
 * or a MT.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetBroadersOfMembersOfCollection extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param collectionUri URI of the collection for which we want to get the broader of the members
	 */
	public GetBroadersOfMembersOfCollection(final IRI collectionIri) {	
		super(
				new SimpleSparqlOperation(
						new QuerySupplier()						
				).withBinding(new SimpleBinding("collection", collectionIri))
		);
	}
	
	/**
	 * Process the bindings and calls <code>handleBroaderOfMemberOfCollection</code> with each tuple [concept]
	 */
	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("broader");
		this.handleBroaderOfMemberOfCollection(concept);
	}
	
	/**
	 * Called for each tuple [concept]
	 * 
	 * @param concept	URI of a concept
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleBroaderOfMemberOfCollection(Resource concept)
	throws TupleQueryResultHandlerException;
	
	/**
	 * Builds a SPARQL Query that fetch the broader <code>?concepts</code> of a <code>?collection</code> variable.
	 * 
	 * @author Thomas Francart
	 */
	public static class QuerySupplier implements Supplier<String> {

		public QuerySupplier() {
		}

		@Override
		public String get() {
			String sparql = "" +
			"SELECT DISTINCT ?broader"+"\n" +
			"WHERE {"+"\n" +
			"  { "+"\n" +
			"	?collection <"+SKOS.MEMBER+"> ?member . " + "\n" +
			"	?broader <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?member . "+"\n" +
			"  } UNION { "+"\n" +
			"	?collection <"+SKOS.MEMBER+"> ?member . " + "\n" +
			"	FILTER NOT EXISTS { ?broader <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?member . } "+"\n" +
			"   BIND(<https://skos-play.sparna.fr/fakeRoot> AS ?broader) "+"\n" +
			"  } "+"\n" +
			"}";
			
			return sparql;
		}		
	}
	
}
