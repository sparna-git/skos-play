package fr.sparna.rdf.skosplay;

public enum OutputType {

	HTML,
	PDF;
	
	/**
	 * Tests whether the application configuration has enabled this display
	 * @return true if the display is enabled
	 */
	public boolean isEnabled() {
		return SkosPlayConfig.getInstance().getDisabledOutputType() == null || !SkosPlayConfig.getInstance().getDisabledOutputType().equals(this.toString());
	}	
	
}
