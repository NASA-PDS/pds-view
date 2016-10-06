<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="defaultWrapper">
	<h1>About the Validator</h1>
	<div class="basicBox">
<h2>Running the Validator</h2>
<p>The PDS3 Volume Validator is a Java applet that tests data volumes on your local machine for compliance with PDS3 standards. It requires access to your computer to read the volume in order to check it for compliance. The first time you run the validator you will be asked whether to run the applet. You must answer <em>Run</em> in order to validate PDS3 volumes. Click the link below to run the Validator.</p>

<blockquote><a href="<s:url action="Validator" />">Start the Validator</a></blockquote>

		<h2>About the Project</h2>
		<div class="contents">The volume validator tests for a variety of issues based on the most recent version of the PDS <a href="http://pds.nasa.gov/tools/standards-reference.shtml" target="_blank">Standards Reference</a> and the current version of the <a href="https://pds.nasa.gov/tools/dictionary.shtml" target="_blank">PDS Data Dictionary</a>. In limited cases where ambiguities exist in these documents, validation has been based on responses from the standards committe and/or practices and findings from senior members of the PDS.</div>
		
		<h2>Validation Parts</h2>
		<div class="contents">The current validation process is broken into two parts, the first being label validation. This is conducted using the product tools library, a colaborative effort between the User Center Technology team at Ames and the Engineering Node at JPL. All label parsing and validation across EN and UCT tools should be consistent from this point on.</div>
		<div class="contents">The second part validates the collection of files and folders that makes up a data set against the rules for assembling and relating these resources as defined by the PDS3 standard.</div>
		
		<h2>Local Data Dictionary Support</h2>
		<div class="contents">The validator supports local data dictionaries in the following fasion. Any file in the document folder ending in ".ful" is treated as a dictionary. List values are merged, all other information is added or overwritten. Currently there are no rules of precidence for multiple local data dictionaries so you should not count on a particular order if more than one local dictionary contains the values for the same entry. Any changes or additions are made use of when validating the containing data set.</div>
		
		<h2>Validation Process</h2>
		<div class="contents">The way the  validation process works is as follows. Clicking the validation button on the main validation page launches a java applet on your computer. Once you browse to the root folder of a data set and click validate, the entire validation process is completed on your machine. During this process, status updates are sent to the validation web page to indicate progress. Once the validation is complete, the results are serialized and sent back to the server for display. The results are cached for the duration of your web session so that you may filter and modify the view of the results.</div>
		
		<h2>Issue List</h2>
		<div class="contents">The list of issues being checked for appears below. It's likely that some issues the validator surfaces are not listed here due to them appearing as a more generic error or bubbling up as an exception. All efforts will be made to make this list as complete and up to date as possible.</div>

		<div class="contents">
			<div><strong>Generic Parsing Problems</strong></div>
			<ul>
				<li><strong>Too Many Tokens</strong> = Too many tokens were found for a statement. This may be a result of unquoted strings or similar errors.</li>
				<li><strong>Bad Line Ending</strong> = Line ended with something other than carriage return followed by line feed (0x0D 0x0A).</li>
				<li><strong>Missing ID</strong> = No id was found for the statement.</li>
				<li><strong>Circular Reference</strong> = An include pointer resulted in a circular reference, for instance, pointing to itself.</li>
				<li><strong>Line too Long</strong> = Line length exceeds recommended 78 chars (not including line ending chars, CRLF).</li>
				<li><strong>Wrong Line Length</strong> = Line length does not match RECORD_LENGTH.</li>
				<li><strong>Missing End Quote</strong> = Quoted text string was not terminated.</li>
				<li><strong>Missing Object Terminator</strong> = Unable to find END_OBJECT statement for object.</li>
				<li><strong>Missing Group Terminator</strong> = Unable to find END_GROUP statement for group.</li>
				<li><strong>Missing End Statement</strong> = Labels must end in an END statement.</li>
				<li><strong>Missing Comment Terminator</strong> = Comment was not terminated.</li>
				<li><strong>Missing Record Bytes</strong> = Having a RECORD_TYPE of FIXED_LENGTH requires a value for RECORD_BYTES.</li>
				<li><strong>Start Byte Of Attached Data Mismatch</strong> = The found start byte for attached data, does not agree with the defined start byte.</li>
				<li><strong>Possible Start Byte Of Attached Data Mismatch</strong> = The found start byte, may not agree with the defined start byte, if the data does not begin with white space.</li>
				<%-- <li><strong>*Duplicate Identifier</strong> = Found two or more assignment statements with the same id.</li> --%>
				<li><strong>No Viable Alternative</strong> = Unable to parse statement at the given.</li>
				<li><strong>Illegal Start of Statement</strong> = Unable to start a statement with the given token.</li>
			</ul>
			
			<div><strong>Label Version Problems</strong></div>
			<ul>
				<li><strong>Missing Version</strong> = Could not find the PDS_VERSION_ID in the first line.</li>
				<li><strong>Mislocated Version</strong> = PDS_VERSION_ID was not found on the first line.</li>
				<li><strong>Version Present In Fragment</strong> = Label fragments should not contain a PDS_VERSION_ID.</li>
				<li><strong>SFDU Present in Fragment</strong> = The label fragment should not contain an SFDU.</li>
			</ul>
			
			<div><strong>Key Problems</strong></div>
			<ul>
				<li><strong>Unknown Key</strong> = No definition was found for the key.</li>
				<li><strong>Long Namespace</strong> = Namespace exceeds a max length.</li>
				<li><strong>Long Identifier</strong> = Identifier exceeds a max length.</li>
			</ul>
			
			<div><strong>Value Problems</strong></div>
			<ul>
				<li><strong>Missing Value</strong> = No value was found for the given statement.</li>
				<li><strong>Unknown Value</strong> = The given value is not in the list of valid values for the key. It may be that the value needs to be added to the dictionary.</li>
				<li><strong>Invalid Value</strong> = The given value is not in the list of valid values for the key.</li>
				<li><strong>Non Alphabetic</strong> = The value, restricted to alphabetic chars, contains non-alphabetic characters.</li>
				<li><strong>Non Alphanumeric</strong> = The value, restricted to alphanumeric chars, contains non-alphanumeric characters.</li>
				<li><strong>Invalid Characters</strong> = Found illegal characters. Only ASCII characters are allowed.</li>
				<li><strong>Bad Double</strong> = A value expected to be a double could not be converted.</li>
				<li><strong>Bad Integer</strong> = A value expected to be an integer could not be converted.</li>
				<li><strong>Too Short</strong> = A value is less than its minimum length.</li>
				<li><strong>Too Long</strong> = A value is longer than its maximum length.</li>
				<li><strong>Exceeds Maximum</strong> = Exceeds maximum value.</li>
				<li><strong>Less Than Minimum</strong> = Less than minimum value.</li>
				<li><strong>Invalid Date</strong> = Could not cast value as date.</li>
				<li><strong>Missing Date Parts</strong> = No year, month, or day found in date value.</li>
				<li><strong>Extra Date Parts</strong> = Value has too many parts to be a date.</li>
				<li><strong>Date Out Of Range</strong> = Date or Time is out of range (ex 2/33/2009).</li>
				<li><strong>Bad Year Length</strong> = Value for year is not 4 digits.</li>
				<li><strong>Bad Month or Day Length</strong> = Month or day-of-year must be 2 or 3 digits in length.</li>
				<li><strong>Bad Month Length</strong> = Month must be 2 digits in length.</li>
				<li><strong>Bad Day Of Month Length</strong> = Day-of-month must be digits in length.</li>
				<li><strong>Bad Fractional Time Length</strong> = Fractional section of time must be 1 to 3 digits in length.</li>
				<li><strong>Bad Time Section</strong> = Hours, minutes, and seconds of the date must be 2 digits in length. </li>
				<li><strong>Illegal Character</strong> = Illegal character for value.</li>
				<li><strong>Manipulated Value</strong> = The value is only valid when the case is changed or spaces are substituted with underscores.</li>
				<li><strong>Type Mismatch</strong> = The value type is illegal for the given key.</li>
				<li><strong>Bad Real</strong> = A value expected to be a real could not be converted.</li>
				<li><strong>Signed Non Decimal</strong> = Non decimal values must not be signed.</li>
				<li><strong>Bad Non Decimal</strong> = A value expected to be a non decimal could not be converted.</li>
				<li><strong>Bad Non Decimal Radix</strong> = A non decimal value has an illegal radix. 2, 4, 16 are the only valid radix supported in the PDS.</li>
				<%--<li><strong>*Bad Catalog Pointer Name Convention</strong> = A catalog pointer does not appear in the list of expected pointer names.</li>--%>
				<li><strong>Unknown Units</strong> = Found units not defined in dictionary.</li>
				<li><strong>Invalid Units</strong> = Units are invalid for the expected value type.</li>
				<li><strong>Bad Value</strong> = A value was unable to be cast to a valid type.</li>
				<li><strong>Placeholder Value</strong> = The value, "NULL", is intended as a placeholder and should be replaced before delivery.</li>
			</ul>
			
			<div><strong>Object / Group Problems</strong></div>
			<ul>
				<li><strong>Invalid Element</strong> = Object or group contains an element which is neither required nor optional.</li>
				<li><strong>Missing Required Element</strong> = Object or group does not contain a required element.</li>
				<li><strong>Invalid Object</strong> = Object or group contains an object which is neither required nor optional.</li>
				<li><strong>Missing Required Object</strong> = Object or group does not contain a required object.</li>
				<li><strong>Missing Required Group</strong> = Object or group does not contain a required group.</li>
				<li><strong>Invalid Group</strong> = Object or group contains a group which is neither required nor optional.</li>
			</ul>
			
			<div><strong>Data Set Validation Errors</strong></div>
			<ul>
				<li><strong>Missing Referenced File</strong> = A referenced file was not found.</li>
				<li><strong>Missing Label</strong> = A label file, defined in the index, was not found.</li>
				<li><strong>Missing Catalog</strong> = A catalog file was not found.</li>
				<li><strong>Un-Indexed Label</strong> = A label was not listed in an index.</li>
				<li><strong>Illegal Indexed Label</strong> = A label that should not appear in the index was listed in the index.</li>
				<li><strong>Unknown File</strong> = Found a file that is not defined by a label.</li>
				<li><strong>Missing Required Folder</strong> = Missing a required folder.</li>
				<li><strong>Missing Required File</strong> = Missing a required file.</li>
				<li><strong>Missing Required Child</strong> = Missing a file required by the presence of a given folder.</li>
				<li><strong>Bad Pointer Name</strong> = A label contains a pointer that does not follow pointer naming conventions.</li>
				<li><strong>No Indexes</strong> = No index files were found.</li>
				<li><strong>Column Number Mismatch</strong> = The number of columns found in a tabular data file does not match the number defined.</li>
				<li><strong>Column Length Mismatch</strong> = The test value for a column does not match the byte length of the column definition.</li>
				<li><strong>Column Type Mismatch</strong> = The sample value for a column was unable to be cast to the type defined for the column.</li>
				<li><strong>Invalid Integer</strong> = The sample value for a column was invalid for the given type. It must be a 1, 2, or 4 byte signed integer.</li>
				<li><strong>Invalid Date</strong> = The sample value for the column was not a valid date format. It must conform to the format YYYY-MMDDThh:mm:ss.sss.</li>
				<li><strong>Empty Directory</strong> = Folder contains no files.</li>
				<li><strong>Empty File</strong> = File contains no data.</li>
				<li><strong>Mismatched Case</strong> = Case of actual file path and described file path do not match.</li>
				<li><strong>Column Length Mismatch</strong> = The column definition specified a given number of bytes but only fewer bytes remained on the test line following the specified start byte.</li>
				<li><strong>Column Out Of Range</strong> = The column definition specified a given start byte which was beyond the end of the test line.</li>
			</ul>
		</div>
	</div>
</div>









 





