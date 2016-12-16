# S55 : The property chain (skosxl:prefLabel, skosxl:literalForm) is a sub-property of skos:prefLabel.
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX skosxl:<http://www.w3.org/2008/05/skos-xl#>
PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>

DELETE {
	?x ?noteProperty ?note
}
INSERT {
	?x ?noteProperty ?noteUri .
	?noteUri rdf:value ?note .
} WHERE {
	?x a skos:Concept .
	
	# compute how many labels on the same concepts are before this one
	{
		SELECT ?x ?noteProperty ?note (COUNT(?anotherNoteBefore) AS ?index)
		WHERE {
			?x ?noteProperty ?note .
			FILTER(isLiteral(?note))
			FILTER(?noteProperty IN (skos:definition))
			OPTIONAL {
				?x skos:definition ?anotherNoteBefore .
				FILTER(STR(?anotherNoteBefore) < STR(?note))
			}
		} GROUP BY ?x ?noteProperty ?note
	}
	
	BIND(IRI(
		CONCAT(
			STR(?x),
			'-def-',
			STR(?index)
		)
	) AS ?noteUri)

}