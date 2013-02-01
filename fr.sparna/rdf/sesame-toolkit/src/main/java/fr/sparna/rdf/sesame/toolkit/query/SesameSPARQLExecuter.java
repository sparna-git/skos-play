package fr.sparna.rdf.sesame.toolkit.query;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Operation;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
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

/**
 * Executes SPARQL helpers onto a Sesame repository 
 * (built with a {@link fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryIfc}). The helpers knows
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
 *	SesameSPARQLExecuter.newExecuter(repository).executeSelect(helper);
 * }
 * </pre>
 * <p>To execute a construct helper :
 * <pre>
 * {@code
 *	Repository repository = ...;
 *	ConstructSPARQLHelperIfc helper = ...;
 *	SesameSPARQLExecuter.newExecuter(repository).executeConstruct(helper);
 * }
 * </pre>
 * 
 * Note that the default behavior of <code>isIncludeInferred</code> method is true, meaning the SPARQL
 * will be executed against the inferred RDF graph. You should set this to false explicitely
 * at the executer or the helper level if you need to make a query against the original RDF data.
 * 
 * @author Thomas Francart
 */
public class SesameSPARQLExecuter {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private Repository repository;
	
	private boolean includeInferred = true;
	
	private Set<URI> defaultGraphs = null;
	
	private Set<URI> namedGraphs = null;
	
	private URI defaultInsertGraph = null;
	
	private Set<URI> defaultRemoveGraphs = null;
	
	public SesameSPARQLExecuter(Repository repository) {
		this(repository, true);
	}
	
	public SesameSPARQLExecuter(Repository repository, boolean includeInferred) {
		super();
		this.repository = repository;
		this.includeInferred = includeInferred;
	}
	
	/**
	 * Convenience static constructor that returns a new instance of <code>SesameSPARQLExecuter</code>
	 * and allows to write
	 * 
	 *  <code>SesameSPARQLExecuter.newExecuter(repository).executeSelect(myHelper)</code>
	 * 
	 * @param repository The repository on which to execute the queries
	 * @return a new instance of SesameSPARQLExecuter
	 */
	public static SesameSPARQLExecuter newExecuter(Repository repository) {
		return new SesameSPARQLExecuter(repository);
	}
	
	/**
	 * A convenience method that sets the default graph, the default insert graph,
	 * and the default remove graph to the provided graph URI. This is equivalent to the
	 * following calls :
	 * <code>
	 *  executer.setDefaultGraphs(Collections.singleton(graph));
	 *	executer.setDefaultInsertGraph(graph);
	 *	executer.setDefaultRemoveGraphs(Collections.singleton(graph));
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
	public void executeSelect(SelectSPARQLHelperIfc helper) 
	throws SPARQLExecutionException {
		try {
			if(repository == null) {
				throw new SPARQLExecutionException("Repository is null. If it comes from a RepositoryProviderIfc, have you called the init() method on the RepositoryProvider ?");
			}
			
			RepositoryConnection connection = this.repository.getConnection();
			TupleQuery tupleQuery;
			try {
				String query = helper.getQuery().getSPARQL();
				log.trace("Executing SPARQL SELECT :\n"+query);
				tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
				
				// on positionne les bindings s'il y en a
				processBindings(tupleQuery, helper.getQuery().getBindings());
				
				// on inclut les inferred statements si demandé
				tupleQuery.setIncludeInferred((helper.getQuery().isIncludeInferred() != null)?helper.getQuery().isIncludeInferred():this.includeInferred);
				
				// on ajoute les datasets si besoin
				tupleQuery = (TupleQuery)processDataset(
						tupleQuery,
						((helper.getQuery().getDefaultGraphs() != null)?helper.getQuery().getDefaultGraphs():this.defaultGraphs),
						((helper.getQuery().getNamedGraphs() != null)?helper.getQuery().getNamedGraphs():this.namedGraphs),
						this.defaultInsertGraph,
						this.defaultRemoveGraphs
				);
				
				// on execute la query
				tupleQuery.evaluate(helper.getHandler());
			} catch (MalformedQueryException e) {
				throw new SPARQLExecutionException(e);
			} finally {
				SesameSPARQLExecuter.closeQuietly(connection);
			}

		} catch (RepositoryException e) {
			throw new SPARQLExecutionException(e);
		} catch (QueryEvaluationException e) {
			throw new SPARQLExecutionException(e);
		} catch (TupleQueryResultHandlerException e) {
			throw new SPARQLExecutionException(e);
		}
	}

	/**
	 * Executes the SPARQL CONSTRUCT query returned by the helper, and pass the helper to the <code>evaluate</code> method
	 */
	public void executeConstruct(ConstructSPARQLHelperIfc helper) 
	throws SPARQLExecutionException {
		try {
			if(repository == null) {
				throw new SPARQLExecutionException("Repository is null. If it comes from a RepositoryProviderIfc, have you called the init() method on the RepositoryProvider ?");
			}
			
			RepositoryConnection connection = this.repository.getConnection();
			GraphQuery graphQuery;
			try {
				String query = helper.getQuery().getSPARQL();
				log.trace("Executing SPARQL CONSTRUCT :\n"+query);
				graphQuery = connection.prepareGraphQuery(QueryLanguage.SPARQL, query);
				
				// on positionne les bindings s'il y en a
				processBindings(graphQuery, helper.getQuery().getBindings());
				
				// on inclut les inferred statements si demandé
				graphQuery.setIncludeInferred((helper.getQuery().isIncludeInferred() != null)?helper.getQuery().isIncludeInferred():this.includeInferred);
				
				// on ajoute les datasets si besoin
				graphQuery = (GraphQuery)processDataset(
						graphQuery,
						((helper.getQuery().getDefaultGraphs() != null)?helper.getQuery().getDefaultGraphs():this.defaultGraphs),
						((helper.getQuery().getNamedGraphs() != null)?helper.getQuery().getNamedGraphs():this.namedGraphs),
						this.defaultInsertGraph,
						this.defaultRemoveGraphs
				);
				
				// on execute la query
				graphQuery.evaluate(helper.getHandler());
			} catch (MalformedQueryException e) {
				throw new SPARQLExecutionException(e);
			} finally {
				SesameSPARQLExecuter.closeQuietly(connection);
			}

		} catch (RepositoryException e) {
			throw new SPARQLExecutionException(e);
		} catch (QueryEvaluationException e) {
			throw new SPARQLExecutionException(e);
		} catch (RDFHandlerException e) {
			throw new SPARQLExecutionException(e);
		}
	}
	
