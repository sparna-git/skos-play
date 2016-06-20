package fr.sparna.rdf.skos.toolkit;

import java.net.URI;
import java.util.HashMap;

import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;


/**
 * Return the broader concepts of the member of a given Collection. Used to determine if the Collection is a ThesaurusArray
 * or a MT.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetBroadersOfMembersOfCollection extends SelectSparqlHelperBase implements SelectSparqlHelperIfc {

	/**
	 * @param collectionUri URI of the collection for which we want to get the broader of the members
	 */
	public GetBroadersOfMembersOfCollection(final URI collectionUri) {
		super(
				new QueryBuilder(),
				new HashMap<String, Object>() {{
					put("collection", collectionUri);
				}}
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
	 * Builds a SPARQL Query that fetch the brodaer <code>?concepts</code> of a <code>?collection</code> variable.
	 * 
	 * @author Thomas Francart
	 */
	public static class QueryBuilder implements SparqlQueryBuilderIfc {

		public QueryBuilder() {
		}

		@Override
		public String getSPARQL() {
			String sparql = "" +
			"SELECT DISTINCT ?broader"+"\n" +
			"WHERE {"+"\n" +
			"	?broader <"+SKOS.NARROWER+">|^<"+SKOS.BROADER+"> ?member . "+"\n" +
			"	?collection <"+SKOS.MEMBER+"> ?member . " + "\n" +
			"}";
			
			return sparql;
		}		
	}
	
}
