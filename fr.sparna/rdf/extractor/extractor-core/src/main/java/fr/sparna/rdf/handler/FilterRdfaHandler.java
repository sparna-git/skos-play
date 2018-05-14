package fr.sparna.rdf.handler;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;
import org.semarglproject.vocab.RDFa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters empty literals
 * 
 * @author Thomas Francart
 *
 */
public class FilterRdfaHandler extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	public FilterRdfaHandler() {
		super();
	}
	
	public FilterRdfaHandler(RDFHandler handler) {
		super(handler);
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		boolean isRDFaType = (
				s.getPredicate().equals(RDF.TYPE)
				&&
				s.getObject() instanceof IRI
				&&
				((IRI)s.getObject()).getNamespace().equals(RDFa.NS)
		);
		
		boolean isRDFaProperty = s.getPredicate().getNamespace().equals(RDFa.NS);
		
		if(
				!isRDFaType
				&&
				!isRDFaProperty
		) {
			super.handleStatement(s);
		}
	}
	
}
