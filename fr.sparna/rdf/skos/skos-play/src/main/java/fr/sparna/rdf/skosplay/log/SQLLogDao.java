package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLLogDao implements LogDaoIfc {

	protected DBConnectionManager connectionManager;
	protected boolean doLog = false;    

	public SQLLogDao(boolean doLog, DBConnectionManager connectionManager) {
		super();
		this.doLog = doLog;
		this.connectionManager = connectionManager;
	}

	/**
	 * Insertion de log dans la base 
	 */
	@Override
	public void insertLog(LogEntry entry) {
		// si on n'a pas activé les logs, on sort tout de suite sans rien inséré
		if(!doLog) {
			return;
		}
		
		entry.setActiondate("NOW()");
		String requete="INSERT INTO statistique(output, type, rendu, langue, url, jour, uri) VALUES('"

				+entry.getOutput()+"','"
				+entry.getDisplayType()+"','"
				+entry.getRendu()+"','"
				+entry.getLangue()+"','"
				+entry.getUrl()+"',"
				+entry.getActiondate()+",'"
				+entry.getUri()+"')";
		try (Connection connection = connectionManager.getDBConnection()) {
			connection.setAutoCommit(false);
			try(Statement stmt = connection.createStatement()) {
				stmt.execute(requete);
				connection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    
    

	public boolean isDoLog() {
		return doLog;
	}

	public void setDoLog(boolean doLog) {
		this.doLog = doLog;
	}

}
