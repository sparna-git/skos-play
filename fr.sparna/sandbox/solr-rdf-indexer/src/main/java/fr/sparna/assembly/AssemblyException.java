package fr.sparna.assembly;

/**
 * Exceptions that can be thrown by <code>AssemblyStation</code>s during
 * the processing of a <code>Assembly</code>.
 * 
 * @author Thomas Francart
 *
 */
public class AssemblyException extends Exception {

	private static final long serialVersionUID = -6695503664913104218L;

	public AssemblyException(String msg) {
		super(msg);
	}

	public AssemblyException(Throwable cause) {
		super(cause);
	}

	public AssemblyException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
