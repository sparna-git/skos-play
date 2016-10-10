package fr.sparna.rdf.datapress.repository;

import fr.sparna.rdf.datapress.DataPressSource;

public interface DataPressRepository {

	public void beginBatch();
	
	public void begin(DataPressSource source);
	
	public void end(DataPressSource source);
	
	public void endBatch();
	
}
