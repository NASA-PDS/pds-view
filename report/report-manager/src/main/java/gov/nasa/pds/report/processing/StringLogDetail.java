//	Copyright 2015, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//
package gov.nasa.pds.report.processing;


/**
 * This {@link LogDetail} implementation is used to represent log details that
 * can be stored as simple Strings.
 * 
 * @author resneck
 */
public class StringLogDetail extends LogDetail{

	protected String value;
	protected String defaultValue;
	
	/**
	 * Create a StringLogDetail object with the given name, pattern,
	 * requirement, and default value.
	 * 
	 * @param name			The name of the log detail.
	 * @param pattern		The pattern used to extract the log detail from
	 * 						the log line.
	 * @param required		Whether the detail must have a value for the
	 * 						containing log line to be considered valid.
	 * @param defaultValue	The default value of the detail.
	 */
	public StringLogDetail(String name, String pattern, boolean required,
			String defaultValue){
		
		super(name, pattern, required);
		this.value = null;
		this.defaultValue = defaultValue;
		
	}
	
	/**
	 * Create a StringLogDetail object with the given name, pattern,
	 * and requirement.
	 * 
	 * @param name			The name of the log detail.
	 * @param pattern		The pattern used to extract the log detail from
	 * 						the log line.
	 * @param required		Whether the detail must have a value for the
	 * 						containing log line to be considered valid.
	 */
	public StringLogDetail(String name, String pattern, boolean required){
		
		super(name, pattern, required);
		this.value = null;
		this.defaultValue = null;
		
	}
	
	/**
	 * Get the value of the log detail or the default value if no value was
	 * extracted from the log line.
	 * 
	 * @return	The log detail value (or default) as a String.
	 */
	public String getValue(){
		if(this.value == null){
			return this.defaultValue;
		}
		return this.value;
	}
	
	/**
	 * This method is meant to be invoked on the StringLogDetail that
	 * represents the log detail in output and accepts the input
	 * StringLogDetail.  The convenience of calling this method is that it will
	 * provide the value of the input log detail or the default value set on
	 * the output log detail, if the input log detail is null or has no value.
	 * 
	 * @param inputDetail	The StringLogDetail storing the log detail value as
	 * 						extracted from the input log detail.
	 * @return				The value of the log detail as determined by the
	 * 						value extracted from input and defaults specified
	 * 						in both the input and output patterns.
	 */
	public String getValue(StringLogDetail inputDetail){
		if(inputDetail == null || inputDetail.getValue() == null){
			return this.defaultValue;
		}
		return inputDetail.getValue();
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogDetail.getType()
	 */
	public String getType(){
		return "string";
	}
	
	/**
	 * Set the value of the log detail to the given String.
	 * 
	 * @param value	The new value of the log detail.
	 */
	public void setValue(String value){
		if(value.equals(this.emptyValue)){
			this.value = null;
		}else{
			this.value = value;
		}
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogDetail.reset()
	 */
	public void reset(){
		this.value = null;
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogDetail.toString()
	 */
	public String toString(){
		return "String log detail: name: " + this.name + " value: " +
				this.value + " pattern: " + this.pattern + " required: " +
				this.required + " default: " + this.defaultValue;
	}
	
}