<?xml version="1.0" encoding="ISO-8859-1" ?>
<!--
This script splits up Schematron rules that contain multiple
assertions into new rules that each contain a single assertion.
This makes it easier to modify the rule context and assertion
test to make the rule more specific.
-->

<xsl:transform version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:sch="http://purl.oclc.org/dsdl/schematron">

<xsl:output method="xml" indent="yes" />

<xsl:template match="sch:rule[count(sch:assert) gt 1 and count(*) eq count(sch:assert)]">
  <xsl:variable name="context" select="@context" />
  <xsl:for-each select="sch:assert">
    <sch:rule context="{$context}">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
      </xsl:copy>
    </sch:rule>
  </xsl:for-each>
</xsl:template>

<!-- The default copy rule. -->
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:transform>
