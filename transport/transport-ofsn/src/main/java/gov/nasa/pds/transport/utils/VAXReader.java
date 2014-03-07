// Copyright 1999-2001 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//

package gov.nasa.pds.transport.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class VAXReader {
	// Normally readLine would do the job. But the VAX format uses
	// the following format:
	//   String length byte
	//   Zero byte
	//   String
	//   Zero byte
	//   Repeat until zero string length encountered
	// If standard ascii file then use readline()

	private static final int BUFSIZE = 1024;
	InputStreamReader reader;
	BufferedReader bufreader;
	String input;
	int start;		// index of line-length or line

	public VAXReader (File file) throws IOException {
		char[] buf = new char[BUFSIZE];
		bufreader = null;
		reader =  new InputStreamReader(new FileInputStream(file), "US-ASCII");
		int bytes = reader.read(buf, 0, BUFSIZE);
		if (buf[1] == 0) {	// must vax since file starts out with zero 2nd byte
			input = new String(buf);
			start = 0;
		} else {
			reader.close();
			reader = null;
			int badChars = 0;
			int newLines = 0;
			for (int i=0; i<Math.min(bytes,100); i++) {
				if (buf[i] > 127 || buf[i] < 32 && !Character.isWhitespace(buf[i])) badChars++;
			}
			for (int i=0; i<bytes; i++) {
				if (buf[i] == '\n' || buf[i] == '\r') {
					newLines++;
					break;
				}
			}
			if (badChars > 0 || newLines == 0) {
				return;		// don't read this file
			}
			bufreader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "US-ASCII"));
		}
	}
		
	public String readLine() throws IOException {
		if (reader != null) {	// VAX file
			int end;	// index to zero-byte following line-length or line
			if (start + 2 >= input.length()) {
				// need another buffer
				char[] buf = new char[BUFSIZE];
				if (reader.read(buf) <= 0) {
					System.err.println("VAX format missing 2-byte length");
					return null;
				}
				input = input.substring(start) + new String(buf);
				start = 0;
			}
			end = input.indexOf('\0', start);
			if (end - start == 0) {
				return null;	// end of text section
			}
			if (end - start != 1) {
				throw new IOException("VAX format missing 2-byte length");
			}							
			int len = input.charAt(start);
			if (len > BUFSIZE) return null;		// must be binary info 
			start = end + 1;	// next line
			if (start + len + 2 >= input.length()) {
				// need another buffer
				char[] buf = new char[BUFSIZE];
				if (reader.read(buf, 0, BUFSIZE) <= 0) {
					throw new IOException("VAX format has incomplete line");
				}
				input = input.substring(start) + new String(buf);
				start = 0;
			}
			end = start + len;
			String newLine = input.substring(start,end);
			if (input.charAt(end) == '\0') start = end + 1;	// next 2-byte length
			else start = end;				// no zero byte at end of line
			return newLine;
		} else if (bufreader != null) {		// standard ascii file
			return bufreader.readLine();
		} else {
			return null;
		}
	}

	public void close() throws IOException {
		if (reader != null) reader.close();
		if (bufreader != null) bufreader.close();
	}
}

