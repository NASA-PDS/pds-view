package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.LabelElement;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
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

public class ValidationAnalyzer {

	// Default scope, to use in test classes.
	static final String SCH_NS = "http://purl.oclc.org/dsdl/schematron";

	private static final String NS_PATH
		= "//"
		+ "*:schema[namespace-uri()='" + SCH_NS + "']"
		+ "/*:ns[namespace-uri()='" + SCH_NS + "'][@uri][@prefix]";

	// Default scope, to use in test classes.
	static final String RULE_PATH
		= "//"
		+ "*:pattern[namespace-uri()='" + SCH_NS + "']"
		+ "/*:rule[namespace-uri()='" + SCH_NS + "'][@context]";

	// Default scope, to ue in test classes.
	static final String ASSERT_PATH
		= "*:assert[namespace-uri()='" + SCH_NS + "'][@test]";

	private static final String LOCAL_NAME_PATTERN_STR = "[A-Za-z0-9_-]+";
	private static final String QNAME_PATTERN_STR = "(?:" + LOCAL_NAME_PATTERN_STR + ":)?" + LOCAL_NAME_PATTERN_STR;
	private static final String CONTEXT_PATTERN_STR
		= "^" + QNAME_PATTERN_STR + "(?:/" + QNAME_PATTERN_STR + ")*$";

	/** A pattern for contexts which we can parse and use. */
	private Pattern CONTEXT_PATTERN = Pattern.compile(CONTEXT_PATTERN_STR);

	private static final String QUOTED_PATTERN_STR = "'[^']*'";
	private static final String QUOTED_LIST_PATTERN_STR
		= QUOTED_PATTERN_STR + "(?:, *" + QUOTED_PATTERN_STR + ")*";

	private static final String ENUM_PATTERN1_STR
		= "^(" + QNAME_PATTERN_STR + ") *= *\\((" + QUOTED_LIST_PATTERN_STR + ")\\)$";
	// Default scope, for unit tests.
	static final Pattern ENUM_PATTERN1 = Pattern.compile(ENUM_PATTERN1_STR);

	private static final String ENUM_PATTERN2_STR
		= "^if \\((" + QNAME_PATTERN_STR + ")\\) then (" + QNAME_PATTERN_STR + ") *= *"
		+ "\\((" + QUOTED_LIST_PATTERN_STR + ")\\) else true\\(\\)$";
	// Default scope, for unit tests.
	static final Pattern ENUM_PATTERN2 = Pattern.compile(ENUM_PATTERN2_STR);

	private static final String VAR_PATTERN_STR = "\\$[A-Za-z0-9_]+";

	private static final String ENUM_PATTERN3_STR
		= "^every (" + VAR_PATTERN_STR + ") in \\((" + QNAME_PATTERN_STR + ")\\) satisfies "
		+ "(" + VAR_PATTERN_STR + ") *= *\\((" + QUOTED_LIST_PATTERN_STR + ")\\)$";
	// Default scope, for use in test classes.
	static final Pattern ENUM_PATTERN3 = Pattern.compile(ENUM_PATTERN3_STR);

	private static final String ENUM_PATTERN4_STR
		= "^\\. *= *\\((" + QUOTED_LIST_PATTERN_STR + ")\\)$";
	// Default scope, for use in unit tests.
	static final Pattern ENUM_PATTERN4 = Pattern.compile(ENUM_PATTERN4_STR);

	private static final String ATTR_ENUM_PATTERN_STR
		= "^(@" + LOCAL_NAME_PATTERN_STR + ") *= *\\((" + QUOTED_LIST_PATTERN_STR + ")\\)$";
	// Default scope, for unit tests.
	static final Pattern ATTR_ENUM_PATTERN = Pattern.compile(ATTR_ENUM_PATTERN_STR);

	private Map<String, List<String>> validValues = new HashMap<String, List<String>>();
	private XPathFactory xPathFactory;
	private DocumentBuilderFactory builderFactory;

	public ValidationAnalyzer() {
		builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		builderFactory.setValidating(false);
		
		xPathFactory = new net.sf.saxon.xpath.XPathFactoryImpl();
	}

