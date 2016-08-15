package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class SimpleItemTest {

	@Test
	public void testCopy() {
		LabelItemType type = getItemType();
		SimpleItem item = new SimpleItem();
		item.setID("1");
		item.setType(type);
		item.setValue("abc");

		SimpleItem newItem = item.copy();
		assertEquals(newItem.getID(), item.getID());
		assertEquals(newItem.getValue(), "abc");
		assertEquals(newItem.getAttributes().size(), 0);
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
	
	@Test
	public void testCopyAttributes() {
		LabelItemType type = getItemType();
		SimpleItem item = new SimpleItem();
		item.setType(type);
		item.setValue("abc");
		
		AttributeItem attribute = new AttributeItem();
		item.setType(getItemType());
		item.addAttribute(attribute);
		
		SimpleItem newItem = item.copy();
		assertEquals(newItem.getValue(), "abc");
		assertEquals(newItem.getAttributes().size(), 1);
	}

	@Test
	public void testIsDeletable() {
		SimpleItem item = new SimpleItem();
		item.setInsertOption(null);
		assertFalse(item.isDeletable());
		
		LabelItemType type = getItemType();
		List<LabelItemType> types = new ArrayList<LabelItemType>();
		types.add(type);
		
		InsertOption insOption = new InsertOption();
		insOption.setTypes(types);
		insOption.setUsedOccurrences(1);
		item.setInsertOption(insOption);
		item.setType(type);
		assertFalse(item.isDeletable());
		
		insOption.setUsedOccurrences(2);
		assertTrue(item.isDeletable());
		
		type.setMinOccurrences(2);
		assertFalse(item.isDeletable());
		
		type.setWildcard(true);
		assertTrue(item.isDeletable());
	}
	
	@Test
	public void testIsMultiline() {	
		LabelItemType type = new LabelItemType();
		type.setElementName("simple");		
		type.setComplex(true);
				
		SimpleItem item = new SimpleItem();
		item.setType(type);
		
		assertFalse(item.isMultiline());
		
		type.setMaxLength(0);
		assertFalse(item.isMultiline());
		
		type.setMaxLength(255);
		assertFalse(item.isMultiline());
		
		type.setMaxLength(-1);
		type.setWhitespacePreserved(false);
		assertFalse(item.isMultiline());
		
		type.setWhitespacePreserved(true);
		assertTrue(item.isMultiline());
		
		List<String> patterns = new ArrayList<String>();
		patterns.add("pattern");
		type.setPatterns(patterns);		
		assertFalse(item.isMultiline());
		
		type.getPatterns().add("\\p{IsBasicLatin}*");
		assertTrue(item.isMultiline());
		
		type.setValidValues(new String[] {"enum"});		
		assertFalse(item.isMultiline());
	}
}
