package fr.sparna.rdf.sesame.toolkit.util;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class GraphExport {

	public static void export(Graph g, RDFHandler handler)
	throws RepositoryException, RDFHandlerException {
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		RepositoryConnection c = r.getConnection();
		c.add(g);
		c.export(handler);
		c.close();
	}
	
}