	public void analyzeSchematronRules(URI uri) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		URL url = null;
		try {
			url = uri.toURL();
		} catch (MalformedURLException e) {
			// Cannot happen, since we got the URI from Java.
		}
		Document document = builder.parse(url.openStream());
		DOMSource source = new DOMSource(document);
		findValidValues(source);
	}

	public List<String> getValidValues(String context) {
		for (Map.Entry<String, List<String>> entry : validValues.entrySet()) {
			if (context.endsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return Collections.emptyList();
	}

	private void findValidValues(DOMSource source) throws XPathExpressionException {
		Map<String, String> prefixMap = getNamespacePrefixes(source);
		findValidValues(source, prefixMap);
	}

	private void findValidValues(DOMSource source, Map<String, String> prefixMap) throws XPathExpressionException {
		NodeList rules = (NodeList) xPathFactory.newXPath().evaluate(RULE_PATH, source, XPathConstants.NODESET);
		for (int i=0; i < rules.getLength(); ++i) {
			Node rule = rules.item(i);
			findValuesForRule(rule, prefixMap);
		}
	}

	private void findValuesForRule(Node rule, Map<String, String> prefixMap) throws XPathExpressionException {
		String contextStr = rule.getAttributes().getNamedItem("context").getNodeValue();
		String context = createContext(contextStr, prefixMap);
		if (context == null) {
			return;
		}

		NodeList asserts = (NodeList) xPathFactory.newXPath().evaluate(ASSERT_PATH, new DOMSource(rule), XPathConstants.NODESET);
		for (int i=0; i < asserts.getLength(); ++i) {
			Node assertion = asserts.item(i);
			String test = assertion.getAttributes().getNamedItem("test").getNodeValue();

			Matcher matcher1 = ENUM_PATTERN1.matcher(test);
			Matcher matcher2 = ENUM_PATTERN2.matcher(test);
			Matcher matcher3 = ENUM_PATTERN3.matcher(test);
			Matcher matcher4 = ENUM_PATTERN4.matcher(test);
			Matcher attrMatcher = ATTR_ENUM_PATTERN.matcher(test);

			if (matcher1.matches()) {
				addValues(context, prefixMap, matcher1.group(1), matcher1.group(2));
			} else if (matcher2.matches() && matcher2.group(1).equals(matcher2.group(2))) {
				addValues(context, prefixMap, matcher2.group(1), matcher2.group(3));
			} else if (matcher3.matches() && matcher3.group(1).equals(matcher3.group(3))) {
				addValues(context, prefixMap, matcher3.group(2), matcher3.group(4));
			} else if (matcher4.matches()) {
				addValues(context, matcher4.group(1));
			} else if (attrMatcher.matches()) {
				addValues(context, prefixMap, attrMatcher.group(1), attrMatcher.group(2));
			}
		}
	}

	private void addValues(String parentContext, Map<String, String> prefixMap, String qNameStr, String valueExpr) {
		String valuesContext = parentContext + "/" + parseQName(qNameStr, prefixMap).toString();
		addValues(valuesContext, valueExpr);
	}

	private void addValues(String valuesContext, String valueExpr) {
		valueExpr = valueExpr.replaceFirst("^'", "").replaceFirst("'$", "");
		List<String> valuesList = new ArrayList<String>();
		for (String value : valueExpr.split("', *'")) {
			valuesList.add(value);
		}

		validValues.put(valuesContext, valuesList);
	}

	// Default scope for unit testing.
	String createContext(String path, Map<String, String> prefixMap) {
		if (!CONTEXT_PATTERN.matcher(path).matches()) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		for (String qNameStr : path.split("/")) {
			if (builder.length() > 0) {
				builder.append('/');
			}
			builder.append(parseQName(qNameStr, prefixMap).toString());
		}

		return builder.toString();
	}

	private QName parseQName(String qNameStr, Map<String, String> prefixMap) {
		String[] parts = qNameStr.split(":");
		if (parts.length == 1) {
			return new QName(parts[0]);
		} else {
			return new QName(namespaceForPrefix(parts[0], prefixMap), parts[1]);
		}
	}

	private String namespaceForPrefix(String prefix, Map<String, String> prefixMap) {
		String ns = prefixMap.get(prefix);
		return (ns!=null ? ns : prefix);
	}

	// Default scope for unit testing.
	Map<String, String> getNamespacePrefixes(DOMSource source) throws XPathExpressionException {
		NodeList nsNodes = (NodeList) xPathFactory.newXPath().evaluate(NS_PATH, source, XPathConstants.NODESET);
		Map<String, String> prefixes = new HashMap<String, String>();

		for (int i=0; i < nsNodes.getLength(); ++i) {
			Node nsNode = nsNodes.item(i);
			String uri = nsNode.getAttributes().getNamedItem("uri").getNodeValue();
			String prefix = nsNode.getAttributes().getNamedItem("prefix").getNodeValue();
			prefixes.put(prefix, uri);
		}

		return prefixes;
	}

	/**
	 * Adds the set of valid values to the type of any descendant of an element.
	 *
	 * @param item the starting item
	 * @param parentContext the context of the parent of the element, or null if this is the root element
	 */
	public void addValidValues(LabelElement item, String parentContext) {
		List<String> validValues = getValidValues(parentContext + "/" + getPathComponent(item));
		if (validValues != null && !validValues.isEmpty()) {
			item.getType().setValidValues(validValues.toArray(new String[validValues.size()]));
			if (validValues.size() == 1) {
				item.getType().setDefaultValue(validValues.get(0));
			}
		}
	}

	private String getPathComponent(LabelElement item) {
		if (item instanceof AttributeItem) {
			return "@" + item.getType().getElementName();
		} else {
			return (new QName(item.getType().getElementNamespace(), item.getType().getElementName())).toString();
		}
	}

}
