package fr.sparna.rdf.rdf4j.toolkit.reader;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

public class PropertyValueReader extends KeyValueReader<IRI, Value> {

	private static final String KEY_VAR_NAME = "x";
	private static final String VALUE_VAR_NAME = "value";

	public PropertyValueReader(IRI property) {
		super(
				new QuerySupplier(property).get(),
				new IriBindingSetGenerator(KEY_VAR_NAME),
				new IriToValueBindingSetParser(KEY_VAR_NAME, VALUE_VAR_NAME)
		);
	}

	public static class QuerySupplier implements Supplier<String> {

		protected IRI property;
			
		public QuerySupplier(IRI property) {
			super();
			this.property = property;
		}

		@Override
		public String get() {
	
			String sparql = "" +
					"SELECT ?"+KEY_VAR_NAME+" ?"+VALUE_VAR_NAME+""+"\n" +
					"WHERE {"+"\n" +
					"	?"+KEY_VAR_NAME+" <"+this.property.stringValue()+"> ?"+VALUE_VAR_NAME+"\n" +
					"}"
			;		
			
			return sparql;
		}
	}
	
}
