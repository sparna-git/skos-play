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
				
				<!-- <link href="http://www.jqueryscript.net/css/jquerysctipttop.css" rel="stylesheet" type="text/css"></link>  -->
				<script src="js/jquery.min.js"></script>
				<script src="bootstrap/js/bootstrap.min.js"></script>
    			
				<style>
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
					
					.kwac-row .alt {
					 	font-style:italic;
					 	text-decoration:none;
					}
					
					.kwic-row .alt {
					 	font-style:italic;
					 	text-decoration:none;
					}

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
						text-decoration:line-through;
					}
					
					.alt-att {
						font-style:italic;
					}
							
				</style>
			</head>
			<body >
				
				<div class="container">
				<div role="navigation" class="navbar navbar-fixed-top" style="margin-left:800px;">
			     	
			    </div>
				
					<!-- if more than one section, and at least have a title, generate navbar at the document level -->
					<xsl:if test="count(disp:body/disp:kosDisplay/disp:section[@title]) > 1">
						
					    	<div class="navbar navbar-fixed-bottom" role ="navigation">
					    	<div class="navbar-header">
						      <a class="navbar-brand" href="#"></a>
						    </div>
					        	 <div class="container-fluid">
					        		<xsl:for-each select="disp:body/disp:kosDisplay/disp:section">
										
										<ul class="nav navbar-nav">
             								<li><a  class="btn btn-primary" href="#{@title}"><xsl:value-of select="@title" /></a> </li>
             							</ul>
        								
									</xsl:for-each>
								</div>
					      		
					    	</div>
					   
					</xsl:if>
				
					<xsl:apply-templates />
				</div>
				
				<script><![CDATA[
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
					
					
					// Select the main list and add the class "hasSubmenu" in each LI that contains an UL
					$('ul').each(function(){
						console.log("hello");
					  $this = $(this);
					  $this.find("li").has("ul").addClass("hasSubmenu");
					});
					
					// Find the last li in each level
					$('li:last-child').each(function(){
					  $this = $(this);
					  $this.closest('ul').css("margin-top","10px");
					    // Add margin in other levels of the list
					    $this.closest('ul').find("li").children("ul").css("margin-top","10px");
					/*  // Check if LI has children
					  if ($this.children('ul').length === 0){
					    // Add border-left in every UL where the last LI has not children
					    $this.closest('ul').css("border-left", "1px solid gray");
					  } else {
					    // Add border in child LI, except in the last one
					    $this.closest('ul').children("li").not(":last").css("border-left","1px solid gray");
					    // Add the class "addBorderBefore" to create the pseudo-element :defore in the last li
					    $this.closest('ul').children("li").last().children("a").addClass("addBorderBefore");
					    // Add margin in the first level of the list
					    $this.closest('ul').css("margin-top","5px");
					    // Add margin in other levels of the list
					    $this.closest('ul').find("li").children("ul").css("margin-top","5px");
					  };*/
					});
					/***Add a button to expand or collapse all****/
					$('div.navbar-fixed-top').each(function(){
			              $this = $(this);
			              $this.prepend("<a href='#' id='btn' style=' width:6%; margin-top:10px;' class='btn btn-primary'><span  class='glyphicon glyphicon-plus'  aria-hidden='true' style='display:none;'>All</span><span class='glyphicon glyphicon-minus' aria-hidden='true' >All</span></a>");
			              $this.children("a").not(":last").removeClass().addClass("toogle");
			          });
					// Add button to expand and condense
					$('ul li.hasSubmenu').each(function(){
					  
					  $this = $(this);
					  $this.prepend("<a href='#'><span  class='glyphicon glyphicon-plus'  style='display:none;' aria-hidden='true'></span><span class='glyphicon glyphicon-minus' aria-hidden='true' ></span></a>");
					  $this.children("a").not(":last").removeClass().addClass("toogle");
            		 
           	
					});
								
					// Actions to expand and consense
					$('ul li.hasSubmenu > a').click(function(){
					  
					     $this = $(this);
						 $this.closest("li").children("ul").toggle("slow");
						 $this.children("span").toggle();
						 return false;
					});
					/**when button expand all is cliked->expand all tree**/
					$('div.navbar-fixed-top > a').click(function(){
		                
			             	$('li.hasSubmenu>a').each(function(){
			             	      $this = $(this);
								 $this.closest("li").children("ul").toggle("slow");
								 $this.children("span.glyphicon").toggle();
				                 return false;
				                
	                		});
	                		
	               return false;
	            }); 
									
			     
			     
			      
			      
			 });
			      	
			            	
				]]></script>
			</body>
		</html>
		
		
	</xsl:template>
	
	<!-- Display header -->
	<xsl:template match="disp:header">
		<div class="header" style="margin-top: 50px;">
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
		<ul >
			<xsl:for-each select="disp:listItem">
				<li >
					<xsl:apply-templates select="disp:conceptBlock" />
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>
	
	<!-- Display a tree -->
	
	<xsl:template match="disp:tree">
	  
	  		<div class="display" >
	  		
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
			<colgroup>
				<xsl:apply-templates select="disp:tableColumn" />
				<!--
				<xsl:call-template name="colnum">
					<xsl:with-param name="colnum" select="@colnum" />
					<xsl:with-param name="index"  select="@colnum" />
				</xsl:call-template>
				 -->
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
	
	<xsl:template match="disp:tableColumn">
		<col span="1" style="width: {@width}%;" />
	</xsl:template>
	
	<!-- generate cols header depending on @colnum attribute on the table element -->
	<!--
	<xsl:template name="colnum">
		<xsl:param name="colnum" />
		<xsl:param name="index" />
		
		<xsl:if test="$index != 0">
			<col span="1" style="width: {format-number(100 div $colnum, '#,##')}%;" />
			<xsl:call-template name="colnum">
				<xsl:with-param name="colnum" select="$colnum" />
				<xsl:with-param name="index" select="$index - 1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	 -->
	
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
		<div id="{@id}" class="kwac-row">
			<!-- style index entry in bold or italic -->
			<span class="{disp:label/disp:str/@style}">
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
		<span id="{@id}" >
			
			<xsl:if test="disp:att">
				<ul class="att">
					<xsl:apply-templates select="disp:att" />
				</ul>
			</xsl:if>
			<span  class="ext-uri" title="{@uri}" ><xsl:apply-templates select="disp:label" /></span>
		</span>
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
	
	<xsl:template match="disp:linkExternal">
        <xsl:choose>
			<xsl:when test="@style"><a href="{@uri}" class="{@style}"><xsl:apply-templates /></a></xsl:when>
			<xsl:otherwise><a href="{@uri}"><xsl:apply-templates /></a></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="disp:str">
		<xsl:choose>
			<xsl:when test="@style"><span class="{@style}">
				<xsl:choose>
					<!-- output the text corresponding to the key if present -->
					<xsl:when test="@key"><xsl:variable name="key" select="@key" /><xsl:value-of select="$labels/labels/*[name() = $key]" /></xsl:when>
					<xsl:otherwise><xsl:value-of select="text()" /></xsl:otherwise>
				</xsl:choose>
			</span></xsl:when>
			<xsl:otherwise>
				<xsl:choose>					
					<!-- output the text corresponding to the key if present -->
					<xsl:when test="@key"><xsl:variable name="key" select="@key" /><xsl:value-of select="$labels/labels/*[name() = $key]" /></xsl:when>
					<xsl:otherwise><xsl:value-of select="text()" /></xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>