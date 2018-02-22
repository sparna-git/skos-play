package fr.sparna.dbpedia.lookup.client;

import java.io.InputStream;

import fr.sparna.dbpedia.lookup.client.schema.ArrayOfResult;

public interface DBpediaLookupResultParser {

	public ArrayOfResult parse(InputStream lookupResponse) throws DBpediaLookupException;
	
}
