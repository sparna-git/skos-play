package fr.sparna.rdf.extractor;

import org.eclipse.rdf4j.rio.RDFHandler;

/**
 * An extractor capable of extracting structured RDF data from a source.
 * @author Thomas Francart
 *
 */
public interface DataExtractor {
	
	public void extract(
			DataExtractionSource in,
			RDFHandler out
	) throws DataExtractionException;
	
	public boolean canHandle(DataExtractionSource source)
	throws DataExtractionException;
	
}
