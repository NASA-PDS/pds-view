<%@ taglib prefix="s" uri="/struts-tags" %>
<style>
.contents { margin-bottom: 25px; }
</style>

<div class="defaultWrapper">
    <div class="title">Troubleshooting / FAQ</div>
    <div class="basicBox" style="font-size: 12px;">
    	<div style="padding: 20px 0px 30px 35px; line-height: 1.7em;">
    		<a href="#no_response">Clicking "Validate Local Volume" does nothing</a>
    		<br />
    		<a href="#hung_validator">Validation appears to hang</a>
    		<br />
    		<a href="#out_of_memory">Validation applet runs out of memory</a>
    		<br />
    		<a href="#invalid_pointers">I'm getting errors on pointers that appear to be valid</a>
    		<br />
    		<a href="#start_byte_mismatch">Apparently valid attached data start byte produce ATTACHED_START_BYTE_MISMATCH errors</a>
    		<br />
    		<a href="#unknown_value">I want to suppress complaints about new values, UNKNOWN_VALUE, that will be added to the master data dictionary</a>
    	</div>
    	
        <div class="label"><a name="no_response"></a>Clicking "Validate Local Volume" does nothing</div>
        <div class="contents">
        	This may be for a variety of reasons:
        	<ul>
        		<li>Your java plugin is not up to date. Install the newest from http://www.java.com/en/download/ or, if you're on a mac, http://sourceforge.net/projects/javaplugin/.</li>
        		<li>The applet is a cached version that is not compatible with the current implementation of the validator. Close all instances of your browser, open a new browser window, clear your cache, navigate back to the validator page.</li>
        		<li>You did not give permission to the applet to see files on your archive. Make sure, when prompted, that you accept the certificate.</li>
        	</ul>
        </div>
        
         <div class="label"><a name="hung_validator"></a>Validation appears to hang</div>
         <div class="contents">Open your java console and make sure that you have not received an irrecoverable error like the java process running out of memory. Opening the console varies by browser, java installation, and OS - if you're having difficulty, email us with your browser info and OS.</div>
         
         <div class="label"><a name="out_of_memory"></a>Validation applet runs out of memory</div>
         <div class="contents">
			Give the java process more memory - this varies across systems but in general you need to find where the Java Runtime Parameters are and add -Xmx128m or greater. For example, on an XP system this process is:

			<ol>
				<li>Open your Control Panel</li>
				<li>Open "Java"</li>
				<li>Click the "Java" tab</li>
				<li>Click the "View" button</li>
				<li>Click the "User" tab</li>
				<li>Double click the Runtime Parameters entry for your most recent java version</li>
				<li>Enter "-Xmx128m" (or greater)</li>
				<li>Repeat for each other version of java</li>
				<li>Click "OK"</li>
				<li>Click "OK"</li>
				<li>Restart your browser</li>
			</ol>
			
			If you're having trouble with this, please contact us with operating system details and anything you know about your java installation.
			<br/><br/>
			If you were able to increase the memory and it still wasn't validating, check to see if you have one or more extremely large labels. This may be a label with hundreds of thousands of statements or an attached label with 50 megs or more of attached data. In the second case, you can try detaching the label from the data. We're investigating a solution for the large attached data issue but the parser technology being used, ANTLR, does not lend itself to large files.
		</div>
         
        <div class="label"><a name="invalid_pointers"></a>I'm getting errors on pointers that appear to be valid</div>
        <div class="contents">Curly braces "{}" indicate a set of files. Parens "()" indicate a location within a file. Make sure you're using the right ones.</div>
        
        <div class="label"><a name="start_byte_mismatch"></a>Apparently valid attached data start byte produce ATTACHED_START_BYTE_MISMATCH errors</div>
        <div class="contents">The first non-whitespace character after the END statement is considered the attached data start byte. A common mistake is padding after the END statement with nulls, 0x00, instead of spaces, 0x20. Using a program like <a href="http://notepad-plus.sourceforge.net/uk/site.htm" target="_blank">Notepad++</a> with all characters displayed (View -> Show Symbol -> Show All Characters) is a good way to check that you have the correct characters.</div>
        
        <div class="label"><a name="unknown_value"></a>I want to suppress complaints about new values, UNKNOWN_VALUE, that will be added to the master data dictionary</div>
        <div class="contents">The validation tool supports use of a local data dictionary which allows you to define your new values prior to validation. The steps to generate a local data dictionary are as follows:
        	<ol>
        		<li>Run the validator to identify values that need to be added to the dictionary.</li>
        		<li>Create a file, PDSDD.FUL, and place it in the DOCUMENT directory.</li>
        		<li>Create a minimal definition for each object that has new values. Node that each definition ends with END as though it were the end of a label.</li>
        		<li>Add only the new values to the STANDARD_VALUE_SET, this will be merged into the existing values.</li>
        		<li>Run the validator again to confirm that there are no further complaints about UNKNOWN_VALUE.</li>
        	</ol>
        	Below is an example local dictionary that allows "CM**-1" as a value for SAMPLING_PARAMETER_UNIT and "EQUINOX MISSION", "SATURN TOUR" as valid values for MISSION_PHASE_NAME.
        	<div style="padding: 15px 0px 0px 30px;"><code style="white-space: pre;">
OBJECT = ELEMENT_DEFINITION
  NAME = SAMPLING_PARAMETER_UNIT
  STANDARD_VALUE_SET = {
     "CM**-1"
	 }
END_OBJECT = ELEMENT_DEFINITION
END 
OBJECT = ELEMENT_DEFINITION
  NAME = MISSION_PHASE_NAME
  STANDARD_VALUE_SET = {
     "EQUINOX MISSION",
	  "SATURN TOUR"
	 }
END_OBJECT = ELEMENT_DEFINITION
END 
        	</code></div>
        </div>
    </div>
</div>