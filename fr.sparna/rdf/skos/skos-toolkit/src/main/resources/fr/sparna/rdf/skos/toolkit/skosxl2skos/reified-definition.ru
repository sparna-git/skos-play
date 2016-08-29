# Special handling of skos:definition exported from VocBench, that reifies the definitions
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX skosxl:<http://www.w3.org/2008/05/skos-xl#>
PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
INSERT {
	?x skos:definition ?y
} WHERE {
	?x skos:definition/rdf:value ?y .
}