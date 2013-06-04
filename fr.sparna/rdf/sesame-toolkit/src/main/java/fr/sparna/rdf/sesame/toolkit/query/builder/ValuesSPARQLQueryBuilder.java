package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class ValuesSPARQLQueryBuilder implements SPARQLQueryBuilderIfc {
	
	protected SPARQLQueryBuilderIfc builder;
	protected String var;
	protected List<? extends Value> values;
	
	
	/**
	 * Wraps the given builder to add VALUES to the given variable
	 * 
	 * @param builder
	 * @param var
	 * @param values
	 */
	public ValuesSPARQLQueryBuilder(
			SPARQLQueryBuilderIfc builder,
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
				if(aValue instanceof org.openrdf.model.URI) {
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
		System.out.println(r.getValueFactory().createURI("abc:def"));
	}
	
}
