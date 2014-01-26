package fr.sparna.solr.indexing;

public class LifecycleException extends Exception {

	private static final long serialVersionUID = 3310023595781511709L;

	public LifecycleException(String msg) {
		super(msg);
	}

	public LifecycleException(Throwable cause) {
		super(cause);
	}

	public LifecycleException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
