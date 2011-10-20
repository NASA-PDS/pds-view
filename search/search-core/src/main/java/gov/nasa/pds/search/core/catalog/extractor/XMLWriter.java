/**
 * 
 */
package gov.nasa.pds.search.core.catalog.extractor;

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

			write(map);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void write(Map map) {
		String name = "";
		String value = "";
		ArrayList valArray;
		try {
			Set set = map.keySet();
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				name = (String) iter.next();
				valArray = (ArrayList) map.get(name);
				for (Iterator i = valArray.iterator(); i.hasNext();) {
					value = (String) i.next();
					// log.info("name: "+name);
					// log.info("value: "+value);
					addElement(name, value, isCleanedAttr(name));
				}
			}

			// set up transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();

			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			// create file from xml tree
			StreamResult result = new StreamResult(new File(basedir, filename));

			DOMSource source = new DOMSource(doc);
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

	public void addElement(String name, String value, boolean clean) {
		// Temporary variables to hold name and value
		String tName, tValue;

		tName = name.trim();
		tValue = value.trim();

		// Utilize JTidy servlet extension to encode all non-letter characters
		// tName = HTMLEncode.encode(tName);
		// tValue = HTMLEncode.encode(tValue);

		if (clean) {
			tValue = tValue.toLowerCase();
			tValue = tValue.replace(' ', '_');
		}

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

	public boolean isCleanedAttr(String s1) {
		String[] elemfacet = { "identifier", "title", "format", "description",
				"publisher", "language", "resContext", "resClass",
				"resLocation", "data_set_terse_desc", "data_set_desc",
				"mission_desc", "target_desc", "host_desc", "instrument_desc",
				"volume_name", "volume_desc" };

		for (int ind = 0; ind < elemfacet.length; ind++) {
			if (s1.compareTo(elemfacet[ind]) == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
