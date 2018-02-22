package fr.sparna.dbpedia.lookup.client;

import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.sparna.dbpedia.lookup.client.schema.ArrayOfResult;
import fr.sparna.dbpedia.lookup.client.schema.Classes;
import fr.sparna.dbpedia.lookup.client.schema.Result;

public class SAXSimpleResultParser extends DefaultHandler implements DBpediaLookupResultParser {

	// final data structure
	private ArrayOfResult result;
	// current result
	private Result currentResult;
	
	private LinkedList<String> currentElementPath = new LinkedList<String>();
	
	@Override
	public ArrayOfResult parse(InputStream lookupResponse)
	throws DBpediaLookupException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser sax = factory.newSAXParser();
			sax.parse(lookupResponse, this);			
			return result;
		} catch (Exception e) {
			throw new DBpediaLookupException(e);
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("ArrayOfResult")) {
			// start parsing. init result
			this.result = new ArrayOfResult();
		} else 	if (qName.equals("Result")) {
			// add a result
			currentResult = new Result();
			this.result.getResult().add(currentResult);
		} else 	if (qName.equals("Classes")) {
			// add classes intermediate element
			currentResult.setClasses(new Classes());
		} else 	if (qName.equals("Class")) {
			// add a class
			currentResult.getClasses().getClazz().add(new fr.sparna.dbpedia.lookup.client.schema.Class());
		}

		currentElementPath.add(qName);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		currentElementPath.removeLast();
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		String s = new String(ch, start, length).trim();
		if (s.length() > 0) {
			if ("Label".equals(currentElementPath.getLast())) {
				if(currentElementPath.get(currentElementPath.size() - 2).equals("Result")) {
					currentResult.setLabel(s);
				} else if(currentElementPath.get(currentElementPath.size() - 2).equals("Class")) {
					currentResult.getClasses().getClazz().get(currentResult.getClasses().getClazz().size() - 1).setLabel(s);
				}
			} else if ("URI".equals(currentElementPath.getLast())) {
				if(currentElementPath.get(currentElementPath.size() - 2).equals("Result")) {
					this.result.getResult().get(this.result.getResult().size()-1).setLabel(s);
				} else if(currentElementPath.get(currentElementPath.size() - 2).equals("Class")) {
					currentResult.getClasses().getClazz().get(currentResult.getClasses().getClazz().size() - 1).setURI(s);
				}
			} else if ("Description".equals(currentElementPath.getLast())) {
				currentResult.setDescription(s);				
			} else if ("Refcount".equals(currentElementPath.getLast())) {
				currentResult.setRefcount(Integer.parseInt(s));
			}
		}
	}
	
}
