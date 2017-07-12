package fr.sparna.rdf.rdf4j.toolkit.query;

/**
 * Builds and return a SPARQL query of any type (SELECt, CONSTRUCT, ASK, INSERT, UPDATE...).
 * Concrete implementations can simply return a String, read from a File, read
 * in a Spring configuration, parse custom format, etc. 
 * 
 * @author Thomas Francart
 *
 */
public interface SparqlQueryBuilderIfc {

	/**
	 * Returns a String representing a valid SPARQL query
	 * 
	 * @return A String representing a valid SPARQL query
	 */
	public String getSPARQL();
	
}
