/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


//package org.apache.oodt.product.handlers.ofsn;
package gov.nasa.pds.transport;

import org.apache.oodt.product.handlers.ofsn.*;

//JDK imports
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


//APACHE imports
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypesFactory;

//OODT imports
import org.apache.oodt.commons.xml.XMLUtils;
import org.apache.oodt.product.handlers.ofsn.metadata.OFSNMetKeys;
import org.apache.oodt.product.handlers.ofsn.metadata.OFSNXMLConfigMetKeys;
import org.apache.oodt.product.handlers.ofsn.metadata.OFSNXMLMetKeys;
import org.apache.oodt.product.handlers.ofsn.metadata.XMLQueryMetKeys;
import org.apache.oodt.product.handlers.ofsn.util.OFSNObjectFactory;
import org.apache.oodt.product.handlers.ofsn.util.OFSNUtils;
//import gov.nasa.pds.transport.utils.OFSNUtils;
import org.apache.oodt.product.LargeProductQueryHandler;
import org.apache.oodt.product.ProductException;
import org.apache.oodt.xmlquery.LargeResult;
import org.apache.oodt.xmlquery.Result;
import org.apache.oodt.xmlquery.XMLQuery;

//APACHE imports
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * An extensible implementation of the PDS-inspired Online File Specification
 * Name (OFSN) style product server. See the ofsn-ps.xml file for a detailed
 * specification of the configuration and motivation behind this product
 * handler.
 *
 * @author mattmann
 * @version $Revision$
 */
