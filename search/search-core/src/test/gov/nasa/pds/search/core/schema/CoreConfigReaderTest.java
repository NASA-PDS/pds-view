package gov.nasa.pds.search.core.schema;

import static org.junit.Assert.*;

import gov.nasa.pds.search.core.constants.TestConstants;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link CoreConfigReader}.
 *
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class CoreConfigReaderTest {

	public static final File CONFIG_PATH = new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE);
	
	
    @Test
    public void testUnmarshall() {
    	System.out.println("-----------------------------------------------------");
    	System.out.println("--- Testing JAXB with CoreConfigReader.unmarshall ---");
    	System.out.println("-----------------------------------------------------");
    	try {
    		CoreConfigReader.unmarshall(new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_DIR_RELATIVE + "core-config-test-1.xml"));
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail("Data Set Config failed validation");
    	}
    }
    
    @Test
    public void validateDefaultConfigurations() {
    	System.out.println("----------------------------------------------");
    	System.out.println("--- Validating Default Configurations Test ---");
    	System.out.println("----------------------------------------------");
		String[] extensions = {"xml"};
		for (File file : FileUtils.listFiles(CONFIG_PATH, extensions, true)) {
			try {
				CoreConfigReader.unmarshall(file);
			} catch (Exception e) {
				e.printStackTrace();
	    		fail("Failed validation - " + file
	    				+ "\n" + e.getMessage());
	    	}
		}
    }
}
