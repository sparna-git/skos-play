package fr.sparna.rdf.skosplay.log;

import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe permet de récuper la requête sql correspondant à l'identifiant passé en paramètre.
 * Ces requêtes sont paramétrer dans un Map dans le fichier spring-dispatcher-servlet.xml.
 * Elle est utilisée par le constructeur de SQLLogComptage et SQLLogDao 
 * 
 * @author clarvie
 *
 */

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
