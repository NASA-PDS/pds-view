package gov.nasa.pds.search.core.util;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.util.PDSDateConvert;
import junit.framework.TestCase;

public class PDSDateConvertTest extends TestCase {
	
	private Map<String, String> timeMap;
	
	@Before public void setUp() {
		this.timeMap = new HashMap<String, String>();
		this.timeMap.put("processing__unk", Constants.DEFAULT_DATETIME);
		this.timeMap.put("processing__1988-08-01", "1988-08-01T00:00:00.000Z");
		this.timeMap.put("1999", "1999-01-01T00:00:00.000Z");
		this.timeMap.put("2000-03", "2000-03-01T00:00:00.000Z");
		this.timeMap.put("2000-003","2000-01-03T00:00:00.000Z");
		this.timeMap.put("2001-4-30", "2001-04-30T00:00:00.000Z");
		this.timeMap.put("2001-04-30", "2001-04-30T00:00:00.000Z");
		this.timeMap.put("2002-05-15t14:20", "2002-05-15T14:20:00.000Z");
		this.timeMap.put("1994-07-20t20:16:32", "1994-07-20T20:16:32.000Z");
		this.timeMap.put("1977-09-05t00:14z", "1977-09-05T00:14:00.000Z");
		this.timeMap.put("1999-354t06:53:12z","1999-12-20T06:53:12.000Z");
		this.timeMap.put("1979-02-26T00:00:35.897", "1979-02-26T00:00:35.897Z");
		this.timeMap.put("19851108070408.649", "1985-11-08T07:04:08.649Z");
		this.timeMap.put("2009-06-30t00:00:00.00", "2009-06-30T00:00:00.000Z");
		this.timeMap.put("2004-001t00:00:00.000", "2004-01-01T00:00:00.000Z");
		this.timeMap.put("2002-02-19t19:00:29.6236", "2002-02-19T19:00:29.623Z");
		this.timeMap.put("2001-266t10:44:29.81", "2001-09-23T10:44:29.810Z");
		this.timeMap.put("2008-03-12T24:00:00.000", "2008-03-13T00:00:00.000Z");
		this.timeMap.put("UNK", Constants.DEFAULT_DATETIME);
		this.timeMap.put("N/A", Constants.DEFAULT_DATETIME);
		this.timeMap.put("NULL", Constants.DEFAULT_DATETIME);
		this.timeMap.put("UNKNOWN", Constants.DEFAULT_DATETIME);
		
		/** Taken from XML validation test files (see resources/testing/time **/
		// Negative ASCII_Date_YMD: valid values
		/*this.timeMap.put("-0123", );
		this.timeMap.put("-1008", );
		this.timeMap.put("-2008", );
		this.timeMap.put("-2008-01", );
		
		// Positive ASCII_Date_YMD: valid values
		this.timeMap.put("0123", );
		this.timeMap.put("1008", );
		this.timeMap.put("2008", );
		this.timeMap.put("2008-01", );
		
		// Negative ASCII_Date_Time_YMD: valid values
		this.timeMap.put(, );
		this.timeMap.put(, );
		this.timeMap.put(, );*/
	}
	
	public void testPDSDateConvertValid() {
		
		// Test Valid Values
		try {
			for (String inputDateTime : this.timeMap.keySet()) {
				String outputDateTime = PDSDateConvert.convert(inputDateTime);
				if (!outputDateTime.equals(this.timeMap.get(inputDateTime))) {
					fail("Invalid conversion."
							+ "\n  Input   - " + inputDateTime 
							+ "\n  Output  - " + outputDateTime
							+ "\n  Correct - " + this.timeMap.get(inputDateTime));
				} else {
					//System.out.println("\n  Input   - " + inputDateTime 
					//		+ "\n  Output  - " + outputDateTime);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
