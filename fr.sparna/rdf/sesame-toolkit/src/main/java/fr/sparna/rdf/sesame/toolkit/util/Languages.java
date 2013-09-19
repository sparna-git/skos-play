package fr.sparna.rdf.sesame.toolkit.util;

public final class Languages {

	private static Languages instance;
	
	private Languages() {
		
	}
	
	public static Languages getInstance() {
		if(instance == null) {
			instance = new Languages();
		}
		return instance;
	}
	
	
	
}
