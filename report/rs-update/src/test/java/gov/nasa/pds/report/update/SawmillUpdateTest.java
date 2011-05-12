package gov.nasa.pds.report.update;

import static org.junit.Assert.fail;

import java.util.logging.Logger;

import gov.nasa.pds.report.update.launch.SawmillLauncher_old;
import gov.nasa.pds.report.update.model.LogPath;
import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.properties.EnvProperties;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the LogTransferLauncher class.
 * 
 * @author jpadams
 */
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
			EnvProperties env = new EnvProperties("/Users/jpadams/dev/workspace/2010-workspace/report/profile-setup/target/classes");
			LogPath logPath = new LogPath(env.getSawmillLogHome(), "target", "transfertest", "test-web");
			ReportServiceUpdate update = new ReportServiceUpdate(logPath, "swr-test", false);
			update.transferLogs("pdsimg-1.jpl.nasa.gov", "jpadams", "2i2VG2cl66wNIQAenLZWaBOTPj5x6fKs", "/home/jpadams/temp*.txt", "log_set_1");
			update.updateSawmill(env.getSawmillHome());
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
