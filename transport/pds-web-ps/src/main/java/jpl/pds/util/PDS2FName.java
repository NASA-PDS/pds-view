// Copyright 1999-2001 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//

package jpl.pds.util;

import java.text.MessageFormat;
import java.text.ParsePosition;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * PDS2FName
 *
 * getNames reads an ODL (label file) and breaks out the file names referenced in
 * the file. Tests the existence of the referenced file names. Outputs this
 * file name and those found in the label, removing any duplicates.
 * 
 * getDirectory returns the entire directory and also calls getNames to find any
 * references found in other directories. It then outputs a list of filenames
 * removing any duplicates.
 *
 * Returns: List of file names separated by spaces, null if decoding error or file does not exist.
 *
 * @author G. Crichton
 */

public class PDS2FName  {
	/** Entry for ZipFileHandler and ZipNFileHandler which returns list of files
         *  belonging to the product label file.
	 *
	 * @param labelFile Product label file.
	 * @param filenames List of filenames included in the product.
         */
        public static String getNames(File labelFile, String filenames) {
		return getNames(labelFile, filenames, fileHandler, null, false);
	}

	/** Returns the filenames in the product and optionally listing of the files scanned.
         *
         * @param labelFile Product label file.
         * @param filenames List of filenames already found.
         * @param caller Controls execution code branches.
         * @param list Listing of files scanned if not null.
         * @return List of filenames discovered appended to filenames list.
         */
        public static String getNames(File labelFile, String filenames, int caller, StringBuffer listing, boolean validLabelFile) {
		// labelFile = current file to read
		// filenames = list of filenames from prev call. Always null for ZipFileHandler.
		// caller = fileHandler: return all filenames
		//          dirHandler: return all filenames but skip some error messages
		//          labelHander: return only label file and STRUCTURE references
		//          recursiveCall: search referenced file (don't check for PDS_VERSION_ID or RECORD_TYPE)
		//	    notLabel: just add filename to list, don't search for references
		// listing = copy labelFile & includes to this buffer if not null
		VAXReader reader = null;
		String pathFilename = null;
		String oldFilenames = filenames;
		try {
			// Add label file to list
			if (!labelFile.exists()) {
				System.err.println("PDS2FName err - label file does not exist: " + labelFile.getPath());
				return oldFilenames;
			}
			if (!labelFile.canRead()) {
				System.err.println("PDS2FName err - reading label file: " + labelFile.getPath());
				return oldFilenames;
			}

			pathFilename = labelFile.getAbsolutePath();
			labelFile = new File(pathFilename);

			// add labelFile if not in list
			if (filenames == null) {
				filenames = pathFilename;
				System.err.println("+" + labelFile.getName() + ": " + labelFile.length());
			}
			else if (filenames.indexOf(pathFilename) < 0) {
				filenames += " " + pathFilename;
				System.err.println("+" + labelFile.getName() + ": " + labelFile.length());
			}
			if ((caller & notLabel) == notLabel) return filenames;	// don't search, just append filename


			String mainDirName =  labelFile.getParent();

			// Search labelFile for referenced files
			String line = null;
			MessageFormat mf = null;
			reader = new VAXReader(labelFile);
			int lineCnt = 0;
			//boolean validLabelFile = false;
			int listingLength = 0;
			while ((line = reader.readLine()) != null) {
				lineCnt++;
				if (listing != null) {
					listingLength = listing.length();
					listing.append(line+NL);
				}
				line = deleteSpace(line);
				Object[] objs = null;
				if (line.startsWith("\000")) {
					if (listing != null) listing.setLength(listingLength);
					break;	// end of VAX header
				}
				if (line.equals("END")) break;		// end of header
				if (!validLabelFile && lineCnt>100) break;	// quiting after 100 lines
				if (line.startsWith("PDS_VERSION_ID") || line.startsWith("RECORD_TYPE")) {
					validLabelFile = true;
					continue;
				}
				if (line.startsWith("^")) {
					// Needs a "=" to be file refernce
					if (line.indexOf('=') < 0) continue;	// ignore this line

					// ^var = number (no file name)
					if (objs == null) {
						mf = new MessageFormat("^{0}={1,number}");
						objs = mf.parse(line, new ParsePosition(0));
						if (objs != null) continue;	// no filename
					}

					// ^var = ("filename"...
					if (objs == null) {
						mf = new MessageFormat("^{0}=(\"{1}\"");
						objs = mf.parse(line, new ParsePosition(0));
					}

					// ^var = ('filename'...
					if (objs == null) {
						mf = new MessageFormat("^{0}=(''{1}''");
						objs = mf.parse(line, new ParsePosition(0));
					}

					// ^var = "filename"
					if (objs == null) {
						mf = new MessageFormat("^{0}=\"{1}\"");
						objs = mf.parse(line, new ParsePosition(0));
					}

					// ^var = 'filename'
					if (objs == null) {
						mf = new MessageFormat("^{0}=''{1}''");
						objs = mf.parse(line, new ParsePosition(0));
					}
				}
				else if (line.startsWith("FILE_NAME=")
					|| line.startsWith("IMAGE=")
					|| line.startsWith("HEADER=")
					|| line.startsWith("DOCUMENT_NAME=")) {

					mf = new MessageFormat("{0}={1,number}");
					objs = mf.parse(line, new ParsePosition(0));
					if (objs != null) continue;	// no filename

					// FILE_NAME/IMAGE/HEADER/DOCUMENT_NAME = number
					mf = new MessageFormat("{0}=\"{1,number}\"");
					objs = mf.parse(line, new ParsePosition(0));
					if (objs != null) continue;	// not file name

					// FILE_NAME/IMAGE/HEADER/DOCUMENT_NAME = "filename"
					mf = new MessageFormat("{0}=\"{1}\"");
					objs = mf.parse(line, new ParsePosition(0));

					// FILE_NAME/IMAGE/HEADER/DOCUMENT_NAME = 'filename'
					if (objs == null) {
						mf = new MessageFormat("{0}=''{1}''");
						objs = mf.parse(line, new ParsePosition(0));
					}

					// FILE_NAME/IMAGE/HEADER/DOCUMENT_NAME = filename
					if (objs == null) {
						mf = new MessageFormat("{0}={1}");
						objs = mf.parse(line, new ParsePosition(0));
					}
				}
				else continue;

				if (objs == null) {	// None of the formats match
					// Skip msg since getDirectory() may pass a non labelFile
					if (!validLabelFile && (caller & dirHandler) == dirHandler) return oldFilenames;
					System.err.println("PDS2FName err - File name format error in "
						+ labelFile.getPath());
					System.err.println("Line:" + line);
					return oldFilenames;
				}

				// find referenced file
				String refFilename = (String)objs[1];
				String altDirName = (String)objs[0];
				String ucAltDirName = altDirName.toUpperCase();

				if (ucAltDirName.endsWith("STRUCTURE"))
					altDirName = "LABEL";
				else if ((caller & labelHandler) == labelHandler) 
			    		continue;			// only want STRUCTURE
				else if (ucAltDirName.endsWith("STRUCTURE") ||
					ucAltDirName.equals("DATA_SET_MAP_PROJECTION_CATALOG"))
					altDirName = "LABEL";
				else if (ucAltDirName.endsWith("DESCRIPTION") ||
					ucAltDirName.equals("DOCUMENT_NAME"))
					altDirName = "DOCUMENT";
				else if (ucAltDirName.equals("DATA_SET_MAP_PROJECTION") ||
					ucAltDirName.endsWith("CATALOG"))
					altDirName = "CATALOG";
				pathFilename = findPath(refFilename, mainDirName, altDirName);

				// If file does not exist, continue without returning this filename
				if (pathFilename == null) {
					System.err.println("-" + refFilename + "   labelFile: " + labelFile);
				} else {
					if (filenames.indexOf(pathFilename) >= 0) continue;   // already in list
					if (listing != null) listing.setLength(listingLength);
					// look for more files only if label file or label reference
					if (ucAltDirName.endsWith("STRUCTURE") |
					    ucAltDirName.endsWith("DESCRIPTION") ||
					    ucAltDirName.endsWith("NOTE") ||
					    ucAltDirName.endsWith("TEXT") ||
					    ucAltDirName.endsWith("LABEL")) {
					    filenames = getNames(new File(pathFilename), filenames, caller|recursiveCall, listing, validLabelFile);
                    }
					else {
					    filenames = getNames(new File(pathFilename), filenames, caller|notLabel, listing, validLabelFile);
                    }
				}
			}
			if (validLabelFile || (caller & recursiveCall) == recursiveCall) return filenames;
			if ((caller & dirHandler) != dirHandler) 		// skip msg since getDirectory() may pass a non labelFile
				System.err.println("PDS2FName err: no PDS Version or Record Type");
			return oldFilenames;
		} catch (IOException e) {
			System.err.println("PDS2FName err reading file: " + labelFile.getPath());
			System.err.println("Error " + e);
			return oldFilenames;
		} finally {
			try { if (reader != null) reader.close();
			} catch (IOException  e) {}
		}
	}

