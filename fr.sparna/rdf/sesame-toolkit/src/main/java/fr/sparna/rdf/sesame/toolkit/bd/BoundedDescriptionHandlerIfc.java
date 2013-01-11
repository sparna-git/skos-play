package fr.sparna.rdf.sesame.toolkit.bd;

import org.openrdf.model.Resource;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

public interface BoundedDescriptionHandlerIfc extends RDFHandler {

	public void handleResource(Resource r) throws RDFHandlerException;
	
}
