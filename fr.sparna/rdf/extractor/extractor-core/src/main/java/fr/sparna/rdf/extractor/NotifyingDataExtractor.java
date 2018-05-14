package fr.sparna.rdf.extractor;

/**
 * A DataExtractor that can notify DataExtractorListeners
 * @author Thomas Francart
 *
 */
public interface NotifyingDataExtractor extends DataExtractor {

	/**
	 * Adds a listener
	 * @param listener
	 */
	public void addListener(DataExtractorListener listener);
	
	/**
	 * Removes a listener
	 * @param listener
	 */
	public void removeListener(DataExtractorListener listener);
	
}
