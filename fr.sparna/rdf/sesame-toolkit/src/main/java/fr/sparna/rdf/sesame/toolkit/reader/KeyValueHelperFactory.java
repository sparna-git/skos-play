package fr.sparna.rdf.sesame.toolkit.reader;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.URI;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public class KeyValueHelperFactory {

	public static KeyValueHelperIfc<URI, Literal> createUriToLiteralHelper(SparqlQueryBuilderIfc builder, String keyVarName, String valueVarName) {
		return new KeyValueHelperBase<URI, Literal>(
				builder,
				new UriKeyMappingGenerator(keyVarName),
				new UriToLiteralBindingSetReader(keyVarName, valueVarName)
		);
	}
	
	public static KeyValueHelperIfc<URI, Literal> createUriToLiteralHelper(String pathOrProperty) {
		if(pathOrProperty.startsWith("http://")) {
			pathOrProperty = "<"+pathOrProperty+">";
		}
		
		return new KeyValueHelperBase<URI, Literal>(
				new SparqlQueryBuilder("SELECT ?key ?value WHERE { ?key "+pathOrProperty+" ?value }"),
				new UriKeyMappingGenerator("key"),
				new UriToLiteralBindingSetReader("key", "value")
		);
	}
	
}
