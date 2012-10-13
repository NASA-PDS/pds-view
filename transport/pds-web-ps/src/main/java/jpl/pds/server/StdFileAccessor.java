// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.

package jpl.pds.server;

import java.io.File;

/**
 * Standard file accessor.
 *
 * This accessor locates file by appending any file name onto a root product directory,
 * specified by the system property named by {@link
 * FileQueryHandler#PRODUCT_DIR_PROPERTY}.
 *
 * @author Kelly
 * @version $Revision$
 */
class StdFileAccessor implements FileAccessor {
	/**
	 * Creates a new <code>StdFileAccessor</code> instance.
	 */
	public StdFileAccessor() {}

	public File locateFile(String filename) {
		return new File(new File(System.getProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY, "/")), filename);
	}
}
