<?xml version="1.0" encoding="UTF-8"?>
<!-- Directory -> HTML transformation

This software was developed by the the Jet Propulsion Laboratory, an
operating division of the California Institute of Technology, for the
National Aeronautics and Space Administration, an independent agency
of the United States Government.

Redistribution and use in source and binary forms, with or without
modification, is not permitted under any circumstance without prior
written permission from the California Institute of Technology.

THIS SOFTWARE IS PROVIDED BY THE AUTHORS AND CONTRIBUTORS ``AS IS''
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

$Id$
-->

<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="text()"/>

  <!-- OBJECTS -->
  <xsl:template match="objects">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- PERSON -->
  <xsl:template match="person">
    <table cellpadding="2" cellspacing="2" border="1">
      <thead bgcolor="#00cccc">
	<tr>
	  <th colspan="2">Person: <xsl:value-of select="fullName"/>
	      (<xsl:value-of select="personID"/>)
	  </th>
	</tr>
      </thead>
      <tbody valign="top">
        <tr>
          <td>User Name</td>
          <td><xsl:value-of select="userName"/></td>
        </tr>
	<tr>
	  <td>Organization</td>
	  <td>
	    <a>
	      <xsl:attribute name="href">
		<xsl:text>?organizationID=</xsl:text>
		<xsl:value-of select="organizationID"/>
	      </xsl:attribute>
	      <xsl:value-of select="organizationID"/>
	    </a>
	  </td>
	</tr>
	<tr>
	  <td>Company</td>
	  <td><xsl:apply-templates select="companyID"/></td>	  
	</tr>
	<tr>
	  <td>Badge Number</td>
	  <td><xsl:value-of select="badgeNumber"/></td>
	</tr>
	<tr>
	  <td>Sponsor</td>
	  <td><xsl:value-of select="sponsor"/></td>
	</tr>
	<tr>
	  <td>Comments</td>
	  <td><xsl:value-of select="comments"/></td>
	</tr>
	<tr>
	  <td>Sponsees</td>
	  <td>
	    <ul>
              <xsl:apply-templates select="sponsees"/>
            </ul>
	  </td>
	</tr>
	<tr>
	  <td>Person Type</td>
	  <td>
	    <a>
	      <xsl:attribute name="href">
		<xsl:text>?personTypeID=</xsl:text>
		<xsl:value-of select="personTypeID"/>
	      </xsl:attribute>
	      <xsl:value-of select="personTypeID"/>
	    </a>
	  </td>
	</tr>
	<tr>
	  <td>First Name</td>
	  <td><xsl:value-of select="firstName"/></td>
	</tr>
	<tr>
	  <td>Last Name</td>
	  <td><xsl:value-of select="lastName"/></td>
	</tr>
	<tr>
	  <td>Middle Initial</td>
	  <td><xsl:value-of select="middleInitial"/></td>
	</tr>
	<tr>
	  <td>Full Name</td>
	  <td><xsl:value-of select="fullName"/></td>
	</tr>
	<tr>
	  <td>Building</td>
	  <td><xsl:value-of select="building"/></td>
	</tr>
	<tr>
	  <td>Room</td>
	  <td><xsl:value-of select="room"/></td>
	</tr>
	<tr>
	  <td>Mail Stop</td>
	  <td><xsl:value-of select="mailStop"/></td>
	</tr>
	<tr>
	  <td>Business Address</td>
	  <td><xsl:value-of select="businessAddress"/></td>
	</tr>
	<tr>
	  <td>Phone</td>
	  <td><xsl:value-of select="phone"/></td>
	</tr>
	<tr>
	  <td>FAX</td>
	  <td><xsl:value-of select="fax"/></td>
	</tr>
	<tr>
	  <td>Email</td>
	  <td>
	    <a>
	      <xsl:attribute name="href">
		mailto:<xsl:value-of select="email"/>
	      </xsl:attribute>
	      <xsl:value-of select="email"/>
	    </a>
	  </td>
	</tr>
	<tr>
	  <td>Email Type</td>
	  <td>
	    <a>
	      <xsl:attribute name="href">
		<xsl:text>?emailTypeID=</xsl:text>
		<xsl:value-of select="emailTypeID"/>
	      </xsl:attribute>
	      <xsl:value-of select="emailTypeID"/>
	    </a>
	  </td>
	</tr>
	<tr>
	  <td>Post Office</td>
	  <td><xsl:value-of select="postOffice"/></td>
	</tr>
	<tr>
	  <td>Projects</td>
	  <td>
	    <ul>
	      <xsl:apply-templates select="projectIDs"/>
            </ul>
	  </td>
	</tr>
      </tbody>
    </table>
  </xsl:template>

  <!-- EMAIL TYPE -->
  <xsl:template match="emailType">
    <table cellpadding="2" cellspacing="2" border="1">
      <thead bgcolor="#00cccc">
	<tr>
	  <th colspan="2">Email Type: <xsl:value-of select="code"/></th>
	</tr>
      </thead>
      <tbody valign="top">
	<tr>
	  <td>Code</td>
	  <td><xsl:value-of select="code"/></td>
	</tr>
	<tr>
	  <td>Description</td>
	  <td><xsl:value-of select="description"/></td>
	</tr>
      </tbody>
    </table>
  </xsl:template>      

  <!-- PERSON TYPE -->
  <xsl:template match="personType">
    <table cellpadding="2" cellspacing="2" border="1">
      <thead bgcolor="#00cccc">
	<tr>
	  <th colspan="2">Person Type: <xsl:value-of select="code"/></th>
	</tr>
      </thead>
      <tbody valign="top">
	<tr>
	  <td>Code</td>
	  <td><xsl:value-of select="code"/></td>
	</tr>
	<tr>
	  <td>Description</td>
	  <td><xsl:value-of select="description"/></td>
	</tr>
      </tbody>
    </table>
  </xsl:template>      

  <!-- ORGANIZATION -->
  <xsl:template match="organization">
    <table cellpadding="2" cellspacing="2" border="1">
      <thead bgcolor="#00cccc">
	<tr>
	  <th colspan="2">Organization: <xsl:value-of select="organizationID"/></th>
	</tr>
      </thead>
      <tbody valign="top">
	<tr>
	  <td>Organization Number</td>
	  <td><xsl:value-of select="organizationID"/></td>
	</tr>
	<tr>
	  <td>Name</td>
	  <td><xsl:value-of select="name"/></td>
	</tr>
	<tr>
	  <td>Parent</td>
	  <td><xsl:value-of select="parentOrganizationID"/></td>
	</tr>
	<tr>
	  <td>Default Account ID</td>
	  <td><xsl:value-of select="defaultAcctID"/></td>
	</tr>
	<tr>
	  <td>Comments</td>
	  <td><xsl:value-of select="comments"/></td>
	</tr>
	<tr>
	  <td>Organization Members</td>
	  <td>
	    <ul>
	      <xsl:apply-templates select="organizationMembers"/>
	    </ul>
	  </td>
	</tr>
	<tr>
	  <td>Projects</td>
	  <td>
	    <ul>
	      <xsl:apply-templates select="projectIDs"/>
            </ul>
	  </td>
	</tr>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template match="personID">
    <li>
      <a>
	<xsl:attribute name="href">
	  <xsl:text>?personID=</xsl:text>
	  <xsl:value-of select="."/>
	</xsl:attribute>
	<xsl:value-of select="."/>
      </a>
    </li>
  </xsl:template>

  <xsl:template match="projectID">
    <li><xsl:value-of select="."/></li>
  </xsl:template>

</xsl:stylesheet>
