package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SQLLogComptageDao implements LogDaoIfc {
	
	protected DBConnectionManager connections;
	
	protected SQLQueryRegistry queryRegistry;
	
	enum Range {
		ALLTIME,
		MONTH,
		YEAR
	}
	 
	
	public SQLLogComptageDao(DBConnectionManager connectionManager, SQLQueryRegistry queryRegistry) {
		super();
		
		this.connections = connectionManager;
		this.queryRegistry = queryRegistry;
	}

	@Override
	public void insertLog(LogEntry entry) {
		
	}
	
	public Map<String, Integer>  ListAllLog() {
		
		Map<String, Integer> resultat=new HashMap<String, Integer>();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "Allprintconvert";
				ResultSet rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID));

				while (rs.next()) {			

					resultat.put(rs.getString("type"), rs.getInt("nombre"));

				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultat;
	}
	
	public List<HistogrammeData> getNumberConvertOrPrintPerDayMonthYear(Range periodeRange) {

		List<HistogrammeData> resultat=new ArrayList<HistogrammeData>();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "NumberConvertOrPrintPerDayMonthYear";
				ResultSet rs=null;
				if(periodeRange.toString().equals("ALLTIME")){
					rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_", " "));
				}else{
					rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_", periodeRange.toString()));
				}
				

				while (rs.next()) {			

					HistogrammeData histoData= new HistogrammeData();
					switch(periodeRange) {
					case ALLTIME:
								histoData.setDayOrMonthOrYear(rs.getDate("periode").toString());
								break;
					case MONTH:
								histoData.setDayOrMonthOrYear(Integer.toString(rs.getInt("periode")));
						
								break;
					case YEAR:
								histoData.setDayOrMonthOrYear(Integer.toString(rs.getInt("periode")));
								break;
					default:
						break;				
					}
					histoData.setNbrePrint(rs.getInt("nbreprint"));
					histoData.setNbreConvert(rs.getInt("nbreconvert"));
					resultat.add(histoData);

				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultat;
	}	

	public Map<String, Integer> getNumberOfFormat() {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "NumberOfFormat";
				ResultSet rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_diff_", "<>"));

				while (rs.next()) {			

					resultat.put(rs.getString("output"), rs.getInt("nombre"));

				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultat;
	}

	public Map<String, Integer> getNumberOflanguage() {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "NumberOfLanguage";
				ResultSet rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID));

				while (rs.next()) {			

					resultat.put(rs.getString("langue"), rs.getInt("nombre"));

				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultat;
	}

	public Map<String, Integer> getNumberOfRendu() {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "NumberOfRendu";
				ResultSet rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_diff_", "<>"));

				while (rs.next()) {			

					resultat.put(rs.getString("rendu"), rs.getInt("nombre"));
				}

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultat;
	}
	
	public Map<String, Integer> getprintConvertLast365Days() {

		Map<String, Integer> resultat=new HashMap<String, Integer>();
		
		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "printConvertLast365Days";
				ResultSet rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID));

				while (rs.next()) {			

					resultat.put(rs.getString("type"), rs.getInt("nbre"));
				}

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultat;
	}
	
	/**
	 * 
	 *  donne la liste des url  non null  de type convert par jour , mois ou année
	 * 
	 */
	
	public ListingData getUrlConverted(int indexDebut, Range periodeRange) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertis";
				ResultSet  rs=null;
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null'  group by url LIMIT 10 offset " +(indexDebut)));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and MONTH(jour)=(MONTH(now())-1)"
																									+ " group by url LIMIT 10 offset " +(indexDebut)));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and YEAR(jour)=(YEAR(now())-1)"
																									+ " group by url LIMIT 10 offset " +(indexDebut)));
							break;
				default:
					break;				
				}
				
				while (rs.next()) {			

					resultat.put(rs.getString("url"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setData(resultat);
				listing.setTotalLignes(getSizeConvert(periodeRange));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}
	/**
	 * 
	 *  donne la liste des url  non null  de type print par jour , mois ou année
	 * 
	 */
	
	public ListingData getUrlPrint(int indexDebut, Range periodeRange) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrint";
				ResultSet  rs=null;
				
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null'  "
																			+ "group by url LIMIT 10 offset " +(indexDebut)));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and MONTH(jour)=(MONTH(now())-1)"
																									+ " group by url LIMIT 10 offset " +(indexDebut)));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and YEAR(jour)=(YEAR(now())-1)"
																									+ " group by url LIMIT 10 offset " +(indexDebut)));
							break;
				default:
					break;				
				}
							
				while (rs.next()) {			

					resultat.put(rs.getString("url"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setData(resultat);
				listing.setTotalLignes(getSizePrint(periodeRange));

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}
	/**
	 * 
	 * @param periodeRange donne le nombre de ligne url non null des actions de type print tout le temps, par mois et par année 
	 * @return
	 */
	
	public Integer getSizePrint(Range periodeRange){

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrintTotaux";
				ResultSet  rs=null;
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by url)"));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and MONTH(jour)=(MONTH(now())-1)"
																									+ " group by url) "));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and YEAR(jour)=(YEAR(now())-1)"
																									+ " group by url )"));
							break;
				default:
					break;				
				}
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
	/**
	 * 
	 * @param periodeRange donne le nombre de ligne url non null des actions de type convert 
	 * @return
	 */
	
	
	public Integer getSizeConvert(Range periodeRange){

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertisTotaux";
				ResultSet  rs=null;
				
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by url)"));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and MONTH(jour)=(MONTH(now())-1)"
																									+ " group by url )"));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and YEAR(jour)=(YEAR(now())-1)"
																									+ " group by url )"));
							break;
				default:
					break;				
				}

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
	/**
	 * 
	 * @param periodeRange donne la liste des identifiants ConceptScheme non null  de type print 
	 * @return
	 */
	
	public ListingData getIdPrint(Range periodeRange) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrint";
				ResultSet  rs=null;
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri "));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and MONTH(jour)=(MONTH(now())-1)"
																									+ " group by uri "));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and YEAR(jour)=(YEAR(now())-1)"
																									+ " group by uri  "));
							break;
				default:
					break;				
				}		

				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizeIdPrint(periodeRange));

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}
	/**
	 * 
	 * @param periodeRange donne la liste des identifiants ConceptScheme non null  de type convert 
	 * @return
	 */
	
	public ListingData getIdConverted(Range periodeRange) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertis";
				ResultSet  rs=null;
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri "));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and MONTH(jour)=(MONTH(now())-1)"
																									+ " group by uri "));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and YEAR(jour)=(YEAR(now())-1)"
																									+ " group by uri  "));
							break;
				default:
					break;				
				}			

				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizeIdConvert(periodeRange));

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}
	/**
	 * 
	 * @param periodeRange donne le nombre de ligne d'action de type print ayant un identifiant ConceptScheme non null
	 * @return
	 */
	
	public Integer getSizeIdPrint(Range periodeRange){

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrintTotaux";
				ResultSet  rs=null;
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri)"));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and MONTH(jour)=(MONTH(now())-1)"
																									+ " group by uri )"));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and YEAR(jour)=(YEAR(now())-1)"
																									+ " group by uri )"));
							break;
				default:
					break;				
				}
				
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
	
	/**
	 * 
	 * @param periodeRange donne le nombre de ligne d'action de type convert ayant un identifiant ConceptScheme non null
	 * @return
	 */
	
	public Integer getSizeIdConvert(Range periodeRange){

		Integer resultat=0;

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertisTotaux";
				ResultSet  rs=null;
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' group by uri)"));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and MONTH(jour)=(MONTH(now())-1)"
																									+ " group by uri )"));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and YEAR(jour)=(YEAR(now())-1)"
																									+ " group by uri )"));
							break;
				default:
					break;				
				}
				
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