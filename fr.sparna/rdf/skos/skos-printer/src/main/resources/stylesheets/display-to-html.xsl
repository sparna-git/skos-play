<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:disp="http://www.sparna.fr/thesaurus-display">
	
	<xsl:output method="html"
            encoding="UTF-8"
            indent="yes"/>
	
	<!-- application language with which we need to generate the labels -->
	<xsl:param name="lang">en</xsl:param>
	<xsl:variable name="labels" select="document(concat('labels-',$lang,'.xml'))" />
	
	<xsl:template match="/">
		<xsl:apply-templates select="disp:kosDocument" />
	</xsl:template>
	
	<xsl:template match="disp:kosDocument">
		<html>
			<head>
				<title><xsl:value-of select="disp:header/disp:title" /> <xsl:value-of select="$labels/broader" /></title>
				<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet"></link>
				<script src="js/jquery-1.9.1.min.js"></script>
				<script src="bootstrap/js/bootstrap.min.js"></script>
				<style>
					.att {
						margin-left: 1em;
						list-style: none;
					}
					.att > li { font-size: 80% }
					
					.ext-link {
						background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAVklEQVR4Xn3PgQkAMQhDUXfqTu7kTtkpd5RA8AInfArtQ2iRXFWT2QedAfttj2FsPIOE1eCOlEuoWWjgzYaB/IkeGOrxXhqB+uA9Bfcm0lAZuh+YIeAD+cAqSz4kCMUAAAAASUVORK5CYII=');
						background-repeat: no-repeat;
						background-position : 100% 50%;
						padding-right:13px;
						cursor:pointer;
					}
					
					.pref {
						font-weight: bold;
					}
					
					.alt {
						font-style:italic;
					}
					
					.kwic-left {
						float:left;
						width:35%;
						margin-right: 0.8em;
						direction: rtl;
						overflow: visible;
						white-space:nowrap;
					}
					
					.kwic-row ul {
						list-style-type:none;
						margin-left:0.8em;
						display:inline;
					}
					
					.kwic-row ul li {
						display:inline;
					}
					
					.kwac-row .att {
					 	margin-bottom:0px;
					}
				</style>
			</head>
			<body style="margin-bottom: 40px;">
				<div class="container">
				
					<!-- if more than one section, and at least have a title, generate navbar at the document level -->
					<xsl:if test="count(disp:body/disp:kosDisplay/disp:section[@title]) > 1">
						<div class="navbar navbar-fixed-bottom">
					    	<div class="navbar-inner">
					        	<ul class="nav">
					        		<xsl:for-each select="disp:body/disp:kosDisplay/disp:section">
										<li><a href="#{@title}"><xsl:value-of select="@title" /></a></li>
									</xsl:for-each>
					      		</ul>
					    	</div>
					    </div>
					</xsl:if>
				
					<xsl:apply-templates />
				</div>
				<script>
			      $(document).ready(function () {
					// add external link behavior to every external link
					/*
					$('span[title]:not(:has(a))').mouseover(function() {
						$(this).addClass('ext-link');
					});
					$('span[title]:not(:has(a))').mouseout(function() {
						$(this).removeClass('ext-link');
					});
					$('span[title]:not(:has(a))').click(function() {
						window.open($(this).attr('title'));
						// change this to the following line to have links open in same window/tab
						// document.location.href = $(this).attr('title');
					});
					*/
					
					// gestion des liens externes
					$('.ext-uri').mouseover(function() {
						$(this).addClass('ext-link');
					});
					$('.ext-uri').mouseout(function() {
						$(this).removeClass('ext-link');
					});
					$('.ext-uri').click(function() {
						window.open($(this).attr('title'));
						// change this to the following line to have links open in same window/tab
						// document.location.href = $(this).attr('title');
					});
					
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
				<xsl:apply-templates select="disp:creator" />
				<xsl:apply-templates select="disp:date" />
				<xsl:apply-templates select="disp:version" />
				<xsl:apply-templates select="disp:description" />
			</div>
		</div>
	</xsl:template>
	<xsl:template match="disp:creator | disp:date | disp:version | disp:description">
		<xsl:value-of select="." /><br /><br />
	</xsl:template>
	
	<!-- Display header -->
	<xsl:template match="disp:footer">
		<div class="footer">
			
		</div>
	</xsl:template>

	<!-- Display body -->
	<xsl:template match="disp:body">
		<!-- display each display -->
		<xsl:apply-templates />
	</xsl:template>

	<!-- A KOS Display -->
	<xsl:template match="disp:kosDisplay">
		<!-- display each section -->
		<div class="display">
			<xsl:apply-templates select="disp:section" />
		</div>
	</xsl:template>
	
	<!-- Process a section -->
	<xsl:template match="disp:section">
		<div class="section" id="{@title}">
			<xsl:if test="@title"><h2 class="title"><xsl:value-of select="@title" /></h2></xsl:if>
			<!-- process either list, table or tree -->
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Display a list -->
	<xsl:template match="disp:list">
		<ul>
			<xsl:for-each select="disp:listItem">
				<li>
					<xsl:apply-templates select="disp:conceptBlock" />
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>
	
	<!-- Display a tree -->
	<xsl:template match="disp:tree">
		<div class="display">
			<ul class="tree">
				<xsl:apply-templates select="disp:node" />
			</ul>
		</div>
	</xsl:template>
	
	<!-- process a tree node -->
	<xsl:template match="disp:node">
		<li id="{@entryId}">
			<!-- print its conceptBlock data -->
			<xsl:apply-templates select="disp:nodeData/disp:conceptBlock" />
			<!-- recurse -->
			<xsl:if test="disp:node">
				<ul>
					<xsl:apply-templates select="disp:node" />
				</ul>
			</xsl:if>
		</li> 
	</xsl:template>

	<!-- Display a table -->
	<xsl:template match="disp:table">
		<table class="table table-striped table-condensed">
			<!-- TODO : change this for tables with more than 2 cells -->
			<colgroup>
               <col span="1" style="width: 50%;" />
               <col span="1" style="width: 50%;" />
            </colgroup>
			<xsl:apply-templates select="disp:tableHeader" />
			<tbody>
				<xsl:for-each select="disp:row">
					<tr>
						<xsl:for-each select="disp:cell">
							<td>
								<xsl:apply-templates />
							</td>
						</xsl:for-each>
					</tr>
				</xsl:for-each>
			</tbody>
		</table>
	</xsl:template>
	
	<xsl:template match="disp:tableHeader">
		<thead>
			<tr>
				<xsl:for-each select="disp:cell">
					<th style="text-align:center;">
						<xsl:apply-templates />
					</th>
				</xsl:for-each>
			</tr>
		</thead>
	</xsl:template>

	<!-- display a KWIC index -->
	<xsl:template match="disp:index">
		<xsl:choose>
			<xsl:when test="@disp:indexStyle = 'kwic'">
				<xsl:apply-templates mode="kwic" />
			</xsl:when>
			<xsl:when test="@disp:indexStyle = 'kwac'">
				<xsl:apply-templates mode="kwac" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates mode="other" />
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template match="disp:entry" mode="kwic">
		<div id="{@id}" class="kwic-row">
			<span class="{disp:label/disp:str/@style}">
				<span class="kwic-left" title="{disp:label/disp:str}">
					<!-- &nbsp; is mandatory for values with nothing in the before part to be correctly aligned -->
					<bdi><xsl:value-of select="@before" /></bdi><xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text>
				</span>
				<span>
					<xsl:if test="disp:label/disp:str/@style = 'pref'">
						<xsl:attribute name="class">ext-uri</xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="@uri" /></xsl:attribute>
					</xsl:if>
					<span>					
						<xsl:value-of select="@key" />
					</span>
					<span class="kwic-right" title="{disp:label/disp:str}">
						<xsl:value-of select="@after" />
					</span>
				</span>
			</span>
			<xsl:if test="disp:att">
				<ul class="att">
					<xsl:apply-templates select="disp:att" />
				</ul>
			</xsl:if>
		</div>
	</xsl:template>


	<xsl:template match="disp:entry" mode="kwac">
		<div id="{@id}" class="kwac-row {disp:label/disp:str/@style}">
			<span>
				<span><xsl:value-of select="@key" /></span><span><xsl:value-of select="@after" /></span>
				<xsl:if test="@before and @before != ''">
					<span>, <xsl:value-of select="@before" /> ~</span>
				</xsl:if>
			</span>
			<xsl:if test="disp:att">
				<ul class="att">
					<xsl:apply-templates select="disp:att" />
				</ul>
			</xsl:if>
		</div>
	</xsl:template>


	<!-- display a concept block -->
	<xsl:template match="disp:conceptBlock">
		<div id="{@id}">
			<span class="ext-uri" title="{@uri}"><xsl:apply-templates select="disp:label" /></span>
			<xsl:if test="disp:att">
				<ul class="att">
					<xsl:apply-templates select="disp:att" />
				</ul>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="disp:label">
		<xsl:apply-templates />		
	</xsl:template>
	
	<xsl:template match="disp:att">
		<li><xsl:apply-templates select="." mode="typeLabel" /> : <xsl:apply-templates /></li>
	</xsl:template>
	
	<!-- select the type label if we find it, otherwise keep the type as it is (for type corresponding to languages) -->
	<xsl:template match="disp:att" mode="typeLabel">
		<xsl:variable name="type" select="@type" />
		
		<xsl:choose>
			<xsl:when test="$labels/labels/*[name() = $type]">
				<xsl:value-of select="$labels/labels/*[name() = $type]" />
			</xsl:when>
			<xsl:when test="contains($type, 'lang:')">
				<!-- remove the 'lang:' marker -->
				<xsl:value-of select="substring($type, 6)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$type" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="disp:link">
		<xsl:choose>
			<xsl:when test="@style"><a href="#{@refId}" class="{@style}"><xsl:apply-templates /></a></xsl:when>
			<xsl:otherwise><a href="#{@refId}"><xsl:apply-templates /></a></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="disp:str">
		<xsl:choose>
			<xsl:when test="@style"><span class="{@style}"><xsl:value-of select="text()" /></span></xsl:when>
			<xsl:otherwise><xsl:value-of select="text()" /></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>