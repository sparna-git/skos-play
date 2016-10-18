package fr.sparna.rdf.datapress.jsonld;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.sparna.commons.xml.SimpleNamespaceContext;
import fr.sparna.rdf.datapress.DataPress;
import fr.sparna.rdf.datapress.DataPressException;
import fr.sparna.rdf.datapress.DataPressSource;

public class JsonLDPress implements DataPress {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private static final String XPATH_JSON_LD = "//xhtml:script[@type='application/ld+json']";

	public JsonLDPress() {
		
	}
	
	public void press(DataPressSource in, RDFHandler out) throws DataPressException {
		log.debug(this.getClass().getSimpleName()+" - Pressing {}", in.getIri());
		long start = System.currentTimeMillis();

		// I. Turn into DOM
		Document dom = in.getContentDom();

		// II.Extract script tags
		NodeList nodes = null;
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			SimpleNamespaceContext snc = new SimpleNamespaceContext();
			snc.setBindings(new HashMap<String, String>(){{ put("xhtml", "http://www.w3.org/1999/xhtml"); }});
			xPath.setNamespaceContext(snc);
			log.debug("Extracting script tags with XPath "+XPATH_JSON_LD);
			nodes = (NodeList)xPath.evaluate(
					XPATH_JSON_LD,
					dom.getDocumentElement(),
					XPathConstants.NODESET
					);
		} catch (XPathExpressionException ignore) {
			ignore.printStackTrace();
		}

		// III. Parse every JSON-LD piece found in the page
		RDFParser parser = Rio.createParser(RDFFormat.JSONLD);
		parser.setRDFHandler(out);
		
		if(nodes != null) {
			for (int i = 0; i < nodes.getLength(); ++i) {
				Element e = (Element) nodes.item(i);
				String content = e.getTextContent();
				log.debug("Parsing JSON-LD :\n {}"+content);

	            try {
	            	// parse and use documentUrl as base URI
					parser.parse(new StringReader(e.getTextContent()), in.getIri().stringValue());
				} catch (Exception e1) {
					log.error("Exception while parsing JSON-LD : {}, moving to next JSON-LD piece"+e1.getMessage());
					e1.printStackTrace();
				}
			}			
		}
		
		

		log.debug(this.getClass().getSimpleName()+" - Done pressing {} in {}ms", in.getIri(), System.currentTimeMillis()-start);
	}

}
