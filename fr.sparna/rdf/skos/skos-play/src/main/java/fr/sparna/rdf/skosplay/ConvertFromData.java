package fr.sparna.rdf.skosplay;

public class ConvertFromData {

	public static final String KEY = ConvertFromData.class.getCanonicalName();

	protected String errorMessagefile;
	protected String googleId;
	protected String baseUrl;

	public String getErrorMessagefile() {
		return errorMessagefile;
	}

	public void setErrorMessagefile(String errorMessage) {
		this.errorMessagefile = errorMessage;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Expose application config to the page
	 */
	public SkosPlayConfig getSkosPlayConfig() {
		return SkosPlayConfig.getInstance();
	}

}
