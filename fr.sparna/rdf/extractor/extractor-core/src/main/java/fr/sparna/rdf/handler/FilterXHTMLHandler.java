package fr.sparna.rdf.handler;

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
public class FilterXHTMLHandler extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private static final String XHTML = "http://www.w3.org/1999/xhtml/vocab#";
	
	public FilterXHTMLHandler() {
		super();
	}
	
	public FilterXHTMLHandler(RDFHandler handler) {
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
