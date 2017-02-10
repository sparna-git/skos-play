package fr.sparna.rdf.skosplay;

import java.util.Date;

import java.util.Map;


public class UploadFormData {

	public static final String KEY = UploadFormData.class.getCanonicalName();
	
	protected String errorMessage;
	
	protected Map<Date, Integer> comptLogList;

	public Map<Date, Integer> getComptLogList() {
		return comptLogList;
	}

	public void setComptLogList(Map<Date, Integer> comptLogList) {
		this.comptLogList = comptLogList;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}	
	
	/**
	 * Expose application config to the page
	 */
	public SkosPlayConfig getSkosPlayConfig() {
		return SkosPlayConfig.getInstance();
	}
	
}
