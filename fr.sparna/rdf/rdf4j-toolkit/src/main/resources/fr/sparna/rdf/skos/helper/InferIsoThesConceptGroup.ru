INSERT {
	?c a isothes:ConceptGroup .
} WHERE {
	?c a skos:Collection .
	FILTER NOT EXISTS {
		?c a isothes:ThesaurusArray .
	}
}