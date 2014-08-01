package gov.nasa.pds.report.profile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

// TODO: Make an implementation of this interface that encrypts sensitive
// info (i.e. passwords, etc.)

public interface ProfileManager{
	
	/**
	 * 
	 * @param path	A String of the absolute path to the profile file or
	 * 				directory containing the profile files.
	 * @return		A {@link List} of {@link Properties} containing all of
	 * 				the needed node information with one Properties per node.
	 */
	public List<Properties> readProfiles(String path) throws FileNotFoundException, IOException;
	
}