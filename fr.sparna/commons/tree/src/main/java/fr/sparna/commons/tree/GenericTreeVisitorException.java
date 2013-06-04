package fr.sparna.commons.tree;

public class GenericTreeVisitorException extends Exception {

	private static final long serialVersionUID = -7623181923377674490L;

	public GenericTreeVisitorException(String msg) {
		super(msg);
	}

	public GenericTreeVisitorException(Throwable cause) {
		super(cause);
	}

	public GenericTreeVisitorException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
