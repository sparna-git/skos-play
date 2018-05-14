package fr.sparna.rdf.microdata.parser;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class MicrodataParserTest {

    public static void main(String...args) throws Exception {
    	String TEST_URI = "https://www.tours.fr/TPL_CODE/TPL_AGENDATOURS/PAR_TPL_IDENTIFIANT/9372/218-agenda.htm";
    	MicrodataParser me = new MicrodataParser();
    	me.parse(URI.create(TEST_URI), readDOM(new URL(TEST_URI)), new TurtleWriter(System.out));
    }
    
    public static Document readDOM(
    		URL documentURL
    ) throws IOException, MicrodataParserException {

    	HtmlDocumentBuilder builder = new HtmlDocumentBuilder(XmlViolationPolicy.ALTER_INFOSET);
        // HtmlParser parser = new HtmlParser(XmlViolationPolicy.ALTER_INFOSET);

    	try {
			// builder.setErrorHandler(new SystemErrErrorHandler());
			return builder.parse(documentURL.openStream());

		} catch (SAXException e) {
			throw new MicrodataParserException("Unable to get DOM representation of HTML file", e);
		}	
    }

}
