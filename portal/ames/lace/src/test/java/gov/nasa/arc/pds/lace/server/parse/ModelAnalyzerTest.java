package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.server.LabelContentsServiceImpl;
import gov.nasa.arc.pds.lace.server.schema.SchemaManager;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.WildcardType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

@Guice(modules = {ParsingTestModule.class})
public class ModelAnalyzerTest {

	private static final String SCHEMA_NAMESPACE = "http://arc.nasa.gov/pds4/model-analyzer";

	private static final String PDS4_0300a_SCHEMA_NAMESPACE = "http://pds.nasa.gov/pds4/pds/v03";
	private static final String PDS4_1_x_SCHEMA_NAMESPACE = "http://pds.nasa.gov/pds4/pds/v1";

	@Inject
	private Provider<SchemaManager> schemaManagerProvider;

	@Inject
	private Provider<LabelContentsServiceImpl> labelContentsServiceImplProvider;

	private ModelAnalyzer analyzer;

	@BeforeMethod
	public void init() {
		analyzer = schemaManagerProvider.get().getAnalyzer();
	}

	@Test(dataProvider="SimpleTests")
	public void testSimple(String rootElementName) {
		Container container = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE, false);
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

	@Test
	public void testSimpleWithBimpleType() {
		LabelElement item = analyzer.getItemForElement("SimpleWithStringType", SCHEMA_NAMESPACE, false);
		assertTrue(item instanceof SimpleItem);

		LabelItemType type = item.getType();
		assertEquals(type.getElementName(), "SimpleWithStringType");
	}

	@Test
	public void testContainerForBimpleType() {
		Container item = analyzer.getContainerForElement("SimpleWithStringType", SCHEMA_NAMESPACE, false);

		LabelItemType type = item.getType();
		assertEquals(type.getElementName(), "SimpleWithStringType");
		assertFalse(type.isComplex());
	}

	@Test(dataProvider="SimpleWithRestrictionTests")
	public void testSimpleRestriction(String rootElementName, int minLength, int maxLength, boolean preserved, String[] patterns, String[] enumerations) {
		LabelItemType type = analyzer.getTypeForElement(rootElementName, SCHEMA_NAMESPACE);
		assertEquals(type.getElementName(), rootElementName);
		assertEquals(type.getMinLength(), minLength);
		assertEquals(type.getMaxLength(), maxLength);
		assertEquals(type.isWhitespacePreserved(), preserved);
		assertEquals(type.getPatterns().size(), patterns.length);
		assertEquals(type.getValidValues().size(), enumerations.length);


	}

	@SuppressWarnings("unused")
	@DataProvider(name="SimpleWithRestrictionTests")
	private Object[][] getSimpleWithRestrictionTests() {
		return new Object[][] {
				// root element name, min length, max length, whitespace preserved, patterns, enumeration
				{ "Simple5", 1 , 255, true , new String[] { "[a-z]", "[A-Z]" }, new String[] { "enum1", "enum2" } },
				{ "Simple6", -1, -1 , false, new String[] { }, new String[] { } }
		};
	}

	@Test(dataProvider="SimpleWithAttributeTests")
	public void testAttribute(String rootElementName) {
		Container container = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE, false);
		assertEquals(container.getContents().size(), 0);
		assertEquals(container.getAttributes().size(), 1);

