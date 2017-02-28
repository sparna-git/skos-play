package fr.sparna.rdf.sesame.toolkit.util;


import java.net.URI;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.RDFInserter;

public class RDFInserterFactory {

	public static RDFInserter newRDFContextInserter(RepositoryConnection connection, Resource...contexts) {
		RDFInserter inserter = new RDFInserter(connection);
		inserter.enforceContext(contexts);
		return inserter;
	}
	
	public static RDFInserter newRDFContextInserter(RepositoryConnection connection, URI context) {
		RDFInserter inserter = new RDFInserter(connection);
		inserter.enforceContext(SimpleValueFactory.getInstance().createIRI(context.toString()));
		return inserter;
	}
	
}
