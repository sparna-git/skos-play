package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.io.FileNotFoundException;
import java.io.InputStream;

import fr.sparna.commons.io.InputStreamUtil;

/**
 * Reads a SPARQL query by retrieving the given resource on the classpath
 * 
 * @author Thomas Francart
 *
 */
public class ResourceSPARQLQueryBuilder implements SPARQLQueryBuilderIfc {

	protected String sparql;

    public ResourceSPARQLQueryBuilder(Class<?> owner, String resource) {
    	if (owner == null) {
            throw new IllegalArgumentException("owner");
        }
    	
    	// Load SPARQL query definition
        InputStream src = owner.getResourceAsStream(resource);
        if (src== null) {
            throw new RuntimeException(new FileNotFoundException(resource));
        }
        
		// read from the stream
		// TODO : specify encoding ?
		this.sparql = InputStreamUtil.readToString(src);
    }
	
    public ResourceSPARQLQueryBuilder(Object owner, String resource) {
        this((Class<?>)owner.getClass(), resource);
    }
	
	@Override
	public String getSPARQL() {
		return sparql;
	}
	
}
