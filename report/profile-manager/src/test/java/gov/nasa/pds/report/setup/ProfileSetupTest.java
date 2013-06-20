package gov.nasa.pds.report.setup;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import gov.nasa.pds.report.profile.manager.ProfileMgrController;
import gov.nasa.pds.report.update.RSUpdateLauncher;
import gov.nasa.pds.report.update.db.DBUtil;
import gov.nasa.pds.report.update.db.DatabaseManager;
import gov.nasa.pds.report.update.model.LogPath;
import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.model.Profile;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the LogTransferLauncher class.
 * 
 * @author jpadams
 */
public class ProfileSetupTest {

	/**
	 * Tests the LogTransferLauncher, which in turn tests 
	 * createDirStruct and getLogs
	 */
	@Ignore
	@Test
	public void testLogTransferLauncher()
	{
		try {
			System.out.println("Testing TransferLogs.java ...");
			Profile profile = new Profile();
			profile.setIdentifier("test1");
			profile.setName("test-profile");
			profile.setNode("en");
			
			LogSet ls = new LogSet();
			ls.setLogSetId(0);
			ls.setHostname("pdsimg-1.jpl.nasa.gov");
			ls.setLabel("test1");
			ls.setPassword("Ph1ll1es8008");
			ls.setPathname("/home/jpadams/temp*.txt");
			ls.setUsername("jpadams");
			
			List<LogSet> lsList = new ArrayList<LogSet>();
			lsList.add(ls);
			profile.setLogSetList(lsList);
			
			
			LogPath logPath = new LogPath("/Users/jpadams/dev/workspace/2010-workspace/report/profile-setup/target/transfertest",profile.getNode(), profile.getName(), ls.getLabel());
			//GenericController transfer = new GenericController(logPath, "/Users/jpadams/dev/workspace/2010-workspace/report/log-transfer/src/main/resources/conf", profile, true);
			
			//transfer.start();
			//transfer.run();
			
			
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the ProfileDBUtil class
	 */
	@Ignore
	@Test
	public void testProfileDBUtil()
	{
		try {
			System.out.println("Testing ProfileDBUtil.java ...");
			DBUtil util = new DBUtil("target/classes");
			List<Profile> pList = util.findAllProfiles();
			
			if (pList.size() > 0) {
				assertTrue("Connection made. " + pList.size() + " profiles queried.", true);
			} else {
				throw new Exception("No profiles returned.");
			}
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
