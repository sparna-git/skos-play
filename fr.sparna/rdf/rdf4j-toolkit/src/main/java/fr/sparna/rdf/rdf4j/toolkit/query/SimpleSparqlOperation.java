package fr.sparna.rdf.rdf4j.toolkit.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.Dataset;

/**
 * Concrete implementation of {@link SparqlOperationIfc} that relies on a Supplier<String> to return the SPARQL query.
 * to return the SPARQL query.
 * 
 * @author Thomas Francart
 *
 */
public class SimpleSparqlOperation implements SparqlOperationIfc {

	protected Collection<Binding> bindings = null;
	
	protected Boolean includeInferred = null;
	
	protected Dataset dataset;
	
	protected Supplier<String> querySupplier;

	/**
	 * Constructs a SparqlQuery with a SparqlQueryBuilderIfc and the given bindings
	 * 
	 * @param builder The builder that will return the SPARQL query in <code>getSPARQL</code>
	 * @param bindings The bindings to inject in the SPARQL query
	 */
	public SimpleSparqlOperation(Supplier<String> querySupplier, Collection<Binding> bindings) {
		super();
		this.querySupplier = querySupplier;
		this.bindings = bindings;
	}
	
	/**
	 * Constructs a SparqlQuery with a SparqlQueryBuilderIfc
	 * 
	 * @param builder The builder that will return the SPARQL query in <code>getSPARQL</code>
	 */
	public SimpleSparqlOperation(Supplier<String> builder) {
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
	 * Gets the SPARQL String from the underlying Supplier<String>
	 */
	@Override
	public String getSPARQL() {
		return this.querySupplier.get();
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
	
	/**
	 * Wraps a list of Supplier<String> into a List<SimpleSparqlOperation>.
	 * 
	 * @param suppliers
	 * @return
	 */
	public static List<SimpleSparqlOperation> fromQueryList(List<? extends Supplier<String>> suppliers) {
		if(suppliers == null) {
			return null;
		}
		
		ArrayList<SimpleSparqlOperation> result = new ArrayList<SimpleSparqlOperation>();
		for (Supplier<String> aBuilder : suppliers) {
			result.add(new SimpleSparqlOperation(aBuilder));
		}
		return result;
	}
	
}
