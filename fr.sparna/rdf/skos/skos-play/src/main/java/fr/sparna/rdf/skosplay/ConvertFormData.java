package fr.sparna.rdf.skosplay;

import java.util.List;

import com.google.api.services.drive.model.File;

public class ConvertFormData {

	public static final String KEY = ConvertFormData.class.getCanonicalName();

	// error messages to display in alerts
	protected String errorMessagefile;
	// default language of the user to initialize the language selection
	protected String defaultLanguage;
	// liste de fichiers issus du google drive
	protected List<File> googleFiles;
	
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

	public List<File> getGoogleFiles() {
		return googleFiles;
	}

	public void setGoogleFiles(List<File> googleFiles) {
		this.googleFiles = googleFiles;
	}
	
}
