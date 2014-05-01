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
//	$Id: ProfileConfigUtil.java 11670 2013-06-20 17:14:33Z jpadams $
//

package gov.nasa.pds.report.sawmill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * This class creates a Sawmill profile on the fly for a new profile.
 * 
 * FIXME 	This needs to be updated to use the new profile config and also
 * 			the built in command-line function create_profile_from_template
 * 			for Sawmill:
 * http://www.sawmill.net/cgi-bin/sawmill8/docs/sawmill.cgi?dp=docs.option&option_name=command_line.action
 * 
 * @author jpadams
 * 
 * 
 * 
 */
public class ProfileConfigUtil {
	private static final String DEFAULT_CFG = "default_profile.cfg";

	private Logger log = Logger.getLogger(this.getClass().getName());
	private File baseCfg;
	private File outputCfg;
	private String logPath;
	private String profileName;

	private String sourcesTxt;
	private String pathname0;

	public ProfileConfigUtil(final String logPath, final String localPath,
			final String profileHome, final String profileName)
			throws IOException {
		this.sourcesTxt = "";
		this.logPath = logPath;
		this.profileName = profileName;
		try {
			System.out.println("ProfileConfigUtil.class.getResource(\"/\" + DEFAULT_CFG) == " + ProfileConfigUtil.class.getResource("/" + DEFAULT_CFG));
			this.baseCfg = new File(ProfileConfigUtil.class.getResource("/" + DEFAULT_CFG).toURI()); //TODO HEre is the issue to fix to get the default_profile.cfg found through the jar
			//System.out.println(baseCfthis.baseCfg.getAbsolutePath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!this.baseCfg.exists())
			throw new FileNotFoundException(localPath + "/" + DEFAULT_CFG
					+ " not found.");

		// Sawmill requires all dashes(-) be replaced with underscores (_)
		this.outputCfg = new File(profileHome + '/'
				+ this.profileName.replace('-', '_') + ".cfg");
	}

	/**
	 * Builds the configuration for Sawmill profile
	 * 
	 * @throws IOException
	 */
	public void addSource(final int setNumber) {
		String newline = "";
		// String logDest = this.logPath + '/' + label;

		if (setNumber == 0) {
			this.pathname0 = this.logPath + "/*";
		} else {
			newline = "\n";
		}

		this.sourcesTxt += "\t" + newline + setNumber + " = {\n"
				+ "\t\tdisabled = \"false\"\n" + "\t\tlabel = \""
				+ this.logPath + "\"\n" + "\t\tpathname = \""
				+ this.logPath + "/*\" \n"
				+ "\t\tpattern_is_regular_expression = \"false\" \n"
				+ "\t\tprocess_subdirectories = \"true\" \n"
				+ "\t\ttype = \"local\"\n" + "\t} # " + setNumber;

		newline = "\n";

		this.sourcesTxt += "\n#LOG_SOURCES#";
	}

	public void createProfile(final boolean isNewProfile) throws IOException {
		String text = setCfgText(isNewProfile, sourcesTxt, pathname0);
		FileUtils.writeStringToFile(this.outputCfg, text);
		this.log.info("New Profile Config: " + this.outputCfg);
	}

	/**
	 * 
	 * @param isNewProfile
	 * @param sourcesTxt
	 * @param pathname0
	 * @return
	 * @throws IOException
	 */
	private String setCfgText(final boolean isNewProfile,
			final String sourcesTxt, final String pathname0) throws IOException {
		String text = "";
		if (isNewProfile) {
			text = FileUtils.readFileToString(this.baseCfg);
			text = text.replace("#PROFILE_NAME#",
					this.profileName.replace('-', '_')).replace(
					"#PROFILE_LABEL#", this.profileName.replace('_', '-'))
					.replace("#LOG_SOURCES#", sourcesTxt).replace(
							"#PATHNAME_0#", pathname0);
		} else {
			text = FileUtils.readFileToString(this.outputCfg);
			text = text.replace("#LOG_SOURCES#", sourcesTxt);
		}
		return text;
	}
}
