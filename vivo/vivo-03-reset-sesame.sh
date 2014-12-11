export VIVO_SESAME=http://localhost:8180/openrdf-sesame/repositories/sanctuaires

echo "Resetting Sesame repository at $VIVO_SESAME..."

# issue a Sesame-specific DELETE operation
# TODO : should be a SPARQL-compliant CLEAR operation
curl -X DELETE $VIVO_SESAME/statements 