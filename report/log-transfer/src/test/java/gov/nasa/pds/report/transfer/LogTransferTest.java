package gov.nasa.pds.report.transfer;

import static org.junit.Assert.fail;

import java.util.logging.Logger;

import gov.nasa.pds.report.transfer.launch.LogTransferLauncher;
import gov.nasa.pds.report.transfer.launch.SawmillUpdateLauncher;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the LogTransferLauncher class.
 * 
 * @author jpadams
 */
public class LogTransferTest {
	
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
			System.out.println("Testing LogTransferLauncher");
			LogTransferLauncher launcher = new LogTransferLauncher("target/transfertest/", 
					"pdsimg-1.jpl.nasa.gov", "jpadams", "2i2VG2cl66wNIQAenLZWaBOTPj5x6fKs", 
					"test1", "/home/jpadams/temp*.txt");
			
			launcher.execute();
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the SawmillUpdateLauncher which updates/rebuilds the Sawmill DB.
	 */
	@Ignore
	@Test
	public void testSawmillUpdateLauncher()
	{
		try {
			System.out.println("Testing SawmillUpdateLauncher");
			SawmillUpdateLauncher launcher = new SawmillUpdateLauncher("src/main/resources/conf", "test-profile", true);
			
			launcher.execute();
		} catch (final SawmillException e) {
			this.LOG.warning(e.getMessage() + " Sawmill profile test-profile must be created"
					+ " in order for SawmillUpdateLauncherTest to work.");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
