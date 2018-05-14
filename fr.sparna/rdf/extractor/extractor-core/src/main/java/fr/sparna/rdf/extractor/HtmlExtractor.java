package fr.sparna.rdf.extractor;

import org.eclipse.rdf4j.rio.RDFHandler;

public class HtmlExtractor implements DataExtractor {

	@Override
	public void extract(DataExtractionSource in, RDFHandler out) throws DataExtractionException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canHandle(DataExtractionSource source)
	throws DataExtractionException {
		if(source.getContentType().contains("text/html")) {
			return true;
		} else {
			return false;
		}
	}

}
