#!/bin/sh

#
# $1 : input odf file (exemples.odf)
#

if [ $# -eq 0 ]
  then
    echo "Usage xxxxx.sh <input_file.odf>"
    exit 1
fi

#
# Commande de lancement du processeur saxon
#
export JAVA_SAXON="java -Xmx1024m -cp saxon9.jar net.sf.saxon.Transform"

filename=$(basename "$1")
extension="${filename##*.}"
output="${filename%.*}"

# unzip odf file to content.xml
unzip -o $1 content.xml

# transform odf to simple XML
$JAVA_SAXON -s:content.xml  -xsl:odf2simplexml.xsl  -o:$output.xml

# clean

# delete content.xml - XML version of the odf file
rm content.xml