
# check script arguments
if [ "$#" -ne 5 ]; then
    echo "Usage: $0 <input_file_path> <target_graph> <vivo_url> <vivo_login> <vivo_password>" >&2
    exit 1
fi

export TURTLE_FILE=$1
export GRAPH_URI=$2
export VIVO_URL=http://localhost:8080/sanctuaires
export VIVO_LOGIN=root@myDomain.com
export VIVO_PASSWORD=password

# temporary file
export TEMP_SPARQL=temp.ru

rm -rf $TEMP_SPARQL

echo "Loading data from $TURTLE_FILE in graph $GRAPH_URI in VIVO $VIVO_URL..."

# echo -n "INSERT DATA { GRAPH <$GRAPH_URI> { " >> $TEMP_SPARQL
# cat $TURTLE_FILE >> $TEMP_SPARQL
# echo "} }" >> $TEMP_SPARQL
echo -n "update=LOAD <file://$TURTLE_FILE> INTO GRAPH <$GRAPH_URI>" >> $TEMP_SPARQL

# -v 	: verbose to print headers
# -i 	: include HTTP headers in the output
# -d 	: data to be posted (will triger content-type multipart)
#
# To run a proxy that will print request content :
# 	nc -l localhost 8000 &
# Then add to the curl command :
#	--proxy localhost:8000
# see :
# 	http://stackoverflow.com/questions/6180162/echo-curl-request-header-body-without-sending-it
curl -v -i -d "email=$VIVO_LOGIN" -d "password=$VIVO_PASSWORD" -d "@$TEMP_SPARQL" "$VIVO_URL/api/sparqlUpdate"