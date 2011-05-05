package gov.nasa.pds.report.transfer.util;

import java.io.File;

public class FileUtil {

	public FileUtil() {}
	
	/**
	 * Creates directory structure to place copied logs
	 * @param path
	 * @return
	 */
	public boolean createDirStruct(String destPath) {
		File dirStruct = new File(destPath);
		if (!dirStruct.exists()) {
			return dirStruct.mkdirs();
		} else {
			return true;
		}
	}
	
}
