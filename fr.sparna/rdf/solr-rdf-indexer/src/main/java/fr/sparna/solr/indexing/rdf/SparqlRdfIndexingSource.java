package fr.sparna.solr.indexing.rdf;

import org.apache.solr.common.SolrInputDocument;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.solr.indexing.IndexingSourceIfc;
import fr.sparna.solr.indexing.LifecycleException;
import fr.sparna.solr.indexing.base.BaseIndexingComponent;

public class SparqlRdfIndexingSource extends BaseIndexingComponent implements IndexingSourceIfc<Resource> {

	public static final String DEFAULT_URI_FIELD_NAME = "uri";
	
	protected String uriFieldName = DEFAULT_URI_FIELD_NAME;
	protected Repository repository;
	protected SparqlQuery query;
	
	// transient temporary connection, closed when processing is finished
	private transient RepositoryConnection connection;
	// transient temporary query result, closed when processing is finished
	private transient TupleQueryResult tqr;
	
	
	public SparqlRdfIndexingSource(Repository repository, SparqlQuery query) {
		super();
		this.repository = repository;
		this.query = query;
	}

	@Override
	public void init() throws LifecycleException {
		super.init();
		try {
			this.connection = this.repository.getConnection();
			this.tqr = Perform.on(this.connection).selectResult(this.query);
		} catch (SparqlPerformException e) {
			throw new LifecycleException(e);
		} catch (RepositoryException e) {
			throw new LifecycleException(e);
		}
		
		// check that the result has one and only one variable
		try {
			if(this.tqr.getBindingNames().size() > 1) {
				throw new LifecycleException("Accept only queries returing one and only one variable (found "+this.tqr.getBindingNames().size()+")");
			}
		} catch (QueryEvaluationException e) {
			throw new LifecycleException(e);
		}
		
	}

	@Override
	public void destroy() throws LifecycleException {
		super.destroy();
		
		try {
			// close our result set
			this.tqr.close();
			// close the connection
			this.connection.close();
		} catch (QueryEvaluationException e) {
			throw new LifecycleException(e);
		} catch (RepositoryException e) {
			throw new LifecycleException(e);
		}
	}

	@Override
	public boolean hasNext() {
		try {
			return this.tqr.hasNext();
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Resource next() {
		try {
			BindingSet binding = tqr.next();
			Value v = binding.getValue(binding.getBindingNames().iterator().next());
			if(!(v instanceof Resource)) {
				throw new RuntimeException("Accept only queries that return Value has result (found "+v.getClass().getName()+", value "+v.toString()+")");
			} else {
				return (Resource)v;
			}
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SolrInputDocument createSolrDocument(Resource x) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.setField(this.uriFieldName, x.toString());
		return doc;
	}	
	
}
