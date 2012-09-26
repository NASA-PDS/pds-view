//	Copyright 2009-2012, by the California Institute of Technology.
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

package gov.nasa.pds.search.core.extractor.registry;

/**
 * Constants used to denote the possible values in the product class
 * XML configuration files.
 * 
 * @author jpadams
 *
 */
public final class MappingTypes {
	/** Output value given into index */
	public static final String OUTPUT = "out";
	
	/** Value represents a slot name */
	public static final String SLOT = "slot";
	
	/** Value represents an attribute name */
	public static final String ATTRIBUTE = "attribute";
	
	/** Value represents an associated object slot */
	public static final String ASSOCIATION = "association";
	
	/** Value is a string, with a substring variable */
	public static final String SUBSTRING = "substring";
}
