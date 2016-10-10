package fr.sparna.rdf.skos.xls2skos;

import org.eclipse.rdf4j.model.Model;

public interface ModelWriterIfc {

	public void saveGraphModel(String graph, Model model);
	
	public void beginWorkbook();
	
	public void endWorkbook();
	
	

}