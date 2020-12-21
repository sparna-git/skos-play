<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:disp="http://www.sparna.fr/thesaurus-display"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:dc="http://purl.org/dc/elements/1.1/"	
>
	
	<!-- application language with which we need to generate the labels -->
	<xsl:param name="lang">en</xsl:param>	
	<xsl:variable name="labels" select="document(concat('labels-',$lang,'.xml'))" />
	
	<xsl:variable name="writingMode">
		<xsl:choose>
			<xsl:when test="/disp:kosDocument/@writing-mode"><xsl:value-of select="/disp:kosDocument/@writing-mode" /></xsl:when>
			<xsl:otherwise>lr-tb</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:template match="/">
		<xsl:apply-templates select="disp:kosDocument" />
	</xsl:template>
	
	<xsl:template match="disp:kosDocument">		
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" writing-mode="{$writingMode}">
		
			<fo:layout-master-set>
				<xsl:apply-templates select="disp:body/disp:kosDisplay" mode="layout-master-set" />
			</fo:layout-master-set>
			
			<!-- Include XMP metadata -->
			<xsl:apply-templates select="disp:kosDocumentMetadata" />

			<!-- the first display will print the header -->		
			<xsl:apply-templates select="disp:body" />
		
		</fo:root>
	</xsl:template>
	
	<xsl:template match="disp:kosDocumentMetadata">
		<xsl:if test="child::*">
			<fo:declarations>
				<x:xmpmeta xmlns:x="adobe:ns:meta/">
					<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
	      				<rdf:Description rdf:about="" xmlns:dc="http://purl.org/dc/elements/1.1/">
	      					<xsl:for-each select="child::*">
	      						<xsl:copy-of select="." />
	      					</xsl:for-each>	      					
	      				</rdf:Description>
	      				<rdf:Description rdf:about=''
						  xmlns='http://ns.adobe.com/pdf/1.3/'
						  xmlns:pdf='http://ns.adobe.com/pdf/1.3/'>
						  <pdf:Producer>SKOS-Play - Sparna</pdf:Producer>
						  <pdf:Keywords><xsl:value-of select="dc:subject" /></pdf:Keywords>
						  <pdf:CreationDate></pdf:CreationDate>
						  <pdf:ModDate><xsl:value-of select="dc:date" /></pdf:ModDate>
						  <pdf:Author><xsl:value-of select="dc:creator" /></pdf:Author>
						  <pdf:Title><xsl:value-of select="dc:title" /></pdf:Title>
						  <pdf:Subject><xsl:value-of select="dc:subject" /></pdf:Subject>
						 </rdf:Description>
	      			</rdf:RDF>
				</x:xmpmeta>				 
			</fo:declarations>		
		</xsl:if>
	</xsl:template>
	
	<!-- generates a different page master for each display, to have different column-count for each of them -->
	<!-- the master-name is generated based on the display node ID, so that it can be referenced later. -->
	<xsl:template match="disp:kosDisplay" mode="layout-master-set">
	
		<xsl:variable name="margin-top">
			<xsl:choose>
				<xsl:when test="disp:section[string-length(@title) > 30]">
					<xsl:value-of select="'3.8cm'" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'2.8cm'" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
	
		<fo:simple-page-master
	  		master-name="pageMaster-{generate-id()}"
	  		page-width="210mm"
			page-height="297mm"
			margin-top="1.5cm"
			margin-bottom="1.5cm"
			margin-left="2.5cm"
			margin-right="2.5cm">
			
			<xsl:variable name="marginBottom">
				<xsl:choose>
					<!-- si on a 2 colonnes on fait remonter un peu le footer dans la goutiere en mettant une marge legerement plus petite que necessaire -->
					<xsl:when test="@column-count = '2'"><xsl:value-of select="'0.8cm'" /></xsl:when>
					<xsl:otherwise><xsl:value-of select="'1.5cm'" /></xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<!-- see http://stackoverflow.com/questions/14347094/xslt-if-attribute-exists-else -->
			<!--
				le truc ci-dessous est un "if exists" : on concat '1' (la valeur par défaut), avec ce qu'il y a potentiellement
				dans l'attribut, puis on prend la substring de ça, soit en commençant à 1 (le premier caractère) s'il n'y avait
				rien dans l'attribut, soit en commencant au second caractere s'il y avait qq chose dans l'attribut.
				
				Donc le résultat c'est le contenu de l'attribut @column-count, ou bien 1 s'il n'y a rien.
			-->
			<fo:region-body column-count="{substring(concat('1', @column-count), 1 + (1 * boolean(@column-count)))}" column-gap="0.8cm" margin-bottom="{$marginBottom}" margin-top="{$margin-top}" />
			<fo:region-before extent="2cm" />
			<fo:region-after extent="1cm" />
			<fo:region-start extent="0cm" />
			<fo:region-end extent="0cm" />
		</fo:simple-page-master>
	</xsl:template>
	
	<!-- Display header -->
	<xsl:template match="disp:header">
		<!-- avoid empty headers if there is nothing to display -->
		<xsl:if test="*">
			<fo:block 
	  			padding-before="1em" 
	  			padding-after="1em"
	  			padding-start="1em"
	  			margin-bottom="1em"
	  			font-family="Nimbus Sans L, Helvetica, Trad Arabic"
	  			span="all"
	  			border-before-style="outset"
	  			border-after-style="outset"
	  			border-start-style="outset"
	  			border-end-style="outset"
	  		>
	  			<fo:block font-size="16pt"><xsl:value-of select="disp:title" /></fo:block>
	  			<xsl:apply-templates select="disp:creator" />
	  			<xsl:apply-templates select="disp:date" />
				<xsl:apply-templates select="disp:version" />
				<xsl:apply-templates select="disp:description" />
	  		</fo:block>
  		</xsl:if>
	</xsl:template>
	<xsl:template match="disp:creator | disp:date | disp:version | disp:description">
		<fo:block margin-left="10pt" font-size="10pt"><xsl:value-of select="." /></fo:block>
	</xsl:template>

	<!-- Document body -->
	<xsl:template match="disp:body">
		<!-- generates automatic summary, etc. ? -->
		<!-- process kosDisplays -->
		<xsl:apply-templates />
		
		<!-- if we are in a complete display with more than one section, add a blank page at the end -->
		<xsl:if test="count(disp:kosDisplay) > 1">
			<fo:page-sequence master-reference="pageMaster-{generate-id(disp:kosDisplay[position() = last()])}" writing-mode="{$writingMode}">
				<fo:flow flow-name="xsl-region-body">
					<!-- add a page break -->
					<fo:block page-break-before="always" />
				</fo:flow>
			</fo:page-sequence>
		</xsl:if>
		
	</xsl:template>


	<!-- A KOS Display -->
	<xsl:template match="disp:kosDisplay">
		<!-- <fo:page-sequence master-reference="pageMaster-{generate-id()}" writing-mode="rl-tb">  -->
		<fo:page-sequence master-reference="pageMaster-{generate-id()}"  writing-mode="{$writingMode}">
			
			<!-- static-content are first, before flow -->
			<fo:static-content flow-name="xsl-region-before">
				<xsl:choose>
					<xsl:when test="disp:section[string-length(@title) > 30]">
						<xsl:call-template name="header-long"/>
					</xsl:when>
					<xsl:when test="disp:section[string-length(@title) > 27 and string-length(@title) &lt;= 30]">
						<xsl:call-template name="header-medium"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="header"/>
					</xsl:otherwise>
				</xsl:choose>								 
			</fo:static-content>		
			<fo:static-content flow-name="xsl-region-after">
				 <xsl:call-template name="footer"/>
			</fo:static-content>
		
			<fo:flow flow-name="xsl-region-body">
				<!-- Print the header if this is the first kosDisplay -->
				<xsl:if test="position() = 1">
					<xsl:apply-templates select="../../disp:header" />
				</xsl:if>
				
				<!-- print sections -->
				<xsl:apply-templates />
			</fo:flow>
  				
		</fo:page-sequence>		
	</xsl:template>	
		
	<xsl:template name="footer">
		<fo:block text-align="center">
			<!-- TEST : on enlève font-family="Helvetica" -->
			<fo:block font-size="12pt" font-weight="bold"><fo:page-number/></fo:block>
			<xsl:if test="/disp:kosDocument/disp:footer/disp:title">
				<!-- font-variant is unsupported for capitals, see https://xmlgraphics.apache.org/fop/compliance.html -->
				<fo:block font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-size="7pt" font-weight="bold"><xsl:value-of select="/disp:kosDocument/disp:footer/disp:title" /></fo:block>
				<!--
				<fo:block font-family="Helvetica" font-size="7pt" font-weight="bold"><xsl:value-of select="/disp:kosDocument/disp:footer/disp:title" /></fo:block>
				-->
			</xsl:if>
			<xsl:if test="/disp:kosDocument/disp:footer/disp:application">
				<!-- Generated by SKOS Play!, sparna.fr -->
				<fo:block font-family="Times" font-size="6pt" font-style="italic"><xsl:value-of select="/disp:kosDocument/disp:footer/disp:application" /></fo:block>
			</xsl:if>
		</fo:block>
		<!--
		<fo:block font-size="7pt" text-align="outside">
			<fo:page-number/> - <fo:inline font-style="italic">Generated by SKOS Play!, sparna.fr</fo:inline>
		</fo:block>
		 -->
	</xsl:template>
	
	<xsl:template name="header">
		<fo:block font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-size="32pt" font-weight="bold">
			<fo:retrieve-marker 
	      		retrieve-class-name="section.head.marker"
	      		retrieve-position="first-including-carryover"
	      		retrieve-boundary="page-sequence"/>
		</fo:block>
		<fo:block font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-size="17pt">
			<!-- generates a whole line of the same character -->
			<fo:leader leader-pattern="use-content" leader-length.optimum="100%">I</fo:leader>
		</fo:block>
	</xsl:template>
	
	<xsl:template name="header-medium">
		<fo:block font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-size="22pt" font-weight="bold">
			<fo:retrieve-marker 
	      		retrieve-class-name="section.head.marker"
	      		retrieve-position="first-including-carryover"
	      		retrieve-boundary="page-sequence"/>
		</fo:block>
		<fo:block font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-size="17pt">
			<!-- generates a whole line of the same character -->
			<fo:leader leader-pattern="use-content" leader-length.optimum="100%">I</fo:leader>
		</fo:block>
	</xsl:template>
	
	<xsl:template name="header-long">
		<fo:block-container height="2cm" max-height="2cm" width="100%" overflow="hidden">
			<fo:block font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-size="22pt">
				<fo:retrieve-marker 
		      		retrieve-class-name="section.head.marker"
		      		retrieve-position="first-including-carryover"
		      		retrieve-boundary="page-sequence"/>
			</fo:block>
		</fo:block-container>
		<fo:block font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-size="17pt">
			<!-- generates a whole line of the same character -->
			<fo:leader leader-pattern="use-content" leader-length.optimum="100%">I</fo:leader>
		</fo:block>
		
	</xsl:template>
	
	<!-- Process a section -->
	<xsl:template match="disp:section">
		<fo:block page-break-after="always">
			<xsl:if test="@title">
				<!-- insert a marker to be retrieved in page header : maximum 75 characters -->
				<fo:marker marker-class-name="section.head.marker">
					<xsl:choose>
						<xsl:when test="string-length(@title) > 85">
							<xsl:value-of select="concat(substring(@title, 0, 82), '...')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@title" />
						</xsl:otherwise>
					</xsl:choose>
				</fo:marker>
			
				<!-- remove title in the flow, it will be in the header -->
				<!--
				<fo:block
					margin-top="9pt"
					margin-bottom="9pt"
					font-size="125%"
					text-align="center"
					font-weight="bold">
					<xsl:value-of select="@title" />
				</fo:block>
				-->
			</xsl:if>
			<xsl:apply-templates />
		</fo:block>
	</xsl:template>

	<!-- Display a list -->
	<xsl:template match="disp:list">
		<fo:block font-size="80%">
			<xsl:for-each select="disp:listItem">
				<fo:block margin-bottom="1mm" keep-together.within-page="always">
					<xsl:apply-templates select="disp:conceptBlock" />
					<!-- generate a rule after the block, except for the last one -->
					<xsl:if test="position() != last()">
						<fo:leader leader-pattern="rule" leader-length.optimum="100%" rule-thickness="0.5pt" />
					</xsl:if>
				</fo:block>
			</xsl:for-each>
		</fo:block>
	</xsl:template>
	
	<!-- display an index -->
	<xsl:template match="disp:index">
		<fo:block font-size="65%">
			<xsl:choose>
				<xsl:when test="@disp:indexStyle = 'kwic'">
							<!--
								It is possible that we generate a table with no rows, which would lead to an error.
								Exemple : KWIC index with title-letter corresponding to excluded permutations	
							 -->
							<xsl:if test="disp:entry">					
								<fo:table>
									<fo:table-column column-width="35%"/>
									<fo:table-column column-width="65%"/>
									<fo:table-body>
										<xsl:apply-templates mode="kwic" />
									</fo:table-body>
								</fo:table>
							</xsl:if>			
				</xsl:when>
				<xsl:when test="@disp:indexStyle = 'kwac'">
					<xsl:apply-templates mode="kwac" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates mode="other" />
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>	
	</xsl:template>
	
	<xsl:template match="disp:entry" mode="kwic">
		<fo:table-row>
			<fo:table-cell>
				<fo:block margin-right="6px" text-align="right">
					<fo:inline keep-together.within-line="always">
						<xsl:call-template name="doStyledString">
							<xsl:with-param name="string" select="@before" />
							<xsl:with-param name="style" select="disp:label/disp:str/@style" />
						</xsl:call-template>
					</fo:inline>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>
					<fo:inline keep-together.within-line="always">
						<xsl:call-template name="doStyledString">
							<xsl:with-param name="string" select="concat(@key, @after)" />
							<xsl:with-param name="style" select="disp:label/disp:str/@style" />
						</xsl:call-template>
						&#160;
						<xsl:apply-templates select="disp:att" mode="inline" />
					</fo:inline>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
	<xsl:template match="disp:entry" mode="kwac">
		<fo:block>
			<xsl:variable name="s"><xsl:value-of select="@key" /><xsl:value-of select="@after" /><xsl:if test="@before and @before != ''">, <xsl:value-of select="@before" /> ~</xsl:if></xsl:variable>
			<xsl:call-template name="doStyledString">
				<xsl:with-param name="string" select="$s" />
				<xsl:with-param name="style" select="disp:label/disp:str/@style" />
			</xsl:call-template>
			<xsl:if test="disp:att">
				<fo:block margin-left="12pt">
					<xsl:apply-templates select="disp:att" />
				</fo:block>
			</xsl:if>
		</fo:block>
	</xsl:template>
	
	
	
	<!-- Display a tree -->
	<xsl:template match="disp:tree">
		<fo:block font-size="80%">
			<!--
			<xsl:apply-templates select="disp:node" />
			-->
			<xsl:apply-templates select="disp:node/disp:node" mode="subtree-root" />
		</fo:block>
	</xsl:template>
	
	<xsl:template match="disp:node" mode="subtree-root">
		<!-- suppression du keep-together.within-page="always" -->
		<fo:block margin-bottom="0.5cm">
			<fo:inline id="{disp:nodeData/disp:conceptBlock/@id}" font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-weight="bold"><xsl:apply-templates select="disp:nodeData/disp:conceptBlock/disp:label" /></fo:inline>
			
			<!-- recurse -->
			<xsl:apply-templates select="disp:node" />
		</fo:block>
	</xsl:template>
	
	<!-- process a tree node -->
	<xsl:template match="disp:node">
		<fo:block>

			<!-- print indentation and non-breaking space if we are at least one level deep -->
			<fo:inline font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-weight="bold"><xsl:for-each select="ancestor::disp:node[parent::disp:node]">L </xsl:for-each></fo:inline>
			<xsl:if test="ancestor::disp:node">&#160;</xsl:if>
			
			<!--
				print its conceptBlock data. Avoid recursing in disp:conceptBlock to avoir generating an fo:block that would
				print the label after a line break after the indentation
			 -->
			<fo:inline id="{disp:nodeData/disp:conceptBlock/@id}"><xsl:apply-templates select="disp:nodeData/disp:conceptBlock/disp:label" /></fo:inline>
			
			<!-- recurse -->
			<xsl:apply-templates select="disp:node" />
		</fo:block>
	</xsl:template>

	<!-- Display a table -->
	<xsl:template match="disp:table">
		<!-- Prevent empty table generation, otherwise FOP gives an error. So test we have at least one row -->
		<xsl:if test="disp:row">
			<fo:block font-size="70%">
				<fo:table>
					<xsl:apply-templates select="disp:tableColumn" />			
					<xsl:apply-templates select="disp:tableHeader" />
					<fo:table-body>
						<xsl:for-each select="disp:row">
							<fo:table-row>
								<xsl:choose>
							        <xsl:when test="(position() mod 2) = 0">
							        	<!-- even node -->
							        	<xsl:attribute name="background-color">#FFFFFF</xsl:attribute>
							        </xsl:when>
							        <xsl:otherwise>
							        	<!-- odd node -->
							            <xsl:attribute name="background-color">#EEEEEE</xsl:attribute>
							        </xsl:otherwise>
							    </xsl:choose>
								<xsl:for-each select="disp:cell">
									<fo:table-cell>
										<fo:block>
											<xsl:apply-templates />
										</fo:block>
									</fo:table-cell>
								</xsl:for-each>
							</fo:table-row>
						</xsl:for-each>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="disp:tableColumn">
		<fo:table-column column-width="{@width}%"/>
	</xsl:template>
	
	<!-- generate table-column header depending on @colnum attribute on the table element -->
	<!--
	<xsl:template name="colnum">
		<xsl:param name="colnum" />
		<xsl:param name="index" />
		
		<xsl:if test="$index != 0">
			<fo:table-column column-width="{format-number(100 div $colnum, '#,##')}%"/>
			<xsl:call-template name="colnum">
				<xsl:with-param name="colnum" select="$colnum" />
				<xsl:with-param name="index" select="$index - 1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	-->
	
	<xsl:template match="disp:tableHeader">
		<fo:table-header>
			<fo:table-row>
				<xsl:for-each select="disp:cell">
				<fo:table-cell>
					<fo:block text-align="center" font-weight="bold">
						<xsl:apply-templates />
					</fo:block>
				</fo:table-cell>
				</xsl:for-each>
			</fo:table-row>
		</fo:table-header>
	</xsl:template>


	<!-- display a concept block -->
	<xsl:template match="disp:conceptBlock">
		<!-- sets the id on the FO element, to be referenced in internal links -->
		<fo:block id="{@id}">
			<xsl:apply-templates select="disp:label" />
			
			<xsl:if test="disp:att">				
				<!-- Delete margin : <fo:block margin-left="12pt"> -->
				<!--
				<fo:block>
					<xsl:apply-templates select="disp:att" />
				</fo:block>
				-->
				
				<!-- all the attributes except definition, scopeNote and translations -->
				<xsl:if test="disp:att[@type != 'definition' and @type != 'scopeNote' and not(contains(@type, 'lang:'))]">
					<fo:table font-size="80%" font-weight="bold" font-family="Nimbus Sans L, Helvetica, Trad Arabic">
						<fo:table-column column-width="2.5em"/>
						<fo:table-column />
						<fo:table-body>
							<xsl:apply-templates select="disp:att[@type != 'definition' and @type != 'scopeNote' and not(contains(@type, 'lang:'))]" mode="table" />
						</fo:table-body>
					</fo:table>
				</xsl:if>
				
				
				<fo:block text-align="justify" font-size="95%">
					<!-- Now display the definition without the attribute type -->
					<xsl:if test="disp:att[@type = 'definition']/*">
						<fo:block font-family="Nimbus Roman No9 L, Times, Trad Arabic" margin-top="0.7mm" line-height="95%">
							<xsl:apply-templates select="disp:att[@type = 'definition']/*" />
						</fo:block>
					</xsl:if>
					
					<!-- Now display the scope note with small asterisk, without attribute type -->
					<xsl:if test="disp:att[@type = 'scopeNote']/*">
						<fo:block margin-top="0.7mm" line-height="95%">
							<!-- je n'ai pas mis baseline-shift="super" sinon ca décale trop la ligne du dessus -->
							<fo:inline font-family="ZapfDingbats">&#x2605;</fo:inline>
							<fo:inline font-family="Nimbus Roman No9 L, Times, Trad Arabic" font-style="italic">&#160;<xsl:apply-templates select="disp:att[@type = 'scopeNote']/*" /></fo:inline>
						</fo:block>
					</xsl:if>
				</fo:block>
				
				<!--  Now display all the translations in other languages -->
				<xsl:if test="disp:att[contains(@type, 'lang:')]">
					<fo:block font-family="Nimbus Sans L, Helvetica, Trad Arabic" font-size="smaller" font-style="italic" margin-top="0.7mm">
						<xsl:apply-templates select="disp:att[contains(@type, 'lang:')]" />
					</fo:block>
				</xsl:if>
				
			</xsl:if>
		</fo:block>
	</xsl:template>

	<xsl:template match="disp:label">
		<fo:inline font-family="Nimbus Sans L, Helvetica, Trad Arabic"><xsl:apply-templates /></fo:inline>
	</xsl:template>
	
	<!-- Display attributes in normal mode -->
	<xsl:template match="disp:att">
		<fo:block font-size="smaller">
			<xsl:if test="not(preceding-sibling::disp:att) or (preceding-sibling::disp:att[1]/@type != @type)">
				<xsl:apply-templates select="." mode="typeLabel" /> :				
			</xsl:if>
			<xsl:apply-templates />
		</fo:block>
	</xsl:template>
	
	<!-- Display attributes in inline mode (after the label in index entries) -->
	<xsl:template match="disp:att" mode="inline">
		<fo:inline font-size="smaller"><xsl:apply-templates select="." mode="typeLabel" /> : <xsl:apply-templates /></fo:inline>
	</xsl:template>
	
	<!-- Display attributes in a table (to have attribute types aligned) -->
	<xsl:template match="disp:att" mode="table">
		<xsl:variable name="type" select="@type" />
		
		<fo:table-row font-size="smaller">
			<fo:table-cell>
				<fo:block>
					<!-- display the type only for the first line, not the following values -->
					<xsl:if test="not(preceding-sibling::disp:att) or (preceding-sibling::disp:att[1]/@type != $type)">
						<xsl:apply-templates select="." mode="typeLabel" />  :
					</xsl:if>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="justify">
					<xsl:apply-templates />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
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
        <fo:basic-link internal-destination="{@refId}">
          <xsl:call-template name="styledString" />
        </fo:basic-link>
	</xsl:template>
	
	<xsl:template match="disp:linkExternal">
        <fo:basic-link external-destination="{@uri}">
          <xsl:call-template name="styledString" />
        </fo:basic-link>
	</xsl:template>

	<xsl:template match="disp:str">
		<xsl:call-template name="styledString" />
	</xsl:template>
	
	<xsl:template name="styledString">
		<xsl:variable name="value">
			<xsl:choose>
				<!-- output the text corresponding to the key if present -->
				<xsl:when test="@key"><xsl:variable name="key" select="@key" /><xsl:value-of select="$labels/labels/*[name() = $key]" /></xsl:when>
				<xsl:otherwise><xsl:value-of select="text()" /></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="doStyledString">
			<xsl:with-param name="string" select="$value" />
			<xsl:with-param name="style" select="@style" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="doStyledString">
		<xsl:param name="string" />
		<xsl:param name="style" />

		<xsl:choose>
			<xsl:when test="$style = 'pref'">
				<fo:inline font-weight="bold"><xsl:value-of select="$string" /></fo:inline>
			</xsl:when>
			<xsl:when test="$style = 'alt'">
				<fo:inline font-style="italic" text-decoration="line-through"><xsl:value-of select="$string" /></fo:inline>
			</xsl:when>
			<xsl:when test="$style = 'alt-att'">
				<fo:inline font-style="italic"><xsl:value-of select="$string" /></fo:inline>
			</xsl:when>
			<xsl:otherwise>
				<fo:inline><xsl:value-of select="$string" /></fo:inline>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>