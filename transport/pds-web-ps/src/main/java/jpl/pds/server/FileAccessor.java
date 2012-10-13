// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.

package jpl.pds.server;

import java.io.File;

/**
 * A way to access files.
 *
 * @author Kelly
 * @version $Revision$
 */
public interface FileAccessor {
	/**
	 * Given a filename, return the appropriate File object for it.
	 *
	 * @param filename Name of file.
	 * @return File of file.
	 * @throws ServerException if an error occurs.
	 */
	File locateFile(String filename) throws ServerException;
}
