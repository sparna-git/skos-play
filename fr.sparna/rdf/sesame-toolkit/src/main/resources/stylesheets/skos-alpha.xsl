<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:results="http://www.w3.org/2005/sparql-results#"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:sparql="XalanExt"
        xmlns:sparqlModel="XalanModelExt"
        extension-element-prefixes="sparql sparqlModel"
        version="2.0">

  <!-- *********************************************************
       An XSLT+SPARQL stylesheet to produce Alphabetic displays
       from a thesaurus encoded in SKOS. The results follows
       the guidelines in section 9.2 of ISO 2788-1986. More
       on this at http://www.w3.org/2006/07/SWD/wiki/SkosDesign/PresentationInformation

       There are some configuration parameters below, you can
       tune them to customize the result. It is also possible to
       edit the markup and the CSS.

       (c) Diego Berrueta, 2008

       ********************************************************* -->

  <!-- KNOWN LIMITATIONS:
       - Definitions and scope notes (SN) are ignored.
       - References to non-preferred labels and top terms are ignored.
  -->

  <!-- BEGIN of configuration parameters -->

  <!-- The language of thesaurus. Only one language can be displayed at
  a time, so you have to choose what language do you want. Use the
  empty string for the default language -->
  <xsl:param name="lang" select="''"/>

  <!--<xsl:param name="rdf-file"
	     select="'file:///Users/berrueta/Documents/transforma-rdf/xslt-sparql/skos/ipsv.rdf'"/-->
  <!--xsl:param name="rdf-file"
	     select="'file:///Users/diego/scratch/svn/transforma-rdf/xslt-sparql/skos/gcl2.1.rdf'"/-->
  <!--xsl:param name="rdf-url"
	     select="'http://isegserv.itd.rl.ac.uk/skos/gcl/gcl2.1.rdf'"/-->

  <!-- END of configuration parameters -->

  <xalan:component prefix="sparql">
    <xalan:script lang="javaclass" src="xalan://net.berrueta.xsltsparql.XalanExt"/>
  </xalan:component>

  <xalan:component prefix="sparqlModel">
    <xalan:script lang="javaclass" src="xalan://net.berrueta.xsltsparql.XalanModelExt"/>
  </xalan:component>

  <xsl:template match="/">
    <xsl:variable name="originalModel"
		  select="sparqlModel:readModel(.)"/>
<!--    <xsl:variable name="originalModel"
		  select="sparqlModel:readModel($rdf-file)"/> -->
    <xsl:variable name="complementaryModel"
		  select="sparqlModel:parseString('
			  @prefix skos: &lt;http://www.w3.org/2004/02/skos/core#&gt;.
			  @prefix compl: &lt;http://berrueta.net/skos-compl#&gt;.
			  skos:broader compl:abbr &quot;BT&quot; ;
                                       compl:priority 4. 
			  skos:narrower compl:abbr &quot;NT&quot; ;
                                        compl:priority 5. 
			  skos:related compl:abbr &quot;RT&quot; ; 
                                       compl:priority 6. ', 'N3')"/>
    <xsl:variable name="model"
		  select="sparqlModel:mergeModels($originalModel, $complementaryModel)"/>
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
	      select="sparqlModel:sparqlModel(concat(sparql:commonPrefixes(),
		      'SELECT ?concept ?label ?prefLabel
		      WHERE {{ ?concept skos:prefLabel ?label
			      } UNION {
			      ?concept skos:prefLabel ?prefLabel .
			      ?concept skos:altLabel ?label } .
			      FILTER (lang(?label) = &quot;',$lang,'&quot;)
                      }'), $model)/results:results/results:result"
	      mode="entry">
	    <xsl:sort select="results:binding[@name='label']"/>
	    <xsl:with-param name="model" select="$model"/>
	  </xsl:apply-templates>
	</ul>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="results:result" mode="entry">
    <xsl:param name="model"/>
    <li>
      <xsl:choose>
	<xsl:when test="results:binding[@name='prefLabel']">
	  <span class="altLabel"><xsl:value-of select="results:binding[@name='label']"/></span>
	  <ul class="use"><li>
	    <xsl:text>USE </xsl:text><xsl:value-of select="results:binding[@name='prefLabel']"/>
	  </li></ul>
	</xsl:when>
	<xsl:otherwise>
	  <span class="prefLabel"><xsl:value-of select="results:binding[@name='label']"/></span>
	  <xsl:variable name="sparqlQuery"
	      select="concat(sparql:commonPrefixes(),
		      'PREFIX compl: &lt;http://berrueta.net/skos-compl#&gt;
		      SELECT ?sndconcept ?abbr ?priority ?label
		      WHERE { &lt;',normalize-space(results:binding[@name='concept']),'&gt; ?p ?sndconcept .
		      ?sndconcept skos:prefLabel ?label .
		      ?p compl:abbr ?abbr .
		      ?p compl:priority ?priority .
		      FILTER (lang(?label) = &quot;',$lang,'&quot;) }
		      ORDER BY ?priority ?label')"/>
	  <ul class="description">
	    <xsl:apply-templates select="sparqlModel:sparqlModel($sparqlQuery,$model)/results:results/results:result" mode="description"/>
	  </ul>
	</xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>

  <xsl:template match="results:result" mode="description">
    <li>
      <span class="abbr"><xsl:value-of select="results:binding[@name='abbr']"/></span> <xsl:value-of select="results:binding[@name='label']"/>
    </li>
  </xsl:template>

</xsl:stylesheet>