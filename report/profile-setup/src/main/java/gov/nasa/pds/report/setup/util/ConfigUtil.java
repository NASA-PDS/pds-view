package gov.nasa.pds.report.setup.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import gov.nasa.pds.report.setup.model.LogSet;
import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.transfer.util.ConfigManager;

/**
 * @author jpadams
 *
 */
public class ConfigUtil {
	private String logPath;
	private String profileName;
	private String realPath;
	
	public ConfigUtil(final String logDest, final String realPath, final String name) {
		this.logPath = logDest;
		this.profileName = name;
		this.realPath = realPath;
	}
	
	/**
	 * Builds the configuration for Sawmill profile
	 * @throws IOException
	 */
	public void buildCfg(final List<LogSet> lsList, final boolean isNewProfile) throws IOException {
		// TODO Currently overwrites profile - Need to allow for addition of Log Sources
		LogSet ls;

		ConfigManager sawmill = new ConfigManager(this.logPath, this.realPath, this.profileName);
		for (Iterator<LogSet> it = lsList.iterator(); it.hasNext();) {
			ls = it.next();
			sawmill.addSource(ls.getLabel(), ls.getSetNumber());
		}

		sawmill.createProfile(isNewProfile);
	}
}
