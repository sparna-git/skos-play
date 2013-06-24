package fr.sparna.rdf.sesame.toolkit.query;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Operation;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.handler.ReadSingleIntegerHandler;
import fr.sparna.rdf.sesame.toolkit.handler.ReadSingleValueHandler;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryConnectionDoorman;

/**
 * Executes SPARQL helpers onto a Sesame repository 
 * (built with a {@link fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder}
 * or a a {@link fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryIfc}). The helpers knows
 * how to return a {@link fr.sparna.rdf.sesame.toolkit.query.SPARQLQueryIfc} and how to process the results
 * of the query with a <code>TupleQueryResultHandler</code> (for SELECT queries), or <code>RDFHandler</code> (for CONSTRUCT queries).
 * This can execute SPARQL SELECT, CONSTRUCT, UPDATE, ASK, INSERT or DELETE.
 * <p />Note that by default, inferred statements WILL be included in the queries.
 * 
 * <p>Usage to execute a select helper :
 * <pre>
 * {@code
 *	Repository repository = ...;
 *	SelectSPARQLHelperIfc helper = ...;
 *	Perform.on(repository).select(helper);
 * }
 * </pre>
 * <p>To execute a construct helper :
 * <pre>
 * {@code
 *	Repository repository = ...;
 *	ConstructSPARQLHelperIfc helper = ...;
 *	Perform.on(repository).construct(helper);
 * }
 * </pre>
 * 
 * Note that the default behavior of <code>isIncludeInferred</code> method is true, meaning the SPARQL
 * will be executed against the inferred RDF graph. You should set this to false explicitely
 * on the Perform instance or on the SPARQLHelper if you need to make a query against the original RDF data.
 * 
 * @author Thomas Francart
 */
public class Perform {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private boolean includeInferred = true;
	
	private Set<URI> defaultGraphs = null;
	
	private Set<URI> namedGraphs = null;
	
	private URI defaultInsertGraph = null;
	
	private Set<URI> defaultRemoveGraphs = null;
	
	private Repository repository;
	
	private RepositoryConnection connection;
	
	public Perform(Repository repository) {
		this(repository, true);
	}
	
	public Perform(Repository repository, boolean includeInferred) {
		super();
		this.repository = repository;
		this.includeInferred = includeInferred;
	}
	
	public Perform(RepositoryConnection connection) {
		this(connection, true);
	}
	
	public Perform(RepositoryConnection connection, boolean includeInferred) {
		super();
		this.connection = connection;
		this.includeInferred = includeInferred;
	}
	
