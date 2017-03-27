package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.skosplay.SkosPlayConfig;

/**
 * Cette classe regroupe tous les comptages pour les conversions et les prints.
 * Elle est utilisée dans le LogController.java
 * 
 * * @author clarvie
 *
 */

public class SQLLogComptageDao implements LogDaoIfc {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected DBConnectionManager connections;
	
	protected SQLQueryRegistry queryRegistry;
	
	enum Range {
		ALLTIME,
		MONTH,
		YEAR,
		TODAY,
		LASTWEEK
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
				String sql = this.queryRegistry.getSQLQuery(QUERY_ID);
				log.trace(sql);
				ResultSet rs = stmt.executeQuery(sql);

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
				String sql =null;
			
				switch(periodeRange){
				
						case ALLTIME:
									sql=this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_", "").replaceAll("_LIM_", "where jour <= NOW() LIMIT 30");
									break;
						case MONTH:
									sql=this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_","MONTH").replaceAll("_LIM_","");
									break;
						case YEAR:
									sql=this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_","YEAR").replaceAll("_LIM_","");
									break;
							default:
								break;				
				}	
				log.trace(sql);
				rs = stmt.executeQuery(sql);
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

		Map<String, Integer> resultat=new LinkedHashMap<String, Integer>();

		String sql = this.queryRegistry.getSQLQuery("NumberOfFormat").replaceAll("_diff_", "<>");
		log.trace(sql);
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
	public Map<String, Integer> getNumberOfPrintOrConvertLanguage(String type) {

		Map<String, Integer> resultat=new LinkedHashMap<String, Integer>();
		String sql=null;
		if(type.equals("print")){
			sql = this.queryRegistry.getSQLQuery("NumberOfPrintLanguage");
		}else if(type.equals("convert")){
			sql = this.queryRegistry.getSQLQuery("NumberOfConvertLanguage");
		}
		log.trace(sql);
		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(sql);				

				while (rs.next()) {	
					if(rs.getString("langue").equals("null")){
						resultat.put("pas de langue", rs.getInt("nombre"));
					}else{
						resultat.put(rs.getString("langue"), rs.getInt("nombre"));
					}
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

		Map<String, Integer> resultat=new LinkedHashMap<String, Integer>();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "NumberOfRendu";
				String sql=this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_diff_", "<>");
				log.trace(sql);
				ResultSet rs = stmt.executeQuery(sql);

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

		Map<String, Integer> resultat=new LinkedHashMap<String, Integer>();
		
		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "printConvertLast365Days";
				String sql=this.queryRegistry.getSQLQuery(QUERY_ID);
				log.trace(sql);
				ResultSet rs = stmt.executeQuery(sql);

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
	 *  donne la liste des url  non null  de type convert ou print par jour , mois ou année
	 * 
	 */
	
	public ListingData getConvertedOrPrintUrl( Range periodeRange,String jour, String type) {

		Map<String, Integer> resultat=new LinkedHashMap<String, Integer>();

		ListingData listing=new ListingData();
		String QUERY_ID =null;
		if(type.equals("convert")){
			QUERY_ID = "UrlsConvertis";
		}else if(type.equals("print")){
			QUERY_ID = "UrlsPrint";
		}
		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				ResultSet  rs=null;
				String sql=null;
				switch(periodeRange) {
				case ALLTIME:
							if(jour.equals("default")){
								sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", " ");
								
								}else{
								sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour='"+jour+"'");
								
								}
							break;
				case MONTH:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()-30)");
							break;
				case YEAR:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()-365)");
							break;
				case TODAY:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour=(now())");
							break;
				case LASTWEEK:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()-7)");
							break;
				default:
					break;				
				}
				log.trace(sql);
				rs = stmt.executeQuery(sql);
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
	 * @param periodeRange donne la liste des identifiants ConceptScheme non null  de type print ou convert
	 * @return
	 */
	
	public ListingData getConvertOrPrintUri(Integer indexDebut,Range periodeRange, String jour, String type) {

		Map<String, Integer> resultat=new LinkedHashMap<String, Integer>();

		ListingData listing=new ListingData();
		String QUERY_ID =null;
		if(type.equals("convert")){
			QUERY_ID = "IdConvertis";
		}else if(type.equals("print")){
			QUERY_ID = "IdPrint";
		}

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				ResultSet  rs=null;
				String sql=null;
				switch(periodeRange) {
				
				case ALLTIME:
							if(jour.equals("default")){
								sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", " ").replace("_OFFSET_",indexDebut.toString());
								
								}else{
								sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour='"+jour+"'").replace("_OFFSET_",indexDebut.toString());
								
								}

							break;
				case MONTH:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour(now()-30)").replace("_OFFSET_",indexDebut.toString());			
							break;
				case YEAR:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()-365)").replace("_OFFSET_",indexDebut.toString());
							break;
							
				case TODAY:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour=(now())").replace("_OFFSET_",indexDebut.toString());
							break;
				case LASTWEEK:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()-7)").replace("_OFFSET_",indexDebut.toString());
							break;
				default:
					break;				
				}		
				log.trace(sql);
				rs = stmt.executeQuery(sql);
		
				while (rs.next()) {			

					resultat.put(rs.getString("uri"), rs.getInt("nombre"));

				}
				listing.setIdlist(resultat);
				if(type.equals("convert")){
					listing.setTotalLignes(getSizeConvertOrPrintUri(periodeRange,jour,"convert"));
				}else if(type.equals("print")){
					listing.setTotalLignes(getSizeConvertOrPrintUri(periodeRange,jour,"print"));
				}
				

			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} 

		return listing;
	}
	/**
	 * 
	 * @param periodeRange donne le nombre de ligne d'action de type print ou convert ayant un identifiant ConceptScheme non null
	 * @return
	 */
	
	public Integer getSizeConvertOrPrintUri(Range periodeRange, String jour, String type){

		Integer resultat=0;
		String QUERY_ID =null;
		ResultSet  rs=null;
		String sql=null;
		if(type.equals("convert")){
			QUERY_ID = "IdConvertisTotaux";
		}else if(type.equals("print")){
			QUERY_ID = "IdPrintTotaux";
		}
		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				
				switch(periodeRange) {
				case ALLTIME:
					if(jour.equals("default")){
					    sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", " ");
					}else{
						sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour='"+jour+"' ");
					}
					break;
				case MONTH:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()-30) ");
							break;
				case YEAR:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()-365) ");
							break;
				case TODAY:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()) ");
							break;
				case LASTWEEK:
							sql=this.queryRegistry.getSQLQuery(QUERY_ID).replace("_DAY_", "and jour>(now()-7) ");
							break;
				default:
					break;				
				}
				log.trace(sql);
				rs = stmt.executeQuery(sql);
				
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