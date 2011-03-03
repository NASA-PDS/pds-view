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
	
	private Profile _profile;
	private ArrayList<LogSet> _lsList;
	private String _cfgText;
	private File _defaultCfg;
	private String _outputCfg;
	private String _logBasePath;
	
	public SawmillUtil(String profileCfgHome, String logBasePath, String path, Profile profile) {
		this._defaultCfg = new File(path+"/default.cfg");
		this._logBasePath = logBasePath;
		this._outputCfg = profileCfgHome+'/'+profile.getName().replace('-', '_')+".cfg";  //Must replace all dashes, otherwise Sawmill will fail
		this._profile = profile;
		this._lsList = profile.getLogSetList();
	}
	
	public void buildCfg() throws IOException {
		int count = 0;
		String sourcesTxt = "";
		String newline = "";
		LogSet ls;
		
		String pathname0 = "";
		for (Iterator<LogSet> it = this._lsList.iterator(); it.hasNext();) {
			ls = it.next();
			
			if (count == 0)
				pathname0 = this._logBasePath+'/'+ls.getLabel()+"/*";
			else if (count == 1)
				newline = "\n";
				
			sourcesTxt += "\t" + newline + count + " = {\n" +
					"\t\tdisabled = \"false\"\n" +
					"\t\tlabel = \""+ls.getLabel()+"\"\n" +
	    	        "\t\tpathname = \""+this._logBasePath+ls.getLabel()+"/*\" \n" +
	    	        "\t\tpattern_is_regular_expression = \"false\" \n" +
	    	        "\t\tprocess_subdirectories = \"false\" \n" +
	    	        "\t\ttype = \"local\"\n" +
	    	      "\t} # "+count;
/*	    	      1 = {
	    	        disabled = "false"
	    	        label = "log source 2"
	    	        pathname = "/Users/jpadams/Documents/Report_Service/metrics/en/pdsdlb2-tomcat/localhost_access_log.*.txt"
	    	        pattern_is_regular_expression = "false"
	    	        process_subdirectories = "false"
	    	        type = "local"
	    	      } # 1*/
			count++;
		}
		
		String text = FileUtils.readFileToString(this._defaultCfg);
		text = text.replace("#PROFILE_NAME#", this._profile.getName().replace('-', '_'))
					.replace("#PROFILE_LABEL#", this._profile.getName().replace('_', '-'))
					.replace("#LOG_SOURCES#", sourcesTxt)
					.replace("#PATHNAME_0#", pathname0);
		
		// TODO May want to utilize Sawmill commandline instead of just placing a config in the profiles directory
		FileUtils.writeStringToFile(new File(this._outputCfg), text);
		this._log.info("output config: "+this._outputCfg);
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
				profile);
		try {
			util.buildCfg();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
