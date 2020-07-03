package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;

/**
 * Returns a query that tests if a Collection has any other collection as members
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public class HasConceptNotInACollectionQuery implements Supplier<String> {

	protected IRI conceptScheme;

	public HasConceptNotInACollectionQuery(IRI conceptScheme) {
		this.conceptScheme = conceptScheme;
	}

	@Override
	public String get() {
		String sparql = "" +
				"ASK"+"\n" +
				" WHERE {"+"\n" +
				"	?concept a <"+SKOS.CONCEPT+"> ."+"\n" +
				((this.conceptScheme != null)?"   ?concept <"+ SKOS.IN_SCHEME +"> <"+this.conceptScheme.stringValue()+">":"") +
				"   FILTER NOT EXISTS { "+"\n" +
				"	  ?collection <"+SKOS.MEMBER+"> ?concept ."+"\n" +
				"   }"+"\n" +
			    "}";
		return sparql;
	}
}
