package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InsertOptionTest {

	@Test(dataProvider="minOccurrences")
	public void testMinOccurences(int minOccurrences) {
		InsertOption insertOption = new InsertOption();
		insertOption.setMinOccurrences(minOccurrences);
		assertEquals(insertOption.getMinOccurrences(), minOccurrences);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="minOccurrences")
	private Integer[][] getMinOccurences() {
		return new Integer[][] {
				{0},
				{1}
		};
	}
	
	@Test(dataProvider="maxOccurrences")
	public void testMaxOccurences(int maxOccurrences) {
		InsertOption insertOption = new InsertOption();
		insertOption.setMaxOccurrences(maxOccurrences);
		assertEquals(insertOption.getMaxOccurrences(), maxOccurrences);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="maxOccurrences")
	private Integer[][] getMaxOccurences() {
		return new Integer[][] {
				{1},
				{-1},
				{10}
		};
	}
	
	@Test(dataProvider="usedOccurrences")
	public void testUsedOccurences(int usedOccurrences) {
		InsertOption insertOption = new InsertOption();
		insertOption.setUsedOccurrences(usedOccurrences);
		if (usedOccurrences < 0) {
			
		}
		assertEquals(insertOption.getUsedOccurrences(), usedOccurrences);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="usedOccurrences")
	private Integer[][] getUsedOccurrences() {
		return new Integer[][] {
				{0},
				{1},
				{10}
		};
	}
	
	@Test(dataProvider="illegalMaxOccurrences", expectedExceptions=IllegalArgumentException.class)
	public void testIllegalMaxOccurrences(int illegalMaxOccurrences) {
		InsertOption insertOption = new InsertOption();
		insertOption.setMaxOccurrences(illegalMaxOccurrences);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="illegalMaxOccurrences")
	private Integer[][] getIllegalMaxOccurrences() {
		return new Integer[][] {
				{0},
				{-2}
		};
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testIllegalUsedOccurrences() {
		InsertOption insertOption = new InsertOption();
		insertOption.setUsedOccurrences(-1);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testIllegalMinOccurrences() {
		InsertOption insertOption = new InsertOption();
		insertOption.setMinOccurrences(-1);
	}
	
	@Test
	public void testGetTypes() {
		List<LabelItemType> types = new ArrayList<LabelItemType>();
		LabelItemType type1 = new LabelItemType();
		type1.setComplex(false);
		type1.setElementName("element1");
		types.add(type1);
		
		LabelItemType type2 = new LabelItemType();
		type2.setComplex(false);
		type2.setElementName("element2");
		types.add(type2);
		
		InsertOption insertOption = new InsertOption();
		insertOption.setTypes(types);
		assertEquals(insertOption.getTypes(), types);
		assertEquals(insertOption.getTypes().get(0).getElementName(), "element1");
	}
}

