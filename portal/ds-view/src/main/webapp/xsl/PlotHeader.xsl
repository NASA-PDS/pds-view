<?xml version="1.0"?>

<!--
Query Response -> HTML transformation

This XSL transformation turns a product server's XML response into an
HTML table.

This software was developed by the the Jet Propulsion Laboratory, an
operating division of the California Institute of Technology, for the
National Aeronautics and Space Administration, an independent agency
of the United States Government.

$Id$
-->

<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xslt/java"
                exclude-result-prefixes="java">

  <xsl:output method="html" omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
     <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="resultElement">
        <xsl:apply-templates select="resultHeader//elemName"/>
  </xsl:template>

  <xsl:template match="resultHeader//elemName">
     <xsl:if test="position() = 1">
        <xsl:element name="INPUT">
           <xsl:attribute name="type">hidden</xsl:attribute>
           <xsl:attribute name="name">Xtitle</xsl:attribute>
           <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
        </xsl:element>
     </xsl:if>
        
     <xsl:if test="position() = 2">
        <xsl:element name="INPUT">
           <xsl:attribute name="type">hidden</xsl:attribute>
           <xsl:attribute name="name">Ytitle</xsl:attribute>
           <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
        </xsl:element>
     </xsl:if>
  </xsl:template>


  <!-- Suppress the query attrbutes, and a few other thigns -->
  <xsl:template match="resultValue"/>
  <xsl:template match="queryAttributes"/>
  <xsl:template match="queryResultModeId"/>
  <xsl:template match="queryPropogationType"/>
  <xsl:template match="queryPropogationLevels"/>
  <xsl:template match="queryMaxResults"/>
  <xsl:template match="queryKWQString"/>
  <xsl:template match="querySelectSet"/>
  <xsl:template match="queryWhereSet"/>
  <xsl:template match="queryResults"/>

</xsl:stylesheet>
