package fr.sparna.dbpedia.lookup.client;

public class DBpediaLookupException extends Exception {

	private static final long serialVersionUID = -2246184162815823690L;

	public DBpediaLookupException(String msg) {
		super(msg);
	}

	public DBpediaLookupException(Throwable cause) {
		super(cause);
	}

	public DBpediaLookupException(String msg, Throwable cause) {
		super(msg, cause);
	}	
	
}
