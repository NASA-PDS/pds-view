package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.testng.annotations.Test;

public class ContainerTest {

	@Test
	public void testCopy() throws CloneNotSupportedException {
		LabelItemType type = getLabelItemType(1, 1);
		SimpleItem item = new SimpleItem();
		List<LabelItem> contents = new ArrayList<LabelItem>();
		contents.add(item);
		Container container = new Container();
		container.setID("1");
		container.setType(type);
		container.setContext(new QName(type.getElementNamespace(), type.getElementName()).toString());
		container.setContents(contents);

		Container copy = (Container) container.copy();
		assertEquals(copy.getID(), container.getID());
		assertSame(copy.getType(), container.getType());
		assertEquals(copy.getContext(), container.getContext());
		assertNotSame(copy.getContents(), container.getContents());
		assertEquals(container.getContents().size(), 1);
		assertEquals(copy.getContents().size(), container.getContents().size());
		assertNotSame(copy.getContents().get(0), container.getContents().get(0));
	}

	@Test
	public void testIsDeletable() {
		Container container = new Container();
		container.setInsertOption(null);
		assertFalse(container.isDeletable());

		LabelItemType type = getLabelItemType(1, -1);
		List<LabelItemType> types = new ArrayList<LabelItemType>();
		types.add(type);
		
		InsertOption insOption = new InsertOption();
		insOption.setTypes(types);
		insOption.setUsedOccurrences(1);
		container.setInsertOption(insOption);
		container.setType(type);
		assertFalse(container.isDeletable());
		
		insOption.setUsedOccurrences(2);
		assertTrue(container.isDeletable());
		
		type.setMinOccurrences(2);
		type.setWildcard(true);
		assertTrue(container.isDeletable());
	}
	
	private LabelItemType getLabelItemType(int minOccurs, int maxOccurs) {
		LabelItemType type = new LabelItemType();
		type.setElementName("x");
		type.setElementNamespace("ns");
		type.setComplex(true);
		type.setInitialContents(null);
		type.setMinOccurrences(minOccurs);
		type.setMaxOccurrences(maxOccurs);		
		return type;
	}
}
