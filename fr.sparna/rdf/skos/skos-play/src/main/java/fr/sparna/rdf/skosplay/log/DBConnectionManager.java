package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Classe permettant d'établir la connexion avec la base
 * @author clarvie
 *
 */

public class DBConnectionManager {
   
   private static final String DB_DRIVER = "org.h2.Driver";
   
   private String dataPath;
   private String jdbcUser = "";
   private String jdbcPassword = "";

   public DBConnectionManager(String dataPath) {
	   this.dataPath = dataPath;
   }
   
   public Connection getDBConnection() {
	   try {
		   Class.forName(DB_DRIVER);
	   } catch (ClassNotFoundException e) {
		   e.printStackTrace();
	   }
	   try {
		   String INIT_SQL = "CREATE TABLE IF NOT EXISTS user ("
		   						+ "id INT AUTO_INCREMENT, "
		   						+ "nom VARCHAR(255), "
		   						+ "email VARCHAR(255), "
		   						+ "PRIMARY KEY (id)"
		   					+ ")\\;"
		   					+ "CREATE TABLE IF NOT EXISTS statistique ("
		   						+ "id INT AUTO_INCREMENT, "
		   						+ "type VARCHAR(255), "
		   						+ "output VARCHAR(255), "
		   						+ "rendu VARCHAR(255), "
		   						+ "langue VARCHAR(20), "
		   						+ "url VARCHAR(1024), "
		   						+ "jour DATE, "
		   						// on utilise un champ VARCHAR sans contrainte de taille
		   						// la taille max est implicitement 2147483647 (voir http://h2-database.66688.n3.nabble.com/VARCHAR-type-with-or-without-length-td2425668.html)
		   						// dans ce champ sont stockées les URIs concaténées des ConceptScheme convertis, donc la valeur peut être très longue
		   						+ "uri VARCHAR, "
		   						+ "iduser INT, "
		   						+ "PRIMARY KEY (id), FOREIGN KEY (iduser) REFERENCES user(id)"
		   						+ ") ";
		   String fullJdbcUrl = "jdbc:h2:"+dataPath+";INIT="+INIT_SQL;
		   return DriverManager.getConnection(fullJdbcUrl, jdbcUser, jdbcPassword);
		   
	   } catch (SQLException e) {
		  e.printStackTrace();
	   }
	   
	   return null;
   }

}
