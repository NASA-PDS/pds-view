<?xml version="1.0" encoding="ISO-8859-1" ?>
<!--
This script transforms one Schematron script into another,
converting rules into equivalent rules, but with more information
in the rule context. It tries to make rules more specific by
taking context from the assertion test and appending it to
the rule context.

For example, consider this rule:

<sch:rule context="//A">
  <sch:assert test="B = (...)" />
</sch:rule>

The context of the rule can be made more specific by hoisting
the "B" out of the assertion test:

<sch:rule context="//A/B">
  <sch:assert test=". = (...)" />
</sch:rule>

We only do this for rule contexts and assertions that match
patterns for which we know this transformation is safe.
-->

<xsl:transform version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:sch="http://purl.oclc.org/dsdl/schematron"
    xmlns:pds="http://pds.nasa.gov/pds4/pds/v1">

<xsl:output method="xml" indent="yes" />

<xsl:variable name="qnPath">^[A-Za-z0-9_.:-]+(/[A-Za-z0-9_.:-]+)*(\[[0-9]+\])?$</xsl:variable>

<xsl:variable name="simpleTest">^([A-Za-z0-9_.:-]+) *= *(\( *'[^']*'( *, *'[^']*')* *\))$</xsl:variable>
<xsl:variable name="ifTest">^if \(([A-Za-z0-9_.:-]+)\) *then *\1 *= *(\( *'[^']*'( *, *'[^']*')* *\)) else true\(\)$</xsl:variable>
<xsl:variable name="everyTest">^every (\$[A-Za-z0-9_]+) in \(([A-Za-z0-9_.:-]+)\) satisfies \1 *= *(\( *'[^']*'( *, *'[^']*')* *\))$</xsl:variable>
<xsl:variable name="ifFunctionTest">^if \(([A-Za-z0-9_.:-]+)\) *then ([A-Za-z0-9_.:-]+)\( *\1,(.*)\) else true\(\)$</xsl:variable>

<xsl:template match="sch:rule[count(sch:assert) eq 1 and fn:matches(@context,$qnPath) and fn:matches(sch:assert/@test,$simpleTest)]">
  <xsl:variable name="newContext" select="concat(@context,'/',fn:replace(sch:assert/@test,$simpleTest,'$1'))" />
  <xsl:variable name="newTest" select="concat('. = ',fn:replace(sch:assert/@test,$simpleTest,'$2'))" />
  
  <sch:rule context="{$newContext}">
    <sch:assert test="{$newTest}">
      <xsl:value-of select="sch:assert" />
    </sch:assert>
  </sch:rule>
</xsl:template>

<xsl:template match="sch:rule[count(sch:assert) eq 1 and fn:matches(@context,$qnPath) and fn:matches(sch:assert/@test,$ifTest)]">
  <xsl:variable name="newContext" select="concat(@context,'/',fn:replace(sch:assert/@test,$ifTest,'$1'))" />
  <xsl:variable name="newTest" select="concat('. = ',fn:replace(sch:assert/@test,$ifTest,'$2'))" />
  
  <sch:rule context="{$newContext}">
    <sch:assert test="{$newTest}">
      <xsl:value-of select="sch:assert" />
    </sch:assert>
  </sch:rule>
</xsl:template>

<xsl:template match="sch:rule[count(sch:assert) eq 1 and fn:matches(@context,$qnPath) and fn:matches(sch:assert/@test,$everyTest)]">
  <xsl:variable name="newContext" select="concat(@context,'/',fn:replace(sch:assert/@test,$everyTest,'$2'))" />
  <xsl:variable name="newTest" select="concat('. = ',fn:replace(sch:assert/@test,$everyTest,'$3'))" />
  
  <sch:rule context="{$newContext}">
    <sch:assert test="{$newTest}">
      <xsl:value-of select="sch:assert" />
    </sch:assert>
  </sch:rule>
</xsl:template>

<xsl:template match="sch:rule[count(sch:assert) eq 1 and fn:matches(@context,$qnPath) and fn:matches(sch:assert/@test,$ifFunctionTest)]">
  <xsl:variable name="newContext" select="concat(@context,'/',fn:replace(sch:assert/@test,$ifFunctionTest,'$1'))" />
  <xsl:variable name="newTest" select="concat(fn:replace(sch:assert/@test,$ifFunctionTest,'$2'),'(.,',fn:replace(sch:assert/@test,$ifFunctionTest,'$3'),')')" />
  
  <sch:rule context="{$newContext}">
    <sch:assert test="{$newTest}">
      <xsl:value-of select="sch:assert" />
    </sch:assert>
  </sch:rule>
</xsl:template>

<!-- Look for the special rule about 5 colons required for logical_identiifer except for *Ingest, *Bundle, and *Collection. -->
<xsl:template match="sch:rule[count(sch:assert) eq 1 and @context='/*'
    and fn:contains(sch:let[@name='num_colons']/@value,'pds:logical_identifier')
    and sch:let[@name='required_colons']/@value='5']">
  <sch:rule context="/pds:*[not(contains(local-name(),'Ingest'))
      and not(contains(local-name(),'Bundle')) and not(contains(local-name(),'Collection'))]/pds:Identification_Area/pds:logical_identifier">
    <sch:assert test="matches(., 'urn:nasa:pds:[^:]+:[^:]+:[^:]+')">
      The logical_identifier must be of the form "urn:nasa:pds:&lt;bundleID&gt;:&lt;collectionID&gt;:&lt;productID&gt;".
      Refer to section 6D in the PDS4 Standards Reference.
    </sch:assert>
  </sch:rule>
</xsl:template>

<!-- Check for required number of colons for Bundle labels. -->
<xsl:template match="sch:rule[@context='pds:Product_Bundle/pds:Identification_Area/pds:logical_identifier'
    and sch:let[@name='num_colons']]">
  <sch:rule context="{@context}">
    <sch:assert test="matches(., 'urn:nasa:pds:[^:]+')">
      The logical_identifier must be of the form "urn:nasa:pds:&lt;bundleID&gt;".
      Refer to section 6D in the PDS4 Standards Reference.
    </sch:assert>
  </sch:rule>
</xsl:template>

<!-- Check for required number of colons for Collection labels. -->
<xsl:template match="sch:rule[@context='pds:Product_Collection/pds:Identification_Area/pds:logical_identifier'
    and sch:let[@name='num_colons']]">
  <sch:rule context="{@context}">
    <sch:assert test="matches(., 'urn:nasa:pds:[^:]+:[^:]+')">
      The logical_identifier must be of the form "urn:nasa:pds:&lt;bundleID&gt;:&lt;collectionID&gt;".
      Refer to section 6D in the PDS4 Standards Reference.
    </sch:assert>
  </sch:rule>
</xsl:template>

<!-- Remove other tests of logical_identifier. -->
<xsl:template match="sch:rule[@context='pds:Identification_Area' and contains(sch:assert/@test, 'if (pds:logical_identifier) then')]" priority="1">
  <!-- Ignore -->
</xsl:template>

<!-- Look for the complicated Identification_Area rule. -->
<xsl:template match="sch:rule[@context='pds:Identification_Area' and contains(sch:assert/@test, 'local-name(/*)')]">
  <!-- Replace all the rules with better ones. -->
  <sch:rule context="pds:Identification_Area/pds:product_class">
    <sch:assert test=". = local-name(/*)">
      The product_class must match the top-level element. Please select a value from the dropdown list.
    </sch:assert>
  </sch:rule>
</xsl:template>

<xsl:template match="sch:rule[@context='pds:Identification_Area' and sch:assert/@test='pds:logical_identifier eq lower-case(pds:logical_identifier)']">
  <sch:rule context="pds:Identification_Area/pds:logical_identifier">
    <sch:assert test="not(matches(., '.*[A-Z].*'))">
      The local_identifier must use lower-case letters.
    </sch:assert>
  </sch:rule>
</xsl:template>

<!-- Replace the rules testing the format of lid_reference and lidvid_reference. -->
<xsl:template match="sch:rule[@context='pds:Internal_Reference' and sch:let/@name='lid_num_colons' and sch:let/@name='lidvid_num_colons']">
  <sch:rule context="pds:Internal_Reference/pds:lid_reference">
    <sch:assert test="matches(., 'urn:nasa:pds:[^:]+(:[^:]+(:[^:]+)?)?')">
      The lid_reference must match one of these forms: "urn:nasa:pds:&lt;bundleID&gt;",
      "urn:nasa:pds:&lt;bundleID&gt;:&lt;collectionID&gt;", or
      "urn:nasa:pds:&lt;bundleID&gt;:&lt;collectionID&gt;:&lt;productID&gt;".
      Refer to section 6D in the PDS4 Standards Reference.
    </sch:assert>
  </sch:rule>
  <sch:rule context="pds:Internal_Reference/pds:lidvid_reference">
    <sch:assert test="matches(., 'urn:nasa:pds:[^:]+(:[^:]+(:[^:]+)?)?::[0-9]+.[0-9]+')">
      The lidvid_reference must match one of these forms: "urn:nasa:pds:&lt;bundleID&gt;::M.n",
      "urn:nasa:pds:&lt;bundleID&gt;:&lt;collectionID&gt;::M.n", or
      "urn:nasa:pds:&lt;bundleID&gt;:&lt;collectionID&gt;:&lt;productID&gt;::M.n"
      where M and n are integers forming the version number M.n.
      Refer to section 6D in the PDS4 Standards Reference.
    </sch:assert>
  </sch:rule>
</xsl:template>

<!-- Remove rules testing local_identifier_reference values, in favor of our more general rules. -->
<xsl:template match="sch:rule[sch:assert[matches(@test, '[a-zA-Z0-9_]+:local_identifier_reference *=.*')] and matches(//sch:ns[1]/@uri, 'http://pds.nasa.gov/pds4/.*')]">
  <!-- Omit -->
</xsl:template>

<!-- Add additional rules at the end of the main PDS4 Schematron rules. -->
<xsl:template match="/sch:schema[sch:ns[@uri='http://pds.nasa.gov/pds4/pds/v1'] and count(sch:ns)=1]">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
    <sch:pattern>
      <sch:rule context="pds:local_identifier_reference">
        <sch:assert test=". = (//pds:*[name() != 'DD_Association']/pds:local_identifier)">
          The local_identifier_reference must match a local_identifier defined elsewhere in the label. Please
          select a value from the list.
        </sch:assert>
      </sch:rule>
      <sch:rule context="pds:DD_Association/pds:local_identifier">
        <sch:assert test=". = (//pds:*[name() != 'DD_Association']/pds:local_identifier)">
          The local_identifier must match a local_identifier defined elsewhere in the label. Please
          select a value from the list.
        </sch:assert>
      </sch:rule>
      <sch:rule context="pds:Mission_Area//*:local_identifier_reference">
        <sch:assert test=". = (//pds:*[name() != 'DD_Association']/pds:local_identifier)">
          The local_identifier_reference must match a local_identifier defined elsewhere in the label. Please
          select a value from the list.
        </sch:assert>
      </sch:rule>
      <sch:rule context="pds:Discipline_Area//*:local_identifier_reference">
        <sch:assert test=". = (//pds:*[name() != 'DD_Association']/pds:local_identifier)">
          The local_identifier_reference must match a local_identifier defined elsewhere in the label. Please
          select a value from the list.
        </sch:assert>
      </sch:rule>
    </sch:pattern>
  </xsl:copy>
</xsl:template>

<!-- The default copy rule. -->
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:transform>
