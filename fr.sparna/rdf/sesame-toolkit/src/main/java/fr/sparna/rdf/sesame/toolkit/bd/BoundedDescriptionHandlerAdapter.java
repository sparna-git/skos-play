package fr.sparna.rdf.sesame.toolkit.bd;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

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
