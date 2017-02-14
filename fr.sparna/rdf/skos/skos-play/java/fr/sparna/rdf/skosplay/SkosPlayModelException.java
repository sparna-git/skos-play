package fr.sparna.rdf.skosplay;

/**
 * An exception that indicates that an error occurred inside the Model part
 * of the application
 * 
 * @author Thomas Francart
 *
 */
public class SkosPlayModelException extends Exception {

	private static final long serialVersionUID = -7887954163755209963L;

	public SkosPlayModelException(String msg) {
		super(msg);
	}

	public SkosPlayModelException(Throwable cause) {
		super(cause);
	}

	public SkosPlayModelException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}