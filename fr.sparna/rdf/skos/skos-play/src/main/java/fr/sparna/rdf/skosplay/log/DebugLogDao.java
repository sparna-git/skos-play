package fr.sparna.rdf.skosplay.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class DebugLogDao implements LogDaoIfc {
	
	ArrayList<LogEntry> lesLogs = new ArrayList<LogEntry>();
	
	@Override
	public void insertLog(LogEntry entry) {
		
		System.out.println(entry);
		lesLogs.add(entry);
	}

	@Override
	public Map<String, Integer> ListAllLog() {
		return null;
	}
	
	
}
