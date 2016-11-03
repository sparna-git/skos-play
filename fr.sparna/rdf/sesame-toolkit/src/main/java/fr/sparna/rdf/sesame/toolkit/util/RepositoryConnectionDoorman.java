package fr.sparna.rdf.sesame.toolkit.util;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

public class RepositoryConnectionDoorman {
	
	public static Object service(Guest worker, Repository r) 
	throws RepositoryException {
		RepositoryConnection c = r.getConnection();
		try {
			return worker.work(c);
		} finally {
			closeQuietly(c);
		}
	}
	
	public static void closeQuietly(RepositoryConnection connection) {
		if(connection != null) {
			try {
				connection.close();
			} catch (RepositoryException ignore) {ignore.printStackTrace();}
		}
	}
	
	public interface Guest {
		
		public Object work(RepositoryConnection connection) throws RepositoryException;
		
	}
	
}
