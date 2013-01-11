package fr.sparna.commons.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An object that knows how to generate an SQL statement and to process the results of this
 * statement. This is to be passed to a {@link SQLExecution} object.
 * 
 * @author thomas
 */
public interface SQLHelper {

	/**
	 * Returns a prepared SQL statement (with '?' instead of values)
	 * 
	 * @return
	 * @throws SQLException
	 */
	public String buildPreparedStatement() throws SQLException;
	
	/**
	 * Injects the values into the prepared SQL statement. Values must be injected in the correct order.
	 * 
	 * @param stmt
	 * @throws SQLException
	 */
	public void prepareStatement(PreparedStatement stmt) throws SQLException;
	
	/**
	 * Handle the result of the previously generated statement
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public void handleResult(ResultSet rs) throws SQLException;

	/**
	 * Frees any resource used once the statement is executed.
	 */
	public void tearDown();
	
}
