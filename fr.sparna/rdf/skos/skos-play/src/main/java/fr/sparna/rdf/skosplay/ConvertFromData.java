package fr.sparna.rdf.skosplay;

public class ConvertFromData {

public static final String KEY = ConvertFromData.class.getCanonicalName();

protected String errorMessagefile;

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

}
