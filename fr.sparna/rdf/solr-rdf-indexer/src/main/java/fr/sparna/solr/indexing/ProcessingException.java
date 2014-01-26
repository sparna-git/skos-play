package fr.sparna.solr.indexing;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = -6695503664913104218L;

	public ProcessingException(String msg) {
		super(msg);
	}

	public ProcessingException(Throwable cause) {
		super(cause);
	}

	public ProcessingException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
