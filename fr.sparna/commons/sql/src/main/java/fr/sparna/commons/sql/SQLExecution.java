package fr.sparna.commons.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Executes an SQL PreparedStatement returned by a {@link SQLHelper}, and defers processing of the
 * result to this helper.
 * By default, the SQLExecution will attempt to automatically close the database connection after each
 * execution. If the connection needs to be kept open, use the constructor with a "false" value for
 * the autoCloseConnection parameter
 * 
 * @author thomas
 *
 */
public class SQLExecution {

	protected Connection connection;
	protected boolean autoCloseConnection = true;
	
	/**
	 * The database connection will be kept open if you set the value false for the
	 * "autoCloseConnection" parameter.
	 * 
	 * @param connection The database connection to use
	 * @param autoCloseConnection False if you want the connection to be kept open.
	 */
	public SQLExecution(Connection connection, boolean autoCloseConnection) {
		super();
		this.connection = connection;
		this.autoCloseConnection = autoCloseConnection;
	}

	/**
	 * The connection will be closed automatically after each execution.
	 * 
	 * @param connection The database connection to use
	 */
	public SQLExecution(Connection connection) {
		this(connection, true);
	}

	/**
	 * Asks the helper to generate a PreparedStatement, to initialize it with values,
	 * executes the SQL and defers the processing of the results to the helper.
	 * 
	 * @param helper
	 * @throws SQLExecutionException
	 */
	public void executeQuery(SQLHelper helper) throws SQLExecutionException {

		// prepare connection, statement, resultSet
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {				

			// Get a statement from the connection
			stmt = connection.prepareStatement(helper.buildPreparedStatement());

			// prepare statement
			helper.prepareStatement(stmt);
			
			rs = stmt.executeQuery();

			while(rs.next()) {
				helper.handleResult(rs);
			}

		} catch(SQLException e) {
			throw new SQLExecutionException(e);
		} finally {
			// Close the resultset
			if(rs != null) {
				try { rs.close(); } catch (SQLException ignore) { ignore.printStackTrace(); }
			}
			// Close the statement
			if(stmt != null) {
				try { stmt.close(); } catch (SQLException ignore) { ignore.printStackTrace(); }
			}
			// Close the connection if required
			if(this.autoCloseConnection) {
				closeConnection(connection);
			}
			
			helper.tearDown();
		}
	}

	/**
	 * Same as executeQuery except that JDBC "executeUpdate" call is used and no ResultSet is being
	 * processed by the helper. This method returns the result of the executeUpdate call.
	 * 
	 * @param helper
	 * @return
	 * @throws SQLExecutionException
	 */
	public int executeUpdate(SQLHelper helper) throws SQLExecutionException {

		// prepare connection, statement
		PreparedStatement stmt = null;

		try {				

			// Get a statement from the connection
			stmt = connection.prepareStatement(helper.buildPreparedStatement());

			// prepare statement
			helper.prepareStatement(stmt);
			
			int result = stmt.executeUpdate();

			return result;
		} catch(SQLException e) {
			throw new SQLExecutionException(e);
		} finally {
			// Close the statement
			if(stmt != null) {
				try { stmt.close(); } catch (SQLException ignore) { ignore.printStackTrace(); }
			}
			// Close the connection
			if(this.autoCloseConnection) {
				closeConnection(connection);
			}
			
			helper.tearDown();
		}
	}

	/**
	 * If true, the connection will be closed after each SQL execution.
	 * 
	 * @return true if the connection is to be closed automatically after the execution, default is true.
	 */
	public boolean isAutoCloseConnection() {
		return autoCloseConnection;
	}

	/**
	 * Closes a Connection to the database. Ignores any exception that could happen by simply printing
	 * the stackTrace.
	 * 
	 * @param connection The connection to close.
	 */
	public static void closeConnection(Connection connection) {
		if(connection != null) {
			try { connection.close(); } catch (SQLException ignore) { ignore.printStackTrace(); }
		}
	}
}
