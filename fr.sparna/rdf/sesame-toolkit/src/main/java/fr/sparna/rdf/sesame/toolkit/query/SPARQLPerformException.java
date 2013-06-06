package fr.sparna.rdf.sesame.toolkit.query;

/**
 * An exception indicating an error happened while performing a SPARQL query
 * 
 * @author Thomas Francart
 */
public class SPARQLPerformException extends Exception {

	private static final long serialVersionUID = -7140173477048016716L;

	public SPARQLPerformException(String msg) {
		super(msg);
	}

	public SPARQLPerformException(Throwable cause) {
		super(cause);
	}

	public SPARQLPerformException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}