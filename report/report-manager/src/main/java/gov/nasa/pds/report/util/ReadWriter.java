//	Copyright 2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//
package gov.nasa.pds.report.util;

import gov.nasa.pds.report.ReportManagerException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * A convenience class to create and manage a file reader and writer pair.
 * This is useful for classes that need to input, transform, and output large
 * files, such as Processors.
 * 
 * This implementation is helpful because it allows for the modification of
 * such large files without needing to keep the entire file in memory.  The
 * down side, however, is that if this class is used as intended, an instance
 * won't allow access to the contents of the entire file at once, as it
 * operates line-by-line to reduce memory demands.
 * 
 * @author resneck
 */
public class ReadWriter{
	
	private static Logger log = Logger.getLogger(ReadWriter.class.getName());
	
	private BufferedReader reader;
	private File readerFile;
	private PrintWriter writer;
	private File writerFile;
	private int lineNum = 0;
	
	public ReadWriter(File readerFile, File writerFile) throws ReportManagerException{
		
		if(readerFile == null || writerFile == null){
			throw new ReportManagerException("Null read and/or write files " +
					"provided");
		}
		
		this.readerFile = readerFile;
		this.writerFile = writerFile;
		
		try{
			this.reader = new BufferedReader(new FileReader(readerFile));
		}catch(FileNotFoundException e){
			throw new ReportManagerException("The input file could not be " +
					"found at " + this.readerFile.getAbsolutePath() + ": " + 
					e.getMessage());
		}
		
		try{
			writer = new PrintWriter(writerFile);
		}catch(FileNotFoundException e){
			this.close();
			throw new ReportManagerException("The output file could not be " +
					"found at " + this.writerFile.getAbsolutePath() + ": " +
					e.getMessage());
		}
		
	}
	
	public String readLine() throws ReportManagerException{
		
		if(reader == null){
			throw new ReportManagerException("The reader to " +
					this.readerFile.getAbsolutePath() + " was not " +
					"initialized before use.");
		}
		
		try{
			String line = this.reader.readLine();
			if(line != null){
				this.lineNum++;
			}
			return line;
		}catch(IOException e){
			throw new ReportManagerException("An IOException occurred while " +
					"reading line " + this.lineNum + " from file " +
					this.readerFile.getAbsolutePath() + ": " + e.getMessage());
		}
		
	}
	
	public void writeLine(String line){
		
		this.writer.println(line);
		this.writer.flush();
		
	}
	
	public void close(){
		
		if(reader != null){
			try{
				reader.close();
			}catch(IOException e){
				log.warning("An error occurred while closing the reader to " +
						this.readerFile.getAbsolutePath() + ": " +
						e.getMessage());
			}
		}
		
		if(writer != null){
			writer.close();
		}
		
	}
	
	public void deleteOutput(){
		
		File badFile = new File(this.writerFile.getAbsolutePath());
		if(!badFile.exists()){
			return;
		}
		
		try{
			FileUtils.forceDelete(badFile);
		}catch(IOException e){
			log.warning("An error occurred while cleaning up a " +
					"potentially erroneous output file " +
					this.writerFile.getAbsolutePath() + ": " + e.getMessage());
		}
		
	}
	
	public int getLineNum(){
		
		return this.lineNum;
		
	}
	
}