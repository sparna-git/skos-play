package fr.sparna.rdf.solr.index.source;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import fr.sparna.assembly.Assembly;
import fr.sparna.assembly.AssemblyLine;
import fr.sparna.assembly.AssemblySource;
import fr.sparna.assembly.LifecycleException;
import fr.sparna.assembly.base.AssemblyFactory;
import fr.sparna.assembly.base.BaseAssemblyLineComponent;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;

public class SparqlResultIteratorAssemblySource<X> extends BaseAssemblyLineComponent<X> implements AssemblySource<X> {

	protected Repository repository;
	protected SparqlQuery query;
	protected AssemblyFactory<X> indexableFactory;
	
	// transient temporary connection, closed when processing is finished
	private transient RepositoryConnection connection;
	// transient temporary query result, closed when processing is finished
	private transient TupleQueryResult tqr;
	
	
	public SparqlResultIteratorAssemblySource(
			Repository repository,
			SparqlQuery query,
			AssemblyFactory<X> indexableFactory
	) {
		this.repository = repository;
		this.query = query;
		this.indexableFactory = indexableFactory;
	}
	

	@Override
	public void init(AssemblyLine<X> assemblyLine) throws LifecycleException {
		super.init(assemblyLine);
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
	public Assembly<X> next() {
		try {
			BindingSet binding = tqr.next();
			Value v = binding.getValue(binding.getBindingNames().iterator().next());
			if(!(v instanceof Resource)) {
				throw new RuntimeException("Accept only queries that return Value has result (found "+v.getClass().getName()+", value "+v.toString()+")");
			} else {
				return this.indexableFactory.buildIndexable(v.stringValue());
			}
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}


	@Override
	public int sizeEstimate() {
		return 0;
	}
	
}
