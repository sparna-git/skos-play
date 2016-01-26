package fr.sparna.rdf.skos.toolkit;

import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;

public interface SKOSNodeSortCriteriaReader {

	public String getLang();
	
	public String readSortCriteria(java.net.URI node) throws SparqlPerformException;
	
}