	// Delete spaces on both sides of "=" character
	static String deleteSpace(String line) {
		String out = "";
		char c;
		for (int i=0; i<line.length(); i++) {
			c = line.charAt(i);
			if (c == ' ' || c == '\t') continue;
			out += c;
		}
		return out;
	}

	static String findPath(String filename, String mainDir, String altDir) {
		// Try main dir first
		String fname = mainDir + File.separatorChar + filename;
		File f = new File(fname);
		// Try given name, lowercase name, and uppercase name
		if (f.canRead()) return f.getAbsolutePath();
		fname = mainDir + File.separatorChar + filename.toLowerCase();
		f = new File(fname);
		if (f.canRead()) return f.getAbsolutePath();
		fname = mainDir + File.separatorChar + filename.toUpperCase();
		f = new File(fname);
		if (f.canRead()) return f.getAbsolutePath();
			

		// If altDir specified, search for altDir and then file
		if (altDir == null || altDir.equals("")) return null;	// Search failed
		// start looking for altDir/filename in main Directory then look up directory tree
		File tree = new File(mainDir);
		while (tree != null) {
			String path = tree.getPath();
			fname = path + File.separatorChar + altDir + File.separatorChar + filename;
			f = new File(fname);
			if (f.canRead()) return fname;
			fname = path + File.separatorChar + altDir.toLowerCase()
				+ File.separatorChar + filename.toLowerCase();
			f = new File(fname);
			if (f.canRead()) return fname;
			fname = path + File.separatorChar + altDir.toUpperCase()
				+ File.separatorChar + filename.toUpperCase();
			f = new File(fname);
			if (f.canRead()) return fname;
			tree = tree.getParentFile();	// up one directory
		}
		return null;
	}

