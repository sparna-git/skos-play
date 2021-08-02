package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;

/**
 * Returns a query that tests if any concept in a ConceptScheme is subject of a skos:broader or skos:narrower predicate
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public class HasConceptWithBroaderOrNarrower implements Supplier<String> {

	protected IRI conceptScheme;

	public HasConceptWithBroaderOrNarrower(IRI conceptScheme) {
		this.conceptScheme = conceptScheme;
	}

	@Override
	public String get() {
		String sparql = "" +
				"ASK"+"\n" +
				" WHERE {"+"\n" +
				"	?concept a <"+SKOS.CONCEPT+"> ."+"\n" +
				((this.conceptScheme != null)?"   ?concept <"+ SKOS.IN_SCHEME +"> <"+this.conceptScheme.stringValue()+"> ."+"\n":"") +
				"	?concept <"+SKOS.BROADER+">|<"+SKOS.NARROWER+"> ?anotherConcept ."+"\n" +
			    "}";
		return sparql;
	}
}
