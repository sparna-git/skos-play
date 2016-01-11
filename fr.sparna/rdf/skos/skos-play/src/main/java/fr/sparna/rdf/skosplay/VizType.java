package fr.sparna.rdf.skosplay;

public enum VizType {

	PARTITION(true),
	TREELAYOUT(true),
	SUNBURST(true),
	AUTOCOMPLETE(false);
	
	protected boolean requiresHierarchy;

	private VizType(boolean requiresHierarchy) {
		this.requiresHierarchy = requiresHierarchy;
	}
	
	/**
	 * Tests whether the application configuration has enabled this viz
	 * @return true if the viz is enabled
	 */
	public boolean isEnabled() {
		return SkosPlayConfig.getInstance().getDisabledVisualisations() == null || !SkosPlayConfig.getInstance().getDisabledVisualisations().contains(this.toString());
	}

	public boolean isRequiresHierarchy() {
		return requiresHierarchy;
	}

	public void setRequiresHierarchy(boolean requiresHierarchy) {
		this.requiresHierarchy = requiresHierarchy;
	}
	
	public static boolean needHierarchyCheck() {
		for (VizType type : VizType.values()) {
			if(type.requiresHierarchy && type.isEnabled()) {
				return true;
			}
		}
		return false;
	}
	
}
