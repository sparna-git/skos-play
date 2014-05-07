<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:fonc="http://http://opendata-preprod.inra.fr/fonctions"
	xmlns:results="http://www.w3.org/2005/sparql-results#"
	xmlns:xsltsparql="http://berrueta.net/research/xsltsparql"
>
	<xsl:import href="xslt-sparql.xsl" />

	<xsl:output indent="yes" method="xml" />

	<xsl:variable name="endpoint">
    	<xsl:value-of select="'http://www.nakala.fr/sparql'"/>
  	</xsl:variable>
  	
  	<xsl:function name="fonc:commonPrefixes">
	    <xsl:value-of select="concat(
	      'PREFIX owl: &lt;http://www.w3.org/2002/07/owl#&gt; ',
	      'PREFIX xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; ',
	      'PREFIX rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; ',
	      'PREFIX rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; ',
	      'PREFIX foaf: &lt;http://xmlns.com/foaf/0.1/&gt; ',
	      'PREFIX dc: &lt;http://purl.org/dc/elements/1.1/&gt; ',
	      'PREFIX dcterms: &lt;http://purl.org/dc/terms/&gt; ',  
	      'PREFIX skos: &lt;http://www.w3.org/2004/02/skos/core#&gt; '
	    )" />
	</xsl:function>

	<xsl:variable name="query-data">
    SELECT ?data
    WHERE {
    	?data a foaf:Document .
    	?data dcterms:publisher ?user .
    }
	</xsl:variable>

	<xsl:template match="/">
		<xsl:call-template name="data-list">
			<xsl:with-param name="results" select="xsltsparql:sparqlEndpoint(concat(fonc:commonPrefixes(), $query-data), $endpoint)" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="data-list">
		<xsl:param name="results" />
		<datas>
			<xsl:apply-templates select="$results/results:result" />
		</datas>
	</xsl:template>
	<xsl:template match="results:result">
		<data></data>
	</xsl:template>
	
	
</xsl:stylesheet>