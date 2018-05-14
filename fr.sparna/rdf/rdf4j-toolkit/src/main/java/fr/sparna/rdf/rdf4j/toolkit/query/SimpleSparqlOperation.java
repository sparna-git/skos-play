package fr.sparna.rdf.rdf4j.toolkit.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.repository.sparql.query.SPARQLQueryBindingSet;

/**
 * Concrete implementation of {@link SparqlOperationIfc} that relies on a Supplier<String> to return the SPARQL query.
 * to return the SPARQL query.
 * 
 * @author Thomas Francart
 *
 */
public class SimpleSparqlOperation implements SparqlOperationIfc {

	protected BindingSet bindingSet = null;
	
	protected Boolean includeInferred = null;
	
	protected Dataset dataset;
	
	protected Supplier<String> querySupplier;

	/**
	 * Constructs a SparqlQuery with a SparqlQueryBuilderIfc and the given bindings
	 * 
	 * @param supplier The supplier that will return the SPARQL query in <code>getSPARQL</code>
	 * @param bindings The bindings to inject in the SPARQL query
	 */
	public SimpleSparqlOperation(Supplier<String> supplier, BindingSet bindingSet) {
		super();
		this.querySupplier = supplier;
		this.bindingSet = bindingSet;
	}
	
	/**
	 * Constructs a SparqlQuery with a query supplier
	 * 
	 * @param supplier The supplier that will return the SPARQL query in <code>getSPARQL</code>
	 */
	public SimpleSparqlOperation(Supplier<String> supplier) {
		this(supplier, null);
	}
	
	/**
	 * Convenience constructor that takes a String as an input and wraps it in a
	 * {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}, along with associated bindings
	 * 
	 * @param sparql a String representing a valid SPARQL query that will be wrapped in
	 * a {@link fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder StringSPARQLQueryBuilder}
	 * @param  bindings The bindings associated to the query
	 */
	public SimpleSparqlOperation(String sparql, BindingSet bindings) {
		this(new SimpleQueryReader(sparql), bindings);
	}
	
	/**
	 * @deprecated use SimpleSparqlOperation(String, BindingSet) instead.
	 * @param sparql
	 * @param bindings
	 */
	public SimpleSparqlOperation(String sparql, Collection<Binding> bindings) {
		this(new SimpleQueryReader(sparql), SimpleSparqlOperation.toBindginSet(bindings));
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
	 * A helper method to set the bindings on this instance that returns this instance, that allows
	 * to create a new instance and pass it bindings in a single statement, e.g. new SimpleSparqlOperation(myString).withBindings(myBindings);
	 * 
	 * @param bindings	The bindings to set
	 * @return this
	 */
	public SimpleSparqlOperation withBindings(BindingSet bindingSet) {
		this.setBindingSet(bindingSet);
		return this;
	}
	
	/**
	 * A helper method to set a single binding on this instance that returns this instance, that allows
	 * to create a new instance and pass it a binding in a single statement, e.g. new SimpleSparqlOperation(myString).withBinding(myBinding);
	 * 
	 * @param binding	The binding to set
	 * @return this
	 */
	public SimpleSparqlOperation withBinding(Binding binding) {
		if(binding != null) {
			SPARQLQueryBindingSet bindingSet = new SPARQLQueryBindingSet();
			bindingSet.addBinding(binding);
			this.setBindingSet(bindingSet);
		}
		return this;
	}
	
	/**
	 * Gets the SPARQL String from the underlying Supplier<String>
	 */
	@Override
	public String getSPARQL() {
		return this.querySupplier.get();
	}
	
	

	@Override
	public BindingSet getBindingSet() {
		return this.bindingSet;
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

	public void setBindingSet(BindingSet bindingSet) {
		this.bindingSet = bindingSet;
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
		if(getBindingSet() == null) {
			return getSPARQL();
		}
		
		String sparqlString = getSPARQL();
		for (Binding b : bindingSet) {
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
	
	public static SPARQLQueryBindingSet toBindginSet(Collection<Binding> bindings) {
		if(bindings == null) {
			return null;
		}
		SPARQLQueryBindingSet bindingSet = new SPARQLQueryBindingSet();
		for (Binding binding : bindings) {
			bindingSet.addBinding(binding);
		}
		return bindingSet;
	}
}
