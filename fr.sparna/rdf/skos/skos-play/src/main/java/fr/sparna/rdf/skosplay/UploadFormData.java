package fr.sparna.rdf.skosplay;

public class UploadFormData {

	public static final String KEY = UploadFormData.class.getCanonicalName();
	
	protected String errorMessage;

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
