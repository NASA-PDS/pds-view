package gov.nasa.pds.report.setup.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import gov.nasa.pds.report.setup.model.LogSet;
import gov.nasa.pds.report.setup.model.Profile;

/**
 * @author jpadams
 *
 */
public class SawmillUtil {
	private Logger log = Logger.getLogger(this.getClass().getName());
	//private Profile _profile;
	//private ArrayList<LogSet> _lsList;
	private File defaultCfg;
	private File outputCfg;
	private String logBasePath;
	private String sawmillProfHome;
	private String profName;

	/**
	 * 
	 * @param sawmillProfHome
	 * @param logBasePath
	 * @param path
	 * @param profName
	 */
	public SawmillUtil(final String sawmillProfHome, final String logBasePath, final String path, final String profName) {
		this.sawmillProfHome = sawmillProfHome;
		this.logBasePath = logBasePath;
		this.profName = profName;
		this.defaultCfg = new File(path + "/default.cfg");
		this.outputCfg = new File(this.sawmillProfHome + '/' + this.profName.replace('-', '_') + ".cfg"); //Must replace all dashes from profile name, otherwise Sawmill will fail
	}

	/**
	 * Builds the configuration for Sawmill profile
	 * @throws IOException
	 */
	public void buildCfg(final ArrayList<LogSet> lsList, final boolean isNewProf) throws IOException {
		// TODO Currently overwrites profile - Need to allow for addition of Log Sources
		String sourcesTxt = "";
		String newline = "";
		LogSet ls;
		int setNum;

		String pathname0 = "";
		for (Iterator<LogSet> it = lsList.iterator(); it.hasNext();) {
			ls = it.next();

			setNum = ls.getSetNumber();

			if (setNum == 0) {
				pathname0 = this.logBasePath + '/' + ls.getLabel() + "/*";
			} else {
				newline = "\n";
			}

			sourcesTxt += "\t" + newline + setNum + " = {\n" +
					"\t\tdisabled = \"false\"\n" +
					"\t\tlabel = \""+ls.getLabel()+"\"\n" +
	    	        "\t\tpathname = \""+this.logBasePath+ls.getLabel()+"/*\" \n" +
	    	        "\t\tpattern_is_regular_expression = \"false\" \n" +
	    	        "\t\tprocess_subdirectories = \"false\" \n" +
	    	        "\t\ttype = \"local\"\n" +
	    	      "\t} # "+setNum;
			
			newline = "\n";
		}
		
		sourcesTxt += "\n#LOG_SOURCES#";
		
		String text = setCfgText(isNewProf, sourcesTxt, pathname0);
		
		FileUtils.writeStringToFile(this.outputCfg, text);
		this.log.info("output config: "+this.outputCfg);
	}
	
	/**
	 * 
	 * @param isNewProf
	 * @param sourcesTxt
	 * @param pathname0
	 * @return
	 * @throws IOException
	 */
	private String setCfgText(final boolean isNewProf, final String sourcesTxt, final String pathname0) throws IOException {
		String text = "";
		if (isNewProf) {
			text = FileUtils.readFileToString(this.defaultCfg);
			text = text.replace("#PROFILE_NAME#", this.profName.replace('-', '_'))
					.replace("#PROFILE_LABEL#", this.profName.replace('_', '-'))
					.replace("#LOG_SOURCES#", sourcesTxt)
					.replace("#PATHNAME_0#", pathname0);
		} else {
			text = FileUtils.readFileToString(this.outputCfg);
			text = text.replace("#LOG_SOURCES#", sourcesTxt);
		}
		return text;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		Profile profile = new Profile();
		profile.setIdentifier("web");
		//profile.setMethod("pull1");
		profile.setName("pdseng-web");
		profile.setNode("eng");

		ArrayList<LogSet> lsList = new ArrayList<LogSet>();
		LogSet ls = new LogSet();

		// Profile name to create/update
		//logInfo.setProfileId(request.getParameter("profile"));
		// Remote machine host name or IP
		ls.setLabel("web1");
		ls.setHostname("pdsdev.jpl.nasa.gov");
		// User name to connect the remote machine
		ls.setUsername("jpadams");
		// Password for remote machine authentication
		ls.setPassword("Ph1ll1es8008");    
		// Source file path on local machine
		//String srcPath = "D:\\Test.txt";
		// Destination directory location on remote machine
		ls.setPathname("/home/jpadams/logs/access_log.*");
		lsList.add(ls);

		profile.setLogSetList(lsList);

		SawmillUtil util = new SawmillUtil("/Users/jpadams/Tomcat/webapps/ROOT/WEB-INF/cgi/LogAnalysisInfo/profiles",
				"/Users/jpadams/Documents/report_service/metrics/test",
				"/Users/jpadams/dev/workspace/2010-workspace/report/transfer-logs/src/main/resources",
				profile.getName());
		/*try {
			util.createCfg(lsList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

}
