package fr.sparna.rdf.extractor.microdata;

import java.net.URI;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import fr.sparna.rdf.extractor.DataExtractionException;
import fr.sparna.rdf.extractor.DataExtractionSource;
import fr.sparna.rdf.extractor.DataExtractor;
import fr.sparna.rdf.extractor.HtmlExtractor;
import fr.sparna.rdf.microdata.parser.MicrodataParser;
import fr.sparna.rdf.microdata.parser.MicrodataParserException;

public class MicrodataExtractor extends HtmlExtractor implements DataExtractor {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public MicrodataExtractor() {
	}
	
	public void extract(DataExtractionSource in, RDFHandler out) throws DataExtractionException {
		log.debug(this.getClass().getSimpleName()+" - Extracting from {}", in.getIri());
		long start = System.currentTimeMillis();

		// I. Parse HTML
		Document dom = in.getContentDom();	
		
    	// II. Parse Microdata
		try {
			log.debug("Parsing Microdata");
			MicrodataParser mp = new MicrodataParser();
			mp.parse(
					URI.create(in.getIri().stringValue()),
					dom,
					out
			);
		} catch (MicrodataParserException e) {
			e.printStackTrace();
		}

		log.debug(this.getClass().getSimpleName()+" - Done extracting from {} in {}ms", in.getIri(), System.currentTimeMillis()-start);
	}

}
