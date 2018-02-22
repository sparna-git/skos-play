package fr.sparna.assembly;

/**
 * Exceptions that can be thrown by <code>AssemblyConsumer</code>s, when consuming
 * <code>Assembly</code> at the end of a <code>AssemblyLine</code>.
 * 
 * @author Thomas Francart
 */
public class ConsumeException extends Exception {

	private static final long serialVersionUID = 3310023595781511709L;

	public ConsumeException(String msg) {
		super(msg);
	}

	public ConsumeException(Throwable cause) {
		super(cause);
	}

	public ConsumeException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
