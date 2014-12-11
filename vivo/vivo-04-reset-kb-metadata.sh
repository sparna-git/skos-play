export VIVO_SESAME=http://localhost:8180/openrdf-sesame/repositories/sanctuaires
export VIVO_KB_METADATA_TURTLE_FILE=/home/thomas/workspace/01-Projets/MAE/ontologie/sanct-vitro-groups.ttl

echo "Reloading VIVO KB metadata in $VIVO_KB_METADATA_TURTLE_FILE in Sesame at $VIVO_SESAME..."

#
# See the Sesame protocol documentation : http://rdf4j.org/sesame/2.7/docs/system.docbook?view#repository-statements
#
# -X PUT 		: use PUT HTTP verb
# --header 		: sets HTTP header
# --data-binary	: sets the content of the file as request content, with no extra processing
#				  in particular, contrary to --data, this does not simulate a form submission
# /statements?context=...	: the graph in which to store the data
# &baseURI=...				: baseURI to interpret the data

curl \
	-X PUT \
	--header "Content-type: application/x-turtle;charset=UTF-8" \
	--data-binary @$VIVO_KB_METADATA_TURTLE_FILE \
	$VIVO_SESAME/statements?context=%3Chttp://vitro.mannlib.cornell.edu/default/vitro-kb-applicationMetadata%3E&baseURI=%3Chttp://this.is.base.uri/%3E