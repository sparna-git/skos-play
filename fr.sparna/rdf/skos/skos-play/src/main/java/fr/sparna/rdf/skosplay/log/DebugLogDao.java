package fr.sparna.rdf.skosplay.log;

import java.util.ArrayList;
import java.util.List;

public class DebugLogDao implements LogDaoIfc {
	
	ArrayList<LogEntry> lesLogs = new ArrayList<LogEntry>();
	
	@Override
	public void writeLog(LogEntry entry) {
		System.out.println(entry);
		lesLogs.add(entry);
	}

	@Override
	public List<LogEntry> ListAllLog() {
		return lesLogs;
	}
	@Override
	public int getConvertNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPrintNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) {
		
		SQLLogDao sql=new SQLLogDao();
		
		LogEntry entry=new LogEntry();
		/*entry.setName("corien");
		entry.setZip(true);
		entry.setXls(true);
		entry.setGraph(false);
		entry.setOutput("SKOS");
		entry.setActiontype("convert");
		sql.writeLog(entry);
		entry.setName("EDouard");
		entry.setZip(true);
		entry.setXls(true);
		entry.setGraph(false);
		entry.setOutput("RDF");
		entry.setActiontype("convert");
		sql.writeLog(entry);*/
		List<LogEntry> ListeRetour = sql.ListAllLog();
		System.out.println(ListeRetour);
		int nbre=sql.getConvertNumber();
		System.out.println("conversion:"+nbre);
		int nbre2=sql.getPrintNumber();
		System.out.println("conversion:"+nbre2);
		
    }

	@Override
	public String getLastConversionDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastPrintDate() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
