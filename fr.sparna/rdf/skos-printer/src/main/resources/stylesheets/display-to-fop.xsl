<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:disp="http://www.sparna.fr/thesaurus-display"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
	<xsl:param name="column-count">1</xsl:param>
	
	<xsl:template match="/">
		<xsl:apply-templates select="disp:display" />
	</xsl:template>
	
	<xsl:template match="disp:display">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		
			<fo:layout-master-set>
				<fo:simple-page-master
			  		master-name="A4"
			  		page-width="210mm"
					page-height="297mm"
					margin-top="2cm"
					margin-bottom="2cm"
					margin-left="2cm"
					margin-right="2cm">
					<fo:region-body column-count="{$column-count}" column-gap="5pt" />
					<fo:region-before extent="2cm" />
					<fo:region-after extent="2cm" />
					<fo:region-start extent="2cm" />
					<fo:region-end extent="2cm" />
				</fo:simple-page-master> 
			</fo:layout-master-set>
		
			<fo:page-sequence master-reference="A4">
			
				<!-- adds page number but this does not work, it overlaps with the text -->
				 <!--
				<fo:static-content flow-name="xsl-region-after">
			      <fo:block font-size="10pt" 
			                font-family="sans-serif" 
			                text-align="end"><fo:page-number/></fo:block>
				</fo:static-content>
				-->
			
  				<fo:flow flow-name="xsl-region-body">
  					<xsl:apply-templates />
  				</fo:flow>
			</fo:page-sequence>
		
		</fo:root>
	</xsl:template>
	
	<xsl:template match="disp:header">
		<fo:block 
  			padding-before="1em" 
  			padding-after="1em"
  			padding-start="1em"
  			margin-bottom="1em"
  			font-family="sans-serif"
  			span="all"
  			border-before-style="outset"
  			border-after-style="outset"
  			border-start-style="outset"
  			border-end-style="outset"
  		>
  			<fo:block font-size="16pt"><xsl:value-of select="disp:title" /></fo:block>
  			<xsl:apply-templates select="disp:date" />
			<xsl:apply-templates select="disp:version" />
			<xsl:apply-templates select="disp:description" />
  		</fo:block>
	</xsl:template>
	<xsl:template match="disp:date">
		<fo:block margin-left="10pt" font-size="10pt"><xsl:value-of select="." /></fo:block>
	</xsl:template>
	<xsl:template match="disp:version">
		<fo:block margin-left="10pt" font-size="10pt"><xsl:value-of select="." /></fo:block>
	</xsl:template>
	<xsl:template match="disp:description">
		<fo:block margin-left="10pt" font-size="10pt"><xsl:value-of select="." /></fo:block>
	</xsl:template>
	
	<!-- Display : Alphabetical -->
	<xsl:template match="disp:alphabetical">
	
		<xsl:choose>
			<!-- case where we process entries directly -->
			<xsl:when test="disp:entry">
				<fo:block font-size="90%">
					<xsl:for-each select="disp:entry">
						<fo:block margin-top="9pt">
							<xsl:apply-templates select="." />
						</fo:block>
					</xsl:for-each>
				</fo:block>
			</xsl:when>
			<!-- case where we have sections -->
			<xsl:when test="disp:section">
				<fo:block font-size="90%">
					<xsl:apply-templates />
				</fo:block>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- Display : Hierarchical -->
	<xsl:template match="disp:hierarchical">
		<fo:block font-size="80%">
			<xsl:for-each select="disp:entry">
				<fo:block>
					<xsl:apply-templates select="." />
				</fo:block>
			</xsl:for-each>
		</fo:block>
	</xsl:template>
	
	<!-- Match a section -->
	<xsl:template match="disp:section">
		<xsl:for-each select="disp:entry">
			<fo:block margin-top="9pt">
				<xsl:apply-templates select="." />
			</fo:block>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="disp:entry">
		<xsl:apply-templates select="disp:label" />
		
		<fo:block margin-left="10pt">
			<xsl:apply-templates select="disp:att | disp:ref" />
			<xsl:apply-templates select="disp:entry" />
		</fo:block>
	</xsl:template>
	
	<xsl:template match="disp:label">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="disp:att | disp:ref">
		<fo:block font-size="smaller"><xsl:value-of select="@disp:type" /> : <xsl:apply-templates /></fo:block>
	</xsl:template>
	
	<xsl:template match="disp:str">
		<xsl:choose>
			<xsl:when test="@disp:type = 'pref'">
				<fo:inline font-weight="bold"><xsl:value-of select="text()" /></fo:inline>
			</xsl:when>
			<xsl:when test="@disp:type = 'alt'">
				<fo:inline font-style="italic"><xsl:value-of select="text()" /></fo:inline>
			</xsl:when>
			<xsl:otherwise>
				<fo:inline><xsl:value-of select="text()" /></fo:inline>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>