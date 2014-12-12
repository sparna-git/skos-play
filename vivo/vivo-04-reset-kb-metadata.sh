
# check script arguments
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <sesame_repository> <kb_metadata_turtle_file>" >&2
    exit 1
fi

export SESAME_REPOSITORY=$1
export VIVO_KB_METADATA_TURTLE_FILE=$2
# this is a fixed value and must not be changed
export TARGET_GRAPH=http://vitro.mannlib.cornell.edu/default/vitro-kb-applicationMetadata
# the base URI to read input file
export BASE_URI=http://this.is.base.uri

echo "Reloading VIVO KB metadata in $VIVO_KB_METADATA_TURTLE_FILE in Sesame at $SESAME_REPOSITORY..."

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
	$SESAME_REPOSITORY/statements?context=%3C$TARGET_GRAPH%3E&baseURI=%3C$BASE_URI/%3E