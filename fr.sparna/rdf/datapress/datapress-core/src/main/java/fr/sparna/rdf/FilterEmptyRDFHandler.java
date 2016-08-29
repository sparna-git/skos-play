package fr.sparna.rdf;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters empty literals
 * 
 * @author Thomas Francart
 *
 */
public class FilterEmptyRDFHandler extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public FilterEmptyRDFHandler() {
		super();
	}
	
	public FilterEmptyRDFHandler(RDFHandler handler) {
		super(handler);
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		if(
				!(s.getObject() instanceof Literal)
				||
				!((Literal)s.getObject()).getLabel().equals("")
		) {
			super.handleStatement(s);
		}
	}
	
}
