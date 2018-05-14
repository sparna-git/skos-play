package fr.sparna.rdf.rdf4j.toolkit.reader;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;

public class TypeReader extends KeyValueReader<IRI, IRI> {

	private static final String KEY_VAR_NAME = "x";
	private static final String VALUE_VAR_NAME = "type";

	public TypeReader() {
		super(
				new QuerySupplier().get(),
				new IriBindingSetGenerator(KEY_VAR_NAME),
				new IriToIriBindingSetParser(KEY_VAR_NAME, VALUE_VAR_NAME)
		);
	}

	public static class QuerySupplier implements Supplier<String> {

		@Override
		public String get() {
	
			String sparql = "" +
					"SELECT ?"+KEY_VAR_NAME+" ?"+VALUE_VAR_NAME+""+"\n" +
					"WHERE {"+"\n" +
					"	?"+KEY_VAR_NAME+" a ?"+VALUE_VAR_NAME+"\n" +
					"}"
			;		
			
			return sparql;
		}
	}
	
}
