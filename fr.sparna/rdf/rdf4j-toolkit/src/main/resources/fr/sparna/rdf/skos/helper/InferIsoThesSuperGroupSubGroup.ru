INSERT {
	?subGroup isothes:superGroup ?superGroup .
	?subGroup ^isothes:subGroup  ?superGroup .
} WHERE {
	?superGroup a isothes:ConceptGroup .
	?superGroup skos:member ?subGroup .
	?subGroup a isothes:ConceptGroup .
}