package fr.sparna.rdf.sesame.toolkit.reader;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public class KeyValueSparqlHelperBase<Key, Value> implements KeyValueSparqlHelperIfc<Key, Value> {

	protected SparqlQueryBuilderIfc SPARQLQueryBuilder;
	protected KeyMappingGeneratorIfc<Key> keyMappingGenerator;
	protected KeyValueBindingSetReaderIfc<Key, Value> keyValueBindingSetReader;

	public KeyValueSparqlHelperBase() {
		super();
	}

	public KeyValueSparqlHelperBase(
			SparqlQueryBuilderIfc SPARQLQueryBuilder,
			KeyMappingGeneratorIfc<Key> keyMappingGenerator,
			KeyValueBindingSetReaderIfc<Key, Value> keyValueBindingSetReader
	) {
		super();
		this.SPARQLQueryBuilder = SPARQLQueryBuilder;
		this.keyMappingGenerator = keyMappingGenerator;
		this.keyValueBindingSetReader = keyValueBindingSetReader;
	}

	@Override
	public SparqlQueryBuilderIfc getSPARQLQueryBuilder() {
		return SPARQLQueryBuilder;
	}

	public void setSPARQLQueryBuilder(SparqlQueryBuilderIfc sPARQLQueryBuilder) {
		SPARQLQueryBuilder = sPARQLQueryBuilder;
	}

	@Override
	public KeyMappingGeneratorIfc<Key> getKeyMappingGenerator() {
		return keyMappingGenerator;
	}

	public void setKeyMappingGenerator(KeyMappingGeneratorIfc<Key> keyMappingGenerator) {
		this.keyMappingGenerator = keyMappingGenerator;
	}

	@Override
	public KeyValueBindingSetReaderIfc<Key, Value> getKeyValueBindingSetReader() {
		return keyValueBindingSetReader;
	}

	public void setKeyValueBindingSetReader(KeyValueBindingSetReaderIfc<Key, Value> keyValueBindingSetReader) {
		this.keyValueBindingSetReader = keyValueBindingSetReader;
	}

	public static KeyValueSparqlHelperBase<URI, URI> createURIHelper(
			SparqlQueryBuilderIfc builder,
			String keyVarName,
			String valueVarName
	) {
		return new KeyValueSparqlHelperBase<URI, URI>(
			builder,
			new UriMappingGenerator(keyVarName),
			new UriToURIBindingSetReader(keyVarName, valueVarName)			
		);
	}
	
	public static KeyValueSparqlHelperBase<URI, Literal> createLiteralHelper(
			SparqlQueryBuilderIfc builder,
			String keyVarName,
			String valueVarName
	) {
		return new KeyValueSparqlHelperBase<URI, Literal>(
			builder,
			new UriMappingGenerator(keyVarName),
			new UriToLiteralBindingSetReader(keyVarName, valueVarName)			
		);
	}
}
