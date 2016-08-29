package fr.sparna.rdf.datapress;

import org.eclipse.rdf4j.rio.RDFHandler;

public interface DataPress {
	
	public void press(
			byte[] in,
			String documentUrl,
			RDFHandler out
	) throws DataPressException;
	
	public void press(
			String uri,
			RDFHandler out
	) throws DataPressException;
	
}
