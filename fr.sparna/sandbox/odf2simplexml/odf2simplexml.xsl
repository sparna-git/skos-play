<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
      xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
      xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0">

   	<xsl:output indent="yes" method="xml" />
   	
   	<xsl:param name="skipEmptyCells">true</xsl:param>
   	
   	<xsl:param name="skipEmptyCellsBoolean" select="number($skipEmptyCells = 'true')" />

   <xsl:template match="office:spreadsheet">
      <document>
         <xsl:apply-templates />
      </document>
   </xsl:template>

   <xsl:template match="table:table">
      <table name="{@table:name}">
      	<xsl:apply-templates />
      </table>
   </xsl:template>

   <xsl:template match="table:table-row">
      <row>
         <xsl:apply-templates/>
      </row>
   </xsl:template>

	<xsl:template match="table:table-cell">
		<xsl:call-template name="cell">
			<!--  on repete une fois, ou bien un nombre de fois egal à la valeur de @table:number-columns-repeated -->
			<xsl:with-param name="i">
				<xsl:choose>
					<xsl:when test="@table:number-columns-repeated"><xsl:value-of select="@table:number-columns-repeated" /></xsl:when>
					<xsl:otherwise>1</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="table-cell" select="." />
			<!-- l'index est le nombre de cellules avant, plus un (celle-ci), plus la somme des @table:number-columns-repeated précédents,
			moins le nombre de cellules précédentes qui portaient un tel attribut -->
			<xsl:with-param name="index" select="
				count(preceding-sibling::table:table-cell)+1 
				+ sum(preceding-sibling::table:table-cell/@table:number-columns-repeated)
				- count(preceding-sibling::table:table-cell[@table:number-columns-repeated])
			"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="cell">
		<!-- le nombre de fois que ce template doit se repeter -->
		<xsl:param name="i" />
		<xsl:param name="index" />
		<xsl:param name="table-cell" />
		
		<!--  determiner le nom de colonne -->
		<xsl:variable name="col">
			<xsl:call-template name="colname">
				<xsl:with-param name="index">
					<xsl:value-of select="$index - 1" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<!-- creer l'élément si non vide -->
		<xsl:if test="not($skipEmptyCellsBoolean) or $table-cell/text:p">
			<xsl:element name="{$col}">
				<xsl:value-of select="$table-cell/text:p" />
			</xsl:element>
		</xsl:if>

		<!-- répéter si besoin -->
		<xsl:if test="$i > 1">
			<xsl:call-template name="cell">
				<xsl:with-param name="i" select="$i - 1" />
				<xsl:with-param name="table-cell" select="$table-cell" />
				<!--  en incrémentant l'index a chaque fois -->
				<xsl:with-param name="index" select="$index + 1" />
			</xsl:call-template>
		</xsl:if>
		
	</xsl:template>


	<!-- Retourne un nom de colonne similaire à Excel basé sur l'offset de la cellule -->
	<xsl:template name="colname">
		<!-- l'index doit commencer à zero -->
		<xsl:param name="index" />
		<xsl:variable name="chars">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
		<!-- le reste de la division -->
		<xsl:variable name="remainder" select="$index mod 26" />
		<!-- le quotient de la division, arrondi a l'inferieur -->
		<xsl:variable name="quotient" select="floor($index div 26)" />
		
		<xsl:choose>
			<!-- On est au-dela du 26eme caractere -->
			<xsl:when test="$quotient > 0">
				<!-- on recurse sur le reste -->
				<xsl:variable name="col">
					<xsl:call-template name="colname">
   						<xsl:with-param name="index"><xsl:value-of select="$remainder" /></xsl:with-param>
		   			</xsl:call-template>
				</xsl:variable>
				<!-- Lettre correspondant au quotient + le reste. Attention, substring commence a 1 -->
				<xsl:value-of select="concat(substring($chars, $quotient, 1), $col)" />
			</xsl:when>
			<xsl:otherwise>
				<!-- on est en-dessous des 26 lettres. Attention, substring commence a 1 -->
				<xsl:value-of select="substring($chars, $index + 1, 1)" />
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>

	
	<!-- template pour matcher tous les textes non-matches -->
	<!-- si non-present, plein de sauts de lignes serons insérés dans le XML resultat -->
	<xsl:template match="text()|@*"></xsl:template>

</xsl:stylesheet> 