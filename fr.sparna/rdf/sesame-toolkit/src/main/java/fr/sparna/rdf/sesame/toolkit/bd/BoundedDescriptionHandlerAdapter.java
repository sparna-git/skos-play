package fr.sparna.rdf.sesame.toolkit.bd;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

public class BoundedDescriptionHandlerAdapter implements BoundedDescriptionHandlerIfc {

	protected RDFHandler delegate;
	
	private Resource resource;
	
	public BoundedDescriptionHandlerAdapter(RDFHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		delegate.endRDF();
	}

	@Override
	public void handleComment(String c) throws RDFHandlerException {
		delegate.handleComment(c);
	}

	@Override
	public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
		delegate.handleNamespace(prefix, uri);
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		delegate.handleStatement(s);
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		delegate.startRDF();
	}

	@Override
	public void handleResource(Resource r) throws RDFHandlerException {
		this.resource = r;
	}

	public Resource getResource() {
		return resource;
	}
	
}
