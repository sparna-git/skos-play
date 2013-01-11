#!/bin/sh
# Examples command-lines for the rdf-toolkit application
export VERSION=1.0-SNAPSHOT

# infer -i <input_file_or_directory> -r <ruleset> -o <output_file>
echo 'Running infer example...'
java -jar ../../../../target/rdf-toolkit-$VERSION-onejar.jar infer -i infer/data -r infer/align-strict.pie -o infer/output.rdf
echo 'Done. Look in infer/output.rdf for result'
# InferOWL <input_file_or_directory> <output_file>
echo 'Running InferOWL example...'
echo 'Look in InferOWL/output.rdf for result'
java -jar rdf-toolkit-$VERSION-onejar.jar InferOWL InferOWL InferOWL/output.rdf
# InferRDFS <input_file_or_directory> <output_file>
echo 'Running InferRDFS example...'
echo 'Look in InferRDFS/output.rdf for result'
java -jar rdf-toolkit-$VERSION-onejar.jar InferRDFS InferRDFS InferRDFS/output.rdf
# InferSPARQL <input_file_or_directory_or_SPRING_config_file> <path_to_CONSTRUCT_SPARQL_directory_or_sparql_file> <output_RDF_file>
echo 'Running InferSPARQL example...'
echo 'Look in InferSPARQL/output.rdf for result'
java -jar rdf-toolkit-$VERSION-onejar.jar InferSPARQL InferSPARQL/data InferSPARQL/output.rdf
# LoadXML <input_XML_file_or_directory> <path_to_XSL_file_to_generate_RDF> <output_file>
echo 'Running LoadXML example...'
echo 'Look in LoadXML/output.rdf for result'
java -jar rdf-toolkit-$VERSION-onejar.jar LoadXML LoadXML/xmlFiles LoadXML/bnf2rdf.xsl LoadXML/output.rdf
# Merge <input_directory> <output_file> [<namespaces_key_value_pairs>]
echo 'Running Merge example...'
echo 'Look in Merge/output.rdf for result'
java -jar rdf-toolkit-$VERSION-onejar.jar Merge Merge Merge/output.rdf bnf:http://www.bnf.fr/autorites/ontology/
# PrettyPrint <input_file_or_directory> <output_file> [<namespaces_key_value_pairs>]
echo 'Running PrettyPrint example...'
echo 'Look in PrettyPrint/output.rdf for result'
java -jar rdf-toolkit-$VERSION-onejar.jar PrettyPrint PrettyPrint PrettyPrint/output.rdf bo:http://www.mondeca.com/system/basicontology# oco:http://www.mondeca.com/system/ontology_creation#
# PrintStatistics <input_file_or_directory>
echo 'Running PrintStatistics example...'
java -jar rdf-toolkit-$VERSION-onejar.jar PrintStatistics PrintStatistics
# QuerySelect <input_file_or_directory> <path_to_SELECT_SPARQL_directory_or_sparql_file> <output_HTML_file>
echo 'Running QuerySelect example...'
echo 'Look in QuerySelect/output.html for result'
java -jar rdf-toolkit-$VERSION-onejar.jar QuerySelect QuerySelect/data.rdf QuerySelect/queries QuerySelect/output.html
echo 'Running QuerySelect Advanced example...'
echo 'Look in QuerySelectAdvanced/output.html for result'
java -jar rdf-toolkit-$VERSION-onejar.jar QuerySelect QuerySelectAdvanced/datasource.xml QuerySelectAdvanced/queries QuerySelectAdvanced/output.html
echo 'Running QuerySelectCSV example...'
echo 'Look in QuerySelect/output-csv for result'
java -jar rdf-toolkit-$VERSION-onejar.jar QuerySelectCSV QuerySelect/data.rdf QuerySelect/queries QuerySelect/output-csv
# Transform <input_file_or_directory> <path_to_CONSTRUCT_SPARQL_directory_or_sparql_file> <output_RDF_file>
echo 'Running Transform example...'
echo 'Look in Transform/output.rdf for result'
java -jar rdf-toolkit-$VERSION-onejar.jar Transform Transform/languages-48.rdf Transform/queries Transform/output.rdf
# PrintSKOSTree <input_file_or_directory> [<language>]
# output is generated in the console and can redirected to a file
echo 'Running PrintSKOSTree example...'
echo 'Look in PrintSKOSTree/output.txt for result'
java -jar rdf-toolkit-$VERSION-onejar.jar PrintSKOSTree PrintSKOSTree/data fr > PrintSKOSTree/output.txt
# PrintSKOSTreeMap <input_file_or_directory> <language> [<root_uri>]
echo 'Running PrintSKOSTreeMap example...'
# java -jar rdf-toolkit-$VERSION-onejar.jar PrintSKOSTreeMap PrintSKOSTreeMap/data fr
