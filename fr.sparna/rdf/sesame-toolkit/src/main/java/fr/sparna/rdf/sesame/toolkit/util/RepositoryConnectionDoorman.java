package fr.sparna.rdf.sesame.toolkit.util;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

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
