// Copyright 2006-2010, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.crawler;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that can hold a list of files and directories. Used to store files
 * and directories found when traversing a directory.
 * 
 * @author mcayanan
 *
 */
public class FileList<T> {
	
	private List<T> files;
	private List<T> directories;
	
	public FileList() {
		files = new ArrayList<T>();
		directories = new ArrayList<T>();
	}
	
	public void add(FileList<T> list) {
		files.addAll(list.getFiles());
		directories.addAll(list.getDirs());
	}
	
	/**
	 * Adds a single object to the end of the file list
	 * @param file a single file to add
	 */
	public void addFile(T file) {
		files.add(file);
	}
	
	/**
	 * Adds a list of objects to the end of the file list
	 * @param list a list of files to add
	 */
	public void addFiles(List<T> list) {
		files.addAll(list);
	}
	
	/**
	 * Adds a single object to the end of the directory list
	 * @param directory a single directory to add
	 */
	public void addDir(T directory) {
		directories.add(directory);
	}
	
	/**
	 * Adds a list of objects to the end of the directory list
	 * @param list a list of directories to add
	 */
	public void addDirs(List<T> list) {
		directories.addAll(list);
	}
	
	/**
	 * Gets files that were added to the list
	 * @return a list of files
	 */
	public List<T> getFiles() {
		return files;
	}
	
	/**
	 * Gets directories that were added to the list
	 * @return a list of directories
	 */
	public List<T> getDirs() {
		return directories;
	}
}
