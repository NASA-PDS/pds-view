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

  <xsl:template match="server">
    <xsl:apply-templates select="name"/>
    <xsl:apply-templates select="state"/>
    <table cellpadding="3" cellspacing="3" class="propertySheet">
      <tbody valign="top" align="left">
        <tr>
          <td colspan="2" bgcolor="#00cccc">Execution Properties</td>
        </tr>
	<xsl:apply-templates select="class"/>
	<xsl:apply-templates select="start/user"/>
	<xsl:apply-templates select="start/config"/>
	<xsl:apply-templates select="host/name"/>
	<xsl:apply-templates select="host/os"/>
	<xsl:apply-templates select="vm"/>
        <xsl:apply-templates select="log"/>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template match="server/name">
    <h2><xsl:value-of select="."/></h2>
  </xsl:template>

  <xsl:template match="state[text() = 'up']">
    <p>State: <span class="good">Running</span><br/>
      <xsl:apply-templates select="../start/date"/>
    </p>
  </xsl:template>

  <xsl:template match="state[text() = 'down']">
    <p>State: <span class="bad">Down</span></p>
  </xsl:template>

  <xsl:template match="start/date">
    Active since <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template name="detail">
    <xsl:param name="label">Unknown</xsl:param>
    <xsl:param name="value">Unknown</xsl:param>
    <tr>
      <td><xsl:value-of select="$label"/></td>
      <td class="value"><xsl:value-of select="$value"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="class">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Class</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="start/user">
    <xsl:apply-templates select="name"/>
    <xsl:apply-templates select="cwd"/>
    <xsl:apply-templates select="home"/>
  </xsl:template>

  <xsl:template match="user/name">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Started By</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="user/cwd">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Current Directory</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="user/home">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Home Directory</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="config">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Config File</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="host/name">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Started On</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="host/os">
    <xsl:apply-templates select="name"/>
    <xsl:apply-templates select="version"/>
    <xsl:apply-templates select="arch"/>
  </xsl:template>

  <xsl:template match="os/name">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Operating System</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="os/version">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">OS Version</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="os/arch">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Machine Architecture</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="vm">
    <xsl:apply-templates select="name"/>
    <xsl:apply-templates select="version"/>
    <xsl:apply-templates select="classpath"/>
    <xsl:apply-templates select="extdirs"/>
  </xsl:template>

  <xsl:template match="vm/name">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">Virtual Machine</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="vm/version">
    <xsl:call-template name="detail">
      <xsl:with-param name="label">VM Version</xsl:with-param>
      <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="vm/classpath">
    <tr>
      <td>Classpath</td>
      <td><code><xsl:value-of select="."/></code></td>
    </tr>
  </xsl:template>

  <xsl:template match="vm/extdirs">
    <tr>
      <td>Extension Dirs</td>
      <td><code><xsl:value-of select="."/></code></td>
    </tr>
  </xsl:template>

  <xsl:template match="log">
    <tr>
      <td>Log Messages</td>
      <td><pre><xsl:apply-templates select="message"/></pre></td>
    </tr>
  </xsl:template>

  <xsl:template match="message">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
