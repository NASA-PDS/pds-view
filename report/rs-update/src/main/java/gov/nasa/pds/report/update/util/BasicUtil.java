package gov.nasa.pds.report.update.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	
}
