package fr.sparna.rdf.sesame.toolkit.reader;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

/**
 * A base implementation of KeyValueHelperIfc<Key, Value> that takes together a 
 * SparqlQueryBuilderIfc, a corresponding KeyMappingGeneratorIfc, and a corresponding 
 * KeyValueBindingSetReaderIfc.
 * 
 * @author Thomas Francart
 *
 * @param <Key>
 * @param <Value>
 */
public class KeyValueHelperBase<Key, Value> implements KeyValueHelperIfc<Key, Value> {

	protected SparqlQueryBuilderIfc SPARQLQueryBuilder;
	protected KeyMappingGeneratorIfc<Key> keyMappingGenerator;
	protected KeyValueBindingSetReaderIfc<Key, Value> keyValueBindingSetReader;

	public KeyValueHelperBase() {
		super();
	}

	public KeyValueHelperBase(
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

	public static KeyValueHelperBase<URI, URI> createURIHelper(
			SparqlQueryBuilderIfc builder,
			String keyVarName,
			String valueVarName
	) {
		return new KeyValueHelperBase<URI, URI>(
			builder,
			new UriKeyMappingGenerator(keyVarName),
			new UriToUriBindingSetReader(keyVarName, valueVarName)			
		);
	}
	
	public static KeyValueHelperBase<URI, Literal> createLiteralHelper(
			SparqlQueryBuilderIfc builder,
			String keyVarName,
			String valueVarName
	) {
		return new KeyValueHelperBase<URI, Literal>(
			builder,
			new UriKeyMappingGenerator(keyVarName),
			new UriToLiteralBindingSetReader(keyVarName, valueVarName)			
		);
	}
}
