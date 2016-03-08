package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.util.ArrayList;

import org.testng.annotations.Test;

public class LabelItemTypeTest {

	@Test
	public void testEquals() {
		LabelItemType type1 = makeLabelItemType();
		LabelItemType type2 = makeLabelItemType();
		Object o = new Object();

		assertEquals(type1, type2);
		assertNotEquals(type1, null);
		assertNotEquals(type1, o);

		type2.setComplex(false);
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		type2.setElementName("gorp");
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		type2.setElementNamespace("gorp");
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		LabelItem item = new SimpleItem();
		type2.getInitialContents().add(item);
		assertNotEquals(type1, type2);
		type2.setInitialContents(null);
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		type2.setMaxLength(100);
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		type2.setMinLength(100);
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		type2.setMaxOccurrences(2);
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		type2.setMinOccurrences(0);
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		type2.setTypeName("gorp");
		assertNotEquals(type1, type2);

		type2 = makeLabelItemType();
		type2.setTypeNamespace("gorp");
		assertNotEquals(type1, type2);
	}

	@Test
	public void testHashcode() {
		LabelItemType type1 = makeLabelItemType();
		LabelItemType type2 = makeLabelItemType();

		assertEquals(type1.hashCode(), type2.hashCode());

		type2.setComplex(false);
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		type2.setElementName("gorp");
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		type2.setElementNamespace("gorp");
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		LabelItem item = new SimpleItem();
		type2.getInitialContents().add(item);
		assertNotEquals(type1.hashCode(), type2.hashCode());
		type2.setInitialContents(null);
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		type2.setMaxLength(100);
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		type2.setMinLength(100);
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		type2.setMaxOccurrences(2);
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		type2.setMinOccurrences(0);
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		type2.setTypeName("gorp");
		assertNotEquals(type1.hashCode(), type2.hashCode());

		type2 = makeLabelItemType();
		type2.setTypeNamespace("gorp");
		assertNotEquals(type1.hashCode(), type2.hashCode());
	}

	private LabelItemType makeLabelItemType() {
		LabelItemType type = new LabelItemType();
		type.setComplex(true);
		type.setElementName("element");
		type.setElementNamespace("namespace1");
		type.setInitialContents(new ArrayList<LabelItem>());
		type.setMaxLength(0);
		type.setMinLength(0);
		type.setMaxOccurrences(1);
		type.setMinOccurrences(1);
		type.setTypeName("type");
		type.setTypeNamespace("namespace2");

		return type;
	}

}
