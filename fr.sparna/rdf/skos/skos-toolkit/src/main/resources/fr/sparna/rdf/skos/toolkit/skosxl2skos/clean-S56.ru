# clean skosxl:altLabel
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX skosxl:<http://www.w3.org/2008/05/skos-xl#>
DELETE WHERE {
	?x skosxl:altLabel ?y .
	?y ?p ?o .
}