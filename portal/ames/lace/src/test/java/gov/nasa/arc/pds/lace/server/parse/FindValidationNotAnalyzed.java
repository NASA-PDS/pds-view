package gov.nasa.arc.pds.lace.server.parse;

import java.io.IOException;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FindValidationNotAnalyzed {

	private static final String ASSERTION_MATCHED = "assertion-matched";
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		builderFactory.setValidating(false);
		
		XPathFactory xPathFactory = new net.sf.saxon.xpath.XPathFactoryImpl();
		
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse("src/main/resources/schema/pds4/1100/PDS4_PDS_1100.sch");
		DOMSource source = new DOMSource(document);

		annotateMatchingRules(source, xPathFactory);
		showUnmatchedRules(source, xPathFactory);
	}
	
	private static void annotateMatchingRules(DOMSource source, XPathFactory xPathFactory) throws XPathExpressionException {
		NodeList rules = (NodeList) xPathFactory.newXPath().evaluate(ValidationAnalyzer.RULE_PATH, source, XPathConstants.NODESET);
		for (int i=0; i < rules.getLength(); ++i) {
			Node rule = rules.item(i);
			annotateMatchingAssertions(rule, xPathFactory);
		}
	}
	
	private static void annotateMatchingAssertions(Node rule, XPathFactory xPathFactory) throws XPathExpressionException {
		NodeList asserts = (NodeList) xPathFactory.newXPath().evaluate(ValidationAnalyzer.ASSERT_PATH, new DOMSource(rule), XPathConstants.NODESET);
		for (int i=0; i < asserts.getLength(); ++i) {
			Node assertion = asserts.item(i);
			String test = assertion.getAttributes().getNamedItem("test").getNodeValue();
			
			Matcher matcher1 = ValidationAnalyzer.ENUM_PATTERN1.matcher(test);
			Matcher matcher2 = ValidationAnalyzer.ENUM_PATTERN2.matcher(test);
			Matcher matcher3 = ValidationAnalyzer.ENUM_PATTERN3.matcher(test);
			Matcher attrMatcher = ValidationAnalyzer.ATTR_ENUM_PATTERN.matcher(test);
			
			if (matcher1.matches()) {
				assertion.setUserData(ASSERTION_MATCHED, true, null);
			} else if (matcher2.matches() && matcher2.group(1).equals(matcher2.group(2))) {
				assertion.setUserData(ASSERTION_MATCHED, true, null);
			} else if (matcher3.matches() && matcher3.group(1).equals(matcher3.group(3))) {
				assertion.setUserData(ASSERTION_MATCHED, true, null);
			} else if (attrMatcher.matches()) {
				assertion.setUserData(ASSERTION_MATCHED, true, null);
			}
		}
	}
	
	private static void showUnmatchedRules(DOMSource source, XPathFactory xPathFactory) throws XPathExpressionException {
		int unmatchedAssertions = 0;
		
		String allAssertsPath = "//*:assert[namespace-uri()='" + ValidationAnalyzer.SCH_NS + "'][@test]";
		NodeList asserts = (NodeList) xPathFactory.newXPath().evaluate(allAssertsPath, source, XPathConstants.NODESET);
		for (int i=0; i < asserts.getLength(); ++i) {
			Node assertion = asserts.item(i);
			
			if (assertion.getUserData(ASSERTION_MATCHED) == null) {
				++unmatchedAssertions;
				Node rule = assertion.getParentNode();
				System.out.println("Rule not matched: " + rule.getAttributes().getNamedItem("context")
						+ " " + assertion.getAttributes().getNamedItem("test"));
			}
		}
		
		System.out.println("Total " + unmatchedAssertions + " unmatched assertions out of " + asserts.getLength());
	}
	
}
