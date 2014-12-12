package gov.nasa.pds.report.util;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.rules.ReportManagerTest;

public class CommandLineWorkerTest extends ReportManagerTest{
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Test
	public void testNominal(){
		
		CommandLineWorker worker = new CommandLineWorker("echo test");
		if(worker.execute() != 0){
			fail("The nominal test command failed");
		}
		
	}
	
	@Test
	public void testFailedCommand(){
		
		CommandLineWorker worker = new CommandLineWorker("false");
		int exitCode = worker.execute();
		if(exitCode != 1){
			fail("Command exited with code other than 1: " + exitCode);
		}
		
	}
	
	@Test
	public void testTimeoutCommand(){
		
		System.setProperty(Constants.COMMANDLINE_TIMEOUT_PROP, "10");
		CommandLineWorker worker = new CommandLineWorker("sleep 15");
		// TODO: Make sure that this test doesn't run forever
		int exitCode = worker.execute();
		if(exitCode != -1){
			fail("Long command exited with code other than -1: " + exitCode);
		}
		
	}
	
}
