package fr.sparna.rdf.sesame.toolkit.bd;

/**
 * Throws to indicate an error in the generation of a bounded description with a {@link BoundedDescriptionGeneratorIfc}
 * 
 * @author Thomas Francart
 */
public class BoundedDescriptionGenerationException extends Exception {
	
	private static final long serialVersionUID = 7872724293561928387L;

	public BoundedDescriptionGenerationException(String msg) {
		super(msg);
	}

	public BoundedDescriptionGenerationException(Throwable cause) {
		super(cause);
	}

	public BoundedDescriptionGenerationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
