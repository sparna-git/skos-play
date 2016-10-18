package fr.sparna.rdf.datapress;

public interface NotifyingDataPress extends DataPress {

	public void addListener(DataPressListener listener);
	
	public void removeListener(DataPressListener listener);
	
}
