package fr.sparna.rdf.handler;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;

public interface RDFHandlerWrapperFactory {

	public RDFHandlerWrapper createRdfHandlerWrapper(RDFHandler handler);
	
}
