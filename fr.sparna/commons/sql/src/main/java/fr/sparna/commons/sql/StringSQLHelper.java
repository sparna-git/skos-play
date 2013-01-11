package fr.sparna.commons.sql;

import java.sql.SQLException;

public class StringSQLHelper extends SQLHelperBase implements SQLHelper {

	protected String statement;
	
	public StringSQLHelper(String statement) {
		super();
		this.statement = statement;
	}

	@Override
	public String buildPreparedStatement() throws SQLException {
		return statement;
	}

}
