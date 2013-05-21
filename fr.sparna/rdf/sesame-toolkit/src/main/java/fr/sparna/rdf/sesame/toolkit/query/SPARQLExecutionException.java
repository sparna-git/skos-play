package fr.sparna.rdf.sesame.toolkit.query;

/**
 * An exception indicating an error happened while performing a SPARQL query
 * 
 * @author Thomas Francart
 */
public class SPARQLExecutionException extends Exception {

	private static final long serialVersionUID = -7140173477048016716L;

	public SPARQLExecutionException(String msg) {
		super(msg);
	}

	public SPARQLExecutionException(Throwable cause) {
		super(cause);
	}

	public SPARQLExecutionException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}