package fr.sparna.commons.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides NoOp implementations for prepareStatement, handleResult and tearDown methods of the
 * SQLHelper interface. The only method to implement for subclasses is buildPreparedStatement.
 * 
 * @author thomas
 *
 */
public abstract class SQLHelperBase implements SQLHelper {


	@Override
	public void prepareStatement(PreparedStatement stmt) throws SQLException {
		// no-op
	}

	@Override
	public void handleResult(ResultSet rs) throws SQLException {
		// no-op
	}

	@Override
	public void tearDown() {
		// no-op
	}

}
