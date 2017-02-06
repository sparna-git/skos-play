package fr.sparna.rdf.skosplay.log;


import java.util.List;
import java.util.Map;


public interface LogDaoIfc {
	
	
	public void insertLog(LogEntry entry);
	
	
	public Map<String, Integer>  ListAllLog();
	
	
	
}
