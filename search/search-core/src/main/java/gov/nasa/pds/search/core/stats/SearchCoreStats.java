package gov.nasa.pds.search.core.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author jpadams
 * 
 */
public class SearchCoreStats {
	public static int numErrors = 0;

	public static int numWarnings = 0;

	public static int numProducts = 0;

	public static int assocCacheHits = 0;

	public static long localStart = System.currentTimeMillis();

	public static long overallTime = 0;
	
	public static boolean solrPostSuccess = false;

	public static Map<String, String> runTimesMap = new HashMap<String, String>();

	public static Map<String, Set<String>> missingSlots = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> missingAssociations = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> missingAssociationTargets = new HashMap<String, Set<String>>();
	public static Set<String> badRegistries = new HashSet<String>();
	
	public static void addMissingSlot(String lid, String slotName) {
		if (missingSlots.containsKey(slotName)) {
			Set<String> set = missingSlots.get(slotName);
			set.add(lid);
			missingSlots.put(slotName, set);
		} else {
			Set<String> set = new HashSet<String>();
			set.add(lid);
			missingSlots.put(slotName, set);
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

	public static void addMissingAssociationTarget(String lid,
			String associatedLid) {
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

	public static void recordLocalTime(String config) {
		long elapsed = System.currentTimeMillis() - localStart;
		overallTime += elapsed;

		runTimesMap.put(config, formatTime(elapsed));
	}

	public static String getOverallTime() {
		return formatTime(overallTime);
	}
	
	public static void resetStart() {
		localStart = System.currentTimeMillis();
	}
	
	private static String formatTime(long ms) {
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        ms -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(ms);
        ms -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);

        StringBuilder sb = new StringBuilder(64);
        sb.append(hours);
        sb.append(" h, ");
        sb.append(minutes);
        sb.append(" m, ");
        sb.append(seconds);
        sb.append(" s");
        
        return sb.toString();
	}
}
