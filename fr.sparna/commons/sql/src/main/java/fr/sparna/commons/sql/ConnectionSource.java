package fr.sparna.commons.sql;

import java.sql.Connection;

/**
 * Returns a Connection to a database. Concrete implementations can use JDBC, a DataSource, or
 * a pool of Connection.
 * 
 * @see http://www.javapractices.com/topic/TopicAction.do?Id=183
 * 
 * @author thomas
 *
 */
public interface ConnectionSource {

	public Connection getConnection() throws ConnectionSourceException;
	
}
