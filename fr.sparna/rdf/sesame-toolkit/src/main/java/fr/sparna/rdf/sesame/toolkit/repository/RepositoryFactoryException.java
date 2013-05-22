package fr.sparna.rdf.sesame.toolkit.repository;

/**
 * An exception that indicates that an error occurred during the initialisation of a Repository
 * by a RepositoryProvider.
 * 
 * @author Thomas Francart
 *
 */
public class RepositoryFactoryException extends Exception {

	private static final long serialVersionUID = -7887954163755209963L;

	public RepositoryFactoryException(String msg) {
		super(msg);
	}

	public RepositoryFactoryException(Throwable cause) {
		super(cause);
	}

	public RepositoryFactoryException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}