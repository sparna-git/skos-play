package fr.sparna.rdf.sesame.toolkit.reader;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

/**
 * A special kind of SparqlQueryBuilderIfc that also implements KeyValueHelperIfc,
 * and returns itself as the builder in <code>getSPARQLQueryBuilder</code>. Concrete
 * implementations of this class must determine and return a KeyMappingGeneratorIfc
 * and a KeyValueBindingSetReaderIfc in the corresponding methods.
 * @author Thomas Francart
 *
 * @param <Key>
 * @param <Value>
 */
public abstract class KeyValueSparqlQueryBuilder<Key, Value> implements SparqlQueryBuilderIfc, KeyValueHelperIfc<Key, Value> {

	@Override
	public SparqlQueryBuilderIfc getSPARQLQueryBuilder() {
		return this;
	}
	
}
