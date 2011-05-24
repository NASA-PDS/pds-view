package gov.nasa.pds.report.update.util;

import java.io.File;

public class FileUtil {

	/**
	 * Creates directory structure to place copied logs
	 * 
	 * @param path
	 * @return
	 */
	public static boolean createDirStruct(String destPath) {
		File dirStruct = new File(destPath);
		if (!dirStruct.exists()) {
			return dirStruct.mkdirs();
		} else {
			return true;
		}
	}

}
