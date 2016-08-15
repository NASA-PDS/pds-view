package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import gov.nasa.arc.pds.lace.shared.InsertionPoint.DisplayType;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class InsertionPointTest {

	@Test 
	public void testComplete() {
		
	}
	
	@Test 
	public void testRequired() {
		
	}
	
	@Test 
	public void testDisplayType() {
		
	}
	
	@Test 
	public void testAlternatives() {
		
	}
	
	@Test
	public void testCopy() throws CloneNotSupportedException {
		LabelItemType type = new LabelItemType();
		type.setElementName("x");
		type.setElementNamespace("ns");
		type.setComplex(true);
		type.setInitialContents(null);
		type.setMinOccurrences(1);
		type.setMaxOccurrences(-1);

		List<LabelItemType> types = new ArrayList<LabelItemType>();
		types.add(type);
		
		InsertOption insertOption = new InsertOption();
		insertOption.setTypes(types);
		insertOption.setMinOccurrences(1);
		insertOption.setMaxOccurrences(-1);
		insertOption.setUsedOccurrences(1);
		
		List<InsertOption> alternatives = new ArrayList<InsertOption>();
		alternatives.add(insertOption);

		InsertionPoint insPoint = new InsertionPoint();
		insPoint.setDisplayType(DisplayType.PLUS_BUTTON.getDisplayType());
		insPoint.setAlternatives(alternatives);

		InsertionPoint copy = (InsertionPoint) insPoint.copy();
		assertEquals(copy.getDisplayType(), DisplayType.PLUS_BUTTON.getDisplayType());
		assertNotSame(copy.getAlternatives(), insPoint.getAlternatives());
		assertEquals(copy.getAlternatives().size(), insPoint.getAlternatives().size());
	}

}
