package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cette classe regroupe tous les comptages pour les conversions et les prints.
 * Elle est utilisée dans le LogController.java
 * 
 * * @author clarvie
 *
 */

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
	/**
	 * retourne le nombre de conversion et de print par mois ou par jour ou par année
	 * @param periodeRange
	 * @return
	 */
	public List<HistogrammeData> getNumberConvertOrPrintPerDayMonthYear(Range periodeRange) {

		List<HistogrammeData> resultat=new ArrayList<HistogrammeData>();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "NumberConvertOrPrintPerDayMonthYear";
				ResultSet rs=null;
				switch(periodeRange){
				
						case ALLTIME:
									rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_", ""));
									break;
						case MONTH:
									rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_","MONTH"));
							
									break;
						case YEAR:
									rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_","YEAR"));
									break;
							default:
								break;				
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

	/**
	 * Retourne un map contenant le format(pdf, html ou datavize) des prints et le nombre de fois qu'il a été printé
	 * @return
	 */
	public Map<String, Integer> getNumberOfFormat() {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		String sql = this.queryRegistry.getSQLQuery("NumberOfFormat").replaceAll("_diff_", "<>");
		
		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(sql);
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

	/**
	 * retourne un map contenant tous les langues des prints et leurs nombres
	 * @return
	 */
	public Map<String, Integer> getNumberOfPrintlanguage() {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		String sql = this.queryRegistry.getSQLQuery("NumberOfPrintLanguage");
		
		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(sql);				

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
	public Map<String, Integer> getNumberOfConvertlanguage() {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		String sql = this.queryRegistry.getSQLQuery("NumberOfConvertLanguage");
		
		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(sql);				

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
	/**
	 * retourne un map contenant tous les rendu en sortie des print/visualize et autant de fois qu'ils ont été printés ou visualisés
	 * @return
	 */
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
	/**
	 * retourne le nombre de print et de conversion pour les 365 derniers jours
	 * @return
	 */
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
	
	public ListingData getUrlConverted( Range periodeRange) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsConvertis";
				ResultSet  rs=null;
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null'  group by url "));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-30)"
																									+ " group by url "));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-365)"
																									+ " group by url "));
							break;
				default:
					break;				
				}
				
				while (rs.next()) {
					resultat.put(rs.getString("url"), rs.getInt("nombre"));
				}
				
				listing.setData(resultat);
				
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
	
	public ListingData getUrlPrint( Range periodeRange) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "UrlsPrint";
				ResultSet  rs=null;
				
				switch(periodeRange) {
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null'  group by url "));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-30)"
																									+ " group by url "));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-365)"
																									+ " group by url "));
							break;
				default:
					break;				
				}
							
				while (rs.next()) {
					resultat.put(rs.getString("url"), rs.getInt("nombre"));
				}
				
				listing.setData(resultat);
				//listing.setTotalLignes(getSizeUrlPrint(periodeRange));

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
	
	/*public Integer getSizeUrlPrint(Range periodeRange){

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
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-30)"
																									+ " group by url )"));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-365)"
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
	}*/
	/**
	 * 
	 * @param periodeRange donne le nombre de ligne url non null des actions de type convert 
	 * @return
	 */
	
	/*
	public Integer getSizeUrlConvert(Range periodeRange){

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
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-30)"
																									+ " group by url )"));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-365)"
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
	}*/
	/**
	 * 
	 * @param periodeRange donne la liste des identifiants ConceptScheme non null  de type print 
	 * @return
	 */
	
	public ListingData getUriPrint(int indexDebut,Range periodeRange) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdPrint";
				ResultSet  rs=null;
				switch(periodeRange) {
				
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null'  "
								 + "group by uri LIMIT 10 offset " +(indexDebut)));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-30)"
																									+ " group by uri LIMIT 10 offset " +(indexDebut)));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-365)"
																									+ " group by uri  LIMIT 10 offset " +(indexDebut)));
							break;
				default:
					break;				
				}		

				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIndexDebut(indexDebut);
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizeUriPrint(periodeRange));

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
	
	public ListingData getUriConverted(int indexDebut, Range periodeRange) {

		Map<String, Integer> resultat=new HashMap<String, Integer>();

		ListingData listing=new ListingData();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "IdConvertis";
				ResultSet  rs=null;
				switch(periodeRange) {
				
				case ALLTIME:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null'  "
								 + "group by uri LIMIT 10 offset " +(indexDebut)));
							break;
				case MONTH:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-30)"
																									+ " group by uri LIMIT 10 offset " +(indexDebut)));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-365)"
																									+ " group by uri  LIMIT 10 offset " +(indexDebut)));
							break;
				default:
					break;				
				}		

				while (rs.next()) {
					resultat.put(rs.getString("uri"), rs.getInt("nombre"));
				}
				listing.setIndexDebut(indexDebut);
				listing.setIdlist(resultat);
				listing.setTotalLignes(getSizeUriConvert(periodeRange));

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
	
	public Integer getSizeUriPrint(Range periodeRange){

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
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-30)"
																									+ " group by uri )"));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-365)"
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
	
	public Integer getSizeUriConvert(Range periodeRange){

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
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-30)"
																									+ " group by uri )"));
					
							break;
				case YEAR:
							rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replace("_diff_", "<>'null' and jour>(now()-365)"
																									+ " group by uri )"));
							break;
				default:
					break;				
				}
				
				while (rs.next()) {
					resultat= rs.getInt("nombre");
				}
				System.out.println("convert:"+resultat);

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return resultat;
	}
	
	interface SQLHelper {
		
	}
	
	
}