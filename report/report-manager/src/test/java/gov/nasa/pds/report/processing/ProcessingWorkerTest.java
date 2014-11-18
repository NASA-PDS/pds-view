package gov.nasa.pds.report.processing;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.rules.ReportManagerTest;

public class ProcessingWorkerTest extends ReportManagerTest{
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Test
	public void testNominal(){
		
		ProcessingWorker worker = new ProcessingWorker("echo test");
		if(worker.execute() != 0){
			fail("The nominal test command failed");
		}
		
	}
	
	@Test
	public void testFailedCommand(){
		
		ProcessingWorker worker = new ProcessingWorker("false");
		int exitCode = worker.execute();
		if(exitCode != 1){
			fail("Command exited with code other than 1: " + exitCode);
		}
		
	}
	
	@Test
	public void testTimeoutCommand(){
		
		int timeoutSeconds = ProcessingWorker.timeout + 5;
		ProcessingWorker worker = new ProcessingWorker("sleep " + timeoutSeconds);
		int exitCode = worker.execute();
		if(exitCode != -1){
			fail("Long command exited with code other than -1: " + exitCode);
		}
		
	}
	
}
