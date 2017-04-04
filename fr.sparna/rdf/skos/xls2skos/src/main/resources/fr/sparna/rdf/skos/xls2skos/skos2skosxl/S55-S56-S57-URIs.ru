# S55 : The property chain (skosxl:prefLabel, skosxl:literalForm) is a sub-property of skos:prefLabel.
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX skosxl:<http://www.w3.org/2008/05/skos-xl#>
PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>
DELETE {
	?x ?labelProperty ?label
}
INSERT {
	?x ?xlLabelProperty ?labelUri .
	?labelUri skosxl:literalForm ?label .
	?labelUri a skosxl:Label .
} WHERE {
	# XL-ify Concepts, Collections, ConceptSchemes
	{ { ?x a skos:Concept . } UNION { ?x a skos:Collection . } UNION { ?x a skos:ConceptScheme . } }
	
	# compute how many labels on the same concepts are before this one
	{
		SELECT ?x ?labelProperty ?label (COUNT(?anotherLabelBefore) AS ?index)
		WHERE {
			?x ?labelProperty ?label .
			FILTER(?labelProperty IN (skos:prefLabel, skos:altLabel, skos:hiddenLabel))
			OPTIONAL {
				?x skos:prefLabel|skos:altLabel|skos:hiddenLabel ?anotherLabelBefore .
				FILTER(STR(?anotherLabelBefore) < STR(?label))
			}
		} GROUP BY ?x ?labelProperty ?label
	}
	
	BIND(IRI(
		CONCAT(
			STR(?x),
			'-label-',
			STR(?index)
		)
	) AS ?labelUri)
	
	BIND(
		IF(?labelProperty = skos:prefLabel,
			skosxl:prefLabel,
			IF(?labelProperty = skos:altLabel,
				skosxl:altLabel,
				IF(?labelProperty = skos:hiddenLabel,
					skosxl:hiddenLabel,
					rdfs:label
				)
			)
		)
	AS ?xlLabelProperty)
}