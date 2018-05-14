package fr.sparna.rdf.extractor;

import java.util.List;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extractor delegating the extraction work to a list of delegate extractors.
 * @author Thomas Francart
 *
 */
public class CompositeExtractor implements DataExtractor {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected List<DataExtractor> extractors;	
	
	public CompositeExtractor(List<DataExtractor> extractors) {
		super();
		this.extractors = extractors;
	}

	public CompositeExtractor() {
		super();
	}

	@Override
	public void extract(
			DataExtractionSource in,
			RDFHandler out
	) throws DataExtractionException {
		log.debug("Extract from {}", in.getIri());
		
		out.startRDF();
		
		if(extractors != null) {
			for (DataExtractor anExtractor : this.extractors) {
				try {
					anExtractor.extract(in , out);
				} catch (Exception e) {
					log.error("Error in extractor {} : {}",anExtractor.getClass().getSimpleName(), e.getMessage());
					e.printStackTrace();
				}			
			}
		}
		
		out.endRDF();
	}
	

	/**
	 * A CompositeExtractor can handle a source if at least one of the delegate source can handle the source.
	 */
	@Override
	public boolean canHandle(DataExtractionSource source) throws DataExtractionException {
		boolean result = false;
		if(extractors != null) {
			for (DataExtractor anExtractor : this.extractors) {
				if(anExtractor.canHandle(source)) {
					result = true;
				}
			}
		}
		return result;
	}

	public List<DataExtractor> getExtractors() {
		return extractors;
	}

	public void setExtractors(List<DataExtractor> extractors) {
		this.extractors = extractors;
	}
	
}
