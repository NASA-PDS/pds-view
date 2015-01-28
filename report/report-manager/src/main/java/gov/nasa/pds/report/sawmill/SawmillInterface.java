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
//	$Id: SawmillDB.java 11670 2013-06-20 17:14:33Z jpadams $
//

package gov.nasa.pds.report.sawmill;

import java.util.List;

/**
 * Class that implement this interface will execute Sawmill commands in
 * different ways.
 * 
 * @author resneck
 *
 */
public interface SawmillInterface{
	
	/**
	 * Run a {@link List} of provided commands in order
	 * 
	 * @param commandList		The {@link List} of commands to run
	 * @throws SawmillException	If the provided List is empty or an error occurs
	 */
	public void runCommands(List<String> commandList) throws SawmillException;
	
}
