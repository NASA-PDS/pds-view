package gov.nasa.pds.search.core.stats;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.list.SetUniqueList;

/**
 * 
 * @author jpadams
 *
 */
public class SearchCoreStats {
  public static int numErrors = 0;

  public static int numWarnings = 0;
	
	public static Map<String, Set<String>> missingSlots = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> missingAssociations = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> missingAssociationTargets = new HashMap<String, Set<String>>();
	
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
}
