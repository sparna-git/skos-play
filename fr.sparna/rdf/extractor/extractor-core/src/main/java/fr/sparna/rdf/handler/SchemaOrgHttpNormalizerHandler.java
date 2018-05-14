package fr.sparna.rdf.handler;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaOrgHttpNormalizerHandler extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public SchemaOrgHttpNormalizerHandler() {
		super();
	}
	
	public SchemaOrgHttpNormalizerHandler(RDFHandler handler) {
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
		} else if(
				s.getPredicate().equals(RDF.TYPE)
				&&
				s.getObject() instanceof IRI
				&&
				((IRI)s.getObject()).getNamespace().equals("https://schema.org/")
		) {
			result = SimpleValueFactory.getInstance().createStatement(
					s.getSubject(),
					s.getPredicate(),
					SimpleValueFactory.getInstance().createIRI("http://schema.org/"+((IRI)s.getObject()).getLocalName())
			);			
		}		
		
		super.handleStatement(result);
	}
	
}
