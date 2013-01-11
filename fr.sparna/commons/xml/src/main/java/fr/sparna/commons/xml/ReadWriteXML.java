package fr.sparna.commons.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ReadWriteXML {

	
	public static void write(Node node, OutputStream output)
	throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT,"yes");
		// tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.transform(new DOMSource(node), new StreamResult(output));
	}

	public static void write(Node node, File aFile)
	throws TransformerException, IOException {
		if(!aFile.exists()) {
			aFile.createNewFile();
		}
		try {
			write(node, new FileOutputStream(aFile));
		} catch (FileNotFoundException ignore) {
			ignore.printStackTrace();
		}
	}

	public static String write(Node node)
	throws TransformerException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		write(node, baos);
		return new String(baos.toByteArray());
	}

	public static Document read(InputStream input)
	throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder domFactory = dbf.newDocumentBuilder();
		return domFactory.parse(input);
	}
	
}
