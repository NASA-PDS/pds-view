<?xml version='1.0' encoding='UTF-8'?>

<xsl:stylesheet version='2.0'
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:pds="http://pds.nasa.gov/"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xsl xs fn pds">

  <xsl:output media-type="text/html" omit-xml-declaration="yes" encoding="ISO-8859-1" indent="yes" />

  <xsl:param name="SOLR_HOME" select="." />
  <xsl:variable name="numTools" as="xs:integer">5</xsl:variable>

  <xsl:variable name="title">PDS: Search Results</xsl:variable>
  <xsl:variable name="ds_result_range">
    <xsl:choose>
      <xsl:when test="//result/@numFound &lt; 1">
        <xsl:text />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="//result/@start + 1" />
        <xsl:text>&#x2013;</xsl:text>
        <xsl:value-of select="//result/@start + count(//result/doc)" />
        <xsl:text> of </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="ds_result_count">
    <xsl:value-of select="//result/@numFound" />
  </xsl:variable>
  <xsl:variable name="ds_result_caption">
    <xsl:choose>
      <xsl:when test="//result/@numFound = 1">result</xsl:when>
      <xsl:otherwise>results</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="ds_result_time">
    <xsl:value-of select="format-number(//lst[@name='responseHeader']/int[@name='QTime'] div 1000.0, '0.000')" />
  </xsl:variable>
  
  <xsl:function name="pds:get-param" as="xs:string">
    <xsl:param name="response" />
    <xsl:param name="name" as="xs:string" />
    <xsl:param name="default" as="xs:string"/>

    <xsl:choose>
      <xsl:when test="$response/lst[@name='responseHeader']/lst[@name='params']/str[@name = $name]">
        <xsl:value-of select="$response/lst[@name='responseHeader']/lst[@name='params']/str[@name = $name]" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$default" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:variable name="searchString" as="xs:string" select="pds:get-param(/response,'q','')" />

  <xsl:variable name="searchParam" as="xs:string">
    <xsl:value-of select="pds:make-param('q',$searchString)" />
  </xsl:variable>

  <xsl:variable name="nonQueryParams" as="xs:string*">
    <xsl:for-each select="/response/lst[@name='responseHeader']/lst[@name='params']/(str[@name ne 'q' and @name ne 'start' and @name ne 'fq' and fn:not(starts-with(@name,'f.'))]|arr[@name ne 'q' and @name ne 'start' and @name ne 'fq' and fn:not(starts-with(@name,'f.'))]/str)">
      <xsl:sequence select="(pds:make-param(if (@name) then @name else parent::*/@name,.))" />
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name="start" as="xs:integer" select="xs:integer(pds:get-param(/response,'start','0'))" />

  <xsl:variable name="rows" as="xs:integer" select="xs:integer(pds:get-param(/response,'rows','50'))" />

  <xsl:variable name="facetParams" as="xs:string*" select="pds:facet-params(/response,'')" />

  <xsl:variable name="facets" select="document(concat($SOLR_HOME,'/conf/xslt/facets.xml'))" />
  <xsl:key name="facet" match="facet" use="@name" />
  <xsl:key name="category" match="category" use="@name" />

  <xsl:variable name="modifications" select="document(concat($SOLR_HOME,'/conf/xslt/capitalize.xml'))" />
  <xsl:key name="modify" match="category" use="@name" />

  <xsl:function name="pds:make-param" as="xs:string">
    <xsl:param name="name" as="xs:string" />
    <xsl:param name="value" />

    <xsl:value-of select="concat(fn:encode-for-uri($name),'=',fn:encode-for-uri(xs:string($value)))" />
  </xsl:function>

  <xsl:function name="pds:make-query" as="xs:string">
    <xsl:param name="params" as="xs:string*" />

    <xsl:value-of separator="&amp;" select="$params" />
  </xsl:function>

  <xsl:function name="pds:facet-params" as="xs:string*">
    <xsl:param name="response" />
    <xsl:param name="excludeFacet" />

    <xsl:for-each select="$response/lst[@name='responseHeader']/lst[@name='params']/(str[(@name = 'fq' and fn:not(starts-with(.,concat($excludeFacet,':')))) or (starts-with(@name,'f.')) and fn:not(starts-with(@name,concat('f.',$excludeFacet,'.')))]|arr[(@name = 'fq') or (starts-with(@name,'f.')) and fn:not(starts-with(@name,concat('f.',$excludeFacet,'.')))]/str[parent::*/@name ne 'fq' or fn:not(starts-with(.,concat($excludeFacet,':')))])">
      <xsl:sequence select="(pds:make-param(if (@name) then @name else parent::*/@name,.))" />
    </xsl:for-each>
  </xsl:function>

  <xsl:function name="pds:caption-string" as="xs:string">
    <xsl:param name="keyName" />
    <xsl:param name="s" />
    <xsl:variable name="sWithSpaces" select="fn:translate($s,'_',' ')" />

    <xsl:choose>
      <xsl:when test="key($keyName,$sWithSpaces,$facets)"><xsl:value-of select="key($keyName,$sWithSpaces,$facets)/@caption" /></xsl:when>
      <xsl:otherwise><xsl:value-of select="$sWithSpaces" /></xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="pds:description" as="xs:string">
    <xsl:param name="desc"  />
    <xsl:param name="title" />
    <xsl:choose>
      <xsl:when test="not($desc) or $desc = 'N/A' or $desc = 'UNK'">
        <xsl:value-of select="concat('Information about ', $title)" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="replace(replace($desc,'&amp;lt;br /&amp;gt;',' '), '&amp;amp;', '&amp;')" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="pds:range" as="xs:integer*">
    <xsl:param name="from" as="xs:integer" />
    <xsl:param name="to" as="xs:integer" />
    <xsl:param name="step" as="xs:integer" />

    <xsl:choose>
      <xsl:when test="$from &gt; $to" />
      <xsl:when test="$from = $to">
        <xsl:sequence select="($from)" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="($from,pds:range($from+$step,$to,$step))" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="pds:page-range" as="xs:integer*">
    <xsl:param name="resultCount" as="xs:integer" />
    <xsl:param name="start" as="xs:integer" />
    <xsl:param name="rows" as="xs:integer" />

    <xsl:sequence select="pds:range(fn:max(($start - (10*$rows), 0)), fn:min(($start + (10*$rows) - 1, $resultCount - 1)), $rows)" />
  </xsl:function>
        

  <xsl:template match='/'>
