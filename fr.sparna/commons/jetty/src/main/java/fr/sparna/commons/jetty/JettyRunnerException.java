package fr.sparna.commons.jetty;

public class JettyRunnerException extends Exception {

	private static final long serialVersionUID = -2454792250865606317L;

	public JettyRunnerException(String msg) {
		super(msg);
	}

	public JettyRunnerException(Throwable cause) {
		super(cause);
	}

	public JettyRunnerException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
