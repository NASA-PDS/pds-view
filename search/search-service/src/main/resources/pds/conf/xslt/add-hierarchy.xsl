<?xml version="1.0"?>

<xsl:stylesheet version="2.0"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
				xmlns:pds="http://pds.nasa.gov/"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="html xsl fn pds xs">

  <xsl:output method="xml" indent="yes" encoding="UTF-8" />

  <xsl:variable name="instrument_types" select="document('instrument-hierarchy.xml')" />
  <xsl:variable name="target_types" select="document('target-hierarchy.xml')" />

  <xsl:key name="category" match="category" use="@name" />

  <xsl:function name="pds:clean" as="xs:string">
    <xsl:param name="text" as="xs:string" />

    <xsl:value-of select="upper-case(replace($text,'_',' '))" />

  </xsl:function>

  <xsl:template match="/">
    <add>
      <xsl:apply-templates select="//doc" />
    </add>
  </xsl:template>

  <xsl:template match="field[@name = 'data_product_type']">
    <xsl:copy-of select="." />
    <field name="facet_type"><xsl:value-of select="concat('1,',.)" /></field>
  </xsl:template>
  
  <xsl:template match="field[@name = 'pds_model_version']">
  	<field name="pds_model_version"><xsl:value-of select="pds:clean(.)" /></field>
    <field name="facet_pds_model_version"><xsl:value-of select="concat('1,',.)" /></field>
  </xsl:template>
  
  <xsl:template match="field[@name = 'agency_name']">
    <xsl:copy-of select="." />
    <field name="facet_agency"><xsl:value-of select="concat('1,',.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'investigation_name']">
    <field name="investigation_name"><xsl:value-of select="pds:clean(.)" /></field>
    <field name="facet_investigation"><xsl:value-of select="concat('1,',.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'data_set_name']">
    <field name="data_set_name"><xsl:value-of select="pds:clean(.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'instrument_type']">
    <field name="instrument_type"><xsl:value-of select="pds:clean(.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'instrument_name']">
    <field name="instrument_name"><xsl:value-of select="pds:clean(.)" /></field>

    <!-- Now add our instrument type hierarchy. -->
    <xsl:call-template name="show-hierarchy">
      <xsl:with-param name="typeName" select="'facet_instrument'" />
      <xsl:with-param name="hierarchy" select="$instrument_types" />
      <xsl:with-param name="key" select="." />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="field[@name = 'instrument_host_id']">
    <field name="instrument_host_id"><xsl:value-of select="pds:clean(.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'instrument_id']">
    <field name="instrument_id"><xsl:value-of select="pds:clean(.)" /></field>
  </xsl:template>
  
  <xsl:template match="field[@name = 'observing_system_component_type'] ">
  	<xsl:copy-of select="." />
	<xsl:variable name="type" as="xs:string">
	  <xsl:value-of select="lower-case(.)" />
	</xsl:variable>
	<xsl:variable name="index" as="xs:integer">
		<xsl:value-of select="count(preceding-sibling::field[@name = 'observing_system_component_type']) + 1"/>
	</xsl:variable>
	<xsl:variable name="name" as="xs:string">
		<xsl:value-of select="../field[@name = 'observing_system_component_name'][$index]" />
	</xsl:variable>
	
  	<xsl:choose>
	    <xsl:when test="$type = 'instrument'">
	    	<field name="facet_instrument"><xsl:value-of select="concat('1,',$name)" /></field>
	    </xsl:when>
	    <xsl:when test="$type = 'spacecraft'">
	    	<field name="facet_investigation"><xsl:value-of select="concat('1,',$name)" /></field>
	    </xsl:when>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="field[@name = 'primary_result_purpose']">
    <field name="primary_result_purpose"><xsl:value-of select="pds:clean(.)" /></field>
    <field name="facet_primary_result_purpose"><xsl:value-of select="concat('1,',.)" /></field>
  </xsl:template>
  
  <xsl:template match="field[@name = 'primary_result_processing_level_id']">
    <field name="primary_result_processing_level_id"><xsl:value-of select="pds:clean(.)" /></field>
    <field name="facet_primary_result_processing_level_id"><xsl:value-of select="concat('1,',.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'target_type']">
    <field name="target_type"><xsl:value-of select="pds:clean(.)" /></field>
  </xsl:template>

  <xsl:template match="field[@name = 'target_name']">
    <field name="target_name"><xsl:value-of select="pds:clean(.)" /></field>

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
