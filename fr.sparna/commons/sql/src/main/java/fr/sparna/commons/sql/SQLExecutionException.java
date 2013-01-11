package fr.sparna.commons.sql;

public class SQLExecutionException extends Exception {

	private static final long serialVersionUID = 823231096631597834L;

	public SQLExecutionException(String msg) {
		super(msg);
	}

	public SQLExecutionException(Throwable cause) {
		super(cause);
	}

	public SQLExecutionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
