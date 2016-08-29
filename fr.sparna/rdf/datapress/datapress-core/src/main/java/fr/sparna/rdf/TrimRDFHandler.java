package fr.sparna.rdf;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trim literals
 * 
 * @author Thomas Francart
 *
 */
public class TrimRDFHandler extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private SimpleValueFactory vf = SimpleValueFactory.getInstance();
	
	public TrimRDFHandler() {
		super();
	}
	
	public TrimRDFHandler(RDFHandler handler) {
		super(handler);
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		if(s.getObject() instanceof Literal) {
			Literal l = (Literal)s.getObject();
			Literal newL;
			if(l.getLanguage().isPresent()) {
				newL = vf.createLiteral(l.getLabel().trim(), l.getLanguage().get());
			} else if(l.getDatatype() != null) {
				newL = vf.createLiteral(l.getLabel().trim(), l.getDatatype());
			} else {
				newL = vf.createLiteral(l.getLabel().trim());
			}
			super.handleStatement(vf.createStatement(s.getSubject(), s.getPredicate(), newL));
		} else {
			super.handleStatement(s);
		}
	}
	
}
