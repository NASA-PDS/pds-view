/**
 * 
 */
package gov.nasa.pds.report.logs.pushpull;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.constants.SftpTestConstants;
import gov.nasa.pds.report.logs.pushpull.model.LogSet;
import gov.nasa.pds.report.rules.PDSTest;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.Utility;

import org.apache.commons.io.FileUtils;
import org.jasypt.util.text.StrongTextEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author jpadams
 *
 */
public class PDSPullTest extends PDSTest {

	private static PDSPullImpl pullImpl;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pullImpl = new PDSPullImpl();
	}
	
	@Before
	public void setUp() throws Exception {
		FileUtils.forceMkdir(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
	}
	
	@Test
	public void testDecrypt() throws Exception {
		String password = "foobar";
		
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword(Constants.CRYPT_PASSWORD);
		String encryptedPassword = encryptor.encrypt(password);
		//System.out.println(encryptedPassword);
		
        Method method = PDSPullImpl.class.getDeclaredMethod("decrypt",
        		String.class);
        method.setAccessible(true);
        String output = (String) method.invoke(pullImpl, encryptedPassword);
		
		assertTrue(password.equals(output));
	}
	
	@Test
	public void testConnect() throws PushPullException {
		assertTrue(pullImpl.connect(SftpTestConstants.TEST_HOST,
				SftpTestConstants.TEST_USER, SftpTestConstants.TEST_PASSWORD,
				true));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPrivateGetFileFileList() throws Exception {
		
		// Remove any Date Logs Filters from previous tests
		DateLogFilter.unsetAll();
		
		pullImpl.connect(SftpTestConstants.TEST_HOST,
				SftpTestConstants.TEST_USER, SftpTestConstants.TEST_PASSWORD,
				true);
		
		String path="/var/log/httpd/access_log*2015-03-*";
		
        Method method = PDSPullImpl.class.getDeclaredMethod("getFileList", String.class);
        method.setAccessible(true);
        List<String> fileList = (List<String>) method.invoke(pullImpl, path);
        
        /*for (String file : fileList) {
        	System.out.println(file);
        }*/
        System.out.println(fileList.size());
		Collections.sort(fileList);
		assertTrue(fileList.size() == 31);
		assertTrue(fileList.get(0).equals(new File(
				SftpTestConstants.TEST_PATH).getName()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetLogs() throws Exception {
		// Initializing variables
		String destinationBase = Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE);
		List<LogSet> logSetList = new ArrayList<LogSet>();
		LogSet logSet = new LogSet();		
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		
		// Initializing encryptor
		encryptor.setPassword(Constants.CRYPT_PASSWORD);
		
		// Remove any Date Logs Filters from previous tests
		DateLogFilter.unsetAll();
		
		// Set some log sets to test
		
		// pdsimg
		logSet.setHostname(SftpTestConstants.TEST_HOST);
		logSet.setUsername(SftpTestConstants.TEST_USER);
		logSet.setPassword("Jplmipl!");
		logSet.setPathname(SftpTestConstants.TEST_PATH);
		logSetList.add(logSet);
		
		// pdsen http
		//logSet = new LogSet();
		//logSet.setHostname("pdsweb1.jpl.nasa.gov");
		//logSet.setUsername("jpadams");
		//logSet.setPassword("");
		//logSet.setPathname("/usr/local/httpd/apache2/logs/access_log.2014-03-01.txt");
		//logSetList.add(logSet);
		
		LogSet ls;
		String destination, hostname;
		for (int i=0; i < logSetList.size(); i++) {
			// Get the logSet
			ls = logSetList.get(i);
			
			// Set the hostname because we will use it a few times
			hostname = ls.getHostname();
			
			// The destination dir
			destination = destinationBase + hostname;
			
			// Create the destination dir
			FileUtils.forceMkdir(new File(destination));
			
			// Connect to the PushPull object
			pullImpl.connect(hostname, ls.getUsername(), ls.getPassword(), true);
			
			// Pull the logs to the desired destination
	        pullImpl.pull(ls.getPathname(), destination);
	        
	        //System.out.println(destination);
	        //for (File file : (List<File>) FileUtils.listFiles(new File(destination), new String[]{"*"}, true)) {
	        //	System.out.println("--" + file.getAbsolutePath());
	        //}
	        
	        // Check if it worked
	        assertTrue(hostname + " failed", (new File(destination + "/" + new File(ls.getPathname()).getName())).exists());
		}

	}
	
}
