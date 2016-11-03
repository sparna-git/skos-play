package fr.sparna.rdf.sesame.jena.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openjena.jenasesame.util.Convert;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.NamespaceImpl;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.query.parser.ParsedBooleanQuery;
import org.eclipse.rdf4j.query.parser.ParsedGraphQuery;
import org.eclipse.rdf4j.query.parser.ParsedOperation;
import org.eclipse.rdf4j.query.parser.ParsedTupleQuery;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.UnknownTransactionStateException;
import org.eclipse.rdf4j.repository.base.RepositoryConnectionBase;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * The connection to a {@link JenaRepository}.
 * Does not support named graphs.
 * 
 * @author Thomas Francart
 *
 */
public class JenaRepositoryConnection extends RepositoryConnectionBase implements RepositoryConnection {

	protected JenaRepository repository;
	
	public JenaRepositoryConnection(JenaRepository repository) {
		super(repository);
		this.repository = repository;
	}

	/* BEGIN NAMESPACE-RELATED OPERATIONS */
	
	/**
	 * Calls setNsPrefixes on the underlying Model with an empty HashMap<String, String>()
	 */
	@Override
	public void clearNamespaces() throws RepositoryException {
		repository.model.setNsPrefixes(new HashMap<String, String>());
	}

	/**
	 * Create a RepositoryResult<Namespace> based on the underlying Model.getNsPrefixMap()
	 */
	@Override
	public RepositoryResult<Namespace> getNamespaces() throws RepositoryException {
		// build list of Namespace
		Collection<Namespace> namespaces = new ArrayList<Namespace>();
		// populate it
		for(Map.Entry<String, String> anEntry : repository.model.getNsPrefixMap().entrySet()) {
			namespaces.add(new NamespaceImpl(anEntry.getKey(), anEntry.getValue()));
		}
		// return RepositoryResult wrapping the list of Namespace
		return new RepositoryResult<Namespace>(
				new CollectionIteration<Namespace, RepositoryException>(namespaces)
		);
	}

	/**
	 * Calls setNsPrefix on the underlying Model with the given prefix and namespace
	 */
	@Override
	public void setNamespace(String prefix, String uri)
	throws RepositoryException {
		repository.model.setNsPrefix(prefix, uri);
	}
	
	/**
	 * Calls getNsPrefixURI on the underlying Model
	 */
	@Override
	public String getNamespace(String prefix) throws RepositoryException {
		return repository.model.getNsPrefixURI(prefix);
	}
	
	/**
	 * calls removeNsPrefix on underlying Model
	 */
	@Override
	public void removeNamespace(String prefix) throws RepositoryException {
		repository.model.removeNsPrefix(prefix);
	}
	
	/* END NAMESPACE-RELATED OPERATIONS */
	
	/* BEGIN TRANSACTION-RELATED OPERATIONS */
	
	
	
	/**
	 * Calls commit() on the underlying Model
	 */
	@Override
	public void commit() throws RepositoryException {
		repository.model.commit();
	}

	@Override
	public void begin() throws RepositoryException {
		repository.model.begin();
	}

	/**
	 * Calls abort() on the underlying Model
	 */
	@Override
	public void rollback() throws RepositoryException {
		repository.model.abort();
	}

	@Override
	public boolean isActive()
	throws UnknownTransactionStateException, RepositoryException {
		// TODO
		return false;
	}	
	
	
	/* END TRANSACTION-RELATED OPERATIONS */


	/**
	 * Since named graphs are not supported, always returns an empty RepositoryResult
	 */
	@Override
	public RepositoryResult<Resource> getContextIDs()
	throws RepositoryException {
		return new RepositoryResult<Resource>(
				new CollectionIteration<Resource, RepositoryException>(new ArrayList<Resource>())
		);
	}
	
	/**
	 * TODO : handle includeInferred ?
	 */
	@Override
	public void exportStatements(
			Resource s,
			IRI p,
			Value o,
			boolean includeInferred,
			RDFHandler handler,
			Resource... contexts
	) throws RepositoryException, RDFHandlerException {
		if(contexts != null && contexts.length > 0) {
			throw new RepositoryException("Named graphs are not supported");
		}
		
		StmtIterator it = repository.model.listStatements(
				(s == null)?null:Convert.resourceToResource(repository.model, s),
				(p == null)?null:Convert.uriToProperty(repository.model, p),
				(o == null)?null:Convert.valueToRDFNode(repository.model, o)
		);
		
		while(it.hasNext()) {
			com.hp.hpl.jena.rdf.model.Statement jenaStmt = it.next();
			handler.handleStatement(Convert.statementToSesameStatement(repository.valueFactory, jenaStmt));
		}
	}

