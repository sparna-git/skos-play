package fr.sparna.rdf.datapress;

public interface DataPressListener {

	public void begin(DataPressSource source);
	
	public void end(DataPressSource source, boolean success);
	
}
