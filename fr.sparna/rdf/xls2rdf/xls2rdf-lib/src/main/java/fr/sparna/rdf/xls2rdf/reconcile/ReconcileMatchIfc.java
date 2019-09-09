package fr.sparna.rdf.xls2rdf.reconcile;

import java.util.List;

public interface ReconcileMatchIfc {

	public String getId();
	
	public String getName();
	
	public List<String> getTypes();
	
	public double getScore();
	
	public boolean isMatch();
	
}
