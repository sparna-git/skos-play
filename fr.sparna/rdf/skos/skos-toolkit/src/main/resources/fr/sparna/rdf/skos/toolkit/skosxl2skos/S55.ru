# S55 : The property chain (skosxl:prefLabel, skosxl:literalForm) is a sub-property of skos:prefLabel.
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX skosxl:<http://www.w3.org/2008/05/skos-xl#>
INSERT {
	?x skos:prefLabel ?y
} WHERE {
	?x skosxl:prefLabel/skosxl:literalForm ?y .
}