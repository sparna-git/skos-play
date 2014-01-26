package fr.sparna.rdf.sesame.toolkit.reader;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

/**
 * An abstraction for objects capable of constructing a SPARQL query, telling how
 * to map the key to the SPARQL query, and how to read the correspondance <Key, Value>
 * from a result line of the executed query.
 *
 * @param <Key>		The type of the key being read (typically Resource or URILang)
 * @param <Value>	The type of the value being read (typically Literal or Resource)
 */
public interface KeyValueHelperIfc<Key, Value> {

	public SparqlQueryBuilderIfc getSPARQLQueryBuilder();
	
	public KeyMappingGeneratorIfc<Key> getKeyMappingGenerator();
	
	public KeyValueBindingSetReaderIfc<Key, Value> getKeyValueBindingSetReader();

}
