package fr.sparna.rdf.handler;

import java.util.List;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps only the statements with a namespace in a given namespace list
 * 
 * @author Thomas Francart
 *
 */
public class FilterKeepNamespaceHandler extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected List<String> namespaces;
	
	public FilterKeepNamespaceHandler() {
		super();
	}
	
	public FilterKeepNamespaceHandler(RDFHandler handler) {
		super(handler);
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {		
		boolean keep = s.getPredicate().equals(RDF.TYPE) || namespaces.contains(s.getPredicate().getNamespace());
		
		if(
				keep
		) {
			super.handleStatement(s);
		}
	}
	
}
