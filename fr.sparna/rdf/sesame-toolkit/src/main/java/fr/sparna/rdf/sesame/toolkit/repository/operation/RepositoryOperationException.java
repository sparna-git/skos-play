package fr.sparna.rdf.sesame.toolkit.repository.operation;

/**
 * An exception that indicates that an error occurred during the initialisation of a Repository
 * by a RepositoryProvider.
 * 
 * @author Thomas Francart
 *
 */
public class RepositoryOperationException extends Exception {

	private static final long serialVersionUID = -6453205812104229168L;

	public RepositoryOperationException(String msg) {
		super(msg);
	}

	public RepositoryOperationException(Throwable cause) {
		super(cause);
	}

	public RepositoryOperationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}