package fr.sparna.rdf.datapress.microdata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.sparna.rdf.datapress.DataPress;
import fr.sparna.rdf.datapress.DataPressBase;
import fr.sparna.rdf.datapress.DataPressException;
import fr.sparna.rdf.microdata.parser.MicrodataParser;
import fr.sparna.rdf.microdata.parser.MicrodataParserException;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class MicrodataPress extends DataPressBase implements DataPress {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public MicrodataPress() {
	}
	
	public void press(byte[] in, String documentUrl, RDFHandler out) throws DataPressException {
		log.debug(this.getClass().getSimpleName()+" - Pressing {}", documentUrl);
		long start = System.currentTimeMillis();

		// I. Parse HTML
		log.debug("Parsing HTML to DOM");
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(XmlViolationPolicy.ALTER_INFOSET);
		Document dom = null;
    	try {
			dom = builder.parse(new ByteArrayInputStream(in));
		} catch (SAXException e) {
			throw new DataPressException("Unable to get DOM representation of HTML file", e);
		} catch (IOException ignore) {
			ignore.printStackTrace();
		}	
		
    	// II. Parse Microdata
		try {
			log.debug("Parsing Microdata");
			MicrodataParser mp = new MicrodataParser();
			mp.parse(
					URI.create(documentUrl),
					dom,
					out
			);
		} catch (MicrodataParserException e) {
			e.printStackTrace();
		}

		log.debug(this.getClass().getSimpleName()+" - Done pressing {} in {}ms", documentUrl, System.currentTimeMillis()-start);
	}

}
