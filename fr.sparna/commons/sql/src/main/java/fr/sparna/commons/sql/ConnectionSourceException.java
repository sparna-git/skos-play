package fr.sparna.commons.sql;

public class ConnectionSourceException extends Exception {

	private static final long serialVersionUID = 823231096631597834L;

	public ConnectionSourceException(String msg) {
		super(msg);
	}

	public ConnectionSourceException(Throwable cause) {
		super(cause);
	}

	public ConnectionSourceException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
