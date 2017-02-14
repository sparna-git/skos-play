package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SQLUrlLastYear {

	DBConnectionManager connections= new DBConnectionManager();

	protected SQLQueryRegistry queryRegistry;

	public SQLUrlLastYear(SQLQueryRegistry queryRegistry) {
		super();
		this.queryRegistry = queryRegistry;
	}

	public ListingData getUrlConvertedLastYear(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();
		ListingData listing =new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertis";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and year(jour)=(year(now())-1) group by url LIMIT 10 offset " +(indexDebut)));

				while (rs.next()) {			

					resultat.put(rs.getString("url"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setData(resultat);
				listing.setTotalLignes(getSizeConvertListLastYear());
				
		 }
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	 }


	public ListingData getIdConvertedLastYear(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();
		ListingData listing =new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertis";
				ResultSet  rs=null;

				//rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and year(jour)=(year(now())-1) group by uri LIMIT 10 offset " +(indexDebut)));
				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' "
																						+ "and year(jour)=(year(now())-1) "
																						+ "group by uri "));
				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizeConvertIdListLastYear());

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}

	public ListingData getUrlPrintLastYear(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();
		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrint";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and year(jour)=(year(now())-1)"
						+ "group by url LIMIT 10 offset " +(indexDebut)));
				while (rs.next()) {			

					resultat.put(rs.getString("url"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setData(resultat);
				listing.setTotalLignes(getSizePrintListLastYear());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}


	public ListingData getIdPrintLastYear(int indexDebut) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();
		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrint";
				ResultSet  rs=null;
				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' "
																						+ "and year(jour)=(year(now())-1) group by uri"
																						+ "group by uri "));
				//rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and year(jour)=(year(now())-1) group by uri"
						//+ "group by uri LIMIT 10 offset " +(indexDebut)));
				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizePrintIdListLastYear());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}

	public Integer getSizeConvertListLastYear() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertisTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and year(jour)=(year(now())-1) group by url)"));

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
	public Integer getSizeConvertIdListLastYear() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertisTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and year(jour)=(year(now())-1) group by uri)"));

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

	public Integer getSizePrintListLastYear() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrintTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and year(jour)=(year(now())-1) group by url)"));

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

	public Integer getSizePrintIdListLastYear() {

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrintTotaux";
				ResultSet  rs=null;

				rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and year(jour)=(year(now())-1) group by uri)"));

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
