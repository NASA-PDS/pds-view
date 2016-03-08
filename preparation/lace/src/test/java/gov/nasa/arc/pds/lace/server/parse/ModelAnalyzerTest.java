package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.server.LabelContentsServiceImpl;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class ModelAnalyzerTest {

	private static final File SCHEMA_FILE = new File("src/test/resources/model-analyzer-schema.xsd");
	private static final String SCHEMA_NAMESPACE = "http://arc.nasa.gov/pds4/model-analyzer";

	private static final File PDS4_0300a_SCHEMA_FILE = new File("src/main/resources/gov/nasa/arc/pds/lace/server/PDS4_OPS_0300a.xsd");
	private static final String PDS4_0300a_SCHEMA_NAMESPACE = "http://pds.nasa.gov/pds4/pds/v03";

	private ModelAnalyzer analyzer;

	@BeforeMethod
	public void init() {
		analyzer = new ModelAnalyzer(SCHEMA_FILE.toURI());
	}

	@Test(dataProvider="SimpleTests")
	public void testSimple(String rootElementName) {
		Container container = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE);
		assertEquals(container.getContents().size(), 1);

		LabelItem item = container.getContents().get(0);
		assertTrue(item instanceof SimpleItem);

		LabelItemType type = ((SimpleItem) item).getType();
		assertEquals(type.getElementName(), "SimpleValue");
		assertEquals(type.getMinOccurrences(), 1);
		assertEquals(type.getMaxOccurrences(), 1);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="SimpleTests")
	private Object[][] getSimpleTests() {
		return new Object[][] {
				// root element name
				{ "Simple1" },
				{ "Simple2" },
				{ "Simple3" },
				{ "Simple4" },
		};
	}

	@Test(dataProvider="MiddlingTests")
	public void testMiddling(String rootElementName) {
		Container root = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 1);

		LabelItem item = root.getContents().get(0);
		assertTrue(item instanceof Container);

		Container child = (Container) item;
		LabelItemType type = child.getType();
		assertEquals(type.getElementName(), "Container");
		assertEquals(type.getMinOccurrences(), 1);
		assertEquals(type.getMaxOccurrences(), 1);

		assertEquals(child.getContents().size(), 1);
		item = child.getContents().get(0);
		assertTrue(item instanceof SimpleItem);

		type = ((SimpleItem) item).getType();
		assertEquals(type.getElementName(), "SimpleValue");
		assertEquals(type.getMinOccurrences(), 1);
		assertEquals(type.getMaxOccurrences(), 1);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="MiddlingTests")
	private Object[][] getMiddlingTests() {
		return new Object[][] {
				// root element name
				{ "Middling1" },
				{ "Middling2" },
				{ "Middling3" },
		};
	}

	@Test(dataProvider="InsPointTests")
	public void testInsertionPoints(
			String rootElementName,
			int minOccurrences,
			int maxOccurrences,
			String[] expectedAlternatives
	) {
		Container root = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 1);

		LabelItem item = root.getContents().get(0);
		if (expectedAlternatives.length > 1) {
			assertTrue(item instanceof InsertionPoint);
		} else {
			assertTrue(item instanceof SimpleItem);
		}
	}

	@SuppressWarnings("unused")
	@DataProvider(name="InsPointTests")
	private Object[][] getInsPointTests() {
		return new Object[][] {
				//root element, min, max, expected alternatives
				{ "InsPoint1", 0, 1, new String[] {"Value"} },
				{ "InsPoint2", 1, -1, new String[] {"Value"} },
				{ "InsPoint3", 0, -1, new String[] {"Value"} },
				{ "InsPoint4", 0, -1, new String[] {"Value1", "Value2"} },
		};
	}

	@Test
	public void testReusingSimpleTypes() {
		Container root = analyzer.getContainerForElement("Common1", SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 3);

		SimpleItem item1 = (SimpleItem) root.getContents().get(0);
		SimpleItem item2 = (SimpleItem) root.getContents().get(1);
		SimpleItem item3 = (SimpleItem) root.getContents().get(2);

		assertEquals(item1.getType().getElementName(), "Value1");
		assertEquals(item2.getType().getElementName(), "Value2");
		assertEquals(item3.getType().getElementName(), "Value3");
	}

	@Test
	public void testReusingContainerTypes() {
		Container root = analyzer.getContainerForElement("Common2", SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 3);

		Container item1 = (Container) root.getContents().get(0);
		Container item2 = (Container) root.getContents().get(1);
		Container item3 = (Container) root.getContents().get(2);

		assertEquals(item1.getType().getElementName(), "Container1");
		assertEquals(item2.getType().getElementName(), "Container2");
		assertEquals(item3.getType().getElementName(), "Container3");
	}

	@Test
	public void testRepeatingContainers() {
		Container root = analyzer.getContainerForElement("Repeating1", SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 1);
		
		Container item1 = (Container) root.getContents().get(0);
		assertEquals(item1.getType().getElementName(), "Container");
		
		InsertionPoint insPoint1 = (InsertionPoint) item1.getContents().get(0);
		assertEquals(insPoint1.getAlternatives().size(), 2);
		
		assertEquals(insPoint1.getAlternatives().get(0).getElementName(), "SimpleValue");
		assertEquals(insPoint1.getAlternatives().get(1).getElementName(), "Container");
		
		/*root = analyzer.getContainerForElement("Repeating2", SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 1);
		
		item1 = (Container) root.getContents().get(0);
		assertEquals(item1.getType().getElementName(), "Container");
		
		SimpleItem child1 = (SimpleItem) item1.getContents().get(0);
		Container  child2 = (Container)  item1.getContents().get(1);
		
		assertEquals(child1.getType().getElementName(), "SimpleValue");
		assertEquals(child2.getType().getElementName(), "Container");*/
	}
	
	private boolean oldAndNewModelMatch;

	@Test(enabled=false)
	public void testOldAndNewModelCreation() throws ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		oldAndNewModelMatch = true;

		LabelContentsServiceImpl oldService = new LabelContentsServiceImpl(PDS4_0300a_SCHEMA_FILE.toURI(), PDS4_0300a_SCHEMA_NAMESPACE);
		ModelAnalyzer newService = new ModelAnalyzer(PDS4_0300a_SCHEMA_FILE.toURI());

		Container oldResult = oldService.getRootContainer("Product_Observational");
		Container newResult = newService.getContainerForElement("Product_Observational", PDS4_0300a_SCHEMA_NAMESPACE);
		newService.expandInsertionPoints(newResult);

		compareContainers(oldResult, newResult, "/");

		if (!oldAndNewModelMatch) {
			System.out.println("----- Old -----");
			showItemTree(oldResult, 0);
			System.out.println("----- New -----");
			showItemTree(newResult, 0);
		}
		assertTrue(oldAndNewModelMatch, "Old and new models do not match");
	}

	private void compareContainers(Container c1, Container c2, String path) {
		compareTypes(c1.getType(), c2.getType(), path);

		String newPath = path + c1.getType().getElementName() + "/";
		compareFacet(newPath, "content length", c1.getContents().size(), c2.getContents().size());

		Iterator<LabelItem> it1 = c1.getContents().iterator();
		Iterator<LabelItem> it2 = c2.getContents().iterator();
		int index = 0;
		while (it1.hasNext() || it2.hasNext()) {
			LabelItem item1 = (it1.hasNext() ? it1.next() : null);
			LabelItem item2 = (it2.hasNext() ? it2.next() : null);

			if (item1 == null) {
				System.out.println(newPath + ": missing from old: " + item2.toString());
				oldAndNewModelMatch = false;
			} else if (item2 == null) {
				System.out.println(newPath + ": missing from new: " + item1.toString());
				oldAndNewModelMatch = false;
			} else if (item1.getClass() != item2.getClass()) {
				System.out.println(newPath + ": at index " + index + ", type mismatch: " + item1.toString() + " and " + item2.toString());
			} else {
				compareItems(item1, item2, newPath);
			}
			++index;
		}
	}

	private void compareTypes(LabelItemType type1, LabelItemType type2, String path) {
		compareFacet(path, "element names", type1.getElementName(), type2.getElementName());
		compareFacet(path, "minOccurs", type1.getMinOccurrences(), type2.getMinOccurrences());
		compareFacet(path, "maxOccurs", type1.getMaxOccurrences(), type2.getMaxOccurrences());
	}

	private void compareItems(LabelItem item1, LabelItem item2, String path) {
		// We can assume item1 and item2 are the same class.
		if (item1 instanceof SimpleItem) {
			compareSimpleItems((SimpleItem) item1, (SimpleItem) item2, path);
		} else if (item1 instanceof Container) {
			compareContainers((Container) item1, (Container) item2, path);
		} else if (item1 instanceof InsertionPoint) {
			compareInsertionPoints((InsertionPoint) item1, (InsertionPoint) item2, path);
		}
	}

	private void compareSimpleItems(SimpleItem item1, SimpleItem item2, String path) {
		compareTypes(item1.getType(), item2.getType(), path);
	}

	private void compareInsertionPoints(InsertionPoint item1, InsertionPoint item2, String path) {
		List<LabelItemType> alt1 = item1.getAlternatives();
		List<LabelItemType> alt2 = item2.getAlternatives();
		compareFacet(path, "number of alternatives", alt1.size(), alt2.size());

		Set<String> onlyInOld = new HashSet<String>();
		for (LabelItemType type : alt1) {
			onlyInOld.add(type.getElementName());
		}
		for (LabelItemType type : alt2) {
			onlyInOld.remove(type.getElementName());
		}

		Set<String> onlyInNew = new HashSet<String>();
		for (LabelItemType type : alt2) {
			onlyInNew.add(type.getElementName());
		}
		for (LabelItemType type : alt1) {
			onlyInNew.remove(type.getElementName());
		}

		if (onlyInOld.size() > 0) {
			System.out.print(path + ": missing alternatives from new: " + onlyInOld.toString());
			System.out.println();
			oldAndNewModelMatch = false;
		}
		if (onlyInNew.size() > 0) {
			System.out.print(path + ": missing alternatives from old: " + onlyInNew.toString());
			System.out.println();
			oldAndNewModelMatch = false;
		}
	}

	private void compareFacet(String path, String facetName, String a, String b) {
		if (!a.equals(b)) {
			System.out.println(path + ": " + facetName + " mismatch [" + a + " != " + b + "]");
			oldAndNewModelMatch = false;
		}
	}

	private void compareFacet(String path, String facetName, int a, int b) {
		if (a != b) {
			System.out.println(path + ": " + facetName + " mismatch [" + Integer.toString(a) + " != " + Integer.toString(b) + "]");
			oldAndNewModelMatch = false;
		}
	}

	private void showItemTree(LabelItem item, int depth) {
		System.out.println(getIndent(depth) + item.toString());
		if (item instanceof Container) {
			for (LabelItem child : ((Container) item).getContents()) {
				showItemTree(child, depth+1);
			}
		}
	}

	private String getIndent(int depth) {
		StringBuilder builder = new StringBuilder();

		for (int i=0; i < depth; ++i) {
			builder.append("   ");
		}

		return builder.toString();
	}

}
