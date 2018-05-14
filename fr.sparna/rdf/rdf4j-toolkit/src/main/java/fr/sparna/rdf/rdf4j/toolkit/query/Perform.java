package fr.sparna.rdf.rdf4j.toolkit.query;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.Operation;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.query.impl.SimpleDataset;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.handler.ReadSingleIntegerHandler;
import fr.sparna.rdf.rdf4j.toolkit.handler.ReadSingleValueHandler;
import fr.sparna.rdf.rdf4j.toolkit.handler.ReadStringListHandler;
import fr.sparna.rdf.rdf4j.toolkit.handler.ReadValueListHandler;

/**
 * Performs queries on a RepositoryConnection, with 3 variants
 * <ul>
 * 		<li>the query string and a TupleQueryResultHandler</li>
 * 		<li>a SparqlOperationIfc and a TupleQueryResultHandler</li>
 * 		<li>a TupleQueryHelperIfc</li>
 * </ul>
 * 
 * @author Thomas Francart
 *
 */
public class Perform {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private boolean includeInferred = true;
	
	private Dataset dataset;
	
	private RepositoryConnection connection;
	
	public Perform(RepositoryConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * Convenience static constructor that returns a new instance of <code>Perform</code>
	 * 
	 *  <code>TupleQueryResult result = Perform.on(connection).selectResult(myQuery)</code>
	 * 
	 * @param repository The repository on which to execute the queries
	 * @return a new instance of Perform
	 */
	public static Perform on(RepositoryConnection connection) {
		return new Perform(connection);
	}
	
	public Perform insertIn(IRI defaultInsertGraph) {
		SimpleDataset d = new SimpleDataset();
		d.setDefaultInsertGraph(defaultInsertGraph);
		this.setDataset(d);
		return this;
	}
	
	public boolean ping() {
		PingSparqlHelper ping = new PingSparqlHelper();
		log.trace("Pinging...");
		this.select(ping);
		log.trace("Ping !");
		return ping.isPinged(); 
	}
	
	/**
	 * Convenience method that directly execute a COUNT query, or another query with a single line of result and a single binding,
	 * and directly returns the results, allowing it to be called in <code>if(Perform.on(repository).count(...) > xxx)</code>
	 * 
	 * @param query
	 * @return
	 * @throws SparqlPerformException
	 */
	public int count(SparqlOperationIfc query) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		ReadSingleIntegerHandler handler = new fr.sparna.rdf.rdf4j.toolkit.handler.ReadSingleIntegerHandler();
		this.select(query, handler);
		return handler.getResultIntValue();
	}
	
