package fr.sparna.rdf.skos.toolkit;

import org.eclipse.rdf4j.model.IRI;

public interface SKOSNodeSortCriteriaReader {

	public String getLang();
	
	public String readSortCriteria(IRI node);
	
}
