package fr.sparna.rdf.skos.xls2skos.reconcile;

import java.util.List;

public interface ReconcileQueryIfc {

	/**
	 * String to search for
	 * @return
	 */
	public String getQuery();
	
	/**
	 * Optional types of the expected result
	 * @return
	 */
	public List<String> getTypes();
	
}