public class OFSNFileHandler implements LargeProductQueryHandler,
XMLQueryMetKeys, OFSNXMLMetKeys, OFSNMetKeys, OFSNXMLConfigMetKeys {

	private static final Logger LOG = Logger.getLogger(OFSNFileHandler.class
			.getName());

	private static final String CMD_SEPARATOR = ";";

	// by default return dir size on listing commands
	private boolean computeDirSize = true;

	// by default return file size on listing commands
	private boolean computeFileSize = true;

	private OFSNFileHandlerConfiguration conf;

	private static Map<String, Object> HANDLER_CACHE;

	/** Start of directory path to remove */
	String productDirName = null;
	int lenProductDirName = 0;

	/** Next product ID to generate. */
	private int productID = 0;

	public OFSNFileHandler() throws InstantiationException {
		// init conf here
		String xmlConfigFilePath = System.getProperty(OFSN_XML_CONF_FILE_KEY);
		this.computeDirSize = Boolean.getBoolean(OFSN_COMPUTE_DIR_SIZE);
		this.computeFileSize = Boolean.getBoolean(OFSN_COMPUTE_FILE_SIZE);


		if (xmlConfigFilePath == null) {
			throw new InstantiationException(
					"Must define xml configuration file path via property : ["
							+ OFSN_XML_CONF_FILE_KEY + "]");
		}

		try {
			this.conf = OFSNFileHandlerConfigurationReader
					.getConfig(xmlConfigFilePath);
		} catch (FileNotFoundException e) {
			throw new InstantiationException("xml configuration file: ["
					+ xmlConfigFilePath + "] not found!");
		}

		if (this.conf.getProductRoot() == null) {
			throw new InstantiationException(
					"Must define: [productRoot] attribute in XML configuration!");
		}

		// used to cache handlers -- map of RT type to Get/List handler instance
		HANDLER_CACHE = new HashMap<String, Object>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.oodt.product.QueryHandler#query(org.apache.oodt.xmlquery.XMLQuery)
	 */
	public XMLQuery query(XMLQuery xmlQuery) throws ProductException {
		String ofsn = OFSNUtils.extractFieldFromQuery(xmlQuery, OFSN);
		String cmd = OFSNUtils.extractFieldFromQuery(xmlQuery, RETURN_TYPE);
		validate(ofsn, cmd);
		String cmdId = ofsn + CMD_SEPARATOR + cmd;
		OFSNHandlerConfig cfg = this.conf.getHandlerConfig(cmd);
		validateHandlerConfig(cfg, cmd);

		if (!ofsn.startsWith(File.separator))
			ofsn = File.separator + ofsn;

		String realPath = this.conf.getProductRoot() + ofsn;
		//LOG.log(Level.INFO, "realPath = " + realPath);
		
		if (isListingCmd(cmd)) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			OFSNListHandler handler = getListHandler(cmd, cfg.getClassName());

			if (cfg.getName().equals("DIRLIST1")) {
				//modified this for PDS-338
				File[] fileList = new File[1];
				fileList[0] = new File(realPath);
				generateOFSNXmlForDirlist1(fileList, cfg, outStream);
			}
			else {
				File[] fileList = handler.getListing(realPath);
			    generateOFSNXml(fileList, cfg, outStream);
			}
			xmlQuery.getResults().add(
					new Result(cmdId, XML_MIME_TYPE, null, cmdId, Collections.EMPTY_LIST,
							outStream.toString()));
		} else if (isGetCmd(cmd)) {
			OFSNGetHandler handler = getGetHandler(cmd, cfg.getClassName());

			LOG.log(Level.INFO, "handler name = " + cfg.getClassName());
			String rtAndPath = cmd + CMD_SEPARATOR + realPath;
			String mimeType;

			// check for and use mimetype conf property if available
			if (cfg.getHandlerConf().containsKey(PROPERTY_MIMETYPE_ATTR)) {
				MediaType mediaType = MediaType.parse(cfg.getHandlerConf()
						.getProperty(PROPERTY_MIMETYPE_ATTR));
				if (mediaType == null) {
					LOG.log(Level.WARNING, "MIME type ["
							+cfg.getHandlerConf().getProperty(PROPERTY_MIMETYPE_ATTR)+"] specified "
							+"for handler ["+cfg.getClassName()+"] invalid. Defaulting to MIME type ["
							+MediaType.OCTET_STREAM.toString()+"]");
					mediaType = MediaType.OCTET_STREAM;
				}
				mimeType = mediaType.toString();
			} else { // use default mimetype of product on disk
				try {
                    mimeType = MimeTypesFactory.create().getMimeType(new File(realPath)).getName();
                } catch (Exception ex) {
                    throw new ProductException("failed to get a MIME type: " + ex.getMessage());
                }
			}
			String resourceId = new File(realPath).getName();
			if (cfg.getHandlerConf().containsKey("extension")) {
			  String extension = cfg.getHandlerConf().getProperty("extension");
			  resourceId = FilenameUtils.removeExtension(resourceId) + "." + extension;
			}
			xmlQuery.getResults().add(
					new LargeResult(/* id */rtAndPath,/* mimeType */ mimeType, /* profileID */null,
							/* resourceID */resourceId, Collections.EMPTY_LIST,
							handler.sizeOf(realPath)));
		} else {
			throw new ProductException("return type: [" + cmd + "] is unsupported!");
		}

		return xmlQuery;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.oodt.product.LargeProductQueryHandler#close(java.lang.String)
	 */
	public void close(String id) {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.oodt.product.LargeProductQueryHandler#retrieveChunk(java.lang.String,
	 * long, int)
	 */
	public byte[] retrieveChunk(String id, long offset, int length)
			throws ProductException {
		// unmarshall the return type and path
		String[] rtTypeAndPathArr = id.split(CMD_SEPARATOR);
		String rtType = rtTypeAndPathArr[0];
		String filepath = rtTypeAndPathArr[1];

		OFSNGetHandler handler = getGetHandler(rtType, this.conf
				.getHandlerClass(rtType));

		return handler.retrieveChunk(filepath, offset, length);
	}

	private void generateOFSNXml(File[] mlsFileList, OFSNHandlerConfig cfg,
			OutputStream outStream) {
		XMLUtils.writeXmlToStream(OFSNUtils.getOFSNDoc(Arrays.asList(mlsFileList),
				cfg, this.conf.getProductRoot(), this.computeDirSize, this.computeFileSize),
				outStream);
	}

	private void generateOFSNXmlForDirlist1(File[] mlsFileList, OFSNHandlerConfig cfg,
			OutputStream outStream) {
		//modified for PDS-338
		writeToFile(mlsFileList, cfg.getName(), outStream);
	}

	public void writeToFile(File[] fileArray, String optArg, OutputStream outStream) {
		boolean oneLevel = optArg.equals("DIRLIST1");
		int j;

		for (j=0; j<fileArray.length; j++) {
			if (!fileArray[j].isDirectory()) {
				System.err.println("DirListHandler can't access dir " + fileArray[j]);
				return;
			}
		}

		StringBuffer b = new StringBuffer("");
		productDirName = this.conf.getProductRoot();
		lenProductDirName = productDirName.length()
				+ (productDirName.endsWith(File.separator) ? 0:1);

		b.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(NL);
		b.append("<dirResult xmlns=\"http://oodt.jpl.nasa.gov/xml/namespaces/dirlist/1.0\">").append(NL);
		String filename = null;
		for (j=0; j<fileArray.length; j++) {
			b.append(listDirFiles(fileArray[j], oneLevel));
		}
		b.append("</dirResult>").append(NL);
		try {
			outStream.write(b.toString().getBytes());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// List all directories in the XML file
	private StringBuffer listDirFiles(File dirFile, boolean oneLevel) {
		StringBuffer b = new StringBuffer("");
		File[] fileList = dirFile.listFiles();
		System.out.println("dirFile = " + dirFile.getAbsolutePath() + "    fileList size = " + fileList.length);
		for(int i=0; i<fileList.length; i++) {
			String filename = null;
			if (!fileList[i].isDirectory()) continue;
			if (fileList[i].canRead()) {
				filename = fileList[i].getAbsolutePath();
				if (filename.startsWith(productDirName))  // Strip productDirName
					filename = filename.substring(lenProductDirName);
				if (filename.contains("/.")) continue;  // Kludge to exclude '.' dirs
				b.append("  <dirEntry>").append(NL);
				b.append("    <OFSN>");
				b.append(filename);
				b.append("</OFSN>").append(NL);
				b.append("    <fileSize>");
				b.append(getDirSize(fileList[i]));
				b.append("</fileSize>").append(NL);
				b.append("  </dirEntry>").append(NL);
			}
			else {
				System.out.println("Can't access to this directory...dir = " + fileList[i]);
			}
		}
		if (oneLevel) return b;		// don't traverse sub directories
		for(int i=0; i<fileList.length; i++) {
			if (!fileList[i].isDirectory()) continue;
			b.append(listDirFiles(fileList[i], oneLevel));
		}
		return b;
	}

	// Total length of all files in this directory
	private long getDirSize(File dirFile) {
		long totalLength = 0;
		File[] fileList = dirFile.listFiles();
		for (int i=0; i<fileList.length; i++) {
			if (fileList[i].isDirectory()) continue;    // don't add size of dir
			if (fileList[i].isHidden()) continue;       // don't add size of hidden file
			totalLength += fileList[i].length();
		}
		return totalLength;
	}

	private void validate(String ofsn, String cmd) throws ProductException {
		if (ofsn == null || cmd == null || (ofsn != null && ofsn.equals(""))
				|| (cmd != null && cmd.equals(""))) {
			throw new ProductException("must specify OFSN and RT parameters!");
		} else if (!OFSNUtils.validateOFSN(ofsn)) {
			throw new ProductException("OFSN is invalid");
		}
	}

	private void validateHandlerConfig(OFSNHandlerConfig cfg, String cmd)
			throws ProductException {
		if (cfg == null) {
			throw new ProductException("Unrecognized command: [" + cmd + "]!");
		}
	}

	private OFSNListHandler getListHandler(String rtType, String className) {
		if (HANDLER_CACHE.containsKey(rtType)) {
			return (OFSNListHandler) HANDLER_CACHE.get(rtType);
		} else {
			OFSNListHandler handler = OFSNObjectFactory.getListHandler(className);
			LOG.log(Level.INFO, "Getting handler config for RT: ["+rtType+"]");
			handler.configure(this.conf.getHandlerConfig(rtType).getHandlerConf());
			HANDLER_CACHE.put(rtType, handler);
			return handler;
		}
	}

	private OFSNGetHandler getGetHandler(String rtType, String className) {
		if (HANDLER_CACHE.containsKey(rtType)) {
			return (OFSNGetHandler) HANDLER_CACHE.get(rtType);
		} else {
			OFSNGetHandler handler = OFSNObjectFactory.getGetHandler(className);
			handler.configure(this.conf.getHandlerConfig(rtType).getHandlerConf());
			HANDLER_CACHE.put(rtType, handler);
			return handler;
		}
	}

	private boolean isListingCmd(String cmd) throws ProductException {
		OFSNHandlerConfig cfg = this.conf.getHandlerConfig(cmd);
		if (cfg == null) {
			throw new ProductException("Unrecognized command: [" + cmd + "]!");
		}

		if (cfg.getType().equals(LISTING_CMD)) {
			return true;
		} else
			return false;
	}

	private boolean isGetCmd(String cmd) throws ProductException {
		OFSNHandlerConfig cfg = this.conf.getHandlerConfig(cmd);

		if (cfg.getType().equals(GET_CMD)) {
			return true;
		} else
			return false;
	}

	/** What PDS considers a newline. */
	private static final String NL = "\r\n";
}
