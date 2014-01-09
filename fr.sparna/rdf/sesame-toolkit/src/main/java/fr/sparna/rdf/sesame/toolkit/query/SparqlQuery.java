package fr.sparna.rdf.sesame.toolkit.query;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderList;

/**
 * Concrete implementation of {@link SparqlQueryIfc} that uses a {@link fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc}
 * to return the SPARQL query.
 * 
 * @author Thomas Francart
 *
 */
public class SparqlQuery implements SparqlQueryIfc {

	protected Map<String, Object> bindings = null;
	
	protected Boolean includeInferred = null;
	
	protected Set<URI> defaultGraphs = null;
	
	protected Set<URI> namedGraphs = null;
	
	protected SparqlQueryBuilderIfc builder;

	/**
	 * Constructs a SparqlQuery with a SparqlQueryBuilderIfc and the given bindings
	 * 
	 * @param builder The builder that will return the SPARQL query in <code>getSPARQL</code>
	 * @param bindings The bindings to inject in the SPARQL query
	 */
	public SparqlQuery(SparqlQueryBuilderIfc builder, Map<String, Object> bindings) {
		super();
		this.builder = builder;
		this.bindings = bindings;
	}
	
	/**
	 * Constructs a SparqlQuery with a SparqlQueryBuilderIfc
	 * 
	 * @param builder The builder that will return the SPARQL query in <code>getSPARQL</code>
	 */
	public SparqlQuery(SparqlQueryBuilderIfc builder) {
		this(builder, null);
	}
	
	/**
	 * Convenience constructor that takes a String as an input and wraps it in a
	 * {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}, along with associated bindings
	 * 
	 * @param sparql a String representing a valid SPARQL query that will be wrapped in
	 * a {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}
	 * @param  bindings The bindings associated to the query
	 */
	public SparqlQuery(String sparql, Map<String, Object> bindings) {
		this(new SparqlQueryBuilder(sparql), bindings);
	}
	
	/**
	 * Convenience constructor that takes a String as an input and wraps it in a
	 * {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}
	 * 
	 * @param sparql a String representing a valid SPARQL query that will be wrapped in
	 * a {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}
	 */
	public SparqlQuery(String sparql) {
		this(new SparqlQueryBuilder(sparql));
	}
	
	/**
	 * Gets the SPARQL String from the underlying SparqlQueryBuilderIfc
	 */
	@Override
	public String getSPARQL() {
		return this.builder.getSPARQL();
	}
	
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
			if(value != null) {
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
		}
		
		return sparqlString;
	}
	
	public static List<SparqlQuery> fromQueryDirectory(File directory) {
		return fromQueryList(SparqlQueryBuilderList.fromDirectory(directory));
	}
	
	public static List<SparqlQuery> fromQueryList(List<? extends SparqlQueryBuilderIfc> builders) {
		if(builders == null) {
			return null;
		}
		
		ArrayList<SparqlQuery> result = new ArrayList<SparqlQuery>();
		for (SparqlQueryBuilderIfc aBuilder : builders) {
			result.add(new SparqlQuery(aBuilder));
		}
		return result;
	}
	
}
