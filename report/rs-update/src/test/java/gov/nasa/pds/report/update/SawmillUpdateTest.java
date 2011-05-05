package gov.nasa.pds.report.update;

import static org.junit.Assert.fail;

import java.util.logging.Logger;

import gov.nasa.pds.report.update.launch.SawmillLauncher_old;
import gov.nasa.pds.report.update.model.LogPath;
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
	 * Tests the LogTransferLauncher, which in turn tests 
	 * createDirStruct and getLogs
	 */
	@Ignore
	@Test
	public void testSawmillUpdateLauncher()
	{
		try {
			System.out.println("Testing LogTransferLauncher for web interface");
			EnvProperties env = new EnvProperties("/Users/jpadams/dev/workspace/2010-workspace/report/profile-setup/target/classes");
			LogPath logPath = new LogPath(env.getSawmillLogHome(), "target", "transfertest", "test-web");
			ReportServiceUpdate launcher = new ReportServiceUpdate();
			
			launcher.transferLogs("pdsimg-1.jpl.nasa.gov", "jpadams", "2i2VG2cl66wNIQAenLZWaBOTPj5x6fKs", "/home/jpadams/temp*.txt", logPath.getPath());
			launcher.updateSawmill(env.getSawmillHome(), "swr-tester", false);
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
