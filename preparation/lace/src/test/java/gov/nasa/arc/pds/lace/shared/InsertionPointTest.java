package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import gov.nasa.arc.pds.lace.shared.InsertionPoint.DisplayType;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class InsertionPointTest {

	@Test
	public void testCopy() throws CloneNotSupportedException {
		LabelItemType type = new LabelItemType();
		type.setElementName("x");
		type.setElementNamespace("ns");
		type.setComplex(true);
		type.setInitialContents(null);
		type.setMinOccurrences(1);
		type.setMaxOccurrences(1);

		List<LabelItemType> alternatives = new ArrayList<LabelItemType>();
		alternatives.add(type);

		InsertionPoint insPoint = new InsertionPoint();
		insPoint.setDisplayType(DisplayType.PLUS_BUTTON.getDisplayType());
		insPoint.setAlternatives(alternatives);
		insPoint.setInsertFirst(0);
		insPoint.setInsertLast(0);
		insPoint.setUsedBefore(-1);
		insPoint.setUsedAfter(1);

		InsertionPoint copy = (InsertionPoint) insPoint.copy();
		assertEquals(copy.getDisplayType(), DisplayType.PLUS_BUTTON.getDisplayType());
		assertEquals(copy.getInsertFirst(), 0);
		assertEquals(copy.getInsertLast(), 0);
		assertEquals(copy.getUsedBefore(), -1);
		assertEquals(copy.getUsedAfter(), 1);

		assertNotSame(copy.getAlternatives(), insPoint.getAlternatives());
		assertEquals(copy.getAlternatives().size(), insPoint.getAlternatives().size());
	}

}
