package fr.sparna.rdf.sesame.toolkit.query;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Value;

/**
 * @author Thomas Francart
 *
 */
public abstract class SPARQLQueryBase implements SPARQLQueryIfc {
	
	protected Map<String, Value> bindings = null;
	
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
	public Map<String, Value> getBindings() {
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

	public void setBindings(Map<String, Value> bindings) {
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
}
