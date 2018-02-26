package fr.sparna.rdf.skosplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PrintFormData {

	protected Map<String, String> languages = new TreeMap<String, String>();
	protected Map<LabeledResource, Integer> conceptCountByConceptSchemes = new TreeMap<LabeledResource, Integer>();
	protected List<String> warningMessages = new ArrayList<String>();
	protected String successMessage;
	protected boolean enableHierarchical = true;
	protected boolean enableTranslations = true;
	protected boolean enableMappings = true;
	protected String loadedDataName;
	protected String loadedDataLicense;
	protected boolean owl2skos;
	protected int nbConcept;
	

	

	public int getNbConcept() {
		return nbConcept;
	}

	public void setNbConcept(int nbConcept) {
		this.nbConcept = nbConcept;
	}

	
	
	
	public boolean isOwl2skos() {
		return owl2skos;
	}

	public void setOwl2skos(boolean owl2skos) {
		this.owl2skos = owl2skos;
	}

	/**
	 * Access a display type based on its name, because we can't access constant from JSTL / EL
	 */
	public Map<String, DisplayType> getDisplayType() {
		Map<String, DisplayType> map = new HashMap<String, DisplayType>();
		for (DisplayType type : DisplayType.values()) {
			map.put(type.toString(), type);
		}
		return map;
	}
	
	/**
	 * Access a viz type based on its name, because we can't access constant from JSTL / EL 
	 */
	public Map<String, VizType> getVizType() {
		Map<String, VizType> map = new HashMap<String, VizType>();
		for (VizType type : VizType.values()) {
			map.put(type.toString(), type);
		}
		return map;
	}
	
	/**
	 * Access an output type based on its name, because we can't access constant from JSTL / EL 
	 */
	public Map<String, OutputType> getOutputType() {
		Map<String, OutputType> map = new HashMap<String, OutputType>();
		for (OutputType type : OutputType.values()) {
			map.put(type.toString(), type);
		}
		return map;
	}
	
	
	public Map<String, String> getLanguages() {
		return languages;
	}

	public void setLanguages(Map<String, String> languages) {
		this.languages = languages;
	}

	public Map<LabeledResource, Integer> getConceptCountByConceptSchemes() {
		return conceptCountByConceptSchemes;
	}

	public void setConceptCountByConceptSchemes(Map<LabeledResource, Integer> conceptCountByConceptSchemes) {
		this.conceptCountByConceptSchemes = conceptCountByConceptSchemes;
	}

	public List<String> getWarningMessages() {
		return warningMessages;
	}

	public void setWarningMessages(List<String> warningMessages) {
		this.warningMessages = warningMessages;
	}

	public boolean isEnableHierarchical() {
		return enableHierarchical;
	}

	public void setEnableHierarchical(boolean enableHierarchical) {
		this.enableHierarchical = enableHierarchical;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public boolean isEnableTranslations() {
		return enableTranslations;
	}

	public void setEnableTranslations(boolean enableTranslations) {
		this.enableTranslations = enableTranslations;
	}

	public boolean isEnableMappings() {
		return enableMappings;
	}

	public void setEnableMappings(boolean enableMappings) {
		this.enableMappings = enableMappings;
	}

	public String getLoadedDataName() {
		return loadedDataName;
	}

	public void setLoadedDataName(String loadedDataName) {
		this.loadedDataName = loadedDataName;
	}

	public String getLoadedDataLicense() {
		return loadedDataLicense;
	}

	public void setLoadedDataLicense(String loadedDataLicense) {
		this.loadedDataLicense = loadedDataLicense;
	}

}
