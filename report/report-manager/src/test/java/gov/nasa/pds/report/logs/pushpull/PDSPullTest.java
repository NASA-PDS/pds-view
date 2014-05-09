/**
 * 
 */
package gov.nasa.pds.report.logs.pushpull;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.PDSTest;
import gov.nasa.pds.report.util.Debugger;
import gov.nasa.pds.report.util.Utility;

import org.jasypt.util.text.StrongTextEncryptor;
import org.junit.After;
import org.junit.AfterClass;
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
	public SingleTestRule test = new SingleTestRule("testGetLogs");
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Debugger.debugFlag = true;
		pullImpl = new PDSPullImpl();
	}
	
	@Test
	public void testDecrypt() throws Exception {
		String password = "foobar";
		
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword(Constants.CRYPT_PASSWORD);
		String encryptedPassword = encryptor.encrypt(password);
		//System.out.println(encryptedPassword);
		
        Method method = PDSPullImpl.class.getDeclaredMethod("decrypt", String.class);
        method.setAccessible(true);
        String output = (String) method.invoke(pullImpl, encryptedPassword);
		
		assertTrue(password.equals(output));
	}
	
	@Test
	public void testConnect() throws PushPullException {
		// Check PDS Imaging
		assertTrue(pullImpl.connect("pdsimg-1.jpl.nasa.gov", "pdsrpt", "QRo5tViYmZgJYNObaALN5wTX911Jagn2", true));
		
		// Check PDS Engineering Node
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPrivateGetFileFileList() throws Exception {
		pullImpl.connect("pdsimg-1.jpl.nasa.gov", "pdsrpt", "QRo5tViYmZgJYNObaALN5wTX911Jagn2", true);
		
		String path="/var/log/httpd/access_log*2014-03-*";
		
        Method method = PDSPullImpl.class.getDeclaredMethod("getFileList", String.class);
        method.setAccessible(true);
        List<String> fileList = (List<String>) method.invoke(pullImpl, path);
        
        /*for (String file : fileList) {
        	System.out.println(file);
        }*/
		Collections.sort(fileList);
		assertTrue(fileList.size() == 31);
		assertTrue(fileList.get(0).equals("access_log.2014-03-01.txt"));
	}

	@Test
	public void testGetLogs() throws Exception {
		pullImpl.connect("pdsimg-1.jpl.nasa.gov", "pdsrpt", "QRo5tViYmZgJYNObaALN5wTX911Jagn2", true);
		
		String filename = "access_log.2014-03-01.txt";
		String basepath = "/var/log/httpd/";
        String destination = Utility.getAbsolutePath(TestConstants.TEST_DIR_RELATIVE);
        
        pullImpl.pull(basepath+filename, destination);

		assertTrue((new File(destination+filename)).exists());
	}
	
}
