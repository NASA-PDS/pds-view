<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:err="http://www.w3.org/2005/xqt-error"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:sch="http://purl.oclc.org/dsdl/schematron"
  xmlns:pds="http://pds.nasa.gov/pds/pds4/schematron-analyzer"
  version="2.0">

  <xsl:output method="xml" indent="yes" encoding="US-ASCII" />
  
  <xsl:variable name="schema" select="fn:doc('PDS4_OPS_0300a.xsd')" />

  <xsl:template match="/">
    <pds:schematron-rules>
      <xsl:apply-templates select="sch:schema/*" />
    </pds:schematron-rules>
  </xsl:template>
  
  <xsl:template match="sch:ns">
    <pds:ns uri="{@uri}" prefix="{@prefix}" />
  </xsl:template>
  
  <xsl:template match="sch:pattern">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="sch:rule">
    <xsl:apply-templates select="sch:assert">
      <xsl:with-param name="context" select="@context" />
    </xsl:apply-templates>   
  </xsl:template>
  
  <xsl:template match="sch:assert">
    <xsl:param name="context" />
    <xsl:if test="fn:matches(@test, &quot;\s*[A-Za-z-_.:]+\s*=\s*\(\s*'[^']+'.*\)&quot;)">
      <xsl:variable name="subContext" select="fn:replace(@test, &quot;\s*([A-Za-z-_.:]+)\s*.*&quot;, &quot;$1&quot;)" />
      <xsl:variable name="values" select="fn:tokenize(fn:replace(@test, &quot;\s*[A-Za-z-_.:]+\s*=\s*\(\s*(.*)\s*\)&quot;, &quot;$1&quot;), &quot;\s*,\s*&quot;)" />
      <xsl:variable name="elementName" select="fn:substring-after($subContext, 'pds:')" />
      <xsl:variable name="count" select="fn:count($schema//xs:element[@name=$elementName])" />
      <xsl:choose>
        <xsl:when test="fn:count($values) = 1">
          <pds:exactly-one context="{$context}" elementName="{$elementName}" subContext="{$subContext}"
            value="{fn:replace(fn:string-join($values, ''), &quot;'(.*)'&quot;, &quot;$1&quot;)}" count="{$count}" />
        </xsl:when>
        <xsl:otherwise>
          <pds:one-of context="{$context}" subContext="{$subContext}" count="{$count}">
            <xsl:for-each select="$values">
              <pds:value value="{fn:replace(., &quot;'(.*)'&quot;, &quot;$1&quot;)}" />
            </xsl:for-each>
          </pds:one-of>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="@*|node()" />

</xsl:stylesheet>