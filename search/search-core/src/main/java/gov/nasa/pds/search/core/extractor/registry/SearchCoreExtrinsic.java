package gov.nasa.pds.search.core.extractor.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.stats.SearchCoreStats;

/**
 * Class leverages the Decorator Pattern by inheriting the functions of
 * {@link ExtrinsicObject} through the {@link ExtrinsicObjectDecorator} class.
 * Currently no methods are overridden, however, this provides that flexibility
 * that should be a part of extending a class.
 * 
 * @author jpadams
 *
 */
public class SearchCoreExtrinsic extends ExtrinsicObjectDecorator {
	private static final long serialVersionUID = 2780915859620067707L;

	/** The value to be returned when a slot is not found **/
	public static final String MISSING_SLOT_VALUE = "";
	
	public SearchCoreExtrinsic(ExtrinsicObject extObject) {
		super(extObject);
	}
	
	/**
	 * Returns a list of Strings that pertain to a particular slot
	 * 
	 * @param slotName
	 * @return
	 * @throws SearchCoreFatalException
	 */
	public List<String> getSlotValues(String slotName) throws SearchCoreFatalException {		
		Slot slot = super.getSlot(slotName.trim());
		RegistryAttributes ra;
		
		if (slot != null) {
			return slot.getValues();
		} else if ((ra = RegistryAttributes.get(slotName)) != null) {
			return Arrays.asList(ra.getValueFromExtrinsic(super.decoratedExtrinsic));
		} else {
			SearchCoreStats.addMissingSlot(super.getLid(), slotName);
			return Arrays.asList(MISSING_SLOT_VALUE);
		}
	}
	
	/**
	 * Utility method to convert list of ExtrinsicObjects to SearchCoreExtrinsic objects
	 * 
	 * @param extObjList
	 * @return
	 */
	public static List<SearchCoreExtrinsic> asSearchCoreExtrinsics(List<ExtrinsicObject> extObjList) {
		List<SearchCoreExtrinsic> sceList = new ArrayList<SearchCoreExtrinsic>();
		for (ExtrinsicObject extObj : extObjList) {
			sceList.add(new SearchCoreExtrinsic(extObj));
		}
		return sceList;
	}
	
}
