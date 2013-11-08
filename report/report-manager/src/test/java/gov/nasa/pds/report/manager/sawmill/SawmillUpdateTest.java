package gov.nasa.pds.report.manager.sawmill;

import static org.junit.Assert.fail;

import java.util.logging.Logger;

import gov.nasa.pds.report.manager.ReportManagerLauncher;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests the LogTransferLauncher class.
 * 
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class SawmillUpdateTest {
	
	private Logger LOG = Logger.getLogger(this.getClass().getName());
	/**
	 * Tests the RSUpdateLauncher
	 */
	@Ignore
	@Test
	public void testRSUpdateLauncher()
	{
		try {
			System.out.println("Testing RSUpdateLauncher");
			//EnvProperties env = new EnvProperties("/Users/jpadams/dev/workspace/2010-workspace/report/profile-setup/target/classes");
			//LogPath logPath = new LogPath(env.getSawmillLogHome(), "target", "transfertest", "test-web");
			//ReportServiceUpdate update = new ReportServiceUpdate(logPath, "swr-test", false);
			//update.transferLogs("pdsimg-1.jpl.nasa.gov", "jpadams", "2i2VG2cl66wNIQAenLZWaBOTPj5x6fKs", "/home/jpadams/temp*.txt", "log_set_1");
			//update.updateSawmill(env.getSawmillHome());
			String[] args = {"-s", "-p","/Users/jpadams/dev/workspace/2010-workspace/report/rs-update/src/main/resources/conf/"};
			ReportManagerLauncher.main(args);
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
