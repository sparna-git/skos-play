package fr.sparna.dbpedia.lookup.client;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fr.sparna.dbpedia.lookup.client.schema.ArrayOfResult;

public class DBpediaLookupResultPrinter {

	public static String toXml(ArrayOfResult result) throws JAXBException {
		Marshaller m = JAXBContext.newInstance("fr.sparna.dbpedia.lookup.client.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(result, baos);
		try {
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException ignore) {
			ignore.printStackTrace();
			return null;
		}
	}
	
	public static String toString(ArrayOfResult result) throws JAXBException {
		return toXml(result);
	}
	
}