	// add filename to list
	static String addFilename(File f, String filenames) {
		String pathFilename = f.getAbsolutePath();
		// add filename if not in list
		if (filenames == null) {
			filenames = pathFilename;
			System.err.println("+" + f.getName() + ": " + f.length());
		}
		else if (filenames.indexOf(pathFilename) < 0) {
			filenames += " " + pathFilename;
			System.err.println("+" + f.getName() + ": " + f.length());
		}
		return filenames;
	}

	// Returns all filenames in the directory and all the filenames referenced in TES files
	// found in the directory that meet the following critera:
	// 1) Decompose the input file name to get the nnnnn sequence. e.g. ATMnnnnn -> nnnnn
	// 2) Get list of all files in the same directory and check whether the filename matches
        //    AAAnnnnn.TAB or AAAnnnnn.VAR or just VAR.LBL where nnnnn matches the TESFile
        //    input parameter.
	//    e.g. nnnnn -> ATMnnnnn.TAB, BOLnnnnn.TAB, etc.
	// 3) For those files that match, read the file, verify that it has a
	//    PDS label, and extract all referenced files. (There could be .FMT files)
	// 4) Do a ZIPN equivalent for for all of the matching files and referenced files.
	// 5) return resulting list of filenames for zipping.
        public static String getTESNames(File TESFile, String filenames) {
		// filenames = list of filenames initialized to null
		String fn = TESFile.getName();
		if (fn.length() != 8) {
			System.err.println("PDS2FName err in filename " + fn + ", format is AAAnnnnn");
			return null;
		}
		String pattern = fn.substring(3, 8);
		for (int i=0; i<5; i++) {
			if (pattern.charAt(i) < '0' || pattern.charAt(i) > '9') {
				System.err.println("PDS2FName err in filename " + fn + ", format is AAAnnnnn");
				return null;
			}
		}

		File[] fileList = TESFile.getParentFile().listFiles();
		if (fileList == null) {
			System.err.println("PDS2FName err: invalid directory in getTesNames");
			return null;
		}
		for (int i=0; i<fileList.length; i++) {
			fn = fileList[i].getName();
			// include all the *nnnnn.TAB files and references
			if (fn.toUpperCase().endsWith(pattern+".TAB"))
				filenames = getNames(fileList[i], filenames);
			
			// include all the *nnnnn.VAR files
			if (fn.toUpperCase().endsWith(pattern+".VAR"))
				filenames = addFilename(fileList[i], filenames);

			// include VAR.LBL file if exists
			if (fileList[i].getName().toUpperCase().equals("VAR.LBL"))
				filenames = addFilename(fileList[i], filenames);
		}
		return filenames;
	}



	// Returns all filenames in the directory and all the filenames referenced in label files
	// found in the directory.  If a subdirectory is found the process is repeated for
	// that subdirectory.
        public static String getDirectory(File dirFile, String filenames) {
		// filenames = list of filenames from prev call.
		if (!dirFile.isDirectory()) {
			System.err.println("PDS2FName err in getDirectory(): " + dirFile.getPath() + " not a directory");
			return null;
		}
		File[] fileList = dirFile.listFiles();
		for(int i=0; i<fileList.length; i++) {
			// Get all of the names for this directory
			if (fileList[i].isDirectory())
				filenames = getDirectory(fileList[i], filenames);
			else {
				// Add this file to filenames list
				filenames = addFilename(fileList[i], filenames);

				// See if this file references other files (maybe it is a label file)
				filenames = getNames(fileList[i], filenames, dirHandler, null, false);
			}
		}
		return filenames;
	}

        public static void main(String[] argv) {
                System.out.println("PDS2FName Program");
		if (argv.length != 1) {
			System.out.println("PDS2FName Program scans ODL for file names");
			System.out.println("Usage: filename/dirname with path");
			System.exit(1);
		}

		File file = new File(argv[0]);
		String result = null;
		if (file.isDirectory()) result = PDS2FName.getDirectory(file, result);
		//else if (file.getName().endsWith("TAB")) result = PDS2FName.getTESNames(file,result);
		else result = PDS2FName.getNames(file, result);
		System.out.println("Results for " + file.getPath());
		if (result == null) result = "";
		for (StringTokenizer tokens = new StringTokenizer(result); tokens.hasMoreTokens();) 
			System.out.println(tokens.nextToken());
                System.exit(0);
        }

	public static final int fileHandler = 1;
	public static final int dirHandler = 2;
	public static final int labelHandler = 4;
	public static final int recursiveCall = 8;
	public static final int notLabel = 16;
	private static final String NL = "\r\n";
}

