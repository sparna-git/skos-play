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
		requete="INSERT INTO statistique(output, type, rendu, langue, url, jour, uri) VALUES('"
				+entry.getOutput()+"','"
				+entry.getDisplayType()+"','"
				+entry.getRendu()+"','"
				+entry.getLangue()+"','"
				+entry.getUrl()+"',"
				+entry.getActiondate()+",'"
				+entry.getUri()+"')";
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

<<<<<<< HEAD
    }
    
    public void writelog(String language, String format, String rendu, String url, String type, String uri)
   	{
   		CreateTableLog table=new CreateTableLog();
   		table.create("statistique", "id INT AUTO_INCREMENT, type VARCHAR(255), output VARCHAR(255), rendu VARCHAR(255), langue VARCHAR(20), url VARCHAR(255), jour DATE, uri VARCHAR(255)");
   		LogEntry entry=new LogEntry();
   		entry.setLangue(language);
   		entry.setOutput(format);
   		entry.setRendu(rendu);
   		entry.setType(type);
   		entry.setUrl(url);
   		entry.setUri(uri);
   		insertLog(entry);
   		
   	}	
=======
	public boolean isDoLog() {
		return doLog;
	}
>>>>>>> c7562b4edb5fd08c38f3ed4b3b2e85be4bf0f890

	public void setDoLog(boolean doLog) {
		this.doLog = doLog;
	}

}
