package fr.sparna.rdf.skosplay;

public class ConvertFormData {

	public static final String KEY = ConvertFormData.class.getCanonicalName();

	protected String errorMessagefile;
	protected String googleId;
	protected String baseUrl;
	protected String defaultLanguage;

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

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

}
