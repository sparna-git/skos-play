#!/bin/sh

export SERVICE_URL="http://localhost:8080/gate-service-1.0-SNAPSHOT/annotate"
export DOCID=$1
export APPLICATION=$2
export ANNOTATIONS="annotation,WrongAnnotation"
export XSLT=""
export ANNOTATION_URL="$SERVICE_URL?annotations=$ANNOTATIONS&application=$APPLICATION&xslt=$XSLT&docId=$DOCID"

echo "sending $1 to $ANNOTATION_URL"
# curl -X POST -d @$1 $SERVICE_URL -v -o temp.xml
curl -H Content-Type:text/xml -X POST -d @$1 $ANNOTATION_URL -v -o temp.xml
xmllint --format temp.xml > $3
