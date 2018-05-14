package fr.sparna.rdf.rdf4j.toolkit.reader;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

public class PropertyLangValueReader extends KeyValueReader<IRI, Literal> {

	private static final String KEY_VAR_NAME = "x";
	private static final String VALUE_VAR_NAME = "value";

	public PropertyLangValueReader(IRI property, String lang) {
		super(
				new QuerySupplier(property, lang).get(),
				new IriBindingSetGenerator(KEY_VAR_NAME),
				new IriToLiteralBindingSetParser(KEY_VAR_NAME, VALUE_VAR_NAME)
		);
	}

	public static class QuerySupplier implements Supplier<String> {

		protected IRI property;
		protected String lang;
			
		public QuerySupplier(IRI property, String lang) {
			super();
			this.property = property;
			this.lang = lang;
		}

		@Override
		public String get() {
			StringBuffer sparql = new StringBuffer();
			sparql.append("SELECT ?"+KEY_VAR_NAME+" ?"+VALUE_VAR_NAME+""+"\n");
			sparql.append("WHERE {"+"\n");
			sparql.append("		?"+KEY_VAR_NAME+" <"+property+"> ?"+VALUE_VAR_NAME+"\n");
			sparql.append("   	FILTER(langMatches(lang(?"+VALUE_VAR_NAME+"), '"+this.lang+"'))"+"\n");
			sparql.append("}");
			;		
			
			return sparql.toString();
		}
	}
	
}
