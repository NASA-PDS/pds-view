package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.inject.AbstractModule;

@Guice(modules = {ValidationAnalyzerTest.Module.class})
public class ValidationAnalyzerTest {

	private static final String PDS4_1100_SCHEMATRON_RULES = "src/main/resources/schema/pds4/1100/PDS4_PDS_1100.sch";

	private Document pds41100SchematronRules;

	@Inject
	private ValidationAnalyzer analyzer;

	@BeforeClass
	public void init() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		builderFactory.setValidating(false);

		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		pds41100SchematronRules = builder.parse(new File(PDS4_1100_SCHEMATRON_RULES));
	}

	@Test
	public void testPrefixes() throws XPathExpressionException {
		Map<String, String> prefixMap = analyzer.getNamespacePrefixes(new DOMSource(pds41100SchematronRules));

		assertEquals(prefixMap.size(), 1);
		assertEquals(prefixMap.get("pds"), "http://pds.nasa.gov/pds4/pds/v1");
	}

	@Test(dataProvider="CreateContextTests")
	public void testCreateContext(String path) {
		Map<String, String> prefixMap = new HashMap<String, String>();
		prefixMap.put("pds", "http://pds.nasa.gov/pds4/pds/v1");
		String context = analyzer.createContext(path, prefixMap);

		StringBuilder builder = new StringBuilder();
		for (QName qName : parsePath(path, prefixMap)) {
			if (builder.length() > 0) {
				builder.append('/');
			}
			builder.append(qName.toString());
		}

		assertEquals(context, builder.toString());
	}

	@DataProvider(name="CreateContextTests")
	private Object[][] getCreateContextTests() {
		return new Object[][] {
				// path
				{ "junk" },
				{ "pds:junk" },
				{ "one/two" },
				{ "pds:one/two" },
				{ "one/pds:two" },
				{ "pds:one/pds:two" },
				{ "pds:one/pds:two/pds:three" },
		};
	}

	private QName[] parsePath(String path, Map<String, String> prefixMap) {
		List<QName> qNames = new ArrayList<QName>();

		for (String segment : path.split("/")) {
			String[] parts = segment.split(":");
			if (parts.length == 1) {
				qNames.add(new QName(parts[0]));
			} else {
				qNames.add(new QName(prefixMap.get(parts[0]), parts[1]));
			}
		}

		return qNames.toArray(new QName[qNames.size()]);
	}

	@Test
	public void testCreateContextNotSimple() {
		Map<String, String> prefixMap = new HashMap<String, String>();
		prefixMap.put("pds", "http://pds.nasa.gov/pds4/pds/v1");

		assertNull(analyzer.createContext("pds:abc[1]", prefixMap));
	}

	@Test(dataProvider="PatternTests")
	public void testPatterns(String s, Pattern matchingPattern) {
		Pattern[] patterns = {
				ValidationAnalyzer.ENUM_PATTERN1,
				ValidationAnalyzer.ENUM_PATTERN2,
				ValidationAnalyzer.ENUM_PATTERN3,
				ValidationAnalyzer.ATTR_ENUM_PATTERN
		};
		for (Pattern p : patterns) {
			assertEquals(p.matcher(s).matches(), p==matchingPattern);
		}
	}

	@DataProvider(name="PatternTests")
	private Object[][] getPatternTests() {
		return new Object[][] {
				// string, pattern to match or null for no match
				{ "pds:abc = ('x')", ValidationAnalyzer.ENUM_PATTERN1 },
				{ "pds:abc = ('x', 'y', 'z')", ValidationAnalyzer.ENUM_PATTERN1 },
				{ "pds:abc = ()", null },
				{ "pds:abc = (1, 2, 3)", null },

				{ "if (pds:abc) then pds:abc = ('x') else true()", ValidationAnalyzer.ENUM_PATTERN2 },
				{ "if (pds:abc) then pds:abc = ('x', 'y', 'z') else true()", ValidationAnalyzer.ENUM_PATTERN2 },
				{ "if (pds:abc) then pds:abc = () else true()", null },
				{ "if (pds:abc) then pds:abc = (1, 2, 3) else true()", null },

				{ "every $ref in (pds:abc) satisfies $ref = ('x')", ValidationAnalyzer.ENUM_PATTERN3 },
				{ "every $ref in (pds:abc) satisfies $ref = ('x', 'y', 'z')", ValidationAnalyzer.ENUM_PATTERN3 },
				{ "every $ref in (pds:abc) satisfies $ref = ()", null },
				{ "every $ref in (pds:abc) satisfies $ref = (1, 2, 3)", null },

				{ ". = ('x')", ValidationAnalyzer.ENUM_PATTERN4 },
				{ ". = ('x', 'y', 'z')", ValidationAnalyzer.ENUM_PATTERN4 },
				{ ". = () else true()", null },
				{ ". = (1, 2, 3)", null },

				{ "@unit = ('byte')", ValidationAnalyzer.ATTR_ENUM_PATTERN },
				{ "@unit = ('byte', 'block')", ValidationAnalyzer.ATTR_ENUM_PATTERN },
				{ "@unit = ()", null },
				{ "@unit = (1, 2, 3)", null },
		};
	}

	@Test(dataProvider="ValidValuesTests")
	public void testGetValidValues(String path, String[] expectedValues) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		analyzer.analyzeSchematronRules(new File(PDS4_1100_SCHEMATRON_RULES).toURI());

		Map<String, String> prefixMap = new HashMap<String, String>();
		prefixMap.put("pds", "http://pds.nasa.gov/pds4/pds/v1");
		String context = analyzer.createContext(path, prefixMap);

		List<String> actual = analyzer.getValidValues(context);
		assertEquals(actual.size(), expectedValues.length);
		for (String s : expectedValues) {
			assertTrue(actual.contains(s));
		}
	}

	@DataProvider(name="ValidValuesTests")
	private Object[][] getValidValuesTests() {
		return new Object[][] {
				// context path, expected values
				{ "pds:Array_2D/pds:axes", new String[] {"2"} },
				{ "pds:File_Area_Observational/pds:Array_2D/pds:axes", new String[] {"2"} },
				{ "pds:axes", new String[0] },

				{ "pds:Facility/pds:type", new String[] {"Laboratory", "Observatory"} },
				{ "pds:xyz/pds:Facility/pds:type", new String[] {"Laboratory", "Observatory"} },
				{ "pds:type", new String[0] },

				// Testing of field data type. (PTOOL-333)
				{
					"pds:Product_Observational/pds:File_Area_Observational/pds:Table_Character/pds:Record_Character/pds:Field_Character/pds:data_type",
					new String[] {
							"ASCII_AnyURI", "ASCII_Boolean", "ASCII_DOI", "ASCII_Date", "ASCII_Date_DOY",
							"ASCII_Date_Time", "ASCII_Date_Time_DOY", "ASCII_Date_Time_UTC",
							"ASCII_Date_Time_YMD", "ASCII_Date_YMD", "ASCII_Directory_Path_Name",
							"ASCII_File_Name", "ASCII_File_Specification_Name", "ASCII_Integer",
							"ASCII_LID", "ASCII_LIDVID", "ASCII_LIDVID_LID", "ASCII_MD5_Checksum",
							"ASCII_NonNegative_Integer", "ASCII_Numeric_Base16", "ASCII_Numeric_Base2",
							"ASCII_Numeric_Base8", "ASCII_Real", "ASCII_String", "ASCII_Time", "ASCII_VID",
							"UTF8_String"
					}
				}
		};
	}

	public static class Module extends AbstractModule {

		@Override
		protected void configure() {
			// No special configuration
		}

	}

}
