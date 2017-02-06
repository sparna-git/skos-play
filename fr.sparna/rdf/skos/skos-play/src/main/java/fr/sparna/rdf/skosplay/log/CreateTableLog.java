package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class CreateTableLog {
   
   private static final String DB_DRIVER = "org.h2.Driver";
   // private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
   private static final String DB_CONNECTION = "jdbc:h2:~/skos-play";
   private static final String DB_USER = "";
   private static final String DB_PASSWORD = "";
   protected String requete="CREATE TABLE IF NOT EXISTS";

   /**
    * 
    * @param tableName the name of the table
    * @param champs specify all columns and their type like in sql. ex : id INT, name varchar(255), PRIMARY KEY (id)"
    */
   public void create(String tableName, String champs) {
	   Connection connection = getDBConnection();
	   Statement stmt = null;
	   try {
		   connection.setAutoCommit(false);
		   stmt = connection.createStatement();
		   requete+=" "+tableName+"("+champs+")";
		   stmt.execute(requete);
		   System.out.println("La table "+tableName+"("+champs+")" +" a été créée avec succès");

	   } catch (SQLException e) {
		   // TODO Auto-generated catch block
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

   public  Connection getDBConnection( ) {
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

}
