package gov.nasa.pds.harvest.context;

import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.File;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InventoryXMLFileReader {
	public static final String MEMBER_ENTRY = "//Standard_Product_Member_Entry";
	private String parentDirectory;
	private int index;
	private XMLExtractor extractor;
	private NodeList memberEntries;
	
	public InventoryXMLFileReader(File file) throws InventoryFileReaderException {
		index = 0;
		parentDirectory = file.getParent();
		try {
			extractor = new XMLExtractor(file);
			memberEntries = extractor.getNodesFromDoc(MEMBER_ENTRY);			
		} catch (Exception e) {
			throw new InventoryFileReaderException("Error reading inventory file: " + e.getMessage());
		}
	}
	
	public InventoryEntry getNext() throws InventoryFileReaderException {
		Node entry = memberEntries.item(index++);
		File file = null;
		String checksum = null;
		try {
			file = new File(extractor.getValueFromItem("directory_path_name", entry));
			checksum = extractor.getValueFromItem("md5_checksum", entry);
		} catch (XPathExpressionException x) {
			throw new InventoryFileReaderException(x.getMessage());
		}
		if(!file.isAbsolute()) {
			file = new File(parentDirectory, file.toString());
		}
		return new InventoryEntry(file, checksum);	
	}
	
	public boolean isEOF() {
		if(index < memberEntries.getLength())
			return false;
		else
			return true;
	}
}
