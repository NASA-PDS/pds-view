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
 * This is the basic data structure object that holds the value of a detail
 * extracted from a line of a log.
 * 
 * @author resneck
 */
public abstract class LogDetail{
		
	protected String name;
	protected String pattern;
	protected boolean required = false;
	
	/**
	 * Create a LogDetail object, specifying it's name, the RE pattern used to
	 * capture the detail, and whether a value is required for the detail that
	 * will be captured.
	 * 
	 * @param name		The name of the detail contained in this object.
	 * @param pattern	The pattern used to capture the detail from logs.
	 * @param required	Whether a value is required for the containing line to
	 * 					be considered valid.
	 */
	public LogDetail(String name, String pattern, boolean required){
		
		this.pattern = pattern;
		this.required = required;
		this.name = name;
		
	}
	
	/**
	 * Get the name of the detail contained in this object.
	 * 
	 * @return	The name of the log detail in a String.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Get the pattern used to capture the log detail.
	 * 
	 * @return	The pattern used to capture the detail, represented as a String.
	 */
	public String getPattern(){
		return this.pattern;
	}
	
	/**
	 * Get the type (i.e. implementation) of the LogDetail object, allowing the
	 * calling code to properly interact with the object without the use of
	 * crude instanceof statements.
	 * 
	 * @return	One of the "string" or "datetime" Strings.
	 */
	abstract public String getType();
	
	/**
	 * Indicate whether the detail to be captured in this object is required.
	 * 
	 * @return	A boolean if the detail is required.
	 */
	public boolean isRequired(){
		return this.required;
	}
	
	/**
	 * Represent the value stored in the LogDetail as a String.
	 * 
	 * @return	A string representing the value of the log detail.
	 */
	abstract public String toString();
	
	/**
	 * Reset the value contained within the LogDetail object.  This should be
	 * called after each line has been processed to ensure that no values are
	 * accidentally carried forward while processing subsequent lines.
	 */
	abstract public void reset();
	
}