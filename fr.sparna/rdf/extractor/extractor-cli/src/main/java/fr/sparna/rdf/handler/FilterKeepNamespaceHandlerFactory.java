package fr.sparna.rdf.handler;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;

public class FilterKeepNamespaceHandlerFactory implements RDFHandlerWrapperFactory {

	@Override
	public RDFHandlerWrapper createRdfHandlerWrapper(RDFHandler handler) {
		return new FilterKeepNamespaceHandler(handler);
	}	
	
}
