package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SQLUrlLastMonth {

	DBConnectionManager connections= new DBConnectionManager();

	protected SQLQueryRegistry queryRegistry;

	public SQLUrlLastMonth(SQLQueryRegistry queryRegistry) {
		super();
		this.queryRegistry = queryRegistry;
	}

	public ListingData getUrlConvertedLastMonth(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertis";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) "
						+ "group by url LIMIT 10 offset " +(indexDebut)));

				while (rs.next()) {			

					resultat.put(rs.getString("url"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setData(resultat);
				listing.setTotalLignes(getSizeConvertListLastMonth());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}
	public ListingData getIdConvertedLastMonth(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertis";
				ResultSet  rs=null;
				
				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) "
																						+ "group by uri "));

				/*rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) "
						+ "group by uri LIMIT 10 offset " +(indexDebut)));*/

				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizeConvertIdListLastMonth());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}

	public ListingData getUrlPrintLastMonth(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrint";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) "
						+ "group by url LIMIT 10 offset " +(indexDebut)));				

				while (rs.next()) {			

					resultat.put(rs.getString("url"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setData(resultat);
				listing.setTotalLignes(getSizePrintListLastMonth());

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}

	public ListingData getIdPrintLastMonth(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrint";
				ResultSet  rs=null;
				
				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) "
																						+ "group by uri "));
				
				/*rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) "
						+ "group by uri LIMIT 10 offset " +(indexDebut)));	*/			

				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizePrintIdListLastMonth());

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}


	public Integer getSizeConvertListLastMonth(){

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertisTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) group by url)"));

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

	public Integer getSizeConvertIdListLastMonth(){

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertisTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) group by uri)"));

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

	public Integer getSizePrintListLastMonth() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrintTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) group by url)"));

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

	public Integer getSizePrintIdListLastMonth() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrintTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and month(jour)=(month(now())-1) group by uri)"));

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


}