<xsl:choose>
  <xsl:when test="response/result">
    <div id="sidebar">
      <xsl:apply-templates select="response/lst[@name='responseHeader']/lst[@name='params']" />
      <xsl:apply-templates select="response/lst[@name='facet_counts']" />
    </div>
  </xsl:when>
</xsl:choose>

    <div id="content">
      <h1>Search Results</h1>
      <div>


    <form class="search_form" method="get">
    <div id="IEBug">
      <p><input type="text" size="60" name="q" value="{$searchString}" />
	<input type="submit" value="Search" />&#160;<a style="position: relative; top: .4em; font-size: 90%;" href="index.jsp">New Search</a></p>
      <xsl:for-each select="response/lst[@name='responseHeader']/lst[@name='params']/(str[@name ne 'q']|arr[@name ne 'q']/str)">
        <input type="hidden" name="{if (@name) then @name else parent::*/@name}" value="{.}" /><xsl:text>
</xsl:text>
      </xsl:for-each>
    </div>
    </form>

<xsl:choose>
  <xsl:when test="response/result">

    <p>
        <xsl:value-of select="$ds_result_range" />
        <strong>
          <xsl:value-of select="concat($ds_result_count,' ',$ds_result_caption)" />
        </strong>
        (<xsl:value-of select="$ds_result_time" /> seconds)
    </p>
<!-- Comment out the Archive Info and Search Tools blocks
    <xsl:if test="response/result/doc[(arr|str)[@name='data_product_type']='Resource']">
      <div style="margin-top: 1em; padding: .25em; font-size: 100%; border: 1px solid #E0E000; background: #FFFFE0;">Archive Information</div>
      <p style="margin-top: .5em; margin-bottom: .5em;">These web pages provide detailed information for the matching investigations. If no page looks appropriate, you can browse the matching search tools and data sets, below.</p>
      <ul class="results">
        <xsl:apply-templates select="response/result/doc[(arr|str)[@name='data_product_type']='Resource']"/>
      </ul>

      <xsl:if test="count(response/result/doc[(arr|str)[@name='data_product_type']='Resource']) > 2">
        <div class="more-info"><a class="info-button">More related archive resources...</a></div>
      </xsl:if>
    </xsl:if>

    <xsl:if test="response/result/doc[(arr|str)[@name='data_product_type']='Product_Context_Search_Tool']">
      <div style="margin-top: 1em; padding: .25em; font-size: 100%; border: 1px solid #E0E000; background: #FFFFE0;">Search Tools</div>
      <p style="margin-top: .5em; margin-bottom: .5em;">These tools let you search for data products matching your query. This is usually the best way to access the data. If no tool looks appropriate, you can browse the matching data sets, below.</p>
      <ul class="results">
        <xsl:apply-templates select="response/result/doc[(arr|str)[@name='data_product_type']='Product_Context_Search_Tool']"/>
      </ul>

      <xsl:if test="count(response/result/doc[(arr|str)[@name='data_product_type']='Product_Context_Search_Tool']) > 2">
        <div class="more-tools"><a class="tools-button">More related search tools...</a></div>
      </xsl:if>
    </xsl:if>
-->
      <ul class="results" style="padding-top: 1em;">
      <div style="margin-top: 1em; margin-bottom: .5em; padding: .25em; font-size: 100%; border: 1px solid #E0E000; background: #FFFFE0;">Data Sets and Information</div>
        <xsl:apply-templates select="response/result/doc[(arr|str)[@name='data_product_type']!='Resource' and (arr|str)[@name='data_product_type']!='Product_Context_Search_Tool' and (arr|str)[@name='data_product_type']!='Service']"/>
        
      </ul>

      <xsl:if test="response/result/@numFound &gt; count(response/result/doc)">
        <p style="margin-top: 1.5em; font-size: 120%;">Result pages:
          <xsl:variable name="q" select="response/lst[@name='responseHeader']/lst[@name='params']/str[@name='q']" />
          <xsl:for-each select="pds:page-range(response/result/@numFound,$start,$rows)">
            <xsl:text> &#160;</xsl:text>
            <xsl:choose>
              <xsl:when test=". = $start">
                <strong><xsl:value-of select="(. idiv $rows) + 1" /></strong>
	      </xsl:when>
              <xsl:otherwise>
                <a href="{concat('?',pds:make-query(($searchParam,$facetParams,$nonQueryParams,pds:make-param('start',.))))}">
                  <xsl:value-of select="(. idiv $rows) + 1" />
                </a>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:for-each>
	</p>
      </xsl:if>
  </xsl:when>
  <xsl:when test="response/str[@name='exception']">
    <p><strong>There is an error in your search: </strong><xsl:value-of select="response/str[@name='exception']" /></p>

<p style="margin-top: 1em; margin-bottom: .5em;">Your search can use any of the following:</p>
  <ul>
    <li>A target name like <strong>mars</strong> or <strong>eros</strong></li>

    <li>An instrument name or type like <strong>spectrometer</strong> or <strong>laser altimeter</strong> or <strong>MOLA</strong></li>
    <li>A target body type like <strong>asteroid</strong></li>
    <li>A word or phrase to find in the description of a data set or search tool</li>

  </ul>

<p style="margin-top: 1em; margin-bottom: .5em;">You can further refine your query by:</p>
  <ul>
    <li>Use quotation marks to specify that words much occur in a phrase, like <strong>&quot;mars express&quot;</strong></li>
    <li>Match against search fields like this: <strong>target:mars</strong></li>
    <li>Fields you can match against: target, instrument, investigation, instrument_type</li>

 </ul>

  </xsl:when>
  <xsl:otherwise>
    <p>There are not results matching your query.</p>
  </xsl:otherwise>
