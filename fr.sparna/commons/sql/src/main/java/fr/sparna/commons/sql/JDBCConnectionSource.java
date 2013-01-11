package fr.sparna.commons.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Creates a Connection with a simple JDBC call.
 * 
 * @author thomas
 *
 */
public class JDBCConnectionSource implements ConnectionSource {
	
	protected String driverClassName;
	protected String jdbcConnectionUrl;
	protected String user;
	protected String password;
	
	
	
	public JDBCConnectionSource(
			String driverClassName,
			String jdbcConnectionUrl,
			String user,
			String password
	) {
		super();
		this.driverClassName = driverClassName;
		this.jdbcConnectionUrl = jdbcConnectionUrl;
		this.user = user;
		this.password = password;
	}

	/**
	 *  Return a {@link Connection} to the database
	 */
	@Override
	public final Connection getConnection() throws ConnectionSourceException {
		Connection result = null;
	
		try {
			Class.forName(this.driverClassName);
			result = DriverManager.getConnection(this.jdbcConnectionUrl, this.user, this.password);
		} catch (SQLException ex ){
			throw new ConnectionSourceException(
					"Cannot create JDBC connection to db", 
					ex
					);
		} catch (ClassNotFoundException e) {
			throw new ConnectionSourceException(
					"Driver class "+this.driverClassName+" not found, check your classpath", 
					e
					);
		}
		return result;
	}

	
	
}
