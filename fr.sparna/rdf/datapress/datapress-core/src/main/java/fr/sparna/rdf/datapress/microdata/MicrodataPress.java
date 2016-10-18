package fr.sparna.rdf.datapress.microdata;

import java.net.URI;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import fr.sparna.rdf.datapress.DataPress;
import fr.sparna.rdf.datapress.DataPressException;
import fr.sparna.rdf.datapress.DataPressSource;
import fr.sparna.rdf.microdata.parser.MicrodataParser;
import fr.sparna.rdf.microdata.parser.MicrodataParserException;

public class MicrodataPress implements DataPress {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public MicrodataPress() {
	}
	
	public void press(DataPressSource in, RDFHandler out) throws DataPressException {
		log.debug(this.getClass().getSimpleName()+" - Pressing {}", in.getIri());
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

		log.debug(this.getClass().getSimpleName()+" - Done pressing {} in {}ms", in.getIri(), System.currentTimeMillis()-start);
	}

}
