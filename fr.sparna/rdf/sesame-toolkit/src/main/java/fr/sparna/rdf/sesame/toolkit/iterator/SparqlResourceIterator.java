package fr.sparna.rdf.sesame.toolkit.iterator;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;

import fr.sparna.commons.lang.ClosableIterator;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;

public class SparqlResourceIterator implements ClosableIterator<Resource> {
	
	protected TupleQueryResultResourceIterator delegate;
	
	public SparqlResourceIterator(Repository repository, SparqlQuery query) throws SparqlPerformException {
		this.delegate = new TupleQueryResultResourceIterator(Perform.on(repository).selectResult(query));
	}

	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}
	
	@Override
	public Resource next() {
		return this.delegate.next();
	}
	
	@Override
	public void remove() {
		this.delegate.remove();
	}
	
	@Override
	public void close() {
		this.delegate.close();
	}
	
}
