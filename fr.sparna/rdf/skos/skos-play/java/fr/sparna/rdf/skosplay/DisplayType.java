package fr.sparna.rdf.skosplay;

public enum DisplayType {

	ALPHABETICAL(false, false, false),
	ALPHABETICAL_EXPANDED(false, false, false),
	HIERARCHICAL(true, false, false),
	HIERARCHICAL_TREE(true, false, false),
	CONCEPT_LISTING(false, false, false),
	TRANSLATION_TABLE(false, true, false),
	COMPLETE_MONOLINGUAL(false, true, false),
	COMPLETE_MULTILINGUAL(true, true, false),
	PERMUTED_INDEX(false, false, false),
	KWIC_INDEX(false, false, false),
	ALIGNMENT_BY_SCHEME(false, false, true),
	ALIGNMENT_ALPHA(false, false, true);
	
	protected boolean requiresHierarchy;
	protected boolean requiresTranslation;
	protected boolean requiresAlignment;	
	
	
	private DisplayType(
			boolean requiresHierarchy,
			boolean requiresTranslation, 
			boolean requiresAlignment
	) {
		this.requiresTranslation = requiresTranslation;
		this.requiresHierarchy = requiresHierarchy;
		this.requiresAlignment = requiresAlignment;
	}

	/**
	 * Tests whether the application configuration has enabled this display
	 * @return true if the display is enabled
	 */
	public boolean isEnabled() {
		return SkosPlayConfig.getInstance().getDisabledDisplays() == null || !SkosPlayConfig.getInstance().getDisabledDisplays().contains(this.toString());
	}

	public boolean isRequiresTranslation() {
		return requiresTranslation;
	}

	public void setRequiresTranslation(boolean requiresTranslation) {
		this.requiresTranslation = requiresTranslation;
	}

	public boolean isRequiresHierarchy() {
		return requiresHierarchy;
	}

	public void setRequiresHierarchy(boolean requiresHierarchy) {
		this.requiresHierarchy = requiresHierarchy;
	}
	
	public boolean isRequiresAlignment() {
		return requiresAlignment;
	}

	public void setRequiresAlignment(boolean requiresAlignment) {
		this.requiresAlignment = requiresAlignment;
	}

	public static boolean needHierarchyCheck() {
		for (DisplayType type : DisplayType.values()) {
			if(type.requiresHierarchy && type.isEnabled()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean needTranslationCheck() {
		for (DisplayType type : DisplayType.values()) {
			if(type.requiresTranslation && type.isEnabled()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean needAlignmentCheck() {
		for (DisplayType type : DisplayType.values()) {
			if(type.requiresAlignment && type.isEnabled()) {
				return true;
			}
		}
		return false;
	}
	
}