	/**
	 * Returns a RepositoryResult built with a {@link JenaStatementIterator} populated with the result
	 * of calling Model.listsStatements on the underlying Model.
	 */
	@Override
	public RepositoryResult<Statement> getStatements(
			Resource s,
			IRI p,
			Value o,
			boolean includeInferred,
			Resource... contexts)
	throws RepositoryException {
		if(contexts != null && contexts.length > 0) {
			throw new RepositoryException("Named graphs are not supported");
		}
		
		StmtIterator it = repository.model.listStatements(
				(s == null)?null:Convert.resourceToResource(repository.model, s),
				(p == null)?null:Convert.uriToProperty(repository.model, p),
				(o == null)?null:Convert.valueToRDFNode(repository.model, o)
		);
		
		org.eclipse.rdf4j.common.iteration.CloseableIteratorIteration<Statement, RepositoryException> iteration = new org.eclipse.rdf4j.common.iteration.CloseableIteratorIteration<Statement, RepositoryException>(
				new JenaStatementIterator(it, this.getValueFactory())
		);
		
		return new RepositoryResult<Statement>(iteration);
	}

	/**
	 * Returns size() on the underlying Model
	 */
	@Override
	public long size(Resource... contexts) throws RepositoryException {
		if(contexts != null && contexts.length > 0) {
			throw new RepositoryException("Named graphs are not supported");
		}
		
		return repository.model.size();
	}

	/**
	 * Calls add(Resource r, Property p, RDFNode n) on the underlying Model
	 * 
	 * TODO : handle no commit
	 */
	@Override
	protected void addWithoutCommit(
			Resource s,
			IRI p,
			Value o,
			Resource... contexts)
	throws RepositoryException {
		if(contexts != null && contexts.length > 0) {
			throw new RepositoryException("Named graphs are not supported");
		}
		
		repository.model.add(
				Convert.resourceToResource(repository.model, s),
				Convert.uriToProperty(repository.model, p),
				Convert.valueToRDFNode(repository.model, o)
		);
	}

	/**
	 * Calls remove(Resource r, Property p, RDFNode n) on the underlying Model
	 * 
	 * TODO : handle no commit
	 */
	@Override
	protected void removeWithoutCommit(
			Resource s,
			IRI p,
			Value o,
			Resource... contexts
	) throws RepositoryException {
		if(contexts != null && contexts.length > 0) {
			throw new RepositoryException("Named graphs are not supported");
		}
		
		repository.model.remove(
				Convert.resourceToResource(repository.model, s),
				Convert.uriToProperty(repository.model, p),
				Convert.valueToRDFNode(repository.model, o)
		);
	}

	/**
	 * Returns a {@link JenaBooleanQuery} built with the underlying Model
	 */
	@Override
	public BooleanQuery prepareBooleanQuery(
			QueryLanguage ql,
			String sparql,
			String baseURI
	) throws RepositoryException, MalformedQueryException {
		if(ql != QueryLanguage.SPARQL) {
			throw new MalformedQueryException("Only QueryLanguage.SPARQL is supported");
		}
		
		return new JenaBooleanQuery(this.repository.model, sparql, baseURI);
	}

	/**
	 * Returns a {@link JenaGraphQuery} built with the underlying Model
	 */
	@Override
	public GraphQuery prepareGraphQuery(
			QueryLanguage ql,
			String sparql,
			String baseURI
	) throws RepositoryException, MalformedQueryException {
		if(ql != QueryLanguage.SPARQL) {
			throw new MalformedQueryException("Only QueryLanguage.SPARQL is supported");
		}
		
		return new JenaGraphQuery(this.repository.model, this.getValueFactory(), sparql, baseURI);
	}

	/**
	 * Returns either a {@link JenaGraphQuery}, a {@link JenaTupleQuery}, or a {@link JenaBooleanQuery}
	 * depending on the type of the query.
	 */
	@Override
	public Query prepareQuery(
			QueryLanguage ql,
			String query,
			String baseURI
	) throws RepositoryException, MalformedQueryException {
		
		// Determine the type of query and call the associated method
		ParsedOperation parsedOperation = QueryParserUtil.parseOperation(ql, query, null);
		if (parsedOperation instanceof ParsedTupleQuery) {
			return prepareTupleQuery(ql, query, baseURI);
		} else if (parsedOperation instanceof ParsedGraphQuery) {
			return prepareGraphQuery(ql, query, baseURI);
		} else if (parsedOperation instanceof ParsedBooleanQuery) {
			return prepareBooleanQuery(ql, query, baseURI);
		} else {
			// cannot find right query type, throw an exception
			throw new MalformedQueryException("Unexpected query type "+ parsedOperation.getClass() + " for query " + query);
		}

	}

	/**
	 * Returns a {@link JenaTupleQuery} built with the underlying Model
	 */
	@Override
	public TupleQuery prepareTupleQuery(
			QueryLanguage ql,
			String query,
			String baseURI
	) throws RepositoryException, MalformedQueryException {
		if(ql != QueryLanguage.SPARQL) {
			throw new MalformedQueryException("Only QueryLanguage.SPARQL is supported");
		}
		
		return new JenaTupleQuery(this.repository.model, this.repository.getValueFactory(), query, baseURI);
	}

	/**
	 * Returns a {@link JenaUpdate} built with the underlying Model
	 */
	@Override
	public Update prepareUpdate(
			QueryLanguage ql,
			String query,
			String baseURI
	) throws RepositoryException, MalformedQueryException {
		if(ql != QueryLanguage.SPARQL) {
			throw new MalformedQueryException("Only QueryLanguage.SPARQL is supported");
		}
		
		return new JenaUpdate(this.repository.model, this.getValueFactory(), query, baseURI);
	}
	
}
