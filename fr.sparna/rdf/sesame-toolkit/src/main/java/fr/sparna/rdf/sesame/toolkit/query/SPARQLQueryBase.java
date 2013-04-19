package fr.sparna.rdf.sesame.toolkit.query;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Thomas Francart
 *
 */
public abstract class SPARQLQueryBase implements SPARQLQueryIfc {
	
	protected Map<String, Object> bindings = null;
	
	protected Boolean includeInferred = null;
	
	protected Set<URI> defaultGraphs = null;
	
	protected Set<URI> namedGraphs = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String getSPARQL();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getBindings() {
		return bindings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isIncludeInferred() {
		return includeInferred;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<URI> getDefaultGraphs() {
		return defaultGraphs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<URI> getNamedGraphs() {
		return namedGraphs;
	}

	public void setBindings(Map<String, Object> bindings) {
		this.bindings = bindings;
	}

	public void setIncludeInferred(Boolean includeInferred) {
		this.includeInferred = includeInferred;
	}

	public void setDefaultGraphs(Set<URI> defaultGraphs) {
		this.defaultGraphs = defaultGraphs;
	}

	public void setNamedGraphs(Set<URI> namedGraphs) {
		this.namedGraphs = namedGraphs;
	}

	/**
	 * Attempts to recreate a SPARQL query by replacing the proper bindings in the raw SPARQL string.
	 * It is however _not_ guaranteed that this will produce a valid SPARQL query and should be used 
	 * for debugging purposes only.
	 */
	@Override
	public String toString() {
		if(getBindings() == null) {
			return getSPARQL();
		}
		
		String sparqlString = getSPARQL();
		for (Entry<String, Object> anEntry : this.getBindings().entrySet()) {
			Object value = anEntry.getValue();
			String replacement;
			if(
					value instanceof java.net.URI
					||
					value instanceof java.net.URL
					||
					value instanceof org.openrdf.model.URI
			) {
				replacement = "<"+value.toString()+">";
			} else if (value instanceof org.openrdf.model.Value) {
				replacement = value.toString();
			} else {
				replacement = "\""+value.toString()+"\"";
			}
			
			sparqlString = sparqlString.replaceAll("[?$]"+anEntry.getKey(), replacement);
		}
		
		return sparqlString;
	}
	
	
}
