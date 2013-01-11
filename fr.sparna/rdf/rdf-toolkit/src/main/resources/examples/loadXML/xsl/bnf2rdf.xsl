<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"        
    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
    xmlns:bnf="http://www.bnf.fr/autorites/ontology/"
    
    version="2.0">
    
    <xsl:output indent="yes" method="xml"  encoding="UTF-8" omit-xml-declaration="no" />
    
    <xsl:variable name="nameSpace_instance">http://www.bnf.fr/autorites/entry/</xsl:variable>
    <xsl:variable name="nameSpace_ontology">http://www.bnf.fr/autorites/ontology/</xsl:variable>
    
    
    
    <xsl:variable name="top" select="record"/>
    <xsl:variable name="id" select="$top/@Numero"/>
    
    <xsl:variable name="personSex" select="$top/element()[@tag='008']/Pos[@Code='17']"/>    
    <xsl:variable name="type" select="concat($nameSpace_ontology,$top/leader/Pos[@Code='09'])"/>
    
    <!-- Zones 100-->
    <xsl:variable name="tag_100" select="$top/datafield[normalize-space(@tag)='100']"/>
    <xsl:variable name="tag_100_latin" select="$top/datafield[normalize-space(@tag)='100' and substring(subfield[lower-case(@code)='w'],5,1)='b']"/>
    <xsl:variable name="tag_100_latin_fr" select="$top/datafield[normalize-space(@tag)='100' and substring(subfield[lower-case(@code)='w'],5,1)='b'  and substring(subfield[lower-case(@code)='w'],7,3)='fre']"/>
    <!-- Zones 110-->
    <xsl:variable name="tag_110" select="$top/datafield[normalize-space(@tag)='110']"/>
    <xsl:variable name="tag_110_latin" select="$top/datafield[normalize-space(@tag)='110' and substring(subfield[lower-case(@code)='w'],5,1)='b']"/>
    <xsl:variable name="tag_110_latin_fr" select="$top/datafield[normalize-space(@tag)='110' and substring(subfield[lower-case(@code)='w'],5,1)='b'  and substring(subfield[lower-case(@code)='w'],7,3)='fre']"/>
    <!-- Zones 400-->
    <xsl:variable name="tag_400" select="$top/datafield[normalize-space(@tag)='400']"/>
    <xsl:variable name="tag_400_latin" select="$top/datafield[normalize-space(@tag)='400' and substring(subfield[lower-case(@code)='w'],5,1)='b']"/>
    <xsl:variable name="tag_400_latin_fr" select="$top/datafield[normalize-space(@tag)='400' and substring(subfield[lower-case(@code)='w'],5,1)='b'  and substring(subfield[lower-case(@code)='w'],7,3)='fre']"/>
    
    <!-- Zones 600 624-->
    <xsl:variable name="tag_600_624" select="$top/element()[@tag='600' or @tag='624']"/>
    <!-- Zones 301, 302, 304, 310, 311, 313, 320, 321, 322 et 502, 504, 510, 511-->
    <xsl:variable name="link" select="$top/element()[(@tag='301' or @tag='302'  or @tag='304' or @tag='310' or @tag='311' or @tag='313' or @tag='320' or @tag='321'  or @tag='322' or @tag='502' or @tag='504' or @tag='511') and (element()/@code='3')]"/>
    
    
    <xsl:template match="/">
        <rdf:RDF
            xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
            xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            xmlns:skos="http://www.w3.org/2004/02/skos/core#"
            xmlns:bnf="http://www.bnf.fr/autorites/ontology/"
            
            
            >
        <xsl:choose>
            
            <xsl:when test="$tag_100_latin_fr">
                <xsl:call-template name="tag100">
                    <xsl:with-param name="node" select="$tag_100_latin_fr[1]"/>
                    <xsl:with-param name="mode">personne</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$tag_100_latin">
                <xsl:call-template name="tag100">
                    <xsl:with-param name="node" select="$tag_100_latin[1]"/>
                    <xsl:with-param name="mode">personne</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$tag_100">
                <xsl:call-template name="tag100">
                    <xsl:with-param name="node" select="$tag_100[1]"/>
                    <xsl:with-param name="mode">personne</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$tag_110_latin_fr">
                <xsl:call-template name="tag100">
                    <xsl:with-param name="node" select="$tag_110_latin_fr[1]"/>
                    <xsl:with-param name="mode">organisation</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$tag_110_latin">
                <xsl:call-template name="tag100">
                    <xsl:with-param name="node" select="$tag_110_latin[1]"/>
                    <xsl:with-param name="mode">organisation</xsl:with-param>
                </xsl:call-template>
                
            </xsl:when>
            <xsl:when test="$tag_110">
                <xsl:call-template name="tag100">
                    <xsl:with-param name="node" select="$tag_110[1]"/>
                    <xsl:with-param name="mode">organisation</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:message>ni 100 ni 110</xsl:message>
                <xsl:result-document href="error/niPersonneNiOrganisation_{$id}.error">                  
                </xsl:result-document>
            </xsl:otherwise>
            
        </xsl:choose>
        </rdf:RDF>
    </xsl:template>
    
    
    <xsl:template name="tag100">
        <xsl:param name="node"/>
        <xsl:param name="mode"/>
        
        <xsl:variable name="prenom" select="normalize-space($node/subfield[lower-case(@code)='m'][1])"/>
        <xsl:variable name="nom" select="normalize-space($node/subfield[lower-case(@code)='a'][1])"/>
        <xsl:variable name="chiffreArabe" select="normalize-space($node/subfield[lower-case(@code)='u'][1])"/>
        <xsl:variable name="chiffreRomain" select="normalize-space($node/subfield[lower-case(@code)='h'][1])"/>
        <xsl:variable name="qualificatifs" select="normalize-space($node/subfield[lower-case(@code)='e'][1])"/>
        <xsl:variable name="dateNaissanceMort" select="normalize-space($node/subfield[lower-case(@code)='d'][1])"/>
        
        <xsl:variable name="nomPrincipal" select="replace(normalize-space(concat($prenom,' ',$nom,' ',$chiffreArabe,' ',$qualificatifs)),'\s+',' ')"/>
        
        <xsl:choose>
            <!-- si le mode est 'personne' et que le nom principal ne soit pas vide-->
            <xsl:when test="$mode='personne' and normalize-space($nomPrincipal)!=''">
                <rdf:Description rdf:about="{$nameSpace_instance}{$id}">
                    <rdfs:label><xsl:value-of select="$nomPrincipal"/></rdfs:label>
                    
                    <xsl:call-template name="getPropertiesNode">
                        <xsl:with-param name="node" select="$node"/>
                        <xsl:with-param name="letters">amheud</xsl:with-param>
                    </xsl:call-template>
                    
                    <xsl:if test="$personSex">
                        <bnf:property_008_17><xsl:value-of select="$personSex"/></bnf:property_008_17>
                    </xsl:if>
                    
                    <xsl:call-template name="getPropertiesNode">
                        <xsl:with-param name="node" select="$tag_600_624"/>
                        <xsl:with-param name="letters">a</xsl:with-param>
                    </xsl:call-template>
                    
                    <xsl:call-template name="tag400"/>
                    <xsl:call-template name="link"/>
                    <rdf:type rdf:resource="{$type}"/>
                </rdf:Description>
            </xsl:when>
            
            
            <!-- si le mode est 'organisation' et que le nom ne soit pas vide-->
            <xsl:when test="$mode='organisation' and normalize-space($nom)!=''">
                <rdf:Description rdf:about="{$nameSpace_instance}{$id}">                    
                    <rdfs:label><xsl:value-of select="$nom"/></rdfs:label>
                    <bnf:property_110_a><xsl:value-of select="$nom"/></bnf:property_110_a>
                    <rdf:type rdf:resource="{$type}"/>
                </rdf:Description>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="link">       
        <xsl:for-each select="$link">
            <xsl:variable name="numTag" select="@tag"/>
            <xsl:element name="bnf:property_{$numTag}_3">
                <xsl:attribute name="rdf:resource" select="concat($nameSpace_instance,element()[@code='3'])"/>                
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    
    <xsl:template name="tag400">       
        <xsl:choose>
            <xsl:when test="$tag_400_latin_fr">
                <xsl:call-template name="doTag400">
                    <xsl:with-param name="node" select="$tag_400_latin_fr"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="doTag400">
                    <xsl:with-param name="node" select="$tag_400_latin"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    <xsl:template name="doTag400">
        <xsl:param name="node"/>
        
        
        <xsl:for-each select="$node">
            
            <xsl:variable name="prenom" select="normalize-space(subfield[lower-case(@code)='m'][1])"/>
            <xsl:variable name="nom" select="normalize-space(subfield[lower-case(@code)='a'][1])"/>
            <xsl:variable name="chiffreArabe" select="normalize-space(subfield[lower-case(@code)='u'][1])"/>
            <xsl:variable name="chiffreRomain" select="normalize-space(subfield[lower-case(@code)='h'][1])"/>
            <xsl:if test="$prenom and $nom">
                <skos:altLabel><xsl:value-of select="$prenom"/><xsl:text> </xsl:text><xsl:value-of select="$nom"/></skos:altLabel>
                <skos:altLabel><xsl:value-of select="$nom"/><xsl:text> </xsl:text><xsl:value-of select="$prenom"/></skos:altLabel>        
            </xsl:if>
            
            <xsl:if test="not($nom) and $prenom and $chiffreArabe">
                <skos:altLabel><xsl:value-of select="$prenom"/><xsl:text> </xsl:text><xsl:value-of select="$chiffreArabe"/></skos:altLabel>        
            </xsl:if>
            
            <xsl:if test="not($nom) and $prenom and $chiffreRomain">
                <skos:altLabel><xsl:value-of select="$prenom"/><xsl:text> </xsl:text><xsl:value-of select="$chiffreRomain"/></skos:altLabel>    
            </xsl:if>
            
            <xsl:if test="$prenom and $nom and $chiffreRomain">
                <skos:altLabel><xsl:value-of select="$prenom"/><xsl:text> </xsl:text><xsl:value-of select="$nom"/><xsl:text> </xsl:text><xsl:value-of select="$chiffreRomain"/></skos:altLabel>
                <skos:altLabel><xsl:value-of select="$nom"/><xsl:text> </xsl:text><xsl:value-of select="$prenom"/><xsl:text> </xsl:text><xsl:value-of select="$chiffreRomain"/></skos:altLabel>      
            </xsl:if>
            
            <xsl:if test="$prenom and $nom and $chiffreArabe">
                <skos:altLabel><xsl:value-of select="$prenom"/><xsl:text> </xsl:text><xsl:value-of select="$nom"/><xsl:text> </xsl:text><xsl:value-of select="$chiffreArabe"/></skos:altLabel>
                <skos:altLabel><xsl:value-of select="$nom"/><xsl:text> </xsl:text><xsl:value-of select="$prenom"/><xsl:text> </xsl:text><xsl:value-of select="$chiffreArabe"/></skos:altLabel>                 
            </xsl:if>
            
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="getPropertiesNode">
        <xsl:param name="node"/>
        <xsl:param name="letters"/>
       
        <xsl:for-each select="$node/element()">
            <xsl:if test="contains($letters,@code)">
                
                <xsl:element name="bnf:property_{parent::node()/@tag}_{@code}">
                    <xsl:value-of select="text()[1]"/>
                </xsl:element>
            </xsl:if>                       
        </xsl:for-each>
        
        
    </xsl:template>
    
</xsl:stylesheet>
