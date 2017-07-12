INSERT {
	?c a isothes:ThesaurusArray .
	?c isothes:superOrdinate ?superOrdinate .
	?superOrdinate isothes:subOrdinate ?c .
} WHERE {
	?c a skos:Collection .
	?c skos:member ?member .
	# all the members are Concepts
	FILTER NOT EXISTS {
		?member a ?memberClass .
		FILTER(?memberClass != skos:Concept)
	}
	# they have a single parent or no parent at all
	{
		SELECT ?c (COUNT(?parent) AS ?parentCount) 
		WHERE {
			?c a skos:Collection .
			?c skos:member ?member .
			?member skos:broader|^skos:narrower ?parent .
		}
		GROUP BY ?c
		HAVING(?parentCount = 1 || ?parentCount = 0)
	}
	OPTIONAL {
		# get the - single - superOrdinate of that Collection
		?c skos:member/(skos:broader|^skos:narrower) ?superOrdinate .
	}
}