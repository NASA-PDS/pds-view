// Copyright 2015, by the California Institute of Technology.
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

package gov.nasa.pds.report.sawmill;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;
import gov.nasa.pds.report.util.SawmillUtil;
import gov.nasa.pds.report.util.Utility;

public class SawmillScriptWriterTest extends ReportManagerTest {
	
	private SawmillScriptWriter writer;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void setUpBeforeClass() throws SawmillException{
		
		System.setProperty(Constants.SAWMILL_SCRIPT_PROP,
				Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE));
		
	}
	
	@Before
	public void setUp() throws IOException{
		FileUtils.forceMkdir(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
		this.writer = new SawmillScriptWriter();
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.forceDelete(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
	}
	
	@Test
	public void testNominal(){
		
		Vector<String> commandList = new Vector<String>();
		commandList.add("sawmill -p test_profile -a ud");
		commandList.add("sawmill -p test_profile -a ect -rn test_report -et true -er -1 -od //test//output/path");
		commandList.add("sawmill -p test_profile -a ect -rn another_report -et true -er -1 -od //test//output/path");
		
		try{
			this.writer.runCommands(commandList);
		}catch(SawmillException e){
			fail(e.getMessage());
		}

		File scriptFile = new File(
				Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE),
				SawmillScriptWriter.SCRIPT_NAME);
		assertTrue("The Sawmill script was not created", scriptFile.exists());
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
			assertEquals("The shebang was not found at the start of the script",
					reader.readLine(), "#!/bin/sh");
			for(String cmd: commandList){
				String foundCommand = reader.readLine();
				assertEquals("No echo command preceding Sawmill command " + cmd,
						"echo Running Sawmill command: " + cmd, foundCommand);
				foundCommand = reader.readLine();
				assertEquals("The command was formatted improperly: " + 
						cmd + " != " + foundCommand,
						cmd, foundCommand);
			}
			String unexpectedOutput = reader.readLine();
			if(unexpectedOutput != null){
				while(unexpectedOutput != null){
					System.out.println("Unexpected output in script: " + unexpectedOutput);
				}
				fail();
			}
			reader.close();
		}catch(Exception e){
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testEmptyCommandList(){
		
		try{
			this.writer.runCommands(null);
			fail("No excpetion was thrown when a null command list was given " +
					"to the Sawmill script writer");
		}catch(SawmillException e){
			// Desired outcome
		}
		
		try{
			Vector<String> commandList = new Vector<String>();
			this.writer.runCommands(commandList);
			commandList.add("-p test_profile -a ud");
			this.writer.runCommands(commandList);
		}catch(Exception e){
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testRunTwice(){
		
		Vector<String> commandList = new Vector<String>();
		commandList.add("echo hello");
		
		try{
			this.writer.runCommands(commandList);
		}catch(Exception e){
			fail(e.getMessage());
		}
		
		try{
			this.writer.runCommands(commandList);
			fail("The Sawmill script writer should throw an error when run " +
					"twice to avoid overwriting the existing script");
		}catch(Exception e){
			// Desired outcome
		}
		
	}

}
