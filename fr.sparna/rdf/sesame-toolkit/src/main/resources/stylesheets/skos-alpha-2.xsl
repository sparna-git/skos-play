<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:results="http://www.w3.org/2005/sparql-results#"
        xmlns:sparql="http://berrueta.net/research/xsltsparql"
        version="2.0">

  <xsl:function name="sparql:prefixes">
    <xsl:value-of select="concat(
      'PREFIX owl: &lt;http://www.w3.org/2002/07/owl#&gt; ',
      'PREFIX xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; ',
      'PREFIX rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; ',
      'PREFIX rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; ',
      'PREFIX foaf: &lt;http://xmlns.com/foaf/0.1/&gt; ',
      'PREFIX dc: &lt;http://purl.org/dc/elements/1.1/&gt; ', 
      'PREFIX skos: &lt;http://www.w3.org/2004/02/skos/core#&gt; ')" />
  </xsl:function>

  <xsl:function name="sparql:sparql">
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

  <!-- BEGIN of configuration parameters -->

  <!-- The language of thesaurus. Only one language can be displayed at
  a time, so you have to choose what language do you want. Use the
  empty string for the default language -->
  <xsl:param name="lang" select="'fr'"/>
  
  <xsl:param name="endpoint" select="'http://localhost:5151/sparql'" />

  <!-- END of configuration parameters -->

  <xsl:template match="/">
    <html>
      <head>
	<title>Vocabulary : alphabetical index</title>
	<style type="text/css">
	  .prefLabel {
	    font-weight:bold;
	  }
	  .description {
	    font-size:-1;
	  }
	  .abbr {
	    font-weight:bold;
	  }
	</style>
      </head>
      <body>
	<h1>Vocabulary: alphabetical index</h1>
	<ul class="vocabulary">
	  <xsl:apply-templates
	      select="sparql:sparql(
	      		concat(
	      			sparql:prefixes(),
			      	'SELECT ?concept ?label ?prefLabel
			      	WHERE {
		      			{ ?concept skos:prefLabel ?label }
		      			UNION 
		      			{
			      		?concept skos:prefLabel ?prefLabel .
			      		?concept skos:altLabel ?label
			    		} .
			    		FILTER (lang(?label) = &quot;',
				    $lang,
				    '&quot;) }'
				),
                $endpoint
           )/results:results/results:result"
	      mode="entry">
	    <xsl:sort select="results:binding[@name='label']"/>
	  </xsl:apply-templates>
	</ul>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="results:result" mode="entry">
    <li>
      <xsl:choose>
	<xsl:when test="results:binding[@name='prefLabel']">
	  <span class="altLabel"><xsl:value-of select="results:binding[@name='label']"/></span>
	  <ul class="use"><li>
	    <xsl:text>USE </xsl:text><xsl:value-of select="results:binding[@name='prefLabel']"/>
	  </li></ul>
	</xsl:when>
      </xsl:choose>
    </li>
  </xsl:template>

</xsl:stylesheet>