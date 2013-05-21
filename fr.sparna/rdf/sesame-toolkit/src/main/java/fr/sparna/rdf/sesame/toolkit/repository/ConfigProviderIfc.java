package fr.sparna.rdf.sesame.toolkit.repository;

import java.io.InputStream;

import org.openrdf.rio.RDFFormat;

/**
 * Returns a config for a Sesame repository (used by ConfigRepositoryProvider).
 * The config can be read from a classpath file, or it can be a special config for OWLIM.
 *
 * @author Thomas Francart
 *
 */
public interface ConfigProviderIfc {

	public InputStream getConfigAsStream();
	
	public RDFFormat getConfigFormat();
	
}