	/**
	 * Convenience static constructor that returns a new instance of <code>Perform</code>
	 * and allows to write
	 * 
	 *  <code>Perform.on(repository).select(myHelper)</code>
	 * 
	 * @param repository The repository on which to execute the queries
	 * @return a new instance of Perform
	 */
	public static Perform on(Repository repository) {
		return new Perform(repository);
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
	
	/**
	 * A convenience method that sets the default graph, the default insert graph,
	 * and the default remove graph to the provided graph URI. This is equivalent to the
	 * following calls :
	 * <code>
	 *  perform.setDefaultGraphs(Collections.singleton(graph));
	 *	perform.setDefaultInsertGraph(graph);
	 *	perform.setDefaultRemoveGraphs(Collections.singleton(graph));
	 * </code>
	 * 
	 * @param graph the URI of the graph to set
	 */
	public void setWorkingGraph(URI graph) {
		this.setDefaultGraphs(Collections.singleton(graph));
		this.setDefaultInsertGraph(graph);
		this.setDefaultRemoveGraphs(Collections.singleton(graph));
	}
	
	/**
	 * Executes the SPARQL SELECT query returned by the helper, and pass the helper to the <code>evaluate</code> method
	 */
	public void select(SelectSPARQLHelperIfc helper) 
	throws SPARQLPerformException {
		
		boolean useOpenedConnection = (this.connection != null);
		
		try {
			RepositoryConnection localConnection = (useOpenedConnection)?this.connection:this.repository.getConnection();
			
			TupleQuery tupleQuery;
			try {
				String query = helper.getQuery().getSPARQL();
				log.trace("Executing SPARQL SELECT :\n"+helper.getQuery().toString());
				tupleQuery = localConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
				// sets bindings, inferred statement flags and datasets
				tupleQuery = (TupleQuery)preprocessOperation(tupleQuery, helper.getQuery());
				
				// on execute la query
				tupleQuery.evaluate(helper.getHandler());
			} catch (MalformedQueryException e) {
				throw new SPARQLPerformException(e);
			} finally {
				if(!useOpenedConnection) { RepositoryConnectionDoorman.closeQuietly(localConnection); }
			}

		} catch (RepositoryException e) {
			throw new SPARQLPerformException(e);
		} catch (QueryEvaluationException e) {
			throw new SPARQLPerformException(e);
		} catch (TupleQueryResultHandlerException e) {
			throw new SPARQLPerformException(e);
		}
	}
	
	/**
	 * Executes the SPARQL SELECT query, and returns a TupleQueryResult (that needs to be closed after that)
	 */
	public TupleQueryResult selectResult(SPARQLQuery sparqlQuery) 
	throws SPARQLPerformException {
		
		if(this.connection == null) {
			throw new SPARQLPerformException("selectResult method can only work on already opened connections");
		}
		
		try {
			RepositoryConnection localConnection = this.connection;
			TupleQuery tupleQuery;
			try {
				String query = sparqlQuery.getSPARQL();
				log.trace("Executing SPARQL SELECT :\n"+query);
				tupleQuery = localConnection.prepareTupleQuery(QueryLanguage.SPARQL, query);
				// sets bindings, inferred statement flags and datasets
				tupleQuery = (TupleQuery)preprocessOperation(tupleQuery, sparqlQuery);
				
				// on execute la query
				return tupleQuery.evaluate();
			} catch (MalformedQueryException e) {
				throw new SPARQLPerformException(e);
			}

		} catch (RepositoryException e) {
			throw new SPARQLPerformException(e);
		} catch (QueryEvaluationException e) {
			throw new SPARQLPerformException(e);
		}
	}

	/**
	 * Executes the SPARQL CONSTRUCT query returned by the helper, and pass the helper to the <code>evaluate</code> method
	 */
	public void construct(ConstructSPARQLHelperIfc helper) 
	throws SPARQLPerformException {
		
		boolean useOpenedConnection = (this.connection != null);
		
		try {
			RepositoryConnection localConnection = (useOpenedConnection)?this.connection:this.repository.getConnection();
			GraphQuery graphQuery;
			try {
				String query = helper.getQuery().getSPARQL();
				log.trace("Executing SPARQL CONSTRUCT :\n"+helper.getQuery().toString());
				graphQuery = localConnection.prepareGraphQuery(QueryLanguage.SPARQL, query);
				// sets bindings, inferred statement flags and datasets
				graphQuery = (GraphQuery)preprocessOperation(graphQuery, helper.getQuery());
				
				// on execute la query
				graphQuery.evaluate(helper.getHandler());
			} catch (MalformedQueryException e) {
				throw new SPARQLPerformException(e);
			} finally {
				if(!useOpenedConnection) { RepositoryConnectionDoorman.closeQuietly(localConnection); }
			}

		} catch (RepositoryException e) {
			throw new SPARQLPerformException(e);
		} catch (QueryEvaluationException e) {
			throw new SPARQLPerformException(e);
		} catch (RDFHandlerException e) {
			throw new SPARQLPerformException(e);
		}
	}
	
	/**
	 * Executes the SPARQL ASK query returned by the helper, and pass on the result to the helper.getWriter() method.
	 * The boolean result is also returned by the method directly.
	 * <p>If helper.getWriter() is null, the result is simply returned by that method.
	 */
	public boolean ask(BooleanSPARQLHelperIfc helper) 
	throws SPARQLPerformException {
		
		boolean useOpenedConnection = (this.connection != null);
		
		try {
			RepositoryConnection localConnection = (useOpenedConnection)?this.connection:this.repository.getConnection();
			BooleanQuery booleanQuery;
			try {
				String query = helper.getQuery().getSPARQL();
				log.trace("Executing SPARQL ASK :\n"+helper.getQuery().toString());
				booleanQuery = localConnection.prepareBooleanQuery(QueryLanguage.SPARQL, query);
				// sets bindings, inferred statement flags and datasets
				booleanQuery = (BooleanQuery)preprocessOperation(booleanQuery, helper.getQuery());
				
				// on execute la query
				boolean result = booleanQuery.evaluate();
				// write result to the writer
				if(helper.getWriter() != null) {
					helper.getWriter().write(result);
				}
				// return result
				return result;
				
			} catch (MalformedQueryException e) {
				throw new SPARQLPerformException(e);
			} catch(IOException ioe) {
				throw new SPARQLPerformException(ioe);
			} finally {
				if(!useOpenedConnection) { RepositoryConnectionDoorman.closeQuietly(localConnection); }
			}

		} catch (RepositoryException e) {
			throw new SPARQLPerformException(e);
		} catch (QueryEvaluationException e) {
			throw new SPARQLPerformException(e);
		}
	}
	
	/**
	 * Convenience method that directly executes a SPARQLQueryIfc containing an ASK query without an associated writer,
	 * and returns the result as a boolean.
	 * 
	 * @param query
	 * @return
	 * @throws SPARQLPerformException
	 */
	public boolean ask(SPARQLQueryIfc query) 
	throws SPARQLPerformException {
		// passing a null writer will cause the executeAsk(BooleanSPARQLHelperIfc helper) to not serialize the result
		return ask(new BooleanSPARQLHelper(query, null));
	}
	
	/**
	 * Convenience method that directly execute a COUNT query, or another query with a single line of result and a single binding,
	 * and directly returns the results, allowing it to be called in <code>if(Perform.on(repository).count(...) > xxx)</code>
	 * 
	 * @param query
	 * @return
	 * @throws SPARQLPerformException
	 */
	public int count(SPARQLQueryIfc query) 
	throws SPARQLPerformException {
		ReadSingleIntegerHandler handler = new ReadSingleIntegerHandler();
		this.select(new SelectSPARQLHelper(query, handler));
		return handler.getResultIntValue();
	}

	/**
	 * Convenience method that directly execute a query with a single line of result and a single binding,
	 * and directly returns the results, allowing it to be called in <code>Value v = Perform.on(repository).read(...)</code>
	 * 
	 * @param query
	 * @return
	 * @throws SPARQLPerformException
	 */
	public Value read(SPARQLQueryIfc query) 
	throws SPARQLPerformException {
		ReadSingleValueHandler handler = new ReadSingleValueHandler();
		this.select(new SelectSPARQLHelper(query, handler));
		return handler.getResult();
	}
	
	
	/**
	 * Executes the update returned by the helper. Nothing is returned from the execution.
	 * 
	 * @param helper
	 * @throws SPARQLPerformException
	 */
	public void update(SPARQLUpdateIfc helper) 
	throws SPARQLPerformException {
		
		boolean useOpenedConnection = (this.connection != null);
		
		try {
			RepositoryConnection localConnection = (useOpenedConnection)?this.connection:this.repository.getConnection();
			Update update;
			try {
				String updateString = helper.getSPARQL();
				log.trace("Executing SPARQL UPDATE :\n"+helper.toString());
				update = localConnection.prepareUpdate(QueryLanguage.SPARQL, updateString);
				// sets bindings, inferred statement flags and datasets
				update = (Update)preprocessOperation(update, helper);
				
				// on execute l'update
				update.execute();
				log.trace("UPDATE executed sucessfully");
			} catch (MalformedQueryException e) {
				throw new SPARQLPerformException(e);
			} finally {
				if(!useOpenedConnection) { RepositoryConnectionDoorman.closeQuietly(localConnection); }
			}

		} catch (RepositoryException e) {
			throw new SPARQLPerformException(e);
		} catch (UpdateExecutionException e) {
			throw new SPARQLPerformException(e);
		}
	}
	
	private Operation preprocessOperation(Operation o, SPARQLQueryIfc query) {
		// on positionne les bindings s'il y en a
		processBindings(o, query.getBindings());
		
		// on inclut les inferred statements si demandé
		o.setIncludeInferred((query.isIncludeInferred() != null)?query.isIncludeInferred():this.includeInferred);
		
		// on ajoute les datasets si besoin
		o = processDataset(
				o,
				((query.getDefaultGraphs() != null)?query.getDefaultGraphs():this.defaultGraphs),
				((query.getNamedGraphs() != null)?query.getNamedGraphs():this.namedGraphs),
				this.defaultInsertGraph,
				this.defaultRemoveGraphs
		);
		
		return o;
	}
	
	private void processBindings(
		Operation o,
		Map<String, Object> bindings
	) {
		if(bindings != null) {
			for (Map.Entry<String, Object> anEntry : bindings.entrySet()) {
				if(anEntry.getValue() instanceof org.openrdf.model.Value) {
					o.setBinding(anEntry.getKey(), (org.openrdf.model.Value)anEntry.getValue());
				} else 	if(anEntry.getValue() instanceof java.net.URI) {
					o.setBinding(anEntry.getKey(), this.repository.getValueFactory().createURI(((java.net.URI)anEntry.getValue()).toString()));
				} else 	if(anEntry.getValue() instanceof java.net.URL) {
					o.setBinding(anEntry.getKey(), this.repository.getValueFactory().createURI(((java.net.URL)anEntry.getValue()).toString()));
				} else {
					o.setBinding(anEntry.getKey(), this.repository.getValueFactory().createLiteral(anEntry.getValue().toString()));
				}
			}
		}
	}
	
	
	// an Operation is either a Query or an Update
	private Operation processDataset(
			Operation o,
			Set<URI> defaultGraphs,
			Set<URI> namedGraphs,
			URI defaultInsertGraph,
			Set<URI> defaultRemoveGraphs
	) {
		if(
				(
						namedGraphs != null
						&&
						namedGraphs.size() > 0
				)
				||
				(
						defaultGraphs != null
						&&
						defaultGraphs.size() > 0
				)
				||
					defaultInsertGraph != null
				||
				(
						defaultRemoveGraphs != null
						&&
						defaultRemoveGraphs.size() > 0
				)
		) {
			DatasetImpl dataset = new DatasetImpl();
			ValueFactory vf = this.repository.getValueFactory();
			if(
					namedGraphs != null
			) {
				for (URI uri : namedGraphs) {
					dataset.addNamedGraph(vf.createURI(uri.toString()));
				}
			}
			if(
					defaultGraphs != null
			) {
				for (URI uri : defaultGraphs) {
					dataset.addDefaultGraph(vf.createURI(uri.toString()));
				}
			}
			if(
				defaultInsertGraph != null	
			) {
				dataset.setDefaultInsertGraph(vf.createURI(defaultInsertGraph.toString()));
			}
			if(
					defaultRemoveGraphs != null	
			) {
				for (URI uri : defaultRemoveGraphs) {
					dataset.addDefaultRemoveGraph(vf.createURI(uri.toString()));
				}
			}
			o.setDataset(dataset);
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

	public Set<URI> getDefaultGraphs() {
		return defaultGraphs;
	}

	public void setDefaultGraphs(Set<URI> defaultGraphs) {
		this.defaultGraphs = defaultGraphs;
	}

	public Set<URI> getNamedGraphs() {
		return namedGraphs;
	}

	public void setNamedGraphs(Set<URI> namedGraphs) {
		this.namedGraphs = namedGraphs;
	}

	public URI getDefaultInsertGraph() {
		return defaultInsertGraph;
	}

	public void setDefaultInsertGraph(URI defaultInsertGraph) {
		this.defaultInsertGraph = defaultInsertGraph;
	}

	public Set<URI> getDefaultRemoveGraphs() {
		return defaultRemoveGraphs;
	}

	public void setDefaultRemoveGraphs(Set<URI> defaultRemoveGraphs) {
		this.defaultRemoveGraphs = defaultRemoveGraphs;
	}
	
}
