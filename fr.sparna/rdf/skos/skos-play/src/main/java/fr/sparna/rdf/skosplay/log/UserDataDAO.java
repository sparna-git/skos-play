package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataDAO {
	DBConnectionManager connections= new DBConnectionManager();
	
	public void insertUserData(LogEntry entry){
		
		Connection connection = connections.getDBConnection(); 
		Statement stmt = null;
		String requete=null;
		requete="INSERT INTO USER(nom, email, idconvert) VALUES('"
				+entry.getName()+"','"
				+entry.getEmail()+"')";
		try {
    		connection.setAutoCommit(false);
    		stmt = connection.createStatement();
    		stmt.execute(requete);
    		System.out.println("requete d'insertion : "+requete+"terminée avec succès");
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
	
	public Integer LastUserentry(String email){

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				
				ResultSet rs = stmt.executeQuery("select id from user where email="+email);

				while (rs.next()) {			

					resultat=rs.getInt("id");

				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultat;
	}
	
	public List<LogEntry> listAlluser(){
		
		ArrayList<LogEntry> ListeRetour = new ArrayList <LogEntry>();
		Connection connection = connections.getDBConnection(); 
		Statement stmt = null;
		String requete=null;
		
		try
		 {
	    	connection.setAutoCommit(false);
	    	stmt = connection.createStatement();
	    	requete="SELECT *from USER";
	    	ResultSet rs = stmt.executeQuery(requete);
	        System.out.println("************récupération des données contenues dans la table Utilisateur******************");
	        		
	        while (rs.next()) {
	        	LogEntry entry=new LogEntry();
	        	entry.setName(rs.getString("Nom"));
	        	entry.setEmail(rs.getString("Email"));
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

}
