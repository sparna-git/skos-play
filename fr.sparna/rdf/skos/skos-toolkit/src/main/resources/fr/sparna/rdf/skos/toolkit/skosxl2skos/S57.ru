# S57 : The property chain (skosxl:hiddenLabel, skosxl:literalForm) is a sub-property of skos:hiddenLabel.
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX skosxl:<http://www.w3.org/2008/05/skos-xl#>
INSERT {
	?x skos:hiddenLabel ?y
} WHERE {
	?x skosxl:hiddenLabel/skosxl:literalForm ?y .
}