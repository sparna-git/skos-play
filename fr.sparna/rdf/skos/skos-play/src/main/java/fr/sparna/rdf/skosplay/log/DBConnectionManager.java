package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnectionManager {
   
   private static final String DB_DRIVER = "org.h2.Driver";
   
   private String jdbcConnectionUrl = "jdbc:h2:~/skos-play";
   private String jdbcUser = "";
   private String jdbcPassword = "";

   public DBConnectionManager() {
	   
   }
   
   public Connection getDBConnection( ) {
	   Connection dbConnection = null;

	   try {
		   Class.forName(DB_DRIVER);
	   } catch (ClassNotFoundException e) {
		   System.out.println(e.getMessage());
	   }
	   try {
		   String INIT_SQL = "CREATE TABLE IF NOT EXISTS statistique (id INT AUTO_INCREMENT, type VARCHAR(255), output VARCHAR(255), rendu VARCHAR(255), langue VARCHAR(20), url VARCHAR(255), jour DATE)";   
		   String fullJdbcUrl = jdbcConnectionUrl+";INIT="+INIT_SQL;
		   dbConnection = DriverManager.getConnection(fullJdbcUrl, jdbcUser, jdbcPassword);
		   return dbConnection;
	   } catch (SQLException e) {
		   System.out.println(e.getMessage());
	   }
	   return dbConnection;
   }

}
