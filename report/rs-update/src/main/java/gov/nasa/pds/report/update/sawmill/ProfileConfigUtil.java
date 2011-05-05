package gov.nasa.pds.report.transfer.sawmill;

import gov.nasa.pds.report.transfer.model.LogPath;
import gov.nasa.pds.report.transfer.model.LogSet;
import gov.nasa.pds.report.transfer.properties.EnvProperties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * @author jpadams
 *
 */
public class ProfileConfigUtil {
	private Logger log = Logger.getLogger(this.getClass().getName());
	private File baseCfg;
	private File outputCfg;
	private LogPath logPath;
	private String profileName;
	
	private String sourcesTxt;
	private String pathname0;
	
	public ProfileConfigUtil(final LogPath logPath, final String localPath, final String name) throws IOException {
		EnvProperties env = new EnvProperties(localPath);
		this.sourcesTxt = "";
		//this.logPath = env.getSawmillLogHome() + "/" + logDest;
		
		this.logPath = logPath;
		this.logPath.setLogHome(env.getSawmillLogHome());
		
		this.profileName = name;
		
		this.baseCfg = new File(localPath + "/default.cfg");
		
		this.outputCfg = new File(env.getSawmillProfileHome() + '/' + this.profileName.replace('-', '_') + ".cfg"); //Must replace all dashes from profile name, otherwise Sawmill will fail
	}
	
	public void buildCfg(final List<LogSet> lsList, final boolean isNewProfile) throws IOException {
		// TODO Currently overwrites profile - Need to allow for addition of Log Sources

		//ConfigManager sawmill = new ConfigManager(this.logPath, this.realPath, this.profileName);
		for (LogSet ls : lsList) {
			this.logPath.setLogSetLabel(ls.getLabel());
			addSource(ls.getSetNumber());
		}

		createProfile(isNewProfile);
	}

	/**
	 * Builds the configuration for Sawmill profile
	 * @throws IOException
	 */
	public void addSource(final int setNumber) throws IOException {
		String newline = "";
		//String logDest = this.logPath + '/' + label;
		
		if (setNumber == 0) {
			this.pathname0 = this.logPath.getPath() + "/*";
		} else {
			newline = "\n";
		}

		this.sourcesTxt += "\t" + newline + setNumber + " = {\n"
				+ "\t\tdisabled = \"false\"\n"
				+ "\t\tlabel = \"" + this.logPath.getLogSetLabel() + "\"\n"
    	        + "\t\tpathname = \"" + this.logPath.getPath() + "/*\" \n"
    	        + "\t\tpattern_is_regular_expression = \"false\" \n"
    	        + "\t\tprocess_subdirectories = \"false\" \n"
    	        + "\t\ttype = \"local\"\n"
    	        + "\t} # " + setNumber;

		newline = "\n";

		this.sourcesTxt += "\n#LOG_SOURCES#";
	}
	
	public void createProfile(final boolean isNewProfile) throws IOException {
		String text = setCfgText(isNewProfile, sourcesTxt, pathname0);
		
		FileUtils.writeStringToFile(this.outputCfg, text);
		this.log.info("output config: " + this.outputCfg);		
	}
	
	/**
	 * 
	 * @param isNewProfile
	 * @param sourcesTxt
	 * @param pathname0
	 * @return
	 * @throws IOException
	 */
	private String setCfgText(final boolean isNewProfile, final String sourcesTxt, final String pathname0) throws IOException {
		String text = "";
		if (isNewProfile) {
			text = FileUtils.readFileToString(this.baseCfg);
			text = text.replace("#PROFILE_NAME#", this.profileName.replace('-', '_'))
					.replace("#PROFILE_LABEL#", this.profileName.replace('_', '-'))
					.replace("#LOG_SOURCES#", sourcesTxt)
					.replace("#PATHNAME_0#", pathname0);
		} else {
			text = FileUtils.readFileToString(this.outputCfg);
			text = text.replace("#LOG_SOURCES#", sourcesTxt);
		}
		return text;
	}
}
