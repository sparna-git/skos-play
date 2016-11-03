package fr.sparna.rdf.sesame.toolkit.bd;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

public interface BoundedDescriptionHandlerIfc extends RDFHandler {

	public void handleResource(Resource r) throws RDFHandlerException;
	
}
