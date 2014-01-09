package fr.sparna.rdf.sesame.toolkit.query;

/**
 * An exception indicating an error happened while performing a SPARQL query
 * 
 * @author Thomas Francart
 */
public class SparqlPerformException extends Exception {

	private static final long serialVersionUID = -7140173477048016716L;

	public SparqlPerformException(String msg) {
		super(msg);
	}

	public SparqlPerformException(Throwable cause) {
		super(cause);
	}

	public SparqlPerformException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}