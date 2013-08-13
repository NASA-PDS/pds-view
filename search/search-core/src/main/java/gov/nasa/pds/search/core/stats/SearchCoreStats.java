package gov.nasa.pds.search.core.stats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author jpadams
 *
 */
public class SearchCoreStats {
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
  public static int numErrors = 0;

  public static int numWarnings = 0;
  
  public static int numProducts = 0;
  
  public static Date localStartTime;
  
  public static Date localEndTime;
  
  public static long overallTime = 0;
  
  public static Map<String, Date> runTimesMap = new HashMap<String, Date>();
	
	public static Map<String, Set<String>> missingSlots = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> missingAssociations = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> missingAssociationTargets = new HashMap<String, Set<String>>();
	public static Set<String> badRegistries = new HashSet<String>();
	
	public static void addMissingSlot(String lid, String slotName) {
	    if (missingSlots.containsKey(lid)) {
	    	Set<String> set = missingSlots.get(lid);
	        set.add(slotName);
	        missingSlots.put(lid, set);
	      } else {
	    	  Set<String> set = new HashSet<String>();
	        set.add(slotName);
	        missingSlots.put(lid, set);
	      }
	}

	public static void addMissingAssociation(String lid, String referenceType) {
	    if (missingAssociations.containsKey(lid)) {
	    	Set<String> set = missingAssociations.get(lid);
	        set.add(referenceType);
	        missingAssociations.put(lid, set);
	      } else {
	    	  Set<String> set = new HashSet<String>();
	        set.add(referenceType);
	        missingAssociations.put(lid, set);
	      }
	}
	
	public static void addMissingAssociationTarget(String lid, String associatedLid) {
	    if (missingAssociationTargets.containsKey(lid)) {
	    	Set<String> set = missingAssociationTargets.get(lid);
	        set.add(associatedLid);
	        missingAssociationTargets.put(lid, set);
	      } else {
	    	Set<String> set = new HashSet<String>();
	        set.add(associatedLid);
	        missingAssociationTargets.put(lid, set);
	      }
	}
	
	public static void addBadRegistry(String registryUrl) {
		badRegistries.add(registryUrl);
	}
	
	public static void recordLocalTime(String config) throws ParseException {
		long time = localStartTime.getTime() - new Date().getTime();
		overallTime += time;
		runTimesMap.put(config, format.parse(String.valueOf(time)));
	}
	
	public static Date getOverallTime() throws ParseException {
		return format.parse(String.valueOf(overallTime));
	}
}
