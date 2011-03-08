/**
 * 
 */
package gov.nasa.pds.report.setup.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import gov.nasa.pds.report.setup.model.LogSet;
import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.setup.properties.EnvProperties;

/**
 * @author jpadams
 *
 */
public class SawmillUtil {

	private Logger _log = Logger.getLogger(this.getClass().getName());
	
	//private Profile _profile;
	//private ArrayList<LogSet> _lsList;
	private String _cfgText;
	private File _defaultCfg;
	private File _outputCfg;
	private String _logBasePath;
	private String _sawmillProfHome;
	private String _profName;
	
	public SawmillUtil(String sawmillProfHome, String logBasePath, String path, String profName) {
		this._sawmillProfHome = sawmillProfHome;
		this._logBasePath = logBasePath;
		this._profName = profName;
		
		this._defaultCfg = new File(path+"/default.cfg");
		this._outputCfg = new File(this._sawmillProfHome+'/' + this._profName.replace('-', '_')+".cfg"); //Must replace all dashes from profile name, otherwise Sawmill will fail

		//this._outputCfg = new File(profileCfgHome+'/'+profile.getName().replace('-', '_')+".cfg");  //Must replace all dashes, otherwise Sawmill will fail
		//this._profile = profile;
		//this._lsList = profile.getLogSetList();
	}
	
	public void createCfg(ArrayList<LogSet> lsList) throws IOException {
		//buildCfg(this._defaultCfg, lsList);
	}
	
	public void updateCfg(ArrayList<LogSet> lsList) throws IOException {
		//buildCfg(this._outputCfg, lsList);
	}
	
	/**
	 * Builds the configuration for Sawmill profile
	 * 
	 * @throws IOException
	 */
	// TODO Currently overwrites profile - Need to allow for addition of Log Sources
	public void buildCfg(ArrayList<LogSet> lsList, boolean isNewProf) throws IOException {
		String sourcesTxt = "";
		String newline = "";
		LogSet ls;
		int setNum;
		
		String pathname0 = "";
		for (Iterator<LogSet> it = lsList.iterator(); it.hasNext();) {
			ls = it.next();
			
			this._log.info("set number"+ls.getSetNumber());
			setNum = ls.getSetNumber();
			
			if (setNum == 0)
				pathname0 = this._logBasePath+'/'+ls.getLabel()+"/*";
			else
				newline = "\n";

			sourcesTxt += "\t" + newline + setNum + " = {\n" +
					"\t\tdisabled = \"false\"\n" +
					"\t\tlabel = \""+ls.getLabel()+"\"\n" +
	    	        "\t\tpathname = \""+this._logBasePath+ls.getLabel()+"/*\" \n" +
	    	        "\t\tpattern_is_regular_expression = \"false\" \n" +
	    	        "\t\tprocess_subdirectories = \"false\" \n" +
	    	        "\t\ttype = \"local\"\n" +
	    	      "\t} # "+setNum;
			
			newline = "\n";
		}
		
		sourcesTxt += "\n#LOG_SOURCES#";
		
		String text = setCfgText(isNewProf, sourcesTxt, pathname0);
		
		FileUtils.writeStringToFile(this._outputCfg, text);
		this._log.info("output config: "+this._outputCfg);
	}
	
	private String setCfgText(boolean isNewProf, String sourcesTxt, String pathname0) throws IOException {
		String text = "";
		if (isNewProf) {
			text = FileUtils.readFileToString(this._defaultCfg);
			text = text.replace("#PROFILE_NAME#", this._profName.replace('-', '_'))
						.replace("#PROFILE_LABEL#", this._profName.replace('_', '-'))
						.replace("#LOG_SOURCES#", sourcesTxt)
						.replace("#PATHNAME_0#", pathname0);
		} else {
			text = FileUtils.readFileToString(this._outputCfg);
			text = text.replace("#LOG_SOURCES#", sourcesTxt);			
		}
		
		return text;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		try {
			util.createCfg(lsList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
