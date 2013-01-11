package fr.sparna.rdf.sesame.toolkit.util;

import org.openrdf.model.Graph;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.sail.memory.MemoryStore;

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
