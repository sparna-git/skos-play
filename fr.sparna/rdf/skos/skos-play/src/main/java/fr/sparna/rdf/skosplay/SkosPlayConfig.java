package fr.sparna.rdf.skosplay;

import org.springframework.beans.factory.annotation.Autowired;


public class SkosPlayConfig {

	// singleton instance
	private static SkosPlayConfig instance;
	
	protected int conceptsLimit;	
	
	@Autowired
	protected ApplicationData applicationData;
	
	/**
	 * Singleton private constructor
	 */
	private SkosPlayConfig() {
	}
	
	/**
	 * Singleton Access
	 */
	public static SkosPlayConfig getInstance() {
		if(instance == null) {
			instance = new SkosPlayConfig();
		}
		return instance;
	}

	public int getConceptsLimit() {
		return conceptsLimit;
	}

	public void setConceptsLimit(int conceptsLimit) {
		this.conceptsLimit = conceptsLimit;
	}

	public ApplicationData getApplicationData() {
		return applicationData;
	}

	public void setApplicationData(ApplicationData applicationData) {
		this.applicationData = applicationData;
	}
	
	
}
