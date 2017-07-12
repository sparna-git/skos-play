package fr.sparna.rdf.rdf4j.toolkit.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.Dataset;

/**
 * Concrete implementation of {@link SparqlOperationIfc} that uses a {@link fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc}
 * to return the SPARQL query.
 * 
 * @author Thomas Francart
 *
 */
public class SimpleSparqlOperation implements SparqlOperationIfc {

	protected Collection<Binding> bindings = null;
	
	protected Boolean includeInferred = null;
	
	protected Dataset dataset;
	
	protected SparqlQueryBuilderIfc builder;

	/**
	 * Constructs a SparqlQuery with a SparqlQueryBuilderIfc and the given bindings
	 * 
	 * @param builder The builder that will return the SPARQL query in <code>getSPARQL</code>
	 * @param bindings The bindings to inject in the SPARQL query
	 */
	public SimpleSparqlOperation(SparqlQueryBuilderIfc builder, Collection<Binding> bindings) {
		super();
		this.builder = builder;
		this.bindings = bindings;
	}
	
	/**
	 * Constructs a SparqlQuery with a SparqlQueryBuilderIfc
	 * 
	 * @param builder The builder that will return the SPARQL query in <code>getSPARQL</code>
	 */
	public SimpleSparqlOperation(SparqlQueryBuilderIfc builder) {
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
	public SimpleSparqlOperation(String sparql, Collection<Binding> bindings) {
		this(new SimpleQueryReader(sparql), bindings);
	}
	
	/**
	 * Convenience constructor that takes a String as an input and wraps it in a
	 * {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}
	 * 
	 * @param sparql a String representing a valid SPARQL query that will be wrapped in
	 * a {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}
	 */
	public SimpleSparqlOperation(String sparql) {
		this(new SimpleQueryReader(sparql));
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
	public Collection<Binding> getBindings() {
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
	public Dataset getDataset() {
		return dataset;
	}

	public void setBindings(Collection<Binding> bindings) {
		this.bindings = bindings;
	}

	public void setIncludeInferred(Boolean includeInferred) {
		this.includeInferred = includeInferred;
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
		for (Binding b : bindings) {
			if(b.getValue() != null) {
				String replacement;
				if(
					b.getValue() instanceof org.eclipse.rdf4j.model.IRI
				) {
					replacement = "<"+b.getValue().toString()+">";
				} else if (b.getValue() instanceof org.eclipse.rdf4j.model.Literal) {
					replacement = b.getValue().toString();
				} else {
					replacement = "\""+b.getValue().toString()+"\"";
				}
				sparqlString = sparqlString.replaceAll("[?$]"+b.getName(), replacement);
			}
		}
		
		return sparqlString;
	}
	
	public static List<SimpleSparqlOperation> fromQueryList(List<? extends SparqlQueryBuilderIfc> builders) {
		if(builders == null) {
			return null;
		}
		
		ArrayList<SimpleSparqlOperation> result = new ArrayList<SimpleSparqlOperation>();
		for (SparqlQueryBuilderIfc aBuilder : builders) {
			result.add(new SimpleSparqlOperation(aBuilder));
		}
		return result;
	}
	
}
