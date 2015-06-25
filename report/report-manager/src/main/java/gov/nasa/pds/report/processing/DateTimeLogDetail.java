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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: Write a decent class doc.
 * 
 * @author resneck
 */
public class DateTimeLogDetail extends LogDetail{
		
	protected Date date;
	protected Date defaultDate;
	protected SimpleDateFormat dateFormat;
	
	/**
	 * Create a new DateTimeLogDetail with the specified name, pattern,
	 * requirement, input format, and default date.
	 * 
	 * @param name				The name of the log detail.
	 * @param pattern			The RE pattern used to extract the log detail
	 * 							from the input log line.
	 * @param required			Whether a value must be provided in the input
	 * 							log line for the line to be considered valid.
	 * @param inputFormat		The String used to create a
	 * 							{@link SimpleDateFormat} to translate String
	 * 							input into a {@link Date} object.
	 * @param defaultDate		The default date of the log detail, represented
	 * 							in a String format and interpreted by the input
	 * 							format (the previous parameter) into a Date.
	 * @throws ParseException	If the String provided as the default date
	 * 							cannot be parsed.
	 */
	public DateTimeLogDetail(String name, String pattern, boolean required,
			String inputFormat, String defaultDate) throws ParseException{
		
		super(name, pattern, required);
		this.date = null;
		this.dateFormat = new SimpleDateFormat(inputFormat);
		this.defaultDate = this.dateFormat.parse(defaultDate);
		
	}
	
	/**
	 * Create a new DateTimeLogDetail with the specified name, pattern,
	 * requirement, and input format.
	 * 
	 * @param name				The name of the log detail.
	 * @param pattern			The RE pattern used to extract the log detail
	 * 							from the input log line.
	 * @param required			Whether a value must be provided in the input
	 * 							log line for the line to be considered valid.
	 * @param inputFormat		The String used to create a
	 * 							{@link SimpleDateFormat} to translate String
	 * 							input into a {@link Date} object.
	 */
	public DateTimeLogDetail(String name, String pattern, boolean required,
			String inputFormat){
		
		super(name, pattern, required);
		this.date = null;
		this.dateFormat = new SimpleDateFormat(inputFormat);
		this.defaultDate = null;
		
	}
	
	/**
	 * Get a String representing the format used to interpret an input String
	 * into a String.
	 * 
	 * @return	The date format String.
	 */
	public String getFormat(){
		return this.dateFormat.toPattern();
	}
	
	/**
	 * Get the date stored by the log detail, formatted according to the format
	 * provided as a parameter.
	 * 
	 * @param outputFormat	A String used to create a {@link SimpleDateFormat}
	 * 						that formats the stored {@link Date} as a String.
	 * @return				A String representing the date stored.
	 */
	public String getDate(String outputFormat){
		if(this.date == null){
			if(this.defaultDate != null){
				return new SimpleDateFormat(outputFormat).format(
						this.defaultDate);
			}
			return null;
		}
		return new SimpleDateFormat(outputFormat).format(this.date);
	}
	
	/**
	 * This method is meant to be invoked on the DateTimeLogDetail that
	 * represents the log detail in output and accepts the input
	 * DateTimeLogDetail.  The convenience of calling this method is that it
	 * will provide the value of the input log detail or the default value set
	 * on the output log detail, if the input log detail is null or has no
	 * value.
	 * 
	 * @param inputDetail	The DateTimeLogDetail storing the log detail date
	 * 						as extracted from the input log detail.
	 * @return				The date of the log detail as determined by the
	 * 						date extracted from input and defaults specified
	 * 						in both the input and output patterns.
	 */
	public String getDate(DateTimeLogDetail inputDetail){
		if(inputDetail == null){
			if(this.defaultDate != null){
				return this.dateFormat.format(this.defaultDate);
			}
		}
		String inputValue = inputDetail.getDate(this.dateFormat.toPattern());
		if(inputValue == null){
			if(this.defaultDate != null){
				return this.dateFormat.format(this.defaultDate);
			}
		}
		return inputValue;
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogDetail.getType()
	 */
	public String getType(){
		return "datetime";
	}
	
	/**
	 * Set the date of the log detail, using the log detail date format to
	 * transform the given String into a Date.
	 * 
	 * @param value	The new date of the log detail.
	 */
	public void setDate(String value) throws ParseException{
		
		// Massage any odd double spaces (present in some xferlogs) out of
		// the string
		value.replaceAll("  ", " ");
		
		this.date = this.dateFormat.parse(value);
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogDetail.reset()
	 */
	public void reset(){
		this.date = null;
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogDetail.toString()
	 */
	public String toString(){
		return "Date-time log detail: name: " + this.name + " date: " +
				this.getDate(this.getFormat()) + " format: " +
				this.getFormat() + " pattern: " + this.pattern + 
				" required: " + this.required + " default: " +
				this.printDefault();
	}
	
	/**
	 * Return the default Date as a String, formatting the Date using the log
	 * detail format.
	 * 
	 * @return	The default date represented as a String.
	 */
	private String printDefault(){
		if(this.defaultDate == null){
			return null;
		}
		return this.dateFormat.format(this.defaultDate);
	}
	
}