package fr.sparna.rdf.sesame.toolkit.reader;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public abstract class KeyValueSparqlQueryBuilder<Key, Value> implements SparqlQueryBuilderIfc, KeyValueSparqlHelperIfc<Key, Value> {

	@Override
	public SparqlQueryBuilderIfc getSPARQLQueryBuilder() {
		return this;
	}
	
}
