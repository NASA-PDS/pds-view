<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="defaultWrapper">
	<div class="title">Version 1.0.19 (December 15, 2014)</div>
    <div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
            	<li><strong>Miscellaneous security fixes</strong></li>
            </ul>
        </div>
    </div>
	<div class="title">Version 1.0.18 (November 13, 2014)</div>
    <div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
            	<li><strong>Added links to the LACE label editor</strong></li>
            </ul>
        </div>
    </div>
	<div class="title">Version 1.0.17 (November 4, 2014)</div>
    <div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
            	<li><strong>Support for PDS4 labels and tables.</strong></li>
            	<li><strong>Improved support for binary tables and binary data types.</strong></li>
            	<li><strong>Ability to view table before all rows are downloaded.</strong></li>
            	<li><strong>Support for bit-field columns.</strong></li>
            	<li><strong>Better support for legacy, malformed PDS3 labels.</strong></li>
            	<li><strong>Updated help text.</strong></li>
            	<li><strong>Added hover text for table columns.</strong></li>
            </ul>
        </div>
    </div>
	<div class="title">Version 1.0.16 (May 5, 2014)</div>
    <div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
            	<li><strong>Miscellaneous security fixes</strong></li>
            </ul>
        </div>
    </div>
	<div class="title">Version 1.0.15 (November 23, 2013)</div>
    <div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
            	<li><strong>Binary fields</strong> - Added more binary field features</li>
            	<li><strong>Miscellaneous security fixes</strong></li>
            </ul>
        </div>
    </div>
	<div class="title">Release 1.0.13 (August 2, 2013)</div>
   	<div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
            	<li><strong>Better Handling of Invalid Labels</strong> - Better handling of obsolete data types. (PTOOL-175, PTOOL-202, PTOOL-205)</li>
            	<li><strong>Support for Table Offsets</strong> - Fixed problem with tables not at start of file. (PTOOL-200)</li>
            	<li><strong>Better Handling of Off-By-One Field Definitions</strong> - Ignoring commas at ends of field data. (PTOOL-203)</li>
            	<li><strong>Bit Fields</strong> - Added support for bit fields (BIT_COLUMN, ITEMS). (PTOOL-204)</li>
            	<li><strong>Binary Tables</strong> - Added support for binary table fields. (PTOOL-225)</li>
            	<li><strong>Security Updates</strong> - Miscellaneous security updates. (PTOOL-226, -230, -231, -232, -245)</li>
            </ul>
        </div>
        <div class="label">Known Issues</div>
        <div class="contents">
            <ul>
                <li>Does not handle complex numbers, data values will be skipped or saved as strings</li>
            	<li>Series objects with sample parameter interval defined as a column not supported</li>
            </ul>
        </div>
    </div>  
	<div class="title">Release Candidate 1.0.2 (July 18, 2013)</div>
   	<div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
            	<li><strong>Contact Information</strong> - Updated contact information. (PTOOL-174)</li>
            	<li><strong>Tables Not at Start of File</strong> - Added support for tables with a starting offset. (PTOOL-200)</li>
            	<li><strong>Gracefully Handle Label Errors</strong> - Better handling for off-by-one errors in column definitions. (PTOOL-203)</li>
            	<li><strong>Support for Bit Fields</strong> - Added support for BIT_COLUMNS and ITEMS. (PTOOL-204)</li>
            	<li><strong>Security Fixes</strong> - Miscellaneous updates related to web site security. (PTOOL-208)</li>
            </ul>
        </div>
        <div class="label">Known Issues</div>
        <div class="contents">
            <ul>
                <li>Does not handle real or complex numbers, data values will be skipped or saved as strings</li>
            	<li>Series objects with sample parameter interval defined as a column not supported</li>
            </ul>
        </div>
    </div>  
	<div class="title">Release Candidate 1.0.1 (July 15, 2010)</div>
   	<div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
                <li>Corrects a stability issue with labels that do not contain DATA_SET_ID or PRODUCT_ID.</li>
            </ul>
        </div>
        <div class="label">Known Issues</div>
        <div class="contents">
            <ul>
                <li>Does not handle real or complex numbers, data values will be skipped or saved as strings</li>
            	<li>Series objects with sample parameter interval defined as a column not supported</li>
            </ul>
        </div>
    </div>  
	<div class="title">Release Candidate 1.00 (June 1, 2010)</div>
   	<div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
                <li>Reads binary table, series and spectrum objects</li>
                <li>Previews all rows with paging</li>
                <li>Usage statistics collected and reported</li>
                <li>Supports structure pointers to files in label folder at root of volume</li>
            </ul>
        </div>
        <div class="label">Known Issues</div>
        <div class="contents">
            <ul>
                <li>Does not handle real or complex numbers, data values will be skipped or saved as strings</li>
            	<li>Series objects with sample parameter interval defined as a column not supported</li>
            </ul>
        </div>
    </div>    
	<div class="title">Version 0.2.02 (September 29, 2009)</div>
   	<div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
                <li>Reads ASCII series objects</li>
                <li>Reads ASCII spectrum objects</li>
                <li>Reads and outputs numbers in scientific notation</li>
				<li>Displays dates and times in valid PDS formats</li>
                <li>Missing constant values are displayed and output correctly</li>
            </ul>
        </div>
        <div class="label">Known Issues</div>
        <div class="contents">
            <ul>
                <li>Does not support binary</li>
            	<li>Supports only top level tabular objects</li>
                <li>Does not support columns with items</li>
                <li>The format of zero value in scientific notation may not exactly match original input</li>
            </ul>
        </div>
    </div>
    </div>
	<div class="title">Version 0.2.01 (August 10, 2009)</div>
   	<div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
                <li>Reads structure include files</li>
                <li>Honors start byte or start record for objects with headers</li>
                <li>Provides feedback to user during reading and writing of large files</li>
                <li>Friendly error messages for unsupported object types and formats</li>
            </ul>
        </div>
        <div class="label">Known Issues</div>
        <div class="contents">
            <ul>
                <li>Insufficient support for numbers in scientific notation</li>
                <li>Column width errors in fixed width format for some columns with missing_constant fields</li>
            </ul>
        </div>
    </div>
	<div class="title">Version 0.2.00 (April 1, 2009)</div>
   	<div class="basicBox" style="font-size: 12px;">
        <div class="label">What's New</div>
        <div class="contents">
            <ul>
            	<li>Addition of row selection</li>
            	<li>Output offered in fixed width column format</li>
                <li>GUI no longer wizard format, allowing user to choose steps of interest</li>
                <li>Removal of Excel file format due to memory issues with larger files, instead CSV can be imported to Excel</li>
            </ul>
        </div>
       <div class="label">Known Issues</div>
        <div class="contents">
            <ul>
                <li>Column width errors in fixed width format for some columns with missing_constant fields</li>
            </ul>
        </div>    
    </div>
	<div class="title">Version 0.1.00 (January 30, 2009)</div>
	   <div class="basicBox" style="font-size: 12px;">
       	<div class="label">What's New</div>
        <div class="contents">
            <ul>
                <li>Initial release - reads labels with simple table objects, allows selection and ordering of columns, outputs comma separated values (CSV) and Excel file types</li>
            </ul>
        </div>
 	</div>
</div>

