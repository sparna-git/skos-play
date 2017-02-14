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
	
	DBConnectionManager connections= new DBConnectionManager();
	
	protected SQLQueryRegistry queryRegistry;
	
	public SQLLogComptageDao(SQLQueryRegistry queryRegistry) {
		super();
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
	
	public List<HistogrammeData> getNumberConvertOrPrintPerDayMonthYear(String day) {

		List<HistogrammeData> resultat=new ArrayList<HistogrammeData>();

		try(Connection connection = connections.getDBConnection()){
			try(Statement stmt = connection.createStatement()) {
				final String QUERY_ID = "NumberConvertOrPrintPerDayMonthYear";
				ResultSet rs = stmt.executeQuery(this.queryRegistry.getSQLQuery(QUERY_ID).replaceAll("_METH_", day));

				while (rs.next()) {			

					HistogrammeData histoData= new HistogrammeData();

					if(day.equals("MONTH")) {
						histoData.setDayOrMonthOrYear(Integer.toString(rs.getInt("periode")));
					} else if(day.equals("YEAR")) {
						histoData.setDayOrMonthOrYear(Integer.toString(rs.getInt("periode")));
					} else{
						histoData.setDayOrMonthOrYear(rs.getDate("periode").toString());
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
	
	
}