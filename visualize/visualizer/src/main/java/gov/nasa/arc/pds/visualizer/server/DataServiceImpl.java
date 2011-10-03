package gov.nasa.arc.pds.visualizer.server;


import gov.nasa.arc.pds.visualizer.client.DataService;
import gov.nasa.arc.pds.visualizer.shared.Constants;
import gov.nasa.pds.domain.Array2DImageProduct;
import gov.nasa.pds.domain.Collection;
import gov.nasa.pds.domain.DocumentProduct;
import gov.nasa.pds.domain.PDSObject;
import gov.nasa.pds.objectAccess.ArchiveLocator;
import gov.nasa.pds.objectAccess.ImageConverter;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/** Implements Data Service API. */
public class DataServiceImpl extends RemoteServiceServlet implements DataService {
	
	Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);
	private static final long serialVersionUID = 1L;
	private String servletRealPath = null;
	private static int pageSize;
	private Properties props = null;

	public DataServiceImpl() {
		super();
		init_properties();
	}

	private void init_properties() {
		if (props != null) return; 
		props = new Properties(); 
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(Constants.appProperties);
		try {
			props.load(inputStream);
		} catch (Exception e) {
			logger.error("RemoteServiceServlet,init: could not open properties resource:",e);	
		}
		pageSize = Integer.parseInt(props.getProperty("pageSize","2000"));
	}

	@Override
	public void init() {
		
		init_properties();
		ServletContext sc = this.getServletContext();
		//  sc.getContextPath() NoSuchElement for Servlet version < 2.5
		servletRealPath = sc.getRealPath("/Visualizer.html"); 
		File f = new File(servletRealPath);
		servletRealPath = f.getParent() + "/";

		int minorVersion =  sc.getMinorVersion();
		int majorVersion =  sc.getMajorVersion();
		logger.debug("RemoteServiceServlet,init: ServletMajor/MinorVersion:"+majorVersion+"."+minorVersion);	
		logger.debug("RemoteServiceServlet,init: , RealPath :"+servletRealPath);
	}

	@Override
	public PDSObject[] fetchToplevel() throws Exception {

		logger.error("fetchToplevel DataService...");
		init_properties();
		List<PDSObject> rv = new ArrayList<PDSObject>();
		Enumeration<?> enumeration = props.propertyNames();
		ObjectProvider oa = null;

		while (enumeration.hasMoreElements()) {
			String key = (String)enumeration.nextElement();
			if (key.startsWith("topLevel")) {
				String fqFilename = props.getProperty(key);
				String filename = fqFilename.substring(fqFilename.lastIndexOf('/') + 1);
				String archiveRoot = fqFilename.substring(0,fqFilename.lastIndexOf('/'));
				oa = new ObjectAccess(archiveRoot);

				PDSObject pdsObject = null;
				try {
					pdsObject = oa.makePDS4Object(filename); 
				} catch (Exception e) {
					pdsObject = new Collection(archiveRoot, filename);
					// errors caught and logged, to allow top level to open if some files are valid and others invalid
					logger.error("top level file not found: {}", fqFilename);
				}
				rv.add(pdsObject);
			}
		}
		return rv.toArray(new PDSObject[rv.size()]);
	}

	@Override
	public PDSObject[] parseImageProduct(String archiveRoot, String readDataFromFile) throws Exception {	

		logger.debug(archiveRoot +" "+ readDataFromFile);
		PDSObject pdsObject = null;
		
		try {
			ObjectProvider oa = new ObjectAccess(archiveRoot);
			pdsObject = oa.parseImageProduct(readDataFromFile);	

			Iterator<PDSObject> l = pdsObject.childrenIterator();
			while (l.hasNext()) {
		        PDSObject newChild = l.next();
				Array2DImageProduct imageChild = (Array2DImageProduct)newChild;
				final int cols = imageChild.getArray_axis_1_elements();
				final int rows = imageChild.getArray_axis_2_elements(); 
				String url = processImage(archiveRoot, imageChild.getThis_file_name(), rows, cols);
				imageChild.setUrl(url);
			}		

		} 	catch (Exception e) {
			logger.error("DataServiceImpl fetchImageData: Exception: ",e);
			throw new IOException("DataServiceImpl fetchImageData: Exception: "+ e.getMessage());
		}	
		return pdsObject.childrenAsArray();	
	}

	@Override
	public PDSObject[] parseTableCharacterProduct(String archiveRoot, String readDataFromFile) throws Exception {
		
		logger.debug(archiveRoot +" "+ readDataFromFile);
		PDSObject pdsObject = null;
		try {
			ObjectProvider oa = new ObjectAccess(archiveRoot);
			pdsObject = oa.parseTableCharacterProduct(readDataFromFile, pageSize);  		
			
		} 	catch (Exception e) {
			logger.error("DataServiceImpl fetchTableData: fetchTableData: ",e);
			throw new IOException("DataServiceImpl fetchImageData: Exception: "+ e.getMessage());
		}	
		return pdsObject.childrenAsArray();	 
	}
	
	@Override
	public PDSObject[] parseDocumentProduct(String archiveRoot, String readDataFromFile) throws Exception {
		
		logger.debug(archiveRoot +" "+ readDataFromFile);
		PDSObject pdsObject = null;
		try {
			ObjectProvider oa = new ObjectAccess(archiveRoot);
			pdsObject = oa.parseDocumentProduct(readDataFromFile);

			Iterator<PDSObject> l = pdsObject.childrenIterator();
			while (l.hasNext()) {
				DocumentProduct docChild = (DocumentProduct) l.next();
				String url = this.processDocument(archiveRoot, docChild);
				docChild.setUrl(url);
			}

		} 	catch (Exception e) {
			logger.error("DataServiceImpl fetchDocumentData: Exception: ",e);
			throw new IOException("DataServiceImpl fetchDocumentData: Exception: "+ e.getMessage());
		}	
		return pdsObject.childrenAsArray();	
	}

	@Override
	public PDSObject[] parseCollection(String archiveRoot, String readDataFromFile) throws Exception {	
		
		logger.debug(archiveRoot +" "+ readDataFromFile);
		PDSObject pdsObject = null;
		try {
			ObjectProvider oa = new ObjectAccess(archiveRoot);			
			pdsObject = oa.parseCollection(readDataFromFile); 

		} 	catch (Exception e) {
			logger.error("DataServiceImpl fetchChildData: Exception: ",e);
			throw new IOException("DataServiceImpl fetchChildData: Exception: "+ e.getMessage());
		}	
		return pdsObject.childrenAsArray();	
	}

	  /**
	   * Converts from raw image to a viewable image.  Creates an output file in the webapp location.
	   * @param archiveRoot the root of the PDS archive
	   * @param inputRelativeFilename - XML file of the data source, relative to archiveRoot.
	   * @param rowCount number of sample rows
	   * @param columnCount number of sample columns
	   * @return URL of the result
	   * @throws Exception upon error
	   */
	String processImage(String archiveRoot, String inputRelativeFilename, final int rows, final int cols) throws Exception {

		assert !inputRelativeFilename.endsWith("xml");
		String fqInputFilename = ArchiveLocator.resolveDataItemLocation(archiveRoot, inputRelativeFilename);
		String absolutePrefix = (this.servletRealPath == null) ? "." : this.servletRealPath;
		String destURL = Constants.moduleName + Constants.IMG_CACHE + inputRelativeFilename; 
		String absoluteWebapp = absolutePrefix + destURL;

		if (Array2DImageProduct.isConvertableImage(fqInputFilename)) {
			destURL = replaceExtension(destURL, Constants.PNG);
			String fqConvertedFilename = replaceExtension(fqInputFilename, Constants.PNG);
			absoluteWebapp = replaceExtension(absoluteWebapp, Constants.PNG);
			if (!isCached(absoluteWebapp)) {
				ImageConverter imageConverter = ImageConverter.getInstance();
				imageConverter.convert(fqInputFilename, fqConvertedFilename, rows, cols);
				copyfile(fqConvertedFilename, absoluteWebapp);
			}
		} else {
			if (!isCached(absoluteWebapp)) {
				copyfile(fqInputFilename, absoluteWebapp); // eg, copy file.GIF to webapp with out conversion
			}
		}
		return destURL;
	}

    String processDocument(String archiveRoot, DocumentProduct leadDoc) throws Exception {

		String inputRelativeFilename = leadDoc.getThis_file_name();
		String destURL = Constants.moduleName + Constants.DOC_CACHE + inputRelativeFilename; 
		String fqInputFilename = ArchiveLocator.resolveDataItemLocation(archiveRoot, inputRelativeFilename);
		String absolutePrefix = (this.servletRealPath == null) ? "." : this.servletRealPath;
		String absoluteWebapp = absolutePrefix + destURL;

		if (!isCached(absoluteWebapp)) {
			copyfile(fqInputFilename, absoluteWebapp); 
			if (leadDoc.hasVisualElements()) {
				for (String e : leadDoc.getVisualElements()) {
					String iFile = new File(fqInputFilename).getParent() +"/"+ e; 
					String oFile = new File(absoluteWebapp).getParent() +"/"+ e;
					copyfile(ArchiveLocator.verifyFileExistsOnPlatform(iFile), oFile); 
				}
			}		
		}
		return destURL;
	}

	/**
	 * Determines if this web resource has already been cached.
	 * @param webResource fully qualified web resource
	 * @return true if image is available to the webapp
	 */
	private boolean isCached(String webResource) {
		return new File(webResource).exists();
	}

	private String copyfile(String srFile, String dtFile) throws Exception{
		InputStream in = null;
		OutputStream out = null;
		logger.debug("copyfile src dest :"+srFile+" "+dtFile);
		try{
			File f1 = new File(srFile);
			File f2 = new File(dtFile);

			String parent = f2.getParent();
			boolean success = (new File(parent)).mkdirs();
			if (success) {
				logger.debug("Directories: "  + parent + " created");
			} 
			in = new FileInputStream(f1);
			out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}

		}  finally {
			if (in != null) in.close();
			if (out != null) out.close();
		}
		return dtFile;
	}

	/** swaps current file extension for a new extension.*/
	private static String replaceExtension(String fn, String newExt) {
		return fn.substring(0, fn.lastIndexOf('.') + 1) + newExt;
	}
}

	