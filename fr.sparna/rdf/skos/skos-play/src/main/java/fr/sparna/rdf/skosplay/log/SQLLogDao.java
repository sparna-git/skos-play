package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLLogDao implements LogDaoIfc {
	private static final String DB_DRIVER = "org.h2.Driver";
    // private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	private static final String DB_CONNECTION = "jdbc:h2:~/skos-play";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    
    @Override
    public void writeLog(LogEntry entry) {
    	Connection connection = getDBConnection();
    	Date javaDate = new Date();
    	long javaTime = javaDate.getTime();
    	Timestamp sqlTimestamp = new Timestamp(javaTime);
    	String dateactuelle=sqlTimestamp.toString();
    	entry.setActiondate(dateactuelle);
    	Statement stmt = null;
    	
    	try {
    		System.out.println("************insertion des données...******************");
    		connection.setAutoCommit(false);
    		stmt = connection.createStatement();
    		stmt.execute("CREATE TABLE IF NOT EXISTS CONVERSION (id INT AUTO_INCREMENT, nom varchar(255), usezip boolean, usexls boolean, usegraph boolean, format varchar(255), jour varchar(255), type varchar(255), PRIMARY KEY (id))");

    		String requetConversion="INSERT INTO CONVERSION(nom, usezip, usexls, usegraph, format, jour, type) VALUES('"
    				+entry.getName()+"','"
    				+entry.isZip()+"','"
    				+entry.isXls()+"','"
    				+entry.isGraph()+"','"
    				+entry.getOutput()+"','"
    				+entry.getActiondate()+"','"
    				+entry.getActiontype()+"')";
    		
    		stmt.execute(requetConversion);
    		connection.commit();
    	} catch (SQLException e) {
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
    
	@Override
    public List<LogEntry> ListAllLog() {
		ArrayList<LogEntry> ListeRetour = new ArrayList <LogEntry>();
    	Connection connection = getDBConnection();
    	Statement stmt = null;

    	try {
    		connection.setAutoCommit(false);
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT* from CONVERSION");
    		System.out.println("************récupération des données contenues dans la table******************");
    		
    		while (rs.next()) {
    			LogEntry entry=new LogEntry();
    			entry.setName(rs.getString("nom"));
    			entry.setOutput(rs.getString("format"));
    			entry.setGraph(rs.getBoolean("usegraph"));
    			entry.setXls(rs.getBoolean("usexls"));
    			entry.setZip(rs.getBoolean("usezip"));
    			entry.setActiondate(rs.getString("jour"));
    			entry.setActiontype(rs.getString("type"));
    			ListeRetour.add(entry);
    		}
    		System.out.println("************récupération des données terminée******************");
    		connection.commit();

    	} catch (SQLException e) {
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
    	return ListeRetour;
    }

    private static Connection getDBConnection( ) {
    	Connection dbConnection = null;
    	
    	try {
    		Class.forName(DB_DRIVER);
    	} catch (ClassNotFoundException e) {
    		System.out.println(e.getMessage());
    	}
    	try {
    		dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
    		return dbConnection;
    	} catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return dbConnection;
    }

	@Override
	public int getConvertNumber() {
		Connection connection = getDBConnection();
    	Statement stmt = null;
    	int compteurligneConvert=0;

    	try {
    		connection.setAutoCommit(false);
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT id , COUNT(*) FROM CONVERSION WHERE type='convert' GROUP BY id;");
    		while (rs.next()) {
    			
    			compteurligneConvert=rs.getInt("id");
    			
    		}
    		System.out.println("nombre de conversion total :"+compteurligneConvert);
    		
    		
    		connection.commit();

    	} catch (SQLException e) {
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
		
		return compteurligneConvert;
	}

	@Override
	public int getPrintNumber() {
		int compteurprint=0;
		Connection connection = getDBConnection();
    	Statement stmt = null;
		try {
    		connection.setAutoCommit(false);
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT id , COUNT(*) FROM CONVERSION WHERE type='print' GROUP BY id;");
    		while (rs.next()) {
    			
    			compteurprint=rs.getInt("id");
    		}
    		System.out.println("nombre de print total :"+compteurprint);
    		connection.commit();

    	} catch (SQLException e) {
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
		return compteurprint;
	}

	@Override
	public String getLastConversionDate() {
		// TODO Auto-generated method stub
		Connection connection = getDBConnection();
		String lastdate=null;
    	Statement stmt = null;

    	try {
    		connection.setAutoCommit(false);
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT jour FROM CONVERSION WHERE type='convert' ORDER BY jour DESC LIMIT 0, 1;");
    		while (rs.next()) {
    			
    			lastdate=rs.getString("jour");
    		}
    		System.out.println("dernière conversion éffectuée le:"+lastdate);
    		
    		
    		connection.commit();

    	} catch (SQLException e) {
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
		
		return lastdate;
	}

	@Override
	public String getLastPrintDate() {
		// TODO Auto-generated method stub
		Connection connection = getDBConnection();
		String lastdate=null;
		Statement stmt = null;

	try {
		    connection.setAutoCommit(false);
		    stmt = connection.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT jour FROM CONVERSION WHERE type='print' ORDER BY jour DESC LIMIT 0, 1;");
		    while (rs.next()) {
		    	 lastdate=rs.getString("jour");
		    	}
		    
		    System.out.println("dernier print éffectué le:"+lastdate);	
		    connection.commit();
		} catch (SQLException e) {
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
		return lastdate;
	}
    

}
