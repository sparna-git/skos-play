package fr.sparna.rdf.skosplay;

public class ConvertFormData {

	public static final String KEY = ConvertFormData.class.getCanonicalName();

	// error messages to display in alerts
	protected String errorMessagefile;
	// google document ID from the form submission
	protected String googleId;
	// base URL of the application for the example files
	protected String baseUrl;
	// default language of the user to initialize the language selection
	protected String defaultLanguage;
	
	protected boolean useZip;
	protected boolean useXl;
	protected String language;
	protected String output;
	protected String graph;

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

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

	public boolean isUseZip() {
		return useZip;
	}

	public void setUseZip(boolean useZip) {
		this.useZip = useZip;
	}

	public boolean isUseXl() {
		return useXl;
	}

	public void setUseXl(boolean useXl) {
		this.useXl = useXl;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	
}
