package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SQLUrlAlltime {

	protected SQLQueryRegistry queryRegistry;
DBConnectionManager connections= new DBConnectionManager();

	public SQLUrlAlltime(SQLQueryRegistry queryRegistry) {
		super();
		this.queryRegistry = queryRegistry;
	}

	public ListingData getUrlConvertedAllTime(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();


		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertis";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by url LIMIT 10 offset " +(indexDebut)));

				while (rs.next()) {			

					resultat.put(rs.getString("url"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setData(resultat);
				listing.setTotalLignes(getSizeConvertListAllTime());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}

	public ListingData getIdConvertedAllTime(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();


		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertis";
				ResultSet  rs=null;
				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri"));
				//rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri LIMIT 10 offset " +(indexDebut)));

				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizeConvertIdListAllTime());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}

	public Integer getSizeConvertListAllTime() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertisTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by url)"));

				while (rs.next()) {			

					resultat= rs.getInt("nombre");

				}

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return resultat;
	}
	public Integer getSizeConvertIdListAllTime() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertisTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri)"));

				while (rs.next()) {			

					resultat= rs.getInt("nombre");

				}

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return resultat;
	}

	public Integer getSizePrintListAllTime() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrintTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by url)"));

				while (rs.next()) {			

					resultat= rs.getInt("nombre");

				}

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return resultat;
	}
	public Integer getSizePrintIdListAllTime() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrintTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri)"));

				while (rs.next()) {			

					resultat= rs.getInt("nombre");

				}

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return resultat;
	}

	public ListingData getIdPrintAllTime( int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();
		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrint";
				ResultSet  rs=null;
				//rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri LIMIT 10 offset " +(indexDebut)));
				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri "));

				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizePrintIdListAllTime());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}

	public ListingData getUrlPrintAllTime( int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();
		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrint";
				ResultSet  rs=null;
				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by url LIMIT 10 offset " +(indexDebut)));

				while (rs.next()) {			

					resultat.put(rs.getString("url"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setData(resultat);
				listing.setTotalLignes(getSizePrintListAllTime());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}

}
