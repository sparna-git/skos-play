package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

/**
 * Returns a query that tests if a Collection has any other collection as members
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public class HasOnlyConceptMembersQuery implements Supplier<String> {

	protected String collection;

	public HasOnlyConceptMembersQuery(String collection) {
		this.collection = collection;
	}

	@Override
	public String get() {
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
