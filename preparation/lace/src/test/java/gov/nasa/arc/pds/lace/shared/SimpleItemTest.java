package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class SimpleItemTest {

	@Test
	public void testCopy() throws CloneNotSupportedException {
		LabelItemType type = new LabelItemType();
		type.setElementName("x");
		type.setElementNamespace("ns");
		type.setComplex(false);
		type.setInitialContents(null);
		type.setMinOccurrences(1);
		type.setMaxOccurrences(1);

		SimpleItem item = new SimpleItem();
		item.setType(type);
		item.setValue("abc");

		SimpleItem newItem = (SimpleItem) item.copy();
		assertEquals(newItem.getValue(), "abc");
	}

}
