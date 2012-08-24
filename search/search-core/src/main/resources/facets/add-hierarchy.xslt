<?xml version="1.0"?>

<xsl:stylesheet version="2.0"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                exclude-result-prefixes="html xsl fn">

  <xsl:output method="xml" indent="yes" encoding="UTF-8" />

  <xsl:variable name="instrument_types" select="document('instrument-hierarchy.xml')" />
  <xsl:variable name="target_types" select="document('target-hierarchy.xml')" />

  <xsl:key name="category" match="category" use="@name" />

  <xsl:template match="/">
    <add>
      <xsl:apply-templates select="//doc" />
    </add>
  </xsl:template>

  <xsl:template match="doc">
    <xsl:copy>
      <xsl:choose>
        <xsl:when test="@boost">
          <xsl:attribute name="boost" select="@boost * (if (lower-case(field[@name='resClass'])='searchtool') then 10.0 else 1.0)" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="boost" select="if (lower-case(field[@name='resClass'])='searchtool') then 10.0 else 1.0" />
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="field[@name = 'resClass']">
    <xsl:copy-of select="." />
    <field name="facet_class"><xsl:value-of select="concat('1,',.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'mission_name']">
    <xsl:copy-of select="." />
    <field name="facet_mission"><xsl:value-of select="concat('1,',.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'instrument_name']">
    <xsl:copy-of select="." />

    <!-- Now add our instrument type hierarchy. -->
    <xsl:call-template name="show-hierarchy">
      <xsl:with-param name="typeName" select="'facet_instrument'" />
      <xsl:with-param name="hierarchy" select="$instrument_types" />
      <xsl:with-param name="key" select="." />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="field[@name = 'target_name']">
    <xsl:copy-of select="." />

    <!-- Now add our target type hierarchy. -->
    <xsl:call-template name="show-hierarchy">
      <xsl:with-param name="typeName" select="'facet_target'" />
      <xsl:with-param name="hierarchy" select="$target_types" />
      <xsl:with-param name="key" select="." />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="show-hierarchy">
    <xsl:param name="typeName" />
    <xsl:param name="hierarchy" />
    <xsl:param name="key" />

    <xsl:for-each select="key('category',lower-case(replace($key,'_',' ')),$hierarchy)">
	    <xsl:variable name="hier" select="@hierarchy" />
	    <xsl:variable name="values" select="tokenize($hier,'&gt;')" />
	    <xsl:call-template name="show-values">
	      <xsl:with-param name="typeName" select="$typeName" />
	      <xsl:with-param name="values" select="$values" />
	    </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="show-values">
    <xsl:param name="typeName" />
    <xsl:param name="values" />

    <xsl:if test="count($values) &gt; 0">
      <field name="{$typeName}"><xsl:value-of select="concat(count($values),',',string-join($values,','))" /></field>
      <xsl:call-template name="show-values">
        <xsl:with-param name="typeName" select="$typeName" />
        <xsl:with-param name="values" select="subsequence($values,1,count($values)-1)" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
