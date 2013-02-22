//	Copyright 2009-2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id
//
package gov.nasa.pds.search.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author jpadams
 * 
 */
public class XMLWriter {

	private Map map;
	private File basedir;
	private String filename;
	private String fname = "";
	private String fnameprefix = "tse";
	private String fnameext = "xml";

	private Document doc;
	private Element classElement;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public XMLWriter(Map map, File basedir, int seq, String classname) {
		try {
			this.map = map;
			this.basedir = basedir;

			filename = getFilename(seq, classname);

			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();

			Element root = doc.createElement("doc");
			doc.appendChild(root);

			classElement = doc.createElement(classname);
			root.appendChild(classElement);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write() {
		String name = "";
		String value = "";
		ArrayList valArray;
		try {
			Set set = this.map.keySet();
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				name = (String) iter.next();
				valArray = (ArrayList) this.map.get(name);
				for (Iterator i = valArray.iterator(); i.hasNext();) {
					value = (String) i.next();
					// log.info("name: "+name);
					// log.info("value: "+value);
					addElement(name, value);
				}
			}

			// set up transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();

			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			// create file from xml tree
			StreamResult result = new StreamResult(new File(basedir, filename));

			DOMSource source = new DOMSource(this.doc);
			trans.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getFilename(int seq, String classname) {
		Integer oidseqi = new Integer(seq);
		String itemoid = oidseqi.toString();

		/* Start profile output */
		String fname = fnameprefix + "_" + classname + "_" + itemoid + "."
				+ fnameext;

		// log.info("Filename: "+fname);

		return fname;
	}

	public void addElement(String name, String value) {
		// Temporary variables to hold name and value
		String tName, tValue;

		tName = name.trim();
		tValue = value.trim();

		// Previous method used to encode only select HTML entities
		tName = repAllCharWStr(tName);
		tValue = repAllCharWStr(tValue);

		Element element = doc.createElement(tName);
		classElement.appendChild(element);

		Text text = doc.createTextNode(tValue);
		element.appendChild(text);
	}

	/**
	 * Replace HTML entities & with &amp;, < with &lt;, > with &gt;, and " with
	 * &quot;
	 */
	public String repAllCharWStr(String s1) {
		String s2;

		s2 = repCharWStr(remNull(s1), '&', "&amp;");
		s2 = repCharWStr(s2, '<', "&lt;");
		s2 = repCharWStr(s2, '>', "&gt;");
		s2 = repCharWStr(s2, '\"', "&quot;");
		return s2;
	}

	/**
	 * Remove String Nulls
	 */
	public String remNull(String s1) {
		if (s1 == null) {
			return "NULL";
		}
		return s1;
	}

	/**
	 * Replace character with string
	 */
	public String repCharWStr(String str1, char rc, String rstr) {
		int p1, str1len;
		char tc;
		StringBuffer sbuff1 = new StringBuffer(str1), sbuff2 = new StringBuffer();

		p1 = 0;
		str1len = sbuff1.length();
		while (p1 < str1len) {
			tc = sbuff1.charAt(p1);
			if (tc == rc) {
				sbuff2.append(rstr);
			} else {
				sbuff2.append(tc);
			}
			p1++;
		}
		return sbuff2.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