</xsl:choose>

      </div>
    </div>

  </xsl:template>
  
	<xsl:template match="doc">
		<xsl:variable name="ds_name">
			<xsl:value-of select="str[@name='title']" />
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="(arr|str)[@name='data_product_type'] = 'Product_Context_Search_Tool'">
				<xsl:choose>
					<xsl:when test="position() > 2">
						<li class="result hidden tool">
							<strong><span class="pds_value"><xsl:value-of select="pds:caption-string('category',lower-case((arr|str)[@name='data_product_type']))" />:</span></strong>
							<a href="{(str|arr)[@name='resource_link']}"><xsl:value-of select="$ds_name" /></a>
							<br />
							<xsl:value-of select="pds:description((arr|str)[@name='description'],str[@name='title'])" />
						</li>
					</xsl:when>
					<xsl:otherwise>
						<li class="result">
							<strong><span class="pds_value"><xsl:value-of select="pds:caption-string('category',lower-case((arr|str)[@name='data_product_type']))" />:</span></strong>
							<a href="{(str|arr)[@name='resource_link']}"><xsl:value-of select="$ds_name" /></a>
							<br />
							<xsl:value-of select="pds:description((arr|str)[@name='description'],str[@name='title'])" />
						</li>
						</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="(arr|str)[@name='data_product_type'] = 'Resource'">
				<xsl:choose>
					<xsl:when test="position() > 2">
						<li class="result hidden info">
							<strong><span class="pds_value"><xsl:value-of select="pds:caption-string('category',lower-case((arr|str)[@name='data_product_type']))" />:</span></strong>
							<a href="{(str|arr)[@name='resLocation']}"><xsl:value-of select="$ds_name" /></a>
							<br />
							<xsl:value-of select="pds:description((arr|str)[@name='description'],str[@name='title'])" />
						</li>
					</xsl:when>
					<xsl:otherwise>
						<li class="result">
							<strong><span class="pds_value"><xsl:value-of select="pds:caption-string('category',lower-case((arr|str)[@name='data_product_type']))" />:</span></strong>
							<a href="{(str|arr)[@name='resLocation']}"><xsl:value-of select="$ds_name" /></a>
							<br />
							<xsl:value-of select="pds:description((arr|str)[@name='description'],str[@name='title'])" />
						</li>
						</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<li class="result">
					<strong><span class="pds_value">
					  <xsl:value-of select="pds:caption-string('category', lower-case((arr|str)[@name='data_product_type']))" />:
					</span></strong>
					<a href="{(str|arr)[@name='resLocation']}"><xsl:value-of select="$ds_name" /></a> <xsl:if test="(arr|str)[@name='node_id'] = 'psa'"> (from ESA) </xsl:if>
					<br />

					<xsl:choose>
						<xsl:when test="(arr|str)[@name='data_product_type'] = 'Instrument'">
							<xsl:value-of select="concat('Information about the ',str[@name='title'],' instrument')" />
						</xsl:when>
						<xsl:when test="(arr|str)[@name='data_product_type'] = 'Instrument_Host'">
							<xsl:value-of select="concat('Information about the ',str[@name='title'],' instrument host')" />
						</xsl:when>
						<xsl:when test="(arr|str)[@name='data_product_type'] = 'Investigation'">
							<xsl:value-of select="concat('Information about the ',str[@name='title'],' investigation')" />
						</xsl:when>
						<xsl:when test="(arr|str)[@name='data_product_type'] = 'Target'">
							<xsl:value-of select="concat('Information about the target ',str[@name='title'])" />
						</xsl:when>
						<xsl:when test="(arr|str)[@name='data_product_type'] = 'Telescope'">
							<xsl:value-of select="concat('Information about the telescope ',str[@name='title'])" />
						</xsl:when>
						<xsl:when test="(arr|str)[@name='data_product_type'] = 'Facility'">
							<xsl:value-of select="concat('Information about the facility ',str[@name='title'])" />
						</xsl:when>
						<xsl:when test="(arr|str)[@name='data_product_type'] = 'Data_Set'">
							<xsl:value-of select="pds:description((arr|str)[@name='description'],(arr|str)[@name='data_set_id'])" />
							<br />
							<span style="font-size: 90%; color: rgb(64, 64, 64);">
								<xsl:value-of select="fn:upper-case((arr|str)[@name='investigation_name'])" />
								-
								<xsl:value-of select="fn:upper-case((arr|str)[@name='data_set_id'])" />
								<xsl:value-of select="if ((arr|date)[@name='start_time']) then concat(' - starting ',(arr|date)[@name='start_time']) else if ((arr|date)[@name='investigation_start_date']) then concat(' - starting ',(arr|date)[@name='investigation_start_date']) else ''" />
							</span>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="pds:description((arr|str)[@name='description'],str[@name='title'])" />
						</xsl:otherwise>
					</xsl:choose>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

  <xsl:template match="lst[@name='params'][*[@name='fq']]">
    <div class="sidebarSection">
    <h2>Current Refinements</h2>
    <ul>
      <xsl:apply-templates select="str[@name='fq']|arr[@name='fq']/str" mode="refinements" />
    </ul>
    </div>
  </xsl:template>

  <xsl:template match="str" mode="refinements">
     <xsl:variable name="facet" select="substring-before(.,':')" />
     <xsl:variable name="caption" select="pds:caption-string('facet',$facet)" />
     <xsl:variable name="hierarchy" select="fn:tokenize(translate(substring-after(.,','),'&quot;',''),',')" />

     <li class="refinements">
       <xsl:value-of select="$caption" />:
       <xsl:choose>
	 <xsl:when test="key('modify',$hierarchy,$modifications)">
	   <span class="pds_value"><xsl:value-of select="pds:caption-string('category',fn:string-join(key('modify',$hierarchy,$modifications)/@modified,' > '))" /></span>&#160;&#160;[&#160;<a title="Remove this refinement" class="x_remove_box" href="{concat('?',pds:make-query(($searchParam,pds:facet-params(/response,$facet),$nonQueryParams)))}">undo</a>&#160;]
	 </xsl:when>
	 <xsl:otherwise>
	   <span class="pds_value"><xsl:value-of select="pds:caption-string('category',fn:string-join($hierarchy,' > '))" /></span>&#160;&#160;[&#160;<a title="Remove this refinement" class="x_remove_box" href="{concat('?',pds:make-query(($searchParam,pds:facet-params(/response,$facet),$nonQueryParams)))}">undo</a>&#160;]
	 </xsl:otherwise>
       </xsl:choose>
     </li>
  </xsl:template>

  <xsl:template match="lst[@name='facet_counts']">
    <div class="sidebarSection">
    <h2>Refine Your Search</h2>
    <xsl:if test="count(lst[@name='facet_fields'][lst[count(int) &gt; 1]]) = 0">
      <ul><li>No further refinements available</li></ul>
    </xsl:if>
    <xsl:apply-templates select="lst[@name='facet_fields']" />
    </div>
  </xsl:template>

  <xsl:template match="lst[@name='facet_fields'][lst[count(int) &gt; 1]]">
    <xsl:apply-templates select="lst[count(int) &gt; 1]" mode="facet" />
  </xsl:template>

  <xsl:template match="lst" mode="facet">
    <xsl:variable name="facet" select="@name" />
    <xsl:variable name="baseQuery" select="pds:make-query(($searchParam,pds:facet-params(/response,$facet),$nonQueryParams))" />
    <h3><span class="pds_value"><xsl:value-of select="pds:caption-string('facet',@name)" /></span></h3>
    <ul>
      <xsl:apply-templates select="int" mode="facet">
        <xsl:with-param name="facet" select="@name" />
        <xsl:with-param name="baseQuery" select="$baseQuery" />
      </xsl:apply-templates>
    </ul>
  </xsl:template>

  <xsl:template match="int" mode="facet">
    <xsl:param name="facet" />
    <xsl:param name="baseQuery" />
    <xsl:variable name="hierarchy" select="fn:tokenize(@name,',')" />
    <xsl:variable name="fq" select="@name" />
    <xsl:variable name="category" select="pds:caption-string('category',fn:subsequence($hierarchy,fn:count($hierarchy)))" />
    <xsl:variable name="prefix" select="concat(number(fn:subsequence($hierarchy,1,1))+1,',',fn:string-join(fn:subsequence($hierarchy,2),','))" />

      <xsl:choose>
	<xsl:when test="key('modify',$category,$modifications)">
	  <li><a class="pds_value" href="{concat('?',pds:make-query(($baseQuery,pds:make-param('fq',concat($facet,':&quot;',@name,'&quot;')),pds:make-param(concat('f.',$facet,'.facet.prefix'),concat($prefix,',')))))}"><xsl:value-of select="key('modify',$category,$modifications)/@modified" /></a> (<xsl:value-of select="." />)</li>
	</xsl:when>
	<xsl:otherwise>
	  <li><a class="pds_value" href="{concat('?',pds:make-query(($baseQuery,pds:make-param('fq',concat($facet,':&quot;',@name,'&quot;')),pds:make-param(concat('f.',$facet,'.facet.prefix'),concat($prefix,',')))))}"><xsl:value-of select="$category" /></a> (<xsl:value-of select="." />)</li>
	</xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template match="*"/>
  
</xsl:stylesheet>
