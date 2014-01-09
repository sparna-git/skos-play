package fr.sparna.rdf.skosplay;

import java.util.ArrayList;
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
	
}
