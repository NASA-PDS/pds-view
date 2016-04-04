// Copyright 2013, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.report.util;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.rules.PDSTest;

public class DynamicDateFilterTest extends PDSTest{
	
	private static final String[] logNames = {
			"log-2014-08-31.txt", "log-2014-09-01.txt", "log-2014-09-15.txt",
			"log-2014-09-30.txt", "log-2014-10-01.txt",
			"log20140831.txt", "log20140901.txt", "log20140915.txt",
			"log20140930.txt", "log20141001.txt",
			"log08312014.txt", "log09012014.txt", "log09152014.txt",
			"log09302014.txt", "log10012014.txt",
			"log-08-31-2014.txt", "log-09-01-2014.txt", "log-09-15-2014.txt",
			"log-09-30-2014.txt", "log-10-01-2014.txt",
			"log140831.txt", "log140901.txt", "log140915.txt",
			"log140930.txt", "log141001.txt",
			"log140831test2.txt", "log140901test2.txt", "log140915test2.txt",
			"log140930test2.txt", "log141001test2.txt"
			};
	private static final String earlyDateString = "09/01/2014";
	private static final String lateDateString = "09/30/2014";
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void setUpClass(){
		DateLogFilter.forceUnsetFilter();
		System.setProperty(Constants.DATE_FILTER_PROP,
				"gov.nasa.pds.report.util.DynamicDateFilter");
	}
	
	@Test
	public void testNoDatesSet() throws Exception{
		
		try{
			DateLogFilter.setStartDate(null);
			DateLogFilter.setEndDate(null);
			assertTrue("Filename did not match with no start or end date set",
					DateLogFilter.match(logNames[0]));
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testStartDateOnly() throws Exception{
		
		try{
			DateLogFilter.setStartDate(earlyDateString);
			DateLogFilter.setEndDate(null);
			for(int i = 0; i < logNames.length; i = i + 5){
				String logName = logNames[i];
				assertFalse("Log (" + logName + ") from before start date (" +
						earlyDateString + ") matched",
						DateLogFilter.match(logName));
				logName = logNames[i + 1];
				assertTrue("Log (" + logName + ") on start date (" +
						earlyDateString + ") did not match",
						DateLogFilter.match(logName));
				logName = logNames[i + 2];
				assertTrue("Log (" + logName + ") after start date (" +
						earlyDateString + ") did not match",
						DateLogFilter.match(logName));
			}
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testEndDateOnly() throws Exception{
		
		try{
			DateLogFilter.setEndDate(lateDateString);
			DateLogFilter.setStartDate(null);
			for(int i = 0; i < logNames.length; i = i + 5){
				String logName = logNames[i + 2];
				assertTrue("Log (" + logName + ") from before end date (" +
						lateDateString + ") did not match",
						DateLogFilter.match(logName));
				logName = logNames[i + 3];
				assertTrue("Log (" + logName + ") on end date (" +
						lateDateString + ") did not match",
						DateLogFilter.match(logName));
				logName = logNames[i + 4];
				assertFalse("Log (" + logName + ") after end date (" +
						lateDateString + ") matched",
						DateLogFilter.match(logName));
			}
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testBothDatesSet() throws Exception{
		
		try{
			DateLogFilter.setStartDate(earlyDateString);
			DateLogFilter.setEndDate(lateDateString);
			for(int i = 0; i < logNames.length; i = i + 5){
				String logName = logNames[i];
				assertFalse("Log (" + logName + ") from before start date (" +
						earlyDateString + ") matched",
						DateLogFilter.match(logName));
				logName = logNames[i + 1];
				assertTrue("Log (" + logName + ") on start date (" +
						earlyDateString + ") did not match",
						DateLogFilter.match(logName));
				logName = logNames[i + 2];
				assertTrue("Log (" + logName + ") between start (" +
						earlyDateString + ") and end (" + lateDateString +
						") dates did not match", DateLogFilter.match(logName));
				logName = logNames[i + 3];
				assertTrue("Log (" + logName + ") on end date (" +
						lateDateString + ") did not match",
						DateLogFilter.match(logName));
				logName = logNames[i + 4];
				assertFalse("Log (" + logName + ") after end date (" +
						lateDateString + ") matched",
						DateLogFilter.match(logName));
			}
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testBothDatesEqual() throws Exception{
		
		try{
			DateLogFilter.setStartDate(earlyDateString);
			DateLogFilter.setEndDate(earlyDateString);
			for(int i = 0; i < logNames.length; i = i + 5){
				String logName = logNames[i];
				assertFalse("Log (" + logName + ") from before dates (" +
						earlyDateString + ") matched",
						DateLogFilter.match(logName));
				logName = logNames[i + 1];
				assertTrue("Log (" + logName + ") on both dates (" +
						earlyDateString + ") did not match",
						DateLogFilter.match(logName));
				logName = logNames[i + 2];
				assertFalse("Log (" + logName + ") after dates (" +
						earlyDateString + ") matched",
						DateLogFilter.match(logName));
			}			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	// Verify that exception is thrown when start and end dates are
	// reversed
	@Test
	public void testDatesReversed(){
		
		try{
			DateLogFilter.setEndDate(null);
			DateLogFilter.setStartDate(lateDateString);
			DateLogFilter.setEndDate(earlyDateString);
			fail("No exception was thrown when start and end dates were " +
					"reversed");
		}catch(IllegalArgumentException e){
			// Desired outcome! =)
		}catch(ReportManagerException e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	// Verify that an exception is thrown when inspecting a file with an
	// unrecognized date format
	// https://www.youtube.com/watch?v=0qIMTA4_YdU
	@Test
	public void testBadDate() throws Exception{
		
		try{
			DateLogFilter.setStartDate(earlyDateString);
			DateLogFilter.setEndDate(lateDateString);
			DateLogFilter.match("log2141001.txt");
			fail("The expected exception was not thrown when trying to " +
					"match a log with a bad date in the filename");
		}catch(ParseException e){
			// Desired behavior
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
}
