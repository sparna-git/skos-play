# Clean reified definitions
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX skosxl:<http://www.w3.org/2008/05/skos-xl#>
PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
DELETE WHERE {
	?x skos:definition ?y .
	?y ?p ?o .
}