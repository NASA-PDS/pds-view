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

import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.rules.PDSTest;

public class DateLogFilterTest extends PDSTest{
	
	private static final String pattern = "log-|yyyy-MM-dd|.txt";
	private static final String[] logNames = {"log-2014-08-31.txt",
			"log-2014-09-01.txt", "log-2014-09-15.txt", "log-2014-09-30.txt",
			"log-2014-10-01.txt"};
	private static final String earlyDateString = "09/01/2014";
	private static final String lateDateString = "09/30/2014";
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Test
	public void testNoDatesSet(){
		
		DateLogFilter.setPattern(pattern);
		DateLogFilter.setStartDate(null);
		DateLogFilter.setEndDate(null);
		try{
			assertTrue("Filename did not match with no start or end date set",
					DateLogFilter.match(logNames[0]));
		}catch(Exception e){
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testStartDateOnly(){
		
		DateLogFilter.setPattern(pattern);
		DateLogFilter.setStartDate(earlyDateString);
		DateLogFilter.setEndDate(null);
		try{
			assertFalse("Log from before start date matched",
					DateLogFilter.match(logNames[0]));
			assertTrue("Log on start date did not match",
					DateLogFilter.match(logNames[1]));
			assertTrue("Log after start date did not match",
					DateLogFilter.match(logNames[2]));
		}catch(Exception e){
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testEndDateOnly(){
		
		DateLogFilter.setPattern(pattern);
		DateLogFilter.setEndDate(lateDateString);
		DateLogFilter.setStartDate(null);
		try{
			assertTrue("Log from before end date did not match",
					DateLogFilter.match(logNames[2]));
			assertTrue("Log on end date did not match",
					DateLogFilter.match(logNames[3]));
			assertFalse("Log after end date matched",
					DateLogFilter.match(logNames[4]));
		}catch(Exception e){
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testBothDatesSet(){
		
		DateLogFilter.setPattern(pattern);
		DateLogFilter.setStartDate(earlyDateString);
		DateLogFilter.setEndDate(lateDateString);
		try{
			assertFalse("Log from before start date matched",
					DateLogFilter.match(logNames[0]));
			assertTrue("Log on start date did not match",
					DateLogFilter.match(logNames[1]));
			assertTrue("Log between start and end dates did not match",
					DateLogFilter.match(logNames[2]));
			assertTrue("Log on end date did not match",
					DateLogFilter.match(logNames[3]));
			assertFalse("Log after end date matched",
					DateLogFilter.match(logNames[4]));
		}catch(Exception e){
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testBothDatesEqual(){
		
		DateLogFilter.setPattern(pattern);
		DateLogFilter.setStartDate(earlyDateString);
		DateLogFilter.setEndDate(earlyDateString);
		try{
			assertFalse("Log from before dates matched",
					DateLogFilter.match(logNames[0]));
			assertTrue("Log on both dates did not match",
					DateLogFilter.match(logNames[1]));
			assertFalse("Log after dates matched",
					DateLogFilter.match(logNames[2]));
		}catch(Exception e){
			fail(e.getMessage());
		}
		
	}
	
	// Verify that exception is thrown when attempting to match filenames
	// to filter without a provided pattern
	@Test
	public void testMatchWithoutPatternSet(){
		
		DateLogFilter.setPattern(null);
		try{
			DateLogFilter.match(logNames[0]);
			fail("No exception was thrown when attempting to match a " +
					"filename to the DateLogFilter with no provided " +
					"filename pattern");
		}catch(ReportManagerException e){
			// Desired outcome! =)
		}catch(ParseException e){
			fail(e.getMessage());
		}
		
	}
	
	// Verify that exception is thrown when start and end dates are
	// reversed
	@Test
	public void testDatesReversed(){
		
		DateLogFilter.setEndDate(null);
		DateLogFilter.setStartDate(lateDateString);
		try{
			DateLogFilter.setEndDate(earlyDateString);
			fail("No exception was thrown when start and end dates were " +
					"reversed");
		}catch(IllegalArgumentException e){
			// Desired outcome! =)
		}
		
	}
	
	// Verify behavior when the pattern doesn't have anything after the date
	@Test
	public void testNoPostDateSubstring(){
		
		DateLogFilter.setStartDate(earlyDateString);
		DateLogFilter.setEndDate(lateDateString);
		DateLogFilter.setPattern("log-|yyyy-MM-dd|");
		try{
			assertTrue("Log with no post-date substring did not match",
					DateLogFilter.match("log-2014-09-15"));
		}catch(Exception e){
			fail(e.getMessage());
		}
		
	}
	
}