package fr.sparna.rdf.extractor;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.Rio;

public class RdfExtractor implements DataExtractor {

	@Override
	public void extract(DataExtractionSource in, RDFHandler out) throws DataExtractionException {
		Optional<RDFFormat> inFormat = Rio.getParserFormatForMIMEType(in.getContentType());
		
		if(!inFormat.isPresent()) {
			throw new DataExtractionException("Unable to process source with content type "+in.getContentType());
		}
		
		RDFParser parser = RDFParserRegistry.getInstance().get(inFormat.get()).get().getParser();
		parser.setRDFHandler(out);
		try {
			parser.parse(new ByteArrayInputStream(in.getContent()), in.getContentType());
		} catch (Exception e) {
			throw new DataExtractionException(e);
		}
	}

	@Override
	public boolean canHandle(DataExtractionSource source)
	throws DataExtractionException {
		return Rio.getParserFormatForMIMEType(source.getContentType()).isPresent();
	}

}
