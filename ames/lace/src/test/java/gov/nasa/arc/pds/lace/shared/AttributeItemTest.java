package gov.nasa.arc.pds.lace.shared;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class AttributeItemTest {

	@Test
	public void testCreation() {
		AttributeItem item = new AttributeItem();
		item.setID("1");
		item.setType(getItemType());
		item.setValue("abc");
		
		assertEquals(item.getID(), "1");
		assertEquals(item.getValue(), "abc");
		assertFalse(item.isDeletable());
		assertFalse(item.isMultiline());
	}
	
	private LabelItemType getItemType() {
		LabelItemType type = new LabelItemType();
		type.setElementName("x");
		type.setElementNamespace("ns");
		type.setComplex(false);
		type.setInitialAttributes(null);
		type.setInitialContents(null);
		type.setMinOccurrences(1);
		type.setMaxOccurrences(1);
		return type;
	}
	
	
}
