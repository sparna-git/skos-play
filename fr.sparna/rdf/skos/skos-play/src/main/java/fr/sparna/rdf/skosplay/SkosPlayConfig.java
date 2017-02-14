package fr.sparna.rdf.skosplay;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fr.sparna.rdf.skosplay.log.SQLQueryRegistry;


public class SkosPlayConfig {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// singleton instance
	private static SkosPlayConfig instance;
	
	protected int conceptsLimit;	
	
	@Autowired
	protected ApplicationData applicationData;	
	
	// displays, visualisations, and output types that are disabled
	protected List<String> disabledDisplays = new ArrayList<String>();
	protected List<String> disabledVisualisations = new ArrayList<String>();
	protected String disabledOutputType;
	
	// indicates the application works in publishing mode
	// that is, disable the possibility to upload other data than the example ones
	protected boolean publishingMode = false;
	
	protected File customCss;
	
	// application title to be inserted in HTML pages
	protected String applicationTitle;
	
	protected SQLQueryRegistry sqlQueryRegistry;
	
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

	@Value("${skosplay.concepts.limit:0}")
	public void setConceptsLimit(int conceptsLimit) {
		this.conceptsLimit = conceptsLimit;
	}

	public ApplicationData getApplicationData() {
		return applicationData;
	}

	public void setApplicationData(ApplicationData applicationData) {
		this.applicationData = applicationData;
	}

	public List<String> getDisabledDisplays() {
		return disabledDisplays;
	}

	// handle spaces in comma-separated list of values.
	// see http://stackoverflow.com/questions/5274362/reading-a-dynamic-property-list-into-a-spring-managed-bean
	// final ':' default to empty String
	@Value("#{T(java.util.Arrays).asList('${skosplay.display.disabled:}')}")
	public void setDisabledDisplays(List<String> disabledDisplays) {
		log.info("Disabling displays : "+disabledDisplays);
		this.disabledDisplays = disabledDisplays;
	}

	// final ':' default to empty String
	@Value("#{T(java.util.Arrays).asList('${skosplay.viz.disabled:}')}")
	public void setDisabledVisualisations(List<String> disabledVisualisations) {
		log.info("Disabling visualisations : "+disabledVisualisations);
		this.disabledVisualisations = disabledVisualisations;
	}

	public List<String> getDisabledVisualisations() {
		return disabledVisualisations;
	}	

	public String getDisabledOutputType() {
		return disabledOutputType;
	}

	// final ':' default to empty String
	@Value("${skosplay.output.disabled:}")
	public void setDisabledOutputType(String disabledOutputType) {
		this.disabledOutputType = disabledOutputType;
	}

	public boolean isPublishingMode() {
		return publishingMode;
	}

	@Value("${skosplay.mode.publishing:false}")
	public void setPublishingMode(boolean publishingMode) {
		log.info("Set publishing mode : "+publishingMode);
		this.publishingMode = publishingMode;
	}

	public File getCustomCss() {
		return customCss;
	}

	@Value("${skosplay.customCss.path:#{null}}")
	public void setCustomCssPath(String customCssPath) {
		log.info("Set custom CSS Path : "+customCssPath);
		if(customCssPath != null) {
			File f = new File(customCssPath);
			if(!f.exists()) {
				log.error("Custom CSS file does not exist or cannot be read : "+f.getAbsolutePath());
				return;
			}
			if(!f.isFile()) {
				log.error("Custom CSS path is not a file : "+f.getAbsolutePath());
				return;
			}
			this.customCss = f;
		}
	}

	public String getApplicationTitle() {
		return applicationTitle;
	}

	@Value("${skosplay.application.title:SKOS Play! - Thesaurus & Taxonomies}")
	public void setApplicationTitle(String applicationTitle) {
		this.applicationTitle = applicationTitle;
	}

	public SQLQueryRegistry getSqlQueryRegistry() {
		return sqlQueryRegistry;
	}

	@Autowired
	public void setSqlQueryRegistry(SQLQueryRegistry sqlQueryRegistry) {
		this.sqlQueryRegistry = sqlQueryRegistry;
	}
	
}
