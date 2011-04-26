package gov.nasa.pds.report.setup;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import gov.nasa.pds.report.setup.model.LogSet;
import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.setup.transfer.TransferLogs;
import gov.nasa.pds.report.setup.util.DBUtil;
import gov.nasa.pds.report.transfer.launch.LogTransferLauncher;
import gov.nasa.pds.report.transfer.launch.SawmillUpdateLauncher;
import gov.nasa.pds.report.transfer.util.DatabaseManager;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the LogTransferLauncher class.
 * 
 * @author jpadams
 */
public class ProfileSetupTest {
	
	private Logger LOG = Logger.getLogger(this.getClass().getName());

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
			
			TransferLogs transfer = new TransferLogs("/Users/jpadams/dev/workspace/2010-workspace/report/log-transfer/src/main/resources/conf", "/Users/jpadams/dev/workspace/2010-workspace/report/profile-setup/target/transfertest", profile, true);
			
			transfer.start();
			//transfer.run();
			
			
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the ProfileDBUtil class
	 */
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
