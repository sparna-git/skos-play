package fr.sparna.rdf.sesame.toolkit.query;

import java.util.Map;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder;

/**
 * Concrete implementation of {@link SPARQLQueryIfc} that uses a {@link fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc}
 * to return the SPARQL query.
 * 
 * @author Thomas Francart
 *
 */
public class SPARQLQuery extends SPARQLQueryBase implements SPARQLQueryIfc {

	protected SPARQLQueryBuilderIfc builder;

	/**
	 * Constructs a SPARQLQuery with a SPARQLQueryBuilderIfc and the given bindings
	 * 
	 * @param builder The builder that will return the SPARQL query in <code>getSPARQL</code>
	 * @param bindings The bindings to inject in the SPARQL query
	 */
	public SPARQLQuery(SPARQLQueryBuilderIfc builder, Map<String, Object> bindings) {
		super();
		this.builder = builder;
		this.bindings = bindings;
	}
	
	/**
	 * Constructs a SPARQLQuery with a SPARQLQueryBuilderIfc
	 * 
	 * @param builder The builder that will return the SPARQL query in <code>getSPARQL</code>
	 */
	public SPARQLQuery(SPARQLQueryBuilderIfc builder) {
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
	public SPARQLQuery(String sparql, Map<String, Object> bindings) {
		this(new StringSPARQLQueryBuilder(sparql), bindings);
	}
	
	/**
	 * Convenience constructor that takes a String as an input and wraps it in a
	 * {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}
	 * 
	 * @param sparql a String representing a valid SPARQL query that will be wrapped in
	 * a {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}
	 */
	public SPARQLQuery(String sparql) {
		this(new StringSPARQLQueryBuilder(sparql));
	}
	
	/**
	 * Gets the SPARQL String from the underlying SPARQLQueryBuilderIfc
	 */
	@Override
	public String getSPARQL() {
		return this.builder.getSPARQL();
	}
	
}
