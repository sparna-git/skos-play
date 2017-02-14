package fr.sparna.rdf.skosplay.log;

import java.util.HashMap;
import java.util.Map;

public class SQLQueryRegistry {

	protected Map<String, String> queriesById = new HashMap<String, String>();
	
	public SQLQueryRegistry(Map<String, String> queriesById) {
		super();
		this.queriesById = queriesById;
	}

	public String getSQLQuery(String queryId) {
		return queriesById.get(queryId);
	}
	
}
