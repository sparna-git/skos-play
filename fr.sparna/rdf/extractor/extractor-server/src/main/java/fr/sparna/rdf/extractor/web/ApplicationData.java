package fr.sparna.rdf.extractor.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Everything that needs to be loaded at an application-wide level
 * 
 * @author Thomas Francart
 */
public class ApplicationData {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String buildVersion;	
	protected String buildTimestamp;	
	
	
	/**
	 * Expose SkosPlayConfig in a getter so that it is accessible in JSP pages
	 * @return
	 */
	public Config getSkosPlayConfig() {
		return Config.getInstance();
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	public void setBuildTimestamp(String buildTimestamp) {
		this.buildTimestamp = buildTimestamp;
	}
	
}
