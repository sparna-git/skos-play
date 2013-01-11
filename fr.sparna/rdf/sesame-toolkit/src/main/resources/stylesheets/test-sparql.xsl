<?xml version="1.0"?> 
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:results="http://www.w3.org/2005/sparql-results#"
	xmlns:xsltsparql="http://berrueta.net/research/xsltsparql"
    version="2.0"
>

  <xsl:function name="xsltsparql:sparql">
    <xsl:param name="query"/>
    <xsl:param name="endpointUrl"/>
    
    <xsl:variable name="encodedQuery" select="encode-for-uri($query)"/>
    
    <xsl:variable name="requestedUrl" select="concat($endpointUrl,'?query=',$encodedQuery)"/>
    
    <xsl:variable name="endpointResponse" select="document($requestedUrl)"/>
    
    <xsl:if test="not($endpointResponse/results:sparql)">
      <xsl:message>The response does not contain &lt;sparql:sparql&gt; in the root!</xsl:message>
      <xsl:message>Results: <xsl:copy-of select="$endpointResponse"/></xsl:message>
    </xsl:if>
    
    <xsl:copy-of select="$endpointResponse/results:sparql"/>
  </xsl:function>

  <xsl:variable name="query" select="'SELECT DISTINCT ?type WHERE { ?x a ?type } LIMIT 10'" />

  <xsl:variable name="endpoint" select="'http://localhost:5151/sparql'" />

  <xsl:template match="/">
    <xsl:apply-templates select="xsltsparql:sparql($query, $endpoint)"/>
  </xsl:template>

  <xsl:template match="results:results">
    There are <xsl:value-of select="count(results:result)"/> result(s)
  </xsl:template>

</xsl:stylesheet>