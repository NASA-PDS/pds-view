package gov.nasa.pds.report.util;

/**
 * Simple debug class to help track functions at runtime
 * 
 * @author jpadams
 *
 */
public class Debugger {

	
	//TODO Allow this to be set from command-line, maybe use Log4j or something like that instead
	public static boolean debugFlag = true;
	
	/** Simple output method
	 * 
	 *  @param msg
	 */
	public static void debug(String msg) {
		if (debugFlag) {
			System.out.println(msg);
		}
	}
}
