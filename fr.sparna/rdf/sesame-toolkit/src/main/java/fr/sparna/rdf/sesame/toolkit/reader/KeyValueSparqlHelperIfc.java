package fr.sparna.rdf.sesame.toolkit.reader;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public interface KeyValueSparqlHelperIfc<Key, Value> {

	public SparqlQueryBuilderIfc getSPARQLQueryBuilder();
	
	public KeyMappingGeneratorIfc<Key> getKeyMappingGenerator();
	
	public KeyValueBindingSetReaderIfc<Key, Value> getKeyValueBindingSetReader();

}
