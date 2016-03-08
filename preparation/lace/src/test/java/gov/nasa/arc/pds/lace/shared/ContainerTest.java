package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class ContainerTest {

	@Test
	public void testCopy() throws CloneNotSupportedException {
		LabelItemType type = new LabelItemType();
		type.setElementName("x");
		type.setElementNamespace("ns");
		type.setComplex(true);
		type.setInitialContents(null);
		type.setMinOccurrences(1);
		type.setMaxOccurrences(1);

		SimpleItem item = new SimpleItem();
		List<LabelItem> contents = new ArrayList<LabelItem>();
		contents.add(item);
		Container container = new Container();
		container.setType(type);
		container.setContents(contents);

		Container copy = (Container) container.copy();
		assertSame(copy.getType(), container.getType());
		assertNotSame(copy.getContents(), container.getContents());
		assertEquals(container.getContents().size(), 1);
		assertEquals(copy.getContents().size(), container.getContents().size());
		assertNotSame(copy.getContents().get(0), container.getContents().get(0));
	}

}
