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

  <!-- Generate a table to display the results -->
  <xsl:template match="/">
    <table cellpadding="2" cellspacing="2" width="100%" border="0">
        <xsl:apply-templates/>
    </table>
  </xsl:template>

  <!-- Display the header and then the results -->
  <xsl:template match="resultElement">
     <tr bgcolor="#bbccdd">
        <xsl:apply-templates select="resultHeader//elemName"/>
     </tr>

     <xsl:apply-templates select="resultValue"/>

  </xsl:template>

  <!-- Template for display the header -->
  <xsl:template match="elemName">
     <th align="left">
        <xsl:value-of select="translate(.,'_',' ')"/>
        <xsl:variable name="eu" select="java:java.lang.String.new(string(../elemUnit))"/>
        <xsl:if test="$eu">
           <br>(<xsl:value-of select="java:toLowerCase($eu)"/>)</br>
        </xsl:if>
     </th>
  </xsl:template>

  <!-- Call the appropriate template to handle the result display -->
  <!-- depending on the resultMimeType -->
  <xsl:template match="resultValue">
     <xsl:choose>

        <xsl:when test="../resultMimeType='text/tab-separated-values'">
           <xsl:call-template name="displayProduct">
              <xsl:with-param name="mimeType">
                 <xsl:value-of select="../resultMimeType"/>
              </xsl:with-param>
           </xsl:call-template>
        </xsl:when>

        <xsl:otherwise>
           <xsl:call-template name="UNKproduct"/>
        </xsl:otherwise>

     </xsl:choose>     
  </xsl:template>

  <!-- We are using the java extension function FormatResults.format -->
  <!-- to generate the HTML table for display the results. -->
  <xsl:template name="displayProduct">
     <xsl:param name="mimeType">text/tab-separated-values</xsl:param>
        <xsl:value-of select="java:jpl.oodt.beans.FormatResults.formatTableResults
           (string(../resultValue),string($mimeType),string(count(../resultHeader//elemName)))" 
           disable-output-escaping="yes"/>
  </xsl:template>

  <xsl:template name="UNKproduct">
     <td>
        <xsl:value-of select="."/>
     </td>
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