	/**
	 * Executes the SPARQL ASK query returned by the helper, and pass on the result to the helper.getWriter() method.
	 * The boolean result is also returned by the method directly.
	 * <p>If helper.getWriter() is null, the result is simply returned by that method.
	 */
	public boolean executeAsk(BooleanSPARQLHelperIfc helper) 
	throws SPARQLExecutionException {
		try {
			if(repository == null) {
				throw new SPARQLExecutionException("Repository is null. If it comes from a RepositoryProviderIfc, have you called the init() method on the RepositoryProvider ?");
			}
			
			RepositoryConnection connection = this.repository.getConnection();
			BooleanQuery booleanQuery;
			try {
				String query = helper.getQuery().getSPARQL();
				log.trace("Executing SPARQL ASK :\n"+query);
				booleanQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, query);
				
				// on positionne les bindings s'il y en a
				processBindings(booleanQuery, helper.getQuery().getBindings());
				
				// on inclut les inferred statements si demandé
				booleanQuery.setIncludeInferred((helper.getQuery().isIncludeInferred() != null)?helper.getQuery().isIncludeInferred():this.includeInferred);
				
				// on ajoute les datasets si besoin
				booleanQuery = (BooleanQuery)processDataset(
						booleanQuery,
						((helper.getQuery().getDefaultGraphs() != null)?helper.getQuery().getDefaultGraphs():this.defaultGraphs),
						((helper.getQuery().getNamedGraphs() != null)?helper.getQuery().getNamedGraphs():this.namedGraphs),
						this.defaultInsertGraph,
						this.defaultRemoveGraphs
				);
				
				// on execute la query
				boolean result = booleanQuery.evaluate();
				// write result to the writer
				if(helper.getWriter() != null) {
					helper.getWriter().write(result);
				}
				// return result
				return result;
				
			} catch (MalformedQueryException e) {
				throw new SPARQLExecutionException(e);
			} catch(IOException ioe) {
				throw new SPARQLExecutionException(ioe);
			} finally {
				SesameSPARQLExecuter.closeQuietly(connection);
			}

		} catch (RepositoryException e) {
			throw new SPARQLExecutionException(e);
		} catch (QueryEvaluationException e) {
			throw new SPARQLExecutionException(e);
		}
	}
	
	/**
	 * Convenience method that directly executes a SPARQLQueryIfc containing an ASK query without an associated writer,
	 * and returns the result as a boolean.
	 * 
	 * @param query
	 * @return
	 * @throws SPARQLExecutionException
	 */
	public boolean executeAsk(SPARQLQueryIfc query) 
	throws SPARQLExecutionException {
		// passing a null writer will cause the executeAsk(BooleanSPARQLHelperIfc helper) to not serialize the result
		return executeAsk(new BooleanSPARQLHelper(query, null));
	}
	
	/**
	 * Executes the update returned by the helper. Nothing is returned from the execution.
	 * 
	 * @param helper
	 * @throws SPARQLExecutionException
	 */
	public void executeUpdate(SPARQLUpdateIfc helper) 
	throws SPARQLExecutionException {
		try {
			if(repository == null) {
				throw new SPARQLExecutionException("Repository is null. If it comes from a RepositoryProviderIfc, have you called the init() method on the RepositoryProvider ?");
			}
			
			RepositoryConnection connection = this.repository.getConnection();
			Update update;
			try {
				String updateString = helper.getSPARQL();
				log.trace("Executing SPARQL UPDATE :\n"+updateString);
				update = connection.prepareUpdate(QueryLanguage.SPARQL, updateString);
				
				// on positionne les bindings s'il y en a
				processBindings(update, helper.getBindings());
				
				// on inclut les inferred statements si demandé
				update.setIncludeInferred((helper.isIncludeInferred() != null)?helper.isIncludeInferred():this.includeInferred);
				
				// on ajoute les datasets si besoin
				update = (Update)processDataset(
						update,
						((helper.getDefaultGraphs() != null)?helper.getDefaultGraphs():this.defaultGraphs),
						((helper.getNamedGraphs() != null)?helper.getNamedGraphs():this.namedGraphs),
						this.defaultInsertGraph,
						this.defaultRemoveGraphs
				);
				
				// on execute l'update
				update.execute();
				log.trace("UPDATE executed sucessfully");
			} catch (MalformedQueryException e) {
				throw new SPARQLExecutionException(e);
			} finally {
				SesameSPARQLExecuter.closeQuietly(connection);
			}

		} catch (RepositoryException e) {
			throw new SPARQLExecutionException(e);
		} catch (UpdateExecutionException e) {
			throw new SPARQLExecutionException(e);
		}
	}
	
	private static void closeQuietly(RepositoryConnection connection) {
		if(connection != null) {
			try {
				connection.close();
			} catch (RepositoryException ignore) {ignore.printStackTrace();}
		}
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
