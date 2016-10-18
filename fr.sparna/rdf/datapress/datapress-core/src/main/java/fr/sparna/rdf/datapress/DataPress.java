package fr.sparna.rdf.datapress;

import org.eclipse.rdf4j.rio.RDFHandler;

public interface DataPress {
	
	public void press(
			DataPressSource in,
			RDFHandler out
	) throws DataPressException;
	
}
