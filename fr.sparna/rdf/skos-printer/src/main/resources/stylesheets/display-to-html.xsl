<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:disp="http://www.sparna.fr/thesaurus-display">
	
	<xsl:output method="html"
            encoding="UTF-8"
            indent="yes"/>
	
	<xsl:template match="/">
		<xsl:apply-templates select="disp:display" />
	</xsl:template>
	
	<xsl:template match="disp:display">
		<html>
			<head>
				<title><xsl:value-of select="disp:header/disp:title" /></title>
				<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet"></link>
				<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
				<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
				<style>
					.unstyled > li { font-size: 80% }
					
					.ext-link {
						background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAVklEQVR4Xn3PgQkAMQhDUXfqTu7kTtkpd5RA8AInfArtQ2iRXFWT2QedAfttj2FsPIOE1eCOlEuoWWjgzYaB/IkeGOrxXhqB+uA9Bfcm0lAZuh+YIeAD+cAqSz4kCMUAAAAASUVORK5CYII=');
						background-repeat: no-repeat;
						background-position : 100% 50%;
						padding-right:13px;
						cursor:pointer;
					}			
				</style>
			</head>
			<body>
				<div class="container">
					<xsl:apply-templates />
				</div>
				<script>
			      $(document).ready(function () {
					// add external link behavior to every external link
					$('span[title]').mouseover(function() {
						$(this).addClass('ext-link');
					});
					$('span[title]').mouseout(function() {
						$(this).removeClass('ext-link');
					});
					$('span[title]').click(function() {
						window.open($(this).attr('title'));
						// change this to the following line to have links open in same window/tab
						// document.location.href = $(this).attr('title');
					});
					
					// add unstyled class to all 'no' class
					$('.no').addClass('unstyled');
			      });					
				</script>
			</body>
		</html>
	</xsl:template>
	
	<!-- Display header -->
	<xsl:template match="disp:header">
		<div class="header">
		<h1><xsl:value-of select="disp:title" /></h1>
			<div>
				<xsl:apply-templates select="disp:date" />
				<xsl:apply-templates select="disp:version" />
				<xsl:apply-templates select="disp:description" />
			</div>
		</div>
	</xsl:template>
	<xsl:template match="disp:date">
		<xsl:value-of select="." /><br /><br />
	</xsl:template>
	<xsl:template match="disp:version">
		<xsl:value-of select="." /><br /><br />
	</xsl:template>
	<xsl:template match="disp:description">
		<xsl:value-of select="." /><br /><br />
	</xsl:template>


	<!-- Display : Alphabetical -->
	<xsl:template match="disp:alphabetical">

		<xsl:choose>
			<!-- case where we process entries directly -->
			<xsl:when test="disp:entry">
				<div class="display">
					<ul>
						<xsl:for-each select="disp:entry">
							<li id="{@entryId}">
								<xsl:apply-templates select="." />
							</li>
						</xsl:for-each>
					</ul>
				</div>
			</xsl:when>
			<!-- case where we have sections -->
			<xsl:when test="disp:section">
				<!-- generation de la "navbar" -->
				<div class="navbar navbar-fixed-bottom">
			    	<div class="navbar-inner">
			        	<ul class="nav">
			        		<xsl:for-each select="disp:section">
								<li><a href="#{@title}"><xsl:value-of select="@title" /></a></li>
							</xsl:for-each>
			      		</ul>
			    	</div>
			    </div>
				<div class="display">
					<xsl:apply-templates select="disp:section" />
				</div>
			</xsl:when>
		</xsl:choose>

	</xsl:template>
	
	<!-- Display : Hierarchical -->
	<xsl:template match="disp:hierarchical">
		<div class="display">
			<ul class="tree">
				<xsl:for-each select="disp:entry">
					<li id="{@entryId}">
						<xsl:apply-templates select="." />
					</li> 
				</xsl:for-each>
			</ul>
		</div>
	</xsl:template>
	
	<!-- Process a section -->
	<xsl:template match="disp:section">
		<div class="section" id="{@title}">
			<h2 class="title"><xsl:value-of select="@title" /></h2>
			<ul>
				<xsl:for-each select="disp:entry">
					<li id="{@entryId}">
						<xsl:apply-templates select="." />
					</li> 
				</xsl:for-each>
			</ul>
		</div>
	</xsl:template>
	
	<xsl:template match="disp:entry">
			<span title="{@disp:concept}"><xsl:apply-templates select="disp:label" /></span>
			<xsl:if test="disp:att | disp:ref">
				<ul class="no">
					<xsl:apply-templates select="disp:att | disp:ref" />
				</ul>
			</xsl:if>
			<!-- descente recursive pour gerer le display hierarchique -->
			<xsl:if test="disp:entry">
				<ul>
					<xsl:for-each select="disp:entry">
						<li id="{@entryId}">
							<xsl:apply-templates select="." />
						</li> 
					</xsl:for-each>
				</ul>
			</xsl:if>
	</xsl:template>

	<xsl:template match="disp:entry" mode="ref">
			<xsl:apply-templates select="disp:label" />
	</xsl:template>
	
	<xsl:template match="disp:label">
		<xsl:apply-templates />		
	</xsl:template>

	<xsl:template match="disp:str">
		<xsl:choose>
			<xsl:when test="@disp:type = 'pref'"><b><xsl:value-of select="text()" /></b></xsl:when>
			<xsl:when test="@disp:type = 'alt'"><i><xsl:value-of select="text()" /></i></xsl:when>
			<xsl:otherwise><xsl:value-of select="text()" /></xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template match="disp:att">
		<li><xsl:value-of select="@disp:type" /> : <xsl:apply-templates /></li>
	</xsl:template>
	
	<xsl:template match="disp:ref">
		<li><a href="#{@entryRef}"><xsl:value-of select="@disp:type" /> : <xsl:apply-templates select="disp:entry" mode="ref" /></a></li>
	</xsl:template>
	
</xsl:stylesheet>