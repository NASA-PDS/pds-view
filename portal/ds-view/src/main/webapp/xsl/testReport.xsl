<?xml version="1.0"?>

<!--
Unit Tests -> HTML transformation

This XSL transformation turns the unit test output into an HTML page.

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

  <xsl:output method="html" omit-xml-declaration="yes" indent="yes"
              doctype-public="-//W3C//DTD HTML 4.0//EN"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="testsuite">
    <html>
      <head>
        <title>Unit Test Results</title>
      </head>
      <style>
	body { font-family: Verdana, serif; background: white; }
	.err { background: yellow; color: red; }
	.fail { color: red; }
      </style>
      <body>
        <h1>Unit Test Results</h1>
        <p>This page lists the results of the nightly unit tests.</p>

        <h2>Summary</h2>
        <table>
          <tbody align="left" valign="top">
            <tr>
              <td>Report generated on:</td>
              <td>
                <xsl:variable name="date"
                              select="java:java.util.Date.new()"/>
                <xsl:value-of select="$date"/>
              </td>
            </tr>
            <tr>
              <td>Number of tests:</td>
              <td><xsl:value-of select="@tests"/></td>
            </tr>
            <tr>
              <td>Number of failures:</td>
              <td><xsl:value-of select="@failures"/></td>
            </tr>
            <tr>
              <td>Number of errors:</td>
              <td><xsl:value-of select="@errors"/></td>
            </tr>
            <tr>
              <td>Time to run:</td>
              <td><xsl:value-of select="@time"/></td>
            </tr>
          </tbody>
        </table>

        <h2>Details</h2>
        <table>
          <thead valign="top">
            <tr>
              <th align="left">Name</th>
              <th align="right">Time</th>
              <th align="left">Status</th>
              <th align="left">Type</th>
              <th align="left">Message</th>
            </tr>
          </thead>
          <tbody align="left" valign="top">
            <xsl:apply-templates select="testcase"/>
          </tbody>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="testcase[(position() mod 2) = 0]">
    <tr bgcolor="#eeeeee">
      <td align="left"><xsl:value-of select="@name"/></td>
      <td align="right"><xsl:value-of select="@time"/></td>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>

  <xsl:template match="testcase[(position() mod 2) = 1]">
    <tr>
      <td align="left"><xsl:value-of select="@name"/></td>
      <td align="right"><xsl:value-of select="@time"/></td>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>

  <xsl:template match="failure">
    <td class="fail" align="left">Failure</td>
    <td align="left"><xsl:value-of select="@type"/></td>
    <td align="left"><xsl:value-of select="@message"/></td>
  </xsl:template>

  <xsl:template match="error">
    <td class="err" align="left">Error</td>
    <td align="left"><xsl:value-of select="@type"/></td>
    <td align="left"><xsl:value-of select="@message"/></td>
  </xsl:template>

  <xsl:template match="text()"/>

</xsl:stylesheet>
