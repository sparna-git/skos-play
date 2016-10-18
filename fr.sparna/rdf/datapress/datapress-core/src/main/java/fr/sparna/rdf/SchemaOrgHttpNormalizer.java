package fr.sparna.rdf;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaOrgHttpNormalizer extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public SchemaOrgHttpNormalizer() {
		super();
	}
	
	public SchemaOrgHttpNormalizer(RDFHandler handler) {
		super(handler);
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		Statement result = s;
		if(
				s.getPredicate().getNamespace().equals("https://schema.org/")
		) {
			result = SimpleValueFactory.getInstance().createStatement(
					s.getSubject(),
					SimpleValueFactory.getInstance().createIRI("http://schema.org/"+s.getPredicate().getLocalName()),
					s.getObject()
			);
		}
		super.handleStatement(result);
	}
	
}
