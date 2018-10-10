package fr.sparna.rdf.rdf4j.toolkit.reader;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

public class PropertyValueReader extends KeyValueReader<IRI, Value> {

	private static final String KEY_VAR_NAME = "x";
	private static final String VALUE_VAR_NAME = "value";

	public PropertyValueReader(IRI property) {
		super(
				new GenericQuerySupplier("<"+property+">").get(),
				new IriBindingSetGenerator(KEY_VAR_NAME),
				new IriToValueBindingSetParser(KEY_VAR_NAME, VALUE_VAR_NAME)
		);
	}
	
	public PropertyValueReader(String path) {
		super(
				new GenericQuerySupplier(path).get(),
				new IriBindingSetGenerator(KEY_VAR_NAME),
				new IriToValueBindingSetParser(KEY_VAR_NAME, VALUE_VAR_NAME)
		);
	}
	
	public PropertyValueReader(PropertyValueReader.GenericQuerySupplier querySupplier) {
		super(
				querySupplier.get(),
				new IriBindingSetGenerator(KEY_VAR_NAME),
				new IriToValueBindingSetParser(KEY_VAR_NAME, VALUE_VAR_NAME)
		);
	}

	public static class GenericQuerySupplier implements Supplier<String> {

		protected String path;
		protected IRI facetProperty;
		protected IRI facetValue;
		protected String lang;
		
		public GenericQuerySupplier(String path) {
			super();
			this.path = path;
		}
		
		public GenericQuerySupplier(String path, String lang, IRI facetProperty, IRI facetValue) {
			super();
			this.path = path;
			this.facetProperty = facetProperty;
			this.facetValue = facetValue;
			this.lang = lang;
		}

		@Override
		public String get() {
	
			String sparql = "" +
					"SELECT ?"+KEY_VAR_NAME+" ?"+VALUE_VAR_NAME+""+"\n" +
					"WHERE {"+"\n" +
					"	?"+KEY_VAR_NAME+" "+this.path+" ?"+VALUE_VAR_NAME+"\n" +
					((this.facetProperty != null && this.facetValue != null)?"	?"+KEY_VAR_NAME+" <"+this.facetProperty.stringValue()+"> <"+this.facetValue.stringValue()+">"+"\n":"")+
					((this.lang != null)?"   FILTER(langMatches(lang(?"+VALUE_VAR_NAME+"), '"+this.lang+"'))"+"\n":"")+
					"}"
			;		
			
			return sparql;
		}
	}
	
}
