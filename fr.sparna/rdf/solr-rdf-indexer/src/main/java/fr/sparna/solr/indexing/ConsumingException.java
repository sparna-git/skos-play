package fr.sparna.solr.indexing;

public class ConsumingException extends Exception {

	private static final long serialVersionUID = 3310023595781511709L;

	public ConsumingException(String msg) {
		super(msg);
	}

	public ConsumingException(Throwable cause) {
		super(cause);
	}

	public ConsumingException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
