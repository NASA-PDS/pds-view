<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright 2001 California Institute of Technology.  ALL RIGHTS RESERVED.
U.S. Government Sponsorship acknowledged.

$Id$
-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:preserve-space elements="msg"/>

  <xsl:template match="status">
    <table cellpadding="3" cellspacing="0">
      <thead class="tableHead">
	<tr>
	  <th colspan="2">Name</th>
	  <th>Class</th>
	  <th>State</th>
	</tr>
      </thead>
      <tbody valign="center" align="left">
	<xsl:apply-templates/>
      </tbody>
    </table>
  </xsl:template>

  <xsl:param name="url">UNDEFINED</xsl:param>

  <xsl:template name="serverRow">
    <xsl:param name="name">object-name</xsl:param>
    <xsl:param name="class">class-name</xsl:param>
    <xsl:param name="underline">notUnderlined</xsl:param>
    <tr>
      <xsl:attribute name="class"><xsl:value-of select="$underline"/></xsl:attribute>
      <td align="center">
        <xsl:attribute name="class"><xsl:value-of select="$underline"/></xsl:attribute>        
        <a href="#">
          <xsl:attribute name="onClick">
            <xsl:text>javascript:nw=window.open('manage.jsp?url=</xsl:text>
            <xsl:value-of disable-output-escaping="yes" select="$url"/>
            <xsl:text disable-output-escaping="yes">&amp;key=</xsl:text>
            <xsl:value-of select="name"/>
            <xsl:text>','Manage','scrollbars=yes,status=no,location=no,menubar=no,toolbar=no,resizable=yes</xsl:text>
	    <xsl:text>,height=480,width=640');nw.opener=self;</xsl:text>
            <xsl:text>return false;</xsl:text>
          </xsl:attribute>
	  <img border="0" src="images/manage.png" alt="Manage"/>
        </a>
      </td>
      <td>
        <xsl:attribute name="class"><xsl:value-of select="$underline"/></xsl:attribute>
        <xsl:value-of select="name"/>
      </td>
      <td>
        <xsl:attribute name="class"><xsl:value-of select="$underline"/></xsl:attribute>
        <xsl:value-of select="class"/>
      </td>
      <td align="center">
        <xsl:attribute name="class"><xsl:value-of select="$underline"/></xsl:attribute>
        <xsl:apply-templates select="state"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="server[position() != last()]">
    <xsl:call-template name="serverRow">
      <xsl:with-param name="name"><xsl:value-of select="name"/></xsl:with-param>
      <xsl:with-param name="class"><xsl:value-of select="class"/></xsl:with-param>
      <xsl:with-param name="underline">underlined</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="server[position() = last()]">
    <xsl:call-template name="serverRow">
      <xsl:with-param name="name"><xsl:value-of select="name"/></xsl:with-param>
      <xsl:with-param name="class"><xsl:value-of select="class"/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="state[text() = 'up']">
    <img src="images/up.png" alt="Up"/>
  </xsl:template>

  <xsl:template match="state[text() = 'down']">
    <img src="images/down.png" alt="Down"/>
  </xsl:template>

</xsl:stylesheet>
