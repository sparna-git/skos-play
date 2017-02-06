package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SQLLogDao implements LogDaoIfc {
	
    CreateTableLog connections= new CreateTableLog();
  /**
   * Insertion de log dans la base 
   */
    @Override
    public void insertLog(LogEntry entry) {
    	Connection connection = connections.getDBConnection(); 
		Statement stmt = null;
		String requete=null;
		entry.setActiondate("NOW()");
		requete="INSERT INTO statistique(output, type, rendu, langue, url, jour) VALUES('"
				+entry.getOutput()+"','"
				+entry.getType()+"','"
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
    
    public void writelog(String language, String format, String rendu, String url, String type)
   	{
   		CreateTableLog table=new CreateTableLog();
   		table.create("statistique", "id INT AUTO_INCREMENT, type VARCHAR(255), output VARCHAR(255), rendu VARCHAR(255), langue VARCHAR(20), url VARCHAR(255), jour DATE");
   		LogEntry entry=new LogEntry();
   		entry.setLangue(language);
   		entry.setOutput(format);
   		entry.setRendu(rendu);
   		entry.setType(type);
   		entry.setUrl(url);
   		insertLog(entry);
   		
   	}	

    @Override
    public Map<String, Integer> ListAllLog() {

    	return null;
    }
    
    
}
