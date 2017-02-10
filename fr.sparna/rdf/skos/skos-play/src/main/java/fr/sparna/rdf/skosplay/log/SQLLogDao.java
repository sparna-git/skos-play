package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLLogDao implements LogDaoIfc {

	DBConnectionManager connections= new DBConnectionManager();
	protected boolean doLog = false;    

	public SQLLogDao(boolean doLog) {
		super();
		this.doLog = doLog;
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
		
		Connection connection = connections.getDBConnection(); 
		Statement stmt = null;
		String requete=null;
		entry.setActiondate("NOW()");
		requete="INSERT INTO statistique(output, type, rendu, langue, url, jour) VALUES('"
				+entry.getOutput()+"','"
				+entry.getDisplayType()+"','"
				+entry.getRendu()+"','"
				+entry.getLangue()+"','"
				+entry.getUrl()+"',"
				+entry.getActiondate()+")";
		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			stmt.execute(requete);
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Exception Message " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean isDoLog() {
		return doLog;
	}

	public void setDoLog(boolean doLog) {
		this.doLog = doLog;
	}

}
