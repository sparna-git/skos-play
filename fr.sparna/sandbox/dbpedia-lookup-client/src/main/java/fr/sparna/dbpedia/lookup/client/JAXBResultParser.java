package fr.sparna.dbpedia.lookup.client;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import fr.sparna.dbpedia.lookup.client.schema.ArrayOfResult;

public class JAXBResultParser implements DBpediaLookupResultParser {

	@Override
	public ArrayOfResult parse(InputStream lookupResponse)
	throws DBpediaLookupException {
		try {
			JAXBContext jc = JAXBContext.newInstance("fr.sparna.dbpedia.lookup.client.schema");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			return (ArrayOfResult)unmarshaller.unmarshal(lookupResponse);
		} catch (JAXBException e) {
			throw new DBpediaLookupException(e);
		}
	}

}
