package fr.sparna.rdf;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters XHTL vocab
 * 
 * @author Thomas Francart
 *
 */
public class FilterXHTMLRDFHandler extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private static final String XHTML = "http://www.w3.org/1999/xhtml/vocab#";
	
	public FilterXHTMLRDFHandler() {
		super();
	}
	
	public FilterXHTMLRDFHandler(RDFHandler handler) {
		super(handler);
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		if(
				!(s.getPredicate().getNamespace().equals(XHTML))
		) {
			super.handleStatement(s);
		}
	}
	
}
