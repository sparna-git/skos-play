package fr.sparna.rdf.skos.toolkit;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

/**
 * Returns a query that tests if a Collection has any other collection as members
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public class HasOnlyConceptMembersQuery implements SparqlQueryBuilderIfc {

	protected String collection;

	public HasOnlyConceptMembersQuery(String collection) {
		this.collection = collection;
	}

	@Override
	public String getSPARQL() {
		String sparql = "" +
				"ASK"+"\n" +
				" WHERE {"+"\n" +
				"	<"+collection+"> <"+SKOS.MEMBER+"> ?concept ."+"\n" +
				"	?concept a <"+SKOS.CONCEPT+"> ."+"\n" +
				"   FILTER NOT EXISTS { "+"\n" +
				"	  <"+collection+"> <"+SKOS.MEMBER+"> ?member ."+"\n" +
				"	  ?member a <"+SKOS.COLLECTION+"> ."+"\n" +
				"   }"+"\n" +
			    "}";
		return sparql;
	}
}
