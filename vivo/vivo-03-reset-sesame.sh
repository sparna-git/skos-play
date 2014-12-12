
# check script arguments
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <sesame_repository> ($0 http://localhost:8080/openrdf-sesame/repositories/test)" >&2
    exit 1
fi

export SESAME_REPOSITORY=$1

echo "Resetting Sesame repository $SESAME_REPOSITORY..."

# issue a Sesame-specific DELETE operation
# TODO : should be a SPARQL-compliant CLEAR operation
curl -X DELETE $SESAME_REPOSITORY/statements 