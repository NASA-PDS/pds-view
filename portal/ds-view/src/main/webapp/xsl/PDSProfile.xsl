<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" omit-xml-declaration="no" indent="yes" encoding="UTF-8"/>
  <xsl:preserve-space elements="*"/>

  <!-- elemObligation not displayed in the PDS data set search view -->
  <xsl:template match="elemObligation">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="elemObligation">
    <!-- Suppress -->
  </xsl:template>

  <!-- elemMaxOccurrence not displayed in the PDS data set search view -->
  <xsl:template match="elemMaxOccurrence">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="elemMaxOccurrence">
    <!-- Suppress -->
  </xsl:template>

  <!-- elemComment not displayed in the PDS data set search view -->
  <xsl:template match="elemComment">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="elemComment">
    <!-- Suppress -->
  </xsl:template>
 
  <!-- Everything else stays the same -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
