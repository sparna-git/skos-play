package fr.sparna.rdf.sesame.toolkit.repository;

import java.io.InputStream;

import org.openrdf.rio.RDFFormat;

/**
 * Loads a Sesame config from a file in the classpath.
 * @author Thomas Francart
 *
 */
public class ClasspathConfigProvider implements ConfigProviderIfc {

	protected String configPath;
	protected RDFFormat configFormat = RDFFormat.TURTLE;
	
	public ClasspathConfigProvider(String configPath) {
		super();
		this.configPath = configPath;
	}

	@Override
	public InputStream getConfigAsStream() {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath);
	}

	@Override
	public RDFFormat getConfigFormat() {
		return configFormat;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public void setConfigFormat(RDFFormat configFormat) {
		this.configFormat = configFormat;
	}

}
