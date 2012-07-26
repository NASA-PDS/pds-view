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

  <xsl:output method="text" omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
     <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="resultElement">
      <xsl:apply-templates select="resultValue"/>
  </xsl:template>

  <xsl:template match="resultValue">
    <xsl:choose>
      <xsl:when test="../resultMimeType='text/tab-separated-values'">
        <!-- GNUPLOT already expects whitespace separated values -->
	<xsl:value-of select="."/>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Suppress the query attrbutes, and a few other thigns -->
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
