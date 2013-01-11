package fr.sparna.rdf.sesame.toolkit.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import fr.sparna.commons.xml.XSLProcessor;

public class XsltSparqlTest {

	public static void main(String... strings) throws Exception {
		
		// InputStream is = XsltSparqlTest.class.getClassLoader().getResourceAsStream("stylesheets/test-pure-xslt-impl.xsl");
		InputStream is = XsltSparqlTest.class.getClassLoader().getResourceAsStream("stylesheets/skos-alpha-2.xsl");
		if(is == null) {
			System.out.println("yiiiiik ! is is null");
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder domFactory = dbf.newDocumentBuilder();
		Document doc = domFactory.newDocument();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XSLProcessor.createSaxonProcessor().transform(is, doc,  baos);
		System.out.println(baos);
	}
	
}
