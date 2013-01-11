package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of SPARQLQueryBuilderIfc that wraps a String containing the SPARQL query.
 * 
 * @author Thomas Francart
 *
 */
public class StringSPARQLQueryBuilder implements SPARQLQueryBuilderIfc {

	protected String sparql;	
	
	/**
	 * Construct a StringSPARQLQueryBuilder with a String holding the query
	 * 
	 * @param sparql The String holding the query.
	 */
	public StringSPARQLQueryBuilder(String sparql) {
		super();
		this.sparql = sparql;
	}
	
	/**
	 * Turns a List<String> into a List<StringSPARQLQueryBuilder>
	 * 
	 * @param strings
	 * 
	 * @return a list of StringSPARQLQueryBuilder, each wrapping one of the Strings.
	 */
	public static List<StringSPARQLQueryBuilder> fromStringList(List<String> strings) {
		if(strings == null) {
			return null;
		}
		
		ArrayList<StringSPARQLQueryBuilder> result = new ArrayList<StringSPARQLQueryBuilder>();
		for (String aString : strings) {
			result.add(new StringSPARQLQueryBuilder(aString));
		}
		return result;
	}

	/**
	 * Returns the String holding the query.
	 */
	@Override
	public String getSPARQL() {
		return sparql;
	}

}
