package fr.sparna.rdf.skosplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PrintFormData {

	protected List<String> languages = new ArrayList<String>();
	protected Map<LabeledResource, Integer> conceptCountByConceptSchemes = new TreeMap<LabeledResource, Integer>();
	protected String warningMessage;
	protected String successMessage;
	protected boolean enableHierarchical = true;
	
	public List<String> getLanguages() {
		return languages;
	}
	
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

	public Map<LabeledResource, Integer> getConceptCountByConceptSchemes() {
		return conceptCountByConceptSchemes;
	}

	public void setConceptCountByConceptSchemes(Map<LabeledResource, Integer> conceptCountByConceptSchemes) {
		this.conceptCountByConceptSchemes = conceptCountByConceptSchemes;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
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
	
}
