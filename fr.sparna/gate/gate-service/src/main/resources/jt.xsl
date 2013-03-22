<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dct="http://purl.org/dc/terms/"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:annot="http://sparna.fr/annotation.owl#">
	
	<xsl:param name="docId" />
	
	<xsl:template match="/">
		<rdf:RDF
		    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		    xmlns:owl="http://www.w3.org/2002/07/owl#"
		    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
		    xmlns:dct="http://purl.org/dc/terms/"
		    xmlns:annot="http://sparna.fr/annotation.owl#">
			
			<xsl:apply-templates select="//annotation" />
		</rdf:RDF>
	</xsl:template>
	
	<xsl:template match="annotation">
		<annot:Annotation>
			<dct:subject rdf:resource="{@uri}" />
			<annot:text><xsl:value-of select="." /></annot:text>
			<annot:startOffset><xsl:value-of select="@startOffset" /></annot:startOffset>
			<annot:endOffset><xsl:value-of select="@endOffset" /></annot:endOffset>
			<annot:onContent rdf:resource="{$docId}" />
			<annot:onContentPart><xsl:value-of select="../@name" /></annot:onContentPart>
		</annot:Annotation>
	</xsl:template>
	
</xsl:stylesheet>