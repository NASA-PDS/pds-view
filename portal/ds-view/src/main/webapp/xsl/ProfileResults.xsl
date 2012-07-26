<?xml version="1.0"?>

<!--
Query Response -> HTML transformation

This XSL transformation turns a query server's XML response into an
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

  <!-- Generate a table -->
  <xsl:template match="/">
    <table cellpadding="5" cellspacing="0" width="100%" border="0">
      <tbody>
        <tr bgcolor="#bbccdd">
          <th align="left">
            Search Results (
              <xsl:value-of select="descendant::queryResults"/> 
              matches found)
          </th>
        </tr>
        <tr>
          <td>
            <table cellpadding="2" cellspacing="2" width="100%">
              <tbody>
                <xsl:apply-templates/>
              </tbody>
            </table>
          </td>
        </tr>
      </tbody>
    </table>
  </xsl:template>

  <!-- Even rows in the table get an alternate (greyish) color background -->
  <xsl:template match="resultElement[(position() mod 2) = 0]">
    <tr bgcolor="#eeeeee">
      <xsl:call-template name="profileRow"/>
    </tr>
  </xsl:template>

  <!-- Odd rows get the default color background -->
  <xsl:template match="resultElement">
    <tr>
      <xsl:call-template name="profileRow"/>
    </tr>
  </xsl:template>

  <!-- Process a single row in the table -->
  <xsl:template name="profileRow">

    <td valign="top" align="left">
      <b><xsl:number value="position()"/>. </b>
      <xsl:apply-templates select="descendant::Title"/>
    </td>

    <td valign="top" align="left">
      <a >
         <xsl:attribute name="href">/viewProfile.jsp?profileMetadataItem=<xsl:number value="position()"/>
         </xsl:attribute>
      View Profile</a>
    </td>

    <td valign="top" align="left">
      <xsl:apply-templates select="descendant::resClass"/>
    </td>

  </xsl:template>

  <xsl:template match="resAttributes/Title">
  <!-- If resLocation is not NULL then make the title a link to the resourceLocation -->
  <!-- If resClass is system.productServer then make the title link to the product search screen -->
  <!-- Otherwise, just display the title -->
    <xsl:choose>
       <xsl:when test="../resClass='system.productServer'">
         <a >
           <xsl:attribute name="href">/viewProduct.jsp?productSearchText=<xsl:value-of 
              select="java:jpl.oodt.beans.FormatResults.getServerName(string(../resLocation))"/>&amp;productTitle=<xsl:value-of select='translate(.," ","+")'/>
           </xsl:attribute>
           <xsl:value-of select="normalize-space(.)"/>
         </a>
       </xsl:when>

       <xsl:when test="../resLocation='NULL'">
          <xsl:value-of select="normalize-space(.)"/>
       </xsl:when>

       <!-- if it is a gif file, we want to display the oodt logo above the gif file -->
       <xsl:when test="contains(normalize-space(../resLocation),'.gif')"> 
          <xsl:choose>
             <xsl:when test="not (substring-after(normalize-space(../resLocation),'.gif'))">
               <a >
               <xsl:attribute name="href">/viewImage.jsp?gif=<xsl:value-of select="../resLocation"/>&amp;productTitle=<xsl:value-of select="."/>
               </xsl:attribute>
               <xsl:value-of select="normalize-space(.)"/>
               </a>
             </xsl:when>

             <xsl:otherwise>
               <a >
               <xsl:attribute name="href">
                  <xsl:value-of select="../resLocation"/>
               </xsl:attribute>
               <xsl:value-of select="normalize-space(.)"/>
               </a>
             </xsl:otherwise>
          </xsl:choose>
       </xsl:when>

       <xsl:otherwise>
         <a >
           <xsl:attribute name="href">
              <xsl:value-of select="../resLocation"/>
           </xsl:attribute>
           <xsl:value-of select="normalize-space(.)"/>
         </a>
       </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- Render the text of the elemName -->
  <xsl:template match="elemName">
    Element name: <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

  <!-- Render the text of the VALUE_INSTANCE, and follow it with a
  single newline.  The formatting of the xsl:text tags below is
  significant! -->
  <xsl:template match="VALUE_INSTANCE">
    <xsl:value-of select="normalize-space(.)"/>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <!-- If the resource location is iiop://whatever..., then it's a
  product server, so set its URL and button label specially. -->
  <xsl:template match="resAttributes[starts-with(normalize-space(resLocation),'iiop')]">
    <form method="post">
      <input type="hidden" name="productSearchText">
        <xsl:attribute name="value">
          <xsl:value-of select="normalize-space(resLocation)"/>
        </xsl:attribute>
      </input>
      <input type="submit" value="Get Product"/>
    </form>
  </xsl:template>

  <!-- Otherwise, the resLocation is another profile. -->
  <xsl:template match="resAttributes">
    <form method="post">
      <xsl:attribute name="action"> 
        <xsl:value-of select="normalize-space(resLocation)"/>
      </xsl:attribute>
      <input type="submit" value="Visit Profile"/>
    </form>
  </xsl:template>

  <!-- Render the text of the resClass directly. -->
  <xsl:template match="resClass">
    <xsl:value-of select="normalize-space(.)"/>
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
