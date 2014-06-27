package fr.sparna.rdf.solr.index.config;

public class ConfigurationException extends Exception {

	private static final long serialVersionUID = 6719332442267035506L;

	public ConfigurationException(String msg) {
		super(msg);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	public ConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}