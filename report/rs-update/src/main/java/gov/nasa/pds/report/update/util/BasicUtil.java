package gov.nasa.pds.report.update.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BasicUtil {

    /**
     * Get the current date time.
     *
     * @return A date time.
     */
    public static String getDateTime() {
        SimpleDateFormat df = new SimpleDateFormat(
        "EEE, MMM dd yyyy 'at' hh:mm:ss a");
        Date date = Calendar.getInstance().getTime();
        return df.format(date);
    }
    
    public static String getFileDate() {
        SimpleDateFormat df = new SimpleDateFormat(
        "yyyyMMdd");
        Date date = Calendar.getInstance().getTime();
        return df.format(date);
    }
    
	public final static List<String> getLocalFileList(String path) {
		File dir = new File(path);
		return Arrays.asList(dir.list());
	}
}
