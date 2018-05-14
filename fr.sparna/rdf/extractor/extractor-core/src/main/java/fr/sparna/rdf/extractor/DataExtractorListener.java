package fr.sparna.rdf.extractor;

/**
 * Listens for event in the processing of a DataExtractorSource
 * @author Thomas Francart
 *
 */
public interface DataExtractorListener {

	/**
	 * Notified when the processing of the input source begins.
	 * @param source
	 */
	public void begin(DataExtractionSource source);
	
	/**
	 * Notified when the processing of the input source ends, with either a successful result of not.
	 * @param source
	 */
	public void end(DataExtractionSource source, boolean success);
	
}