		LabelItemType type = container.getAttributes().get(0).getType();
		assertEquals(type.getElementName(), "attribute");
	}

	@SuppressWarnings("unused")
	@DataProvider(name="SimpleWithAttributeTests")
	private Object[][] getSimpleWithAttributeTests() {
		return new Object[][] {
				// root element name
				{ "SimpleWithAttribute1" },
				{ "SimpleWithAttribute2" },
				{ "SimpleWithAttribute3" },
		};
	}

	public void testAttributeWithNesting() {
		Container container = analyzer.getContainerForElement("SimpleWithAttributeAndNesting", SCHEMA_NAMESPACE, false);
		assertEquals(container.getContents().size(), 1);

		LabelItem item = container.getContents().get(0);
		assertTrue(item instanceof Container);

		Container wrapper = (Container) item;
		assertEquals(wrapper.getType().getElementName(), "SimpleWithAttributeAndNesting");
		assertEquals(wrapper.getContents().size(), 1);
		item = wrapper.getContents().get(0);
		assertTrue(item instanceof SimpleItem);

		SimpleItem offset = (SimpleItem) wrapper.getContents().get(0);
		assertEquals(offset.getType().getElementName(), "offset");

		assertEquals(offset.getAttributes().size(), 1);
		AttributeItem attribute = offset.getAttributes().get(0);
		LabelItemType type = attribute.getType();
		assertEquals(type.getElementName(), "attribute");
	}

	@Test(dataProvider="MiddlingTests")
	public void testMiddling(String rootElementName) {
		Container root = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE, false);
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
			int usedOccurrences,
			String[] expectedAlternatives
	) {
		Container root = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE, false);
		assertEquals(root.getContents().size(), 1);

		LabelItem item = root.getContents().get(0);
		assertTrue(item instanceof InsertionPoint);

		List<InsertOption> alternatives = ((InsertionPoint) item).getAlternatives();
		assertEquals(alternatives.size(), 1);

		assertEquals(alternatives.get(0).getMaxOccurrences(), maxOccurrences);
		assertEquals(alternatives.get(0).getMinOccurrences(), minOccurrences);
		assertEquals(alternatives.get(0).getUsedOccurrences(), usedOccurrences);
		assertEquals(alternatives.get(0).getTypes().size(), expectedAlternatives.length);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="InsPointTests")
	private Object[][] getInsPointTests() {
		return new Object[][] {
				//root element, min, max, used, expected insert options
				{ "InsPoint1", 0,  1, 0, new String[] {"Container"} },
				{ "InsPoint2", 0, -1, 0, new String[] {"Container"} },
				{ "InsPoint3", 1, -1, 0, new String[] {"Container"} },
				{ "InsPoint4", 1, -1, 0, new String[] {"Value"} },
				{ "InsPoint5", 0, -1, 0, new String[] {"Value"} },
				{ "InsPoint6", 0, -1, 0, new String[] {"Value1", "Value2"} },
				{ "InsPoint7", 1,  1, 0, new String[] {"Value1", "Value2"} }
		};
	}

	@Test(dataProvider="WildcardTypeTests")
	public void testWildcardTypes(
			String rootElementName,
			int minOccurrences,
			int maxOccurrences,
			int usedOccurrences,
			String restriction
	) {
		Container root = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE, false);
		assertEquals(root.getContents().size(), 1);

		LabelItem item = root.getContents().get(0);
		assertTrue(item instanceof InsertionPoint);

		InsertionPoint insPoint = (InsertionPoint) item;
		assertEquals(insPoint.getDisplayType(), InsertionPoint.DisplayType.ANY.getDisplayType());

		List<InsertOption> alternatives = insPoint.getAlternatives();
		assertEquals(alternatives.size(), 1);

		InsertOption insertOption = alternatives.get(0);
		assertEquals(insertOption.getMaxOccurrences(), maxOccurrences);
		assertEquals(insertOption.getMinOccurrences(), minOccurrences);
		assertEquals(insertOption.getUsedOccurrences(), usedOccurrences);
		assertEquals(insertOption.getTypes().size(), 1);

		LabelItemType type = insertOption.getTypes().get(0);
		assertTrue(type instanceof WildcardType);

		List<String> expectedNamespaces = new ArrayList<String>();
		for (String ns : schemaManagerProvider.get().getNamespaces()) {
			if (restriction.equals("##other") && !ns.equals(SCHEMA_NAMESPACE)) {
				expectedNamespaces.add(ns);
			} else if (restriction.equals("##targetNamespace") && ns.equals(SCHEMA_NAMESPACE)) {
				expectedNamespaces.add(ns);
			} else if (restriction.equals("##any")) {
				expectedNamespaces.add(ns);
			}
		}

		if (!restriction.startsWith("##") && !restriction.isEmpty()) {
			for (String ns : restriction.split(" ")) {
				expectedNamespaces.add(ns);
			}
		}

		List<String> actual = ((WildcardType) type).getNamespaces();

		if (restriction.equals("##local")) {
			// Ensure that either the actual list is empty or contains just a null.
			assertTrue(actual.isEmpty() || (actual.size()==1 && actual.get(0)==null));
		} else {
			checkNamespaces(actual, expectedNamespaces);
		}
	}

	private void checkNamespaces(List<String> namespaces, List<String> expectedNamespaces) {
		for (String ns : namespaces) {
			assertTrue(expectedNamespaces.contains(ns), "Namespace not in expected: " + ns);
		}
		for (String ns : expectedNamespaces) {
			assertTrue(expectedNamespaces.contains(ns), "Expected namespace not present: " + ns);
		}
	}

	@SuppressWarnings("unused")
	@DataProvider(name="WildcardTypeTests")
	private Object[][] getWildcardTypeTests() {
		return new Object[][] {
				// root element, min, max, used, expected namespaces
				{ "AnyInsPoint1", 0, -1, 0, "##other" },
				{ "AnyInsPoint2", 0, -1, 0, "##any" },
				{ "AnyInsPoint3", 0, -1, 0, "##targetNamespace" },
				{ "AnyInsPoint4", 0, -1, 0, PDS4_0300a_SCHEMA_NAMESPACE + " " + SCHEMA_NAMESPACE },
				{ "AnyInsPoint5", 0, -1, 0, "##local" }
		};
	}

	@Test
	public void testReusingSimpleTypes() {
		Container root = analyzer.getContainerForElement("Common1", SCHEMA_NAMESPACE, false);
		assertEquals(root.getContents().size(), 3);

		LabelItemType rootItem1Type = ((SimpleItem) root.getContents().get(0)).getType();
		LabelItemType rootItem2Type = ((SimpleItem) root.getContents().get(1)).getType();
		LabelItemType rootItem3Type = ((InsertionPoint) root.getContents().get(2)).getAlternatives().get(0).getTypes().get(0);

		assertEquals(rootItem1Type.getElementName(), "Value1");
		assertEquals(rootItem2Type.getElementName(), "Value2");
		assertEquals(rootItem3Type.getElementName(), "Value3");

		assertEquals(rootItem1Type.getMinOccurrences(), 1);
		assertEquals(rootItem1Type.getMaxOccurrences(), 1);
		assertEquals(rootItem2Type.getMinOccurrences(), 0);
		assertEquals(rootItem2Type.getMaxOccurrences(), 1);
		assertEquals(rootItem3Type.getMinOccurrences(), 1);
		assertEquals(rootItem3Type.getMaxOccurrences(), -1);
	}

	@Test
	public void testReusingContainerTypes() {
		Container root = analyzer.getContainerForElement("Common2", SCHEMA_NAMESPACE, false);
		List<LabelItem> contents1 = root.getContents();
		assertEquals(contents1.size(), 4);

		LabelItemType rootItem1Type = ((Container) contents1.get(0)).getType();
		LabelItemType rootItem2Type = (((InsertionPoint) contents1.get(1)).getAlternatives().get(0)).getTypes().get(0);
		LabelItemType rootItem3Type = (((InsertionPoint) contents1.get(2)).getAlternatives().get(0)).getTypes().get(0);
		LabelItemType rootItem4Type = (((InsertionPoint) contents1.get(3)).getAlternatives().get(0)).getTypes().get(0);

		assertEquals(rootItem1Type.getElementName(), "Container1");
		assertEquals(rootItem2Type.getElementName(), "Container2");
		assertEquals(rootItem3Type.getElementName(), "Container3");
		assertEquals(rootItem4Type.getElementName(), "Container4");

		assertEquals(rootItem1Type.getMinOccurrences(), 1);
		assertEquals(rootItem1Type.getMaxOccurrences(), 1);
		assertEquals(rootItem2Type.getMinOccurrences(), 0);
		assertEquals(rootItem2Type.getMaxOccurrences(), 1);
		assertEquals(rootItem3Type.getMinOccurrences(), 1);
		assertEquals(rootItem3Type.getMaxOccurrences(), -1);
		assertEquals(rootItem4Type.getMinOccurrences(), 0);
		assertEquals(rootItem4Type.getMaxOccurrences(), -1);
	}

	@Test
	public void testRepeatingContainers() {
		Container root = analyzer.getContainerForElement("Repeating1", SCHEMA_NAMESPACE, false);
		assertEquals(root.getContents().size(), 1);

		Container item1 = (Container) root.getContents().get(0);
		assertEquals(item1.getType().getElementName(), "Container");

		InsertionPoint insPoint1 = (InsertionPoint) item1.getContents().get(0);
		List<InsertOption> alternatives = insPoint1.getAlternatives();
		assertEquals(alternatives.size(), 1);

		List<LabelItemType> types = alternatives.get(0).getTypes();
		assertEquals(types.size(), 2);
		assertEquals(types.get(0).getElementName(), "SimpleValue");
		assertEquals(types.get(1).getElementName(), "Container");
	}

	@Test
	public void testSubstitutionGroup() {
		Container root = analyzer.getContainerForElement("ContainerWithSubstitutionGroup", SCHEMA_NAMESPACE, false);
		assertEquals(root.getContents().size(), 1);

		LabelItem item = root.getContents().get(0);
		assertTrue(item instanceof InsertionPoint);

		InsertionPoint insPoint = (InsertionPoint) item;
		List<InsertOption> alternatives = insPoint.getAlternatives();
		assertEquals(alternatives.size(), 1);

		assertEquals(alternatives.get(0).getTypes().size(), 2); // GroupElement1 and GroupElement2
		assertEquals(alternatives.get(0).getTypes().get(0).getElementName(), "GroupElement1");
		assertEquals(alternatives.get(0).getTypes().get(1).getElementName(), "GroupElement2");
	}

	@Test
	public void testEnumeratedValues() {
		LabelItemType type = analyzer.getTypeForElement("StringWithEnumerations", SCHEMA_NAMESPACE);
		assertEquals(type.getValidValues().size(), 2);
		assertTrue(type.getValidValues().contains("ONE"));
		assertTrue(type.getValidValues().contains("TWO"));
		assertNull(type.getDefaultValue());
	}

	@Test
	public void testSingleEnumeratedValue() {
		LabelItemType type = analyzer.getTypeForElement("StringWithOneEnumeratedValue", SCHEMA_NAMESPACE);
		assertEquals(type.getValidValues().size(), 1);
		assertTrue(type.getValidValues().contains("ONE"));
		assertEquals(type.getDefaultValue(), "ONE");
	}

	@Test
	public void testAttributeWithDefault() {
		LabelItemType type = analyzer.getTypeForElement("ElementWithAttributeThatHasDefault", SCHEMA_NAMESPACE);
		assertEquals(type.getInitialAttributes().size(), 1);

		AttributeItem attr = type.getInitialAttributes().get(0);
		assertEquals(attr.getType().getDefaultValue(), "DEFAULT");
	}

	@Test
	public void testAttributeWithEnumeration() {
		LabelItemType type = analyzer.getTypeForElement("ElementWithAttributeThatHasEnumerations", SCHEMA_NAMESPACE);
		assertEquals(type.getInitialAttributes().size(), 1);

		AttributeItem attr = type.getInitialAttributes().get(0);
		assertEquals(attr.getType().getValidValues().size(), 2);
		assertTrue(attr.getType().getValidValues().contains("ONE"));
		assertTrue(attr.getType().getValidValues().contains("TWO"));
		assertNull(attr.getType().getDefaultValue());
	}

	private boolean oldAndNewModelMatch;

	@Test(enabled=false)
	public void testOldAndNewModelCreation() throws ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		oldAndNewModelMatch = true;

		LabelContentsServiceImpl oldService = labelContentsServiceImplProvider.get();
		ModelAnalyzer newService = schemaManagerProvider.get().getAnalyzer();

		Container oldResult = oldService.getRootContainer("Product_Observational", PDS4_0300a_SCHEMA_NAMESPACE);
		Container newResult = newService.getContainerForElement("Product_Observational", PDS4_0300a_SCHEMA_NAMESPACE, false);
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

	@Test
	public void testEnumerationsFromUnionType() {
		LabelItemType type = analyzer.getTypeForElement("colorOrFlip", SCHEMA_NAMESPACE);
		assertEquals(type.getValidValues().size(), 5);
		assertTrue(type.getValidValues().contains("red"));
		assertTrue(type.getValidValues().contains("green"));
		assertTrue(type.getValidValues().contains("blue"));
		assertTrue(type.getValidValues().contains("heads"));
		assertTrue(type.getValidValues().contains("tails"));
		assertNull(type.getDefaultValue());
	}

	@Test
	public void testTypeDocumentation() {
		LabelItemType type = analyzer.getTypeForElement("elementForTypeWithDocumentation", SCHEMA_NAMESPACE);
		assertTrue(type.getDocumentation().contains("Documentation for typeWithDocumentation."));
	}

	@Test
	public void testElementDocumentation() {
		LabelItemType type = analyzer.getTypeForElement("elementWithDocumentation", SCHEMA_NAMESPACE);
		assertTrue(type.getDocumentation().contains("Documentation for elementWithDocumentation."));
	}

	/**
	 * Tests that if documentation exists both on the element and on its type,
	 * that only the element documentation is used.
	 */
	@Test
	public void testElementAndTypeDocumentation() {
		LabelItemType type = analyzer.getTypeForElement("elementWithDocumentationForTypeWithDocumentation", SCHEMA_NAMESPACE);
		assertTrue(type.getDocumentation().contains("Documentation for elementWithDocumentationForTypeWithDocumentation."));
		assertFalse(type.getDocumentation().contains("Documentation for typeWithDocumentation."));
	}

	@Test
	public void testPatternDocumentationInSubTypes() {
		LabelItemType type = analyzer.getTypeForElement("lowerCaseVowel", SCHEMA_NAMESPACE);
		String doc = type.getDocumentation();
		assertTrue(doc.contains("[aei]")); // Pattern from subtype
		assertTrue(doc.contains("[ou]")); // Pattern from subtype
		assertFalse(doc.contains("[a-z]")); // Pattern from supertype, overridden in subtype
	}

	@Test
	public void testPatternInUnrestrictedSubType() {
		LabelItemType type = analyzer.getTypeForElement("subTypeNoRestrictions", SCHEMA_NAMESPACE);
		String doc = type.getDocumentation();
		assertTrue(doc.contains("[a-z]")); // Pattern from supertype
	}

	@Test
	public void testConstraints() {
		LabelItemType type = analyzer.getTypeForElement("docTopLevel", SCHEMA_NAMESPACE);
		List<LabelItem> contents = type.getInitialContents();
		// We're expecting:
		//   0. simpleNonRepeating
		//   1. insPt - simpleRepeating
		//   2. lengthMinMax
		//   3. numericMinMax
		//   4. withPatterns
		LabelItemType simpleNonRepeating = ((LabelElement) contents.get(0)).getType();
		assertNull(simpleNonRepeating.getDocumentation());

		InsertionPoint simpleRepeatingInsPt = (InsertionPoint) contents.get(1);
		assertEquals(simpleRepeatingInsPt.getAlternatives().size(), 1);
		assertEquals(simpleRepeatingInsPt.getAlternatives().get(0).getTypes().size(), 1);
		LabelItemType simpleRepeating = simpleRepeatingInsPt.getAlternatives().get(0).getTypes().get(0);
		assertTrue(simpleRepeating.getDocumentation().toLowerCase().contains("occurrences"));

		LabelItemType lengthMinMax = ((LabelElement) contents.get(2)).getType();
		assertTrue(lengthMinMax.getDocumentation().toLowerCase().contains("length"));

		LabelItemType numericMinMax = ((LabelElement) contents.get(3)).getType();
		assertTrue(numericMinMax.getDocumentation().toLowerCase().contains("range"));

		LabelItemType withPatterns = ((LabelElement) contents.get(4)).getType();
		assertTrue(withPatterns.getDocumentation().toLowerCase().contains("patterns"));
	}

	@Test
	public void testPDS4Documentation() {
		Container container;
		LabelItemType type;

		container = analyzer.getContainerForElement("Product_Observational", PDS4_1_x_SCHEMA_NAMESPACE);
		LabelElement logicalIdentifier = findChild(container, PDS4_1_x_SCHEMA_NAMESPACE, "Identification_Area", "logical_identifier");
		assertNotNull(logicalIdentifier);
		type = logicalIdentifier.getType();
		assertTrue(type.getDocumentation()!=null && !type.getDocumentation().isEmpty());
		assertTrue(type.getDocumentation().contains("logical identifier"));
	}

	private LabelElement findChild(Container parent, String ns, String... path) {
		LabelElement child = findChild(parent, ns, path[0]);
		if (path.length == 1) {
			return child;
		} else if (!(child instanceof Container)){
			return null;
		} else {
			String[] rest = new String[path.length - 1];
			System.arraycopy(path, 1, rest, 0, rest.length);
			return findChild((Container) child, ns, rest);
		}
	}

	private LabelElement findChild(Container parent, String ns, String name) {
		for (LabelItem childItem : parent.getContents()) {
			if (childItem instanceof LabelElement) {
				LabelElement child = (LabelElement) childItem;
				LabelItemType type = child.getType();
				if (ns.equals(type.getElementNamespace()) && name.equals(type.getElementName())) {
					return child;
				}
			}
		}

		return null;
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
		/*List<LabelItemType> alt1 = item1.getAlternatives();
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
		}*/
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