	/**
	 * Same As count(new SimpleSparqlOperation(query))
	 * 
	 * @param query
	 * @return
	 * @throws TupleQueryResultHandlerException
	 * @throws QueryEvaluationException
	 * @throws RepositoryException
	 */
	public int count(String query) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		return count(new SimpleSparqlOperation(query));
	}	

	/**
	 * Convenience method that directly execute a query with a single line of result and a single binding,
	 * and directly returns the results, allowing it to be called in <code>Value v = Perform.on(repository).read(...)</code>
	 * 
	 * @param query
	 * @return
	 * @throws SparqlPerformException
	 */
	public Value read(SparqlOperationIfc query) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		ReadSingleValueHandler handler = new ReadSingleValueHandler();
		this.select(query, handler);
		return handler.getResult();
	}
	
	/**
	 * Same as read(new SimpleSparqlOperation(query))
	 * @param query
	 * @return
	 * @throws TupleQueryResultHandlerException
	 * @throws QueryEvaluationException
	 * @throws RepositoryException
	 */
	public Value read(String query) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		return read(new SimpleSparqlOperation(query));
	}
	
	/**
	 * Convenience method that directly execute a query with a single binding and possibly multiple lines of result,
	 * and directly returns the results, allowing it to be called in <code>List<Value> v = Perform.on(repository).readList(...)</code>
	 * 
	 * @param query
	 * @return
	 * @throws SparqlPerformException
	 */
	public List<Value> readList(SparqlOperationIfc query) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		ReadValueListHandler handler = new ReadValueListHandler();
		this.select(query, handler);
		return handler.getResult();
	}
	
	/**
	 * Same as readList(new SimpleSparqlOperation(query))
	 * @param query
	 * @return
	 * @throws TupleQueryResultHandlerException
	 * @throws QueryEvaluationException
	 * @throws RepositoryException
	 */
	public List<Value> readList(String query) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		return readList(new SimpleSparqlOperation(query));
	}
	
	/**
	 * Convenience method that directly execute a query with a single binding and possibly multiple lines of result,
	 * and directly returns the results as a String List, allowing it to be called in <code>List<String> v = Perform.on(repository).readStringList(...)</code>
	 * 
	 * @param query
	 * @return
	 * @throws SparqlPerformException
	 */
	public List<String> readStringList(SparqlOperationIfc query) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		ReadStringListHandler handler = new ReadStringListHandler();
		this.select(query, handler);
		return handler.getResult();
	}
	
	/**
	 * Same as readStringList(new SimpleSparqlOperation(query))
	 * 
	 * @param query
	 * @return
	 * @throws TupleQueryResultHandlerException
	 * @throws QueryEvaluationException
	 * @throws RepositoryException
	 */
	public List<String> readStringList(String query) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		return readStringList(new SimpleSparqlOperation(query));
	}
	
	/**
	 * Executes the SPARQL SELECT query returned by the helper, and pass the helper to the <code>evaluate</code> method
	 */
	public void select(TupleQueryHelperIfc helper) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		select(helper.getOperation(), helper.getHandler());
	}
	
	/**
	 * Executes the SPARQL SELECT query returned by the helper, and pass the helper to the <code>evaluate</code> method
	 */
	public void select(SparqlOperationIfc query, TupleQueryResultHandler handler) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {
		log.trace("Executing SPARQL SELECT :\n"+query);
		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, query.getSPARQL());
		// sets bindings, inferred statement flags and datasets
		tupleQuery = (TupleQuery)preprocessOperation(tupleQuery, query.getBindingSet(), (query.isIncludeInferred() != null)?query.isIncludeInferred():this.includeInferred, query.getDataset());
		
		// on execute la query
		tupleQuery.evaluate(handler);
	}
	
	/**
	 * Executes the SPARQL SELECT query given as a String, and pass the helper to the <code>evaluate</code> method
	 */
	public void select(String query, TupleQueryResultHandler handler) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {			
		log.trace("Executing SPARQL SELECT :\n"+query);
		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
		// sets bindings, inferred statement flags and datasets
		tupleQuery = (TupleQuery)preprocessOperation(tupleQuery, null, this.includeInferred, this.getDataset());
		
		// on execute la query
		tupleQuery.evaluate(handler);
	}
	
	/**
	 * Executes the SPARQL CONSTRUCT/GRAPH query returned by the helper, and pass the helper to the <code>evaluate</code> method
	 */
	public void graph(GraphQueryHelperIfc helper) 
	throws QueryEvaluationException, RepositoryException {
		graph(helper.getOperation(), helper.getHandler());
	}
	
	/**
	 * Executes the SPARQL CONSTRUCT/DESCRIBE query returned by the helper, and pass the helper to the <code>evaluate</code> method
	 */
	public void graph(SparqlOperationIfc query, RDFHandler handler) 
	throws QueryEvaluationException, RepositoryException {
		log.trace("Executing SPARQL GRAPH :\n"+query);
		GraphQuery graphQuery = this.connection.prepareGraphQuery(QueryLanguage.SPARQL, query.getSPARQL());
		// sets bindings, inferred statement flags and datasets
		graphQuery = (GraphQuery)preprocessOperation(graphQuery, query.getBindingSet(), (query.isIncludeInferred() != null)?query.isIncludeInferred():this.includeInferred, query.getDataset());
		
		// on execute la query
		graphQuery.evaluate(handler);
	}
	
	/**
	 * Executes the SPARQL CONSTRUCT/DESCRIBE given as a String, and pass the helper to the <code>evaluate</code> method
	 */
	public void graph(String query, RDFHandler handler) 
	throws QueryEvaluationException, RepositoryException {			
		log.trace("Executing SPARQL GRAPH :\n"+query);
		GraphQuery graphQuery = this.connection.prepareGraphQuery(QueryLanguage.SPARQL, query);
		// sets bindings, inferred statement flags and datasets
		graphQuery = (GraphQuery)preprocessOperation(graphQuery, null, this.includeInferred, this.getDataset());
		
		// on execute la query
		graphQuery.evaluate(handler);
	}
	
	/**
	 * Executes a SPARQL ASK query 
	 */
	public boolean ask(SparqlOperationIfc operation) {
		log.trace("Executing SPARQL ASK :\n"+operation.getSPARQL());

		String query = operation.getSPARQL();
		BooleanQuery booleanQuery = this.connection.prepareBooleanQuery(QueryLanguage.SPARQL, query);
		// sets bindings, inferred statement flags and datasets
		booleanQuery = (BooleanQuery)preprocessOperation(booleanQuery, operation.getBindingSet(), (operation.isIncludeInferred() != null)?operation.isIncludeInferred():this.includeInferred, operation.getDataset());
			
		// on execute la query
		return booleanQuery.evaluate();
	}
	
	/**
	 * Executes a SPARQL ASK query 
	 */
	public boolean ask(String query) {
		return ask(new SimpleSparqlOperation(query));
	}
	
	/**
	 * Executes the update returned by the helper. Nothing is returned from the execution.
	 * 
	 * @param helper
	 * @throws SparqlPerformException
	 */
	public void update(SparqlOperationIfc updateOperation) 
	throws RepositoryException, UpdateExecutionException {
		log.trace("Executing SPARQL UPDATE :\n"+updateOperation);
		Update update = this.connection.prepareUpdate(QueryLanguage.SPARQL, updateOperation.getSPARQL());
		// sets bindings, inferred statement flags and datasets
		update = (Update)preprocessOperation(update, updateOperation.getBindingSet(), (updateOperation.isIncludeInferred() != null)?updateOperation.isIncludeInferred():this.includeInferred, updateOperation.getDataset());
		
		// on execute l'update
		update.execute();
		log.trace("UPDATE executed sucessfully");
	}
	
	public void update(String updateString) 
	throws TupleQueryResultHandlerException, QueryEvaluationException, RepositoryException {			
		log.trace("Executing SPARQL UPDATE :\n"+updateString);
		Update update = this.connection.prepareUpdate(QueryLanguage.SPARQL, updateString);
		// sets bindings, inferred statement flags and datasets
		update = (Update)preprocessOperation(update, null, this.includeInferred, this.getDataset());
		
		// on execute l'update
		update.execute();
		log.trace("UPDATE executed sucessfully");
	}
	
	
	private static Operation preprocessOperation(
			final Operation o,
			BindingSet bindingSet,
			Boolean includeInferred,
			Dataset d
			
	) {				
		// on positionne les bindings s'il y en a
		if(bindingSet != null) {
			for (Binding binding : bindingSet) {
				o.setBinding(binding.getName(), binding.getValue());
			}
		}

		// on inclut les inferred statements si demandé
		o.setIncludeInferred(includeInferred);
		
		// on ajoute les datasets si besoin
		if(d != null) {
			o.setDataset(d);
		}
		
		return o;
	}
	
	public boolean isIncludeInferred() {
		return includeInferred;
	}

	/**
	 * Sets whether the queries executed will include the inferred statements, if nothing is set at the helper level.
	 * If something is set at the helper level, this value on the executer will be ignored. Defaults to true. 
	 * 
	 * @param includeInferred
	 */
	public void setIncludeInferred(boolean includeInferred) {
		this.includeInferred = includeInferred;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
}
