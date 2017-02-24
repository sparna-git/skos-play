package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.util.List;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class ValuesSparqlQueryBuilder implements SparqlQueryBuilderIfc {
	
	protected SparqlQueryBuilderIfc builder;
	protected String var;
	protected List<? extends Value> values;
	
	
	/**
	 * Wraps the given builder to add VALUES to the given variable
	 * 
	 * @param builder
	 * @param var
	 * @param values
	 */
	public ValuesSparqlQueryBuilder(
			SparqlQueryBuilderIfc builder,
			String var,
			List<? extends Value> values
	) {
		super();
		this.builder = builder;
		this.var = var;
		this.values = values;
	}
	
	@Override
	public String getSPARQL() {
		StringBuffer sparql = new StringBuffer(builder.getSPARQL());
		
		if(var != null && values != null && values.size() > 0) {
			sparql.append(" VALUES ?"+var+"");
			sparql.append(" { ");
			for (Value aValue : this.values) {
				if(aValue instanceof org.eclipse.rdf4j.model.URI) {
					sparql.append("<"+aValue.stringValue()+">");
				} else {
					sparql.append(aValue.toString());
				}
				sparql.append(" ");
			}
			sparql.append("}");
		}
		
		return sparql.toString();
	}
	
	public static void main(String...strings) throws Exception {
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		System.out.println(r.getValueFactory().createLiteral("toto", "fr").toString());
		System.out.println(r.getValueFactory().createIRI("abc:def"));
	}
	
}
