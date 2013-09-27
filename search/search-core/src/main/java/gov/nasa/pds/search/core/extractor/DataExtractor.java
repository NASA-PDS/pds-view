//	Copyright 2013, by the California Institute of Technology.
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
//	$Id: SearchCoreLauncher.java 12098 2013-09-18 15:53:49Z jpadams $
//

package gov.nasa.pds.search.core.extractor;

import java.io.File;
import java.util.List;

/**
 * Interface for classes use to query data to include in index
 * 
 * @author jpadams
 *
 */
public interface DataExtractor {

	/**
	 * Driver method for extraction of data from data source
	 * 
	 * @throws Exception 
	 */
	public void run() throws Exception;
	
	/**
	 * Return the List of core configuration files
	 * 
	 * @throws Exception
	 * @return
	 */
	public List<File> getCoreConfigs(File configDir) throws Exception;
	
	public File getConfDir();
		
	public void setConfDir(File confDir);
		
	public int getQueryMax();
			
	public void setQueryMax(int queryMax);

}
