// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.

package jpl.pds.server;

import java.io.File;
import jpl.eda.util.Utility;
import java.io.IOException;

/**
 * File accessor for file shares in a Windows cluster.
 *
 * @author Kelly
 * @version $Revision$
 */
class NetShareAccessor implements FileAccessor {
	/**
	 * Creates a new <code>NetShareAccessor</code> instance.
	 *
	 * @throws IOException if an error occurs running the mount command.
	 * @throws InterruptedException if an error occurs waiting for the mount command to finish.
	 */
	public NetShareAccessor() throws IOException, InterruptedException {
		productDir = new File(System.getProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY, "/"));
		String cmd = System.getProperty(MOUNT_PROPERTY, DEFAULT_MOUNT_CMD);
		Process p = Runtime.getRuntime().exec(cmd);
		p.getOutputStream().close();
		Utility.redirect(p.getInputStream(), System.out);
		Utility.redirect(p.getErrorStream(), System.err);
		p.waitFor();
	}

	public File locateFile(String filename) {
		return new File(productDir, filename);
	}

	/** Root of all evil---er, um, products. */
	private File productDir;

	/** Name of property that specifies the mount command. */
	public static final String MOUNT_PROPERTY = "jpl.pds.server.NetShareAccessor.mountCmd";

	/** Default mount command. */
	private static final String DEFAULT_MOUNT_CMD = "c:\\winnt\\system32\\cmd.exe /c c:\\pdsoodt\\s01_mount.bat";
}
