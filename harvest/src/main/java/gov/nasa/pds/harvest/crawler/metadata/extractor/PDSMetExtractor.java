// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.harvest.crawler.metadata.extractor;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.NodeList;

import gov.nasa.jpl.oodt.cas.metadata.MetExtractor;
import gov.nasa.jpl.oodt.cas.metadata.MetExtractorConfig;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.jpl.oodt.cas.metadata.exceptions.MetExtractionException;
import gov.nasa.pds.harvest.context.ReferenceEntry;
import gov.nasa.pds.harvest.crawler.metadata.CoreXPaths;
import gov.nasa.pds.harvest.crawler.metadata.PDSCoreMetKeys;
import gov.nasa.pds.harvest.util.XMLExtractor;

public class PDSMetExtractor implements MetExtractor, PDSCoreMetKeys {
	private PDSMetExtractorConfig config;
	
	public PDSMetExtractor(PDSMetExtractorConfig config) {
		this.config = config;
	}
	
	public Metadata extractMetadata(File product) throws MetExtractionException {
		XMLExtractor xmlExtractor = null;
		Metadata metadata = new Metadata();
		String objectType = "";
		String logicalID = "";
		String version = "";
		String title = "";
		NodeList references = null;
		
		try {
			xmlExtractor = new XMLExtractor(product);
		} catch (Exception e) {
			throw new MetExtractionException("Parse failure: " + e.getMessage());
		}
		try {
			objectType = xmlExtractor.getValueFromDoc(CoreXPaths.map.get(OBJECT_TYPE));
			logicalID = xmlExtractor.getValueFromDoc(CoreXPaths.map.get(LOGICAL_ID));
			version = xmlExtractor.getValueFromDoc(CoreXPaths.map.get(PRODUCT_VERSION));
			title = xmlExtractor.getValueFromDoc(CoreXPaths.map.get(TITLE));
			references = xmlExtractor.getNodesFromDoc(CoreXPaths.map.get(REFERENCES));
		} catch (XPathExpressionException x) {
			//TODO: getMessage() doesn't always return a message
			throw new MetExtractionException(x.getMessage());
		}
		metadata.addMetadata(LOGICAL_ID, logicalID);
		metadata.addMetadata(PRODUCT_VERSION, version);
		metadata.addMetadata(TITLE, title);
		metadata.addMetadata(OBJECT_TYPE, objectType);
		if((objectType != null) && (config.hasObjectType(objectType))) {
			List<String> metXPaths = new ArrayList<String>();
			metXPaths.addAll(config.getMetXPaths(objectType));
			for(String xpath : metXPaths) {
				try {
					NodeList list = xmlExtractor.getNodesFromDoc(xpath);
					for(int i=0; i < list.getLength(); i++) {
						metadata.addMetadata(list.item(i).getNodeName(), xmlExtractor.getValuesFromDoc(xpath));
					}
				} catch (XPathExpressionException xe) {
					throw new MetExtractionException("Bad XPath Expression: " + xpath);
				}
			}
		}
		List<ReferenceEntry> refEntries = new ArrayList<ReferenceEntry>();
		String name = "";
		String value = "";
		for(int i=0; i < references.getLength(); i++) {
			try {
				NodeList children = xmlExtractor.getNodesFromItem("*", references.item(i));
				ReferenceEntry re = new ReferenceEntry();
				for(int j=0; j < children.getLength(); j++) {
					name = children.item(j).getNodeName();
					value = children.item(j).getTextContent();
					if(name.equals("lidvid_reference")) {
						try {
							re.setLogicalID(value.split("::")[0]);
							re.setVersion(value.split("::")[1]);
						} catch (ArrayIndexOutOfBoundsException ae) {
							throw new MetExtractionException(
								"Expected a LID-VID reference, but found this: "
								+ value);
						}
					} else if(name.equals("lid_reference")) {
						re.setLogicalID(value);
					} else if(name.equals("reference_association_type")) {
						re.setAssociationType(value);
					} else if(name.equals("referenced_object_type")) {
						re.setObjectType(value);
					}
				}
				refEntries.add(re);
			} catch (Exception e) {
				throw new MetExtractionException(e.getMessage());
			}
		}
		metadata.addMetadata(REFERENCES, refEntries);
		
		return metadata;
	}

	public Metadata extractMetadata(String product) throws MetExtractionException {
		return extractMetadata(new File(product));
	}

	public Metadata extractMetadata(URL product) throws MetExtractionException {
		return extractMetadata(product.toExternalForm());
	}

	public Metadata extractMetadata(File product, File configFile)
			throws MetExtractionException {
		// No need to implement at this point
		return null;
	}

	public Metadata extractMetadata(File product, String configFile)
			throws MetExtractionException {
		// No need to implement at this point
		return null;
	}

	public Metadata extractMetadata(File product, MetExtractorConfig config)
			throws MetExtractionException {
		setConfigFile(config);
		return extractMetadata(product);
	}

	public Metadata extractMetadata(URL product, MetExtractorConfig config)
			throws MetExtractionException {
		setConfigFile(config);
		return extractMetadata(product);
	}

	public void setConfigFile(File configFile) throws MetExtractionException {
		// No need to implement at this point
	}

	public void setConfigFile(String configFile) throws MetExtractionException {
		// No need to implement at this point		
	}

	public void setConfigFile(MetExtractorConfig config) {
		this.config = (PDSMetExtractorConfig) config;	
	}

}
