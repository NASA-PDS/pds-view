package gov.nasa.pds.web.ui.utils;

import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.FileAreaObservationalSupplemental;
import gov.nasa.arc.pds.xml.generated.GroupFieldBinary;
import gov.nasa.arc.pds.xml.generated.GroupFieldCharacter;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.PackedDataFields;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.web.applets.CancelledException;
import gov.nasa.pds.web.ui.containers.ColumnInfo;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.tabularData.Column;
import gov.nasa.pds.web.ui.containers.tabularData.Element;
import gov.nasa.pds.web.ui.containers.tabularData.Row;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

/**
 * Reads PDS4 labels, extracts the label's metadata, builds
 * temporary tables, and populates the tables 
 * 
 * @author ghflore1
 *
 */
public class TabularPDS4DataLoader extends TabularDataLoader {

	/**
	 *	Defines type of label this loader processes
	 */
	private final String PDS_LABEL_TYPE = "pds4";
	private File xmlFile = null;
	private URL xmlFileURL = null;

	public TabularPDS4DataLoader(String labelURLString, File xmlFile,
			URL dataTableURL, URL xmlFileURL) {
		super(labelURLString);
		
		this.xmlFile = xmlFile;
		this.xmlFileURL = xmlFileURL;
		
		try {
			readSupportedObjects();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO LAB specify this slice object is not valid in a way that can
			// be polled
			throw new RuntimeException("problem creating slice:  " //$NON-NLS-1$
					+ e.getMessage());
		}
	}
	
	/*
	 * this constructor does NOT need to call readSupportedOjects because
	 * sliceContainer is passed in nor parse the label
	 */
	public TabularPDS4DataLoader(SliceContainer slice,
			final StatusContainer status, final String sessionId) {
		super(slice,status,sessionId);
	}
	
	/*
	 * gets labelcontainer's tabularPointers and passes them and the
	 * ObjectStatement for each pointer, including external structures, to
	 * BuildTabularDataContainer, which creates a TabularDataContainer out of
	 * it, which is then added to the slice. also inserts columns for the object
	 * statements into column table for persistence.
	 */
	private void readSupportedObjects() throws Exception {

		ObjectProvider objectAccess = new ObjectAccess();
		ProductObservational product = null;

		try {
			product = objectAccess.getProduct(xmlFile,
					ProductObservational.class);
		} catch (gov.nasa.pds.objectAccess.ParseException e) {
			e.printStackTrace();
		}

		// the label could have multiple fileAreaObservational's
		List<FileAreaObservational> fileAreaList = product
				.getFileAreaObservationals();
		
		// the label could have multiple fileAreaObservationalSupplemental
		List<FileAreaObservationalSupplemental> fileAreaSupplementalList = product
				.getFileAreaObservationalSupplementals();

		// fileArea are required, so there should at least be one in the xml
		if (!fileAreaList.isEmpty()) {

			for (FileAreaObservational fileAreaObservational : fileAreaList) {		

				// for each instance of fileArea
				for (Object obj : objectAccess.getTableObjects(fileAreaObservational)) {
					
					TabularDataContainer tabularDataContainer = buildTabularDataContainer(
							objectAccess, obj, fileAreaObservational.getFile().getFileName());
					
					// Save information to display to the user
					StringBuilder fileInformation = new StringBuilder();
					extractFileInformation(fileInformation, fileAreaObservational);
					tabularDataContainer.setFileInformation(fileInformation.toString());
					
					// add tabularContainer into the SliceContainer so information is passed
					// between processes
					this.slice.addTabularDataObject(tabularDataContainer);
					
					// insert information for each column into temporary table
					insertColumns(tabularDataContainer,
							this.slice.getLabelURLString());
				}
			}

		}else {
			throw new Exception("No file area observations found in the label.");
		}
		
		// fileAreaSupplemental are optional, so no exception thrown
		if (!fileAreaSupplementalList.isEmpty()) {

			// extract data for each of the fileAreaObservationalSupplemental
			for (FileAreaObservationalSupplemental fileAreaObservationalSupplemental : fileAreaSupplementalList) {

				for (Object obj : objectAccess
						.getTableObjects(fileAreaObservationalSupplemental)) {

					TabularDataContainer tabularDataContainer = buildTabularDataContainer(
							objectAccess, obj,
							fileAreaObservationalSupplemental.getFile()
									.getFileName());
					
					// Save information to display to the user
					StringBuilder fileInformation = new StringBuilder();
					extractFileInformation(fileInformation, fileAreaObservationalSupplemental);
					tabularDataContainer.setFileInformation(fileInformation.toString());

					// add tabularContainer into the SliceContainer so
					// information is passed
					// between processes
					this.slice.addTabularDataObject(tabularDataContainer);

					// insert information for each column into temporary table
					insertColumns(tabularDataContainer,
							this.slice.getLabelURLString());
				}
			}
		}
	}
	
	
	private void extractTableInformation(StringBuilder tableInformation,
			Object object) {

		// TableCharacter instance
		if (object instanceof TableCharacter) {

			TableCharacter tableCharacter = (TableCharacter) object;
			
			// Optional
			// name
			if (tableCharacter.getName() != null
					&& !tableCharacter.getName().isEmpty()) {
				tableInformation.append("- File Name: " + "\t\t"
						+ tableCharacter.getName());
				tableInformation.append("\n");
			}

			// local identifier
			if (tableCharacter.getLocalIdentifier() != null
					&& !tableCharacter.getLocalIdentifier().isEmpty()) {
				tableInformation.append("- Local Idendifier: " + "\t"
						+ tableCharacter.getLocalIdentifier());
				tableInformation.append("\n");
			}
			
			// description
			if (tableCharacter.getDescription() != null
					&& !tableCharacter.getDescription().isEmpty()) {
				tableInformation.append("- Description: " + "\t\t"
						+ tableCharacter.getDescription());
				tableInformation.append("\n");
			}
			
			// Required
			// offset
			if (tableCharacter.getOffset() != null) {
				tableInformation.append("- Offset: " + "\t\t\t"
						+ tableCharacter.getOffset().getValue() + " [unit = "
						+ tableCharacter.getOffset().getUnit() + "]");
				tableInformation.append("\n");
			}
			
			// records
			tableInformation.append("- Records: " + "\t\t"
					+ tableCharacter.getRecords());
			tableInformation.append("\n");

			// record delimiter
			tableInformation.append("- Record Delimiter: " + "\t"
					+ tableCharacter.getRecordDelimiter());
			tableInformation.append("\n");
			
			// Record Character
			// fields
			tableInformation.append("- Fields: " + "\t\t\t"
					+ tableCharacter.getRecordCharacter().getFields());
			tableInformation.append("\n");

			// groups		
			tableInformation.append("- Groups: " + "\t\t\t"
					+ tableCharacter.getRecordCharacter().getGroups());
			tableInformation.append("\n");

			// record length
			tableInformation.append("- Record Length: " + "\t"
					+ tableCharacter.getRecordCharacter().getRecordLength().getValue()
					+ "[unit = " + tableCharacter.getRecordCharacter().getRecordLength().getUnit() + "]");
			tableInformation.append("\n");
			
			// TableDelimited instance
		} else if (object instanceof TableDelimited) {

			TableDelimited tableDelimited = (TableDelimited) object;
			
			// Optional
			// name
			if (tableDelimited.getName() != null
					&& !tableDelimited.getName().isEmpty()) {
				tableInformation.append("- Name: " + "\t\t\t\t"
						+ tableDelimited.getName());
				tableInformation.append("\n");
			}

			// local identifier
			if (tableDelimited.getLocalIdentifier() != null
					&& !tableDelimited.getLocalIdentifier().isEmpty()) {
				tableInformation.append("- Local Idendifier: " + "\t\t"
						+ tableDelimited.getLocalIdentifier());
				tableInformation.append("\n");
			}
			
			// object length
			if (tableDelimited.getObjectLength() != null) {
				tableInformation.append("- Object Length: " + "\t\t"
						+ tableDelimited.getObjectLength().getValue()
						+ " [unit = "
						+ tableDelimited.getObjectLength().getUnit() + "]");
				tableInformation.append("\n");
			}

			// description
			if (tableDelimited.getDescription() != null
					&& !tableDelimited.getDescription().isEmpty()) {
				tableInformation.append("- Description: " + "\t\t\t"
						+ tableDelimited.getDescription());
				tableInformation.append("\n");
			}

			// Required
			
			// offset
			if (tableDelimited.getOffset() != null) {
				tableInformation.append("- Offset: " + "\t\t\t\t"
						+ tableDelimited.getOffset().getValue() + " [unit = "
						+ tableDelimited.getOffset().getUnit() + "]");
				tableInformation.append("\n");
			}
			
			// parsing standard id
			if (tableDelimited.getParsingStandardId() != null
				&& !tableDelimited.getParsingStandardId().isEmpty()) {
				tableInformation.append("- Parsing Standard ID: " + "\t"
						+ tableDelimited.getParsingStandardId());
				tableInformation.append("\n");
			}

			// records
			tableInformation.append("- Records: " + "\t\t\t"
					+ tableDelimited.getRecords());
			tableInformation.append("\n");

			// record delimiter
			tableInformation.append("- Record Delimiter: " + "\t\t"
					+ tableDelimited.getRecordDelimiter());
			tableInformation.append("\n");
			
			// field delimiter
			tableInformation.append("- Field Delimiter: " + "\t\t"
					+ tableDelimited.getFieldDelimiter());
			tableInformation.append("\n");
			
			// Record Binary
			// required
			// fields
			tableInformation.append("- Fields: " + "\t\t\t\t"
					+ tableDelimited.getRecordDelimited().getFields());
			tableInformation.append("\n");

			// groups
			tableInformation.append("- Groups: " + "\t\t\t\t"
					+ tableDelimited.getRecordDelimited().getGroups());
			tableInformation.append("\n");

			// optional
			// record length
			if (tableDelimited.getRecordDelimited().getMaximumRecordLength() != null) {
				tableInformation.append("- Max Record Length: "
						+ "\t"
						+ tableDelimited.getRecordDelimited()
								.getMaximumRecordLength().getValue()
						+ "[unit = "
						+ tableDelimited.getRecordDelimited()
								.getMaximumRecordLength().getUnit() + "]");
				tableInformation.append("\n");
			}

			// TableBinary instance
		} else if (object instanceof TableBinary) {

			TableBinary tableBinary = (TableBinary) object;

			// Optional
			// name
			if (tableBinary.getName() != null
					&& !tableBinary.getName().isEmpty()) {
				tableInformation.append("- Name: " + "\t\t\t"
						+ tableBinary.getName());
				tableInformation.append("\n");
			}

			// local identifier
			if (tableBinary.getLocalIdentifier() != null
					&& !tableBinary.getLocalIdentifier().isEmpty()) {
				tableInformation.append("- Local Idendifier: " + "\t"
						+ tableBinary.getLocalIdentifier());
				tableInformation.append("\n");
			}
			
			// description
			if (tableBinary.getDescription() != null
					&& !tableBinary.getDescription().isEmpty()) {
				tableInformation.append("- Description: " + "\t\t"
						+ tableBinary.getDescription());
				tableInformation.append("\n");
			}
			
			// record delimiter
			if (tableBinary.getRecordDelimiter() != null
					&& !tableBinary.getRecordDelimiter().isEmpty()) {
				tableInformation.append("- Record Delimiter: " + "\t"
						+ tableBinary.getRecordDelimiter());
				tableInformation.append("\n");
			}
			
			// Required
			// offset
			if (tableBinary.getOffset() != null) {
				tableInformation.append("- Offset: " + "\t\t\t"
						+ tableBinary.getOffset().getValue() + " [unit = "
						+ tableBinary.getOffset().getUnit() + "]");
				tableInformation.append("\n");
			}

			// records
			tableInformation.append("- Records: " + "\t\t"
					+ tableBinary.getRecords());
			tableInformation.append("\n");
			
			// Record Binary
			// fields
			tableInformation.append("- Fields: " + "\t\t\t"
					+ tableBinary.getRecordBinary().getFields());
			tableInformation.append("\n");
			
			// groups		
			tableInformation.append("- Groups: " + "\t\t\t"
					+ tableBinary.getRecordBinary().getGroups());
			tableInformation.append("\n");
			
			// record length
			tableInformation.append("- Record Length: " + "\t"
					+ tableBinary.getRecordBinary().getRecordLength().getValue()
					+ "[unit = " + tableBinary.getRecordBinary().getRecordLength().getUnit() + "]");
			tableInformation.append("\n");
			
		}
	}
	
	/**
	 * Extract file and table information from FileAreaObservational and
	 * FileAreaObservationalSupplemental objects
	 * 
	 * @param fileInformation
	 * @param tableInformation
	 * @param fileAreaObservational
	 */
	private void extractFileInformation(StringBuilder fileInformation, Object object) {
		
		if(object instanceof FileAreaObservational) {

			FileAreaObservational fileAreaObservational = (FileAreaObservational) object;
			
			// required
			fileInformation.append("- File Name: " + "\t\t\t"
					+ fileAreaObservational.getFile().getFileName());
			fileInformation.append("\n");

			// the rest are optional. Therefore, if these are not specified, do
			// not show them
			// local identifier
			if (fileAreaObservational.getFile().getLocalIdentifier() != null
					&& !fileAreaObservational.getFile().getLocalIdentifier()
							.isEmpty()) {
				fileInformation.append("- Local Identifier: " + "\t\t"
						+ fileAreaObservational.getFile().getLocalIdentifier());
				fileInformation.append("\n");
			}

			// creation_date_time
			if (fileAreaObservational.getFile().getCreationDateTime() != null
					&& !fileAreaObservational.getFile().getCreationDateTime()
							.isEmpty()) {
				fileInformation
						.append("- Creation Date-Time: "
								+ "\t"
								+ fileAreaObservational.getFile()
										.getCreationDateTime());
				fileInformation.append("\n");
			}

			// file_size
			if (fileAreaObservational.getFile().getFileSize() != null) {
				fileInformation.append("- File Size: "
						+ "\t\t\t"
						+ fileAreaObservational.getFile().getFileSize()
								.getValue()
						+ " [unit = "
						+ fileAreaObservational.getFile().getFileSize()
								.getUnit() + "]");
				fileInformation.append("\n");
			}

			// records
			if (fileAreaObservational.getFile().getRecords() != null) {
				fileInformation.append("- Records: " + "\t\t\t"
						+ fileAreaObservational.getFile().getRecords());
				fileInformation.append("\n");
			}

			// md5_checksum
			if (fileAreaObservational.getFile().getMd5Checksum() != null
					&& !fileAreaObservational.getFile().getMd5Checksum()
							.isEmpty()) {
				fileInformation.append("- MD5 Checksum: " + "\t\t"
						+ fileAreaObservational.getFile().getMd5Checksum());
				fileInformation.append("\n");
			}

			// comment
			if (fileAreaObservational.getFile().getComment() != null
					&& !fileAreaObservational.getFile().getComment().isEmpty()) {
				fileInformation.append("- Comment: " + "\t\t\t"
						+ fileAreaObservational.getFile().getComment());
				fileInformation.append("\n");
			}
			// FileAreaObservationalSupplemental
		} else if(object instanceof FileAreaObservationalSupplemental) {

			FileAreaObservationalSupplemental fileAreaObservationalSupplemental = (FileAreaObservationalSupplemental) object;

			// required
			fileInformation
			.append("- File Name: "
					+ "\t\t\t"
					+ fileAreaObservationalSupplemental.getFile()
					.getFileName());
			fileInformation.append("\n");

			// the rest are optional. Therefore, if these are not specified, do
			// not show them
			// local identifier
			if (fileAreaObservationalSupplemental.getFile()
					.getLocalIdentifier() != null
					&& !fileAreaObservationalSupplemental.getFile()
					.getLocalIdentifier().isEmpty()) {
				fileInformation.append("- Local Identifier: "
						+ "\t\t"
						+ fileAreaObservationalSupplemental.getFile()
						.getLocalIdentifier());
				fileInformation.append("\n");
			}

			// creation_date_time
			if (fileAreaObservationalSupplemental.getFile()
					.getCreationDateTime() != null
					&& !fileAreaObservationalSupplemental.getFile()
					.getCreationDateTime().isEmpty()) {
				fileInformation.append("- Creation Date-Time: "
						+ "\t"
						+ fileAreaObservationalSupplemental.getFile()
						.getCreationDateTime());
				fileInformation.append("\n");
			}

			// file_size
			if (fileAreaObservationalSupplemental.getFile().getFileSize() != null) {
				fileInformation.append("- File Size: "
						+ "\t\t\t"
						+ fileAreaObservationalSupplemental.getFile()
						.getFileSize().getValue()
						+ " [unit = "
						+ fileAreaObservationalSupplemental.getFile()
						.getFileSize().getUnit() + "]");
				fileInformation.append("\n");
			}

			// records
			if (fileAreaObservationalSupplemental.getFile().getRecords() != null) {
				fileInformation.append("- Records: "
						+ "\t\t\t"
						+ fileAreaObservationalSupplemental.getFile()
						.getRecords());
				fileInformation.append("\n");
			}

			// md5_checksum
			if (fileAreaObservationalSupplemental.getFile().getMd5Checksum() != null
					&& !fileAreaObservationalSupplemental.getFile()
					.getMd5Checksum().isEmpty()) {
				fileInformation.append("- MD5 Checksum: "
						+ "\t\t"
						+ fileAreaObservationalSupplemental.getFile()
						.getMd5Checksum());
				fileInformation.append("\n");
			}

			// comment
			if (fileAreaObservationalSupplemental.getFile().getComment() != null
					&& !fileAreaObservationalSupplemental.getFile()
					.getComment().isEmpty()) {
				fileInformation.append("- Comment: "
						+ "\t\t\t"
						+ fileAreaObservationalSupplemental.getFile()
						.getComment());
				fileInformation.append("\n");
			}
		}
	}
	
	/**
	 * Build TabularDataContainer object that stores all the important table
	 * information that will be used throughout the different stages of the
	 * table population process
	 * 
	 * @param objectAccess
	 * @param tableCharacter
	 * @param tableType
	 * @param fileAreaObservational
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("nls")
	public TabularDataContainer buildTabularDataContainer(
			ObjectProvider objectAccess, Object obj,
			String dataFileName) throws Exception {

		// build TabularDataContainer based on table type
		TabularDataContainer table = new TabularDataContainer(); 
		TableType tableType;
		boolean tableTypeExists = false;
		StringBuilder tableInformation = new StringBuilder();
		
		// Create ColumnDefs that will be used to create columns
		List<ColumnInfo> columnDefs = new ArrayList<ColumnInfo>();
		
		// set the type this TabularDataContainer holds
		table.setPDSType(this.PDS_LABEL_TYPE);

		// TableCharacter instance
		if (obj instanceof TableCharacter) {

			// get table type and type cast object to write table type
			tableType = TableType.FIXED_TEXT;
			TableCharacter tableCharacter = (TableCharacter) obj;
			
			// Required attributes
			//set offset - where table starts
			table.setOffset(tableCharacter.getOffset().getValue());
			table.setOffset(tableCharacter.getOffset());
			table.setTotalRows(tableCharacter.getRecords());
			
			table.setType(tableType.toString());
			
			// set record delimiter
			table.setRecordDelimiter(tableCharacter
					.getRecordDelimiter());
			
			table.setFieldsCount(tableCharacter.getRecordCharacter().getFields());
			table.setGroupsCount(tableCharacter.getRecordCharacter().getGroups());
			table.setRowBytes(tableCharacter.getRecordCharacter().getRecordLength().getValue());
			
			//FIXED_TEXT type
			table.setFormat(tableType.getStringTypeRepresentation());
			table.setRecordLength(tableCharacter.getRecordCharacter().getRecordLength());
			
			List<Object> fieldAndgroupFieldCharacters = objectAccess
					.getFieldCharacterAndGroupFieldCharacters(tableCharacter);
			
			columnDefs = buildColumnDefinitions(fieldAndgroupFieldCharacters, tableType);

			// Set the grand total number of column definitions
			table.setLabelTotalColumnCount(columnDefs.size());

			// Optional attributes
			table.setName(tableCharacter.getName());
			table.setLocalIdentifier(tableCharacter.getLocalIdentifier());
			table.setDescription(tableCharacter.getDescription());
			
			if (tableCharacter.getLocalIdentifier() != null)
				this.slice.setDataSetId(tableCharacter.getLocalIdentifier());
			
			// extract table information to display
			extractTableInformation(tableInformation,obj);
			if (tableInformation.length() == 0)
				table.setTableInformation("No information available.");
			else
				table.setTableInformation(tableInformation.toString());
			
			//TODO: save Uniformly_Sampled object?
			
			tableTypeExists = true;

		// TableDelimited instance
		} else if (obj instanceof TableDelimited) {
			
			tableType = TableType.DELIMITED;
			TableDelimited tableDelimited = (TableDelimited) obj;
			
			// Required fields
			//set offset - where table starts
			table.setOffset(tableDelimited.getOffset().getValue());
			table.setOffset(tableDelimited.getOffset());
			table.setParsingStandardID(tableDelimited.getParsingStandardId());
			table.setTotalRows(tableDelimited.getRecords());
			// set record and field delimiters
			table.setFieldDelimiter(tableDelimited.getFieldDelimiter());
			table.setRecordDelimiter(TabularPDS4DataUtils.getRecordDelimiterACIIFormat(tableDelimited.getRecordDelimiter()));
			table.setFieldsCount(tableDelimited.getRecordDelimited().getFields());
			table.setGroupsCount(tableDelimited.getRecordDelimited().getGroups());
			
			table.setType(tableType.toString());
			
			//FIXED_DELIMITED type
			table.setFormat(tableType.getStringTypeRepresentation());
			
			// Create ColumnDefs that will be used to create columns.
			columnDefs = new ArrayList<ColumnInfo>();
			
			List<Object> fieldAndgroupFieldDelimiteds = objectAccess
					.getFieldDelimitedAndGroupFieldDelimiteds(tableDelimited);

			columnDefs = buildColumnDefinitions(fieldAndgroupFieldDelimiteds, tableType);
						
			// Set the grand total number of column definitions
			table.setLabelTotalColumnCount(columnDefs.size());		
			
			// Optional fields
			table.setName(tableDelimited.getName());
			table.setLocalIdentifier(tableDelimited.getLocalIdentifier());
			table.setObjectLength(tableDelimited.getObjectLength());
			table.setDescription(tableDelimited.getDescription());
			table.setMaximumRecordLength(tableDelimited.getRecordDelimited().getMaximumRecordLength());
			
			if (tableDelimited.getLocalIdentifier() != null)
				this.slice.setDataSetId(tableDelimited.getLocalIdentifier());
				
			if (tableDelimited.getRecordDelimited().getMaximumRecordLength() != null)
				table.setRowBytes(tableDelimited.getRecordDelimited().getMaximumRecordLength().getValue());
		
			// extract table information to display
			extractTableInformation(tableInformation, obj);
			if (tableInformation.length() == 0)
				table.setTableInformation("No information available.");
			else
				table.setTableInformation(tableInformation.toString());
			
			tableTypeExists = true;
			
			//TODO: save Uniformly_Sampled object?
			
		//TableBinary instance
		} else if (obj instanceof TableBinary) {
			
			tableType = TableType.FIXED_BINARY;
			TableBinary tableBinary = (TableBinary) obj;
			
			// Required attributes
			//set offset - where table starts
			table.setOffset(tableBinary.getOffset().getValue());
			table.setOffset(tableBinary.getOffset());
			table.setTotalRows(tableBinary.getRecords());
			table.setFieldsCount(tableBinary.getRecordBinary().getFields());
			table.setGroupsCount(tableBinary.getRecordBinary().getGroups());
			table.setRowBytes(tableBinary.getRecordBinary().getRecordLength().getValue());
			table.setRecordLength(tableBinary.getRecordBinary().getRecordLength());
			
			table.setType(tableType.toString());
			
			//BINARY type
			table.setFormat(tableType.getStringTypeRepresentation());
			
			List<Object> fieldAndgroupFieldBinaries = objectAccess
					.getFieldBinaryAndGroupFieldBinaries(tableBinary);
			
			columnDefs = buildColumnDefinitions(fieldAndgroupFieldBinaries, tableType);
			
			// Set the grand total number of column definitions			
			table.setLabelTotalColumnCount(columnDefs.size());	
			
			// Optional attributes
			table.setName(tableBinary.getName());
			table.setLocalIdentifier(tableBinary.getLocalIdentifier());
			table.setDescription(tableBinary.getDescription());
			table.setRecordDelimiter(tableBinary.getRecordDelimiter());
			
			if (tableBinary.getLocalIdentifier() != null)
				this.slice.setDataSetId(tableBinary.getLocalIdentifier());
			
			// extract table information to display
			extractTableInformation(tableInformation, obj);
			if (tableInformation.length() == 0)
				table.setTableInformation("No information available.");
			else
				table.setTableInformation(tableInformation.toString());
	
			tableTypeExists = true;
			
			//TODO: save Uniformly_Sampled object?
		}
		
		// set the following attributes common to all table types
		// ensure an instance of the table type exists before
		// setting these attributes
		if (tableTypeExists) {

			// build URL of data table file
			File dataFile = new File(dataFileName);
			URL dataTableURL = TabularPDS4DataUtils.getURLofSameLevelFile(
					xmlFileURL, dataFile);

			if (dataTableURL != null) {
				table.setTabFileUrl(dataTableURL.toString());
				table.setTabFileURL(dataTableURL);
			}

			// Keep track of the column names to check for duplicates
			StringBuilder columnNameList = new StringBuilder("; ;");
			int duplicateID = 1;

			// loop through columnsDefs and create column objects
			// this code also checks to ensure no duplicate column
			// names exist
			int i = 0;
			for (ColumnInfo columnInfo : columnDefs) {

				// Check for duplicate column names. If found, then change the
				// column name
				// by appending an ID number (e.g., SPARE, SPARE-2, SPARE-3,
				// etc...) in order
				// to differentiate between columns with the same NAME field
				if (!columnNameList
						.toString()
						.toLowerCase()
						.contains(
								";" + columnInfo.getName().toLowerCase() + ";")) {
					columnNameList.append(columnInfo.getName() + ";");

				} else {
					
					// Each duplicate column should have a consecutive duplicate
					// ID.
					// For example, if the column names are [A B C A B D E],
					// then
					// the new names are [A B C A-1 B-1 D E] and not [A B C A-1
					// B-2 D E]
					String newColumnName = columnInfo.getName() + "-"
							+ String.valueOf(duplicateID);

					while (columnNameList.toString().toLowerCase()
							.contains(";" + newColumnName.toLowerCase() + ";")) {
						duplicateID++;
						newColumnName = columnInfo.getName() + "-"
								+ String.valueOf(duplicateID);
					}

					columnInfo.setName(columnInfo.getName() + "-"
							+ String.valueOf(duplicateID));
					columnNameList.append(columnInfo.getName() + ";");
					duplicateID = 1;
				}

				// set isSelected = true for all
				SliceColumn c = new SliceColumn(columnInfo,
						columnDefs.indexOf(columnInfo), true,
						columnDefs.indexOf(columnInfo));
				// add to column collections
				
				table.getColumns().add(c);
				table.getSelectedColumns().add(c);
				i++;

				// If there are too many columns, cap at
				// NUMBER_OF_COLUMNS_ALLOWED
				// since mysql is only able
				// to handle about 574 columns. Do not process/look at any
				// column >
				// 500
				// TODO: Find a way to display more than 500 columns. When so,
				// remove this if statement!
				if (i == NUMBER_OF_COLUMNS_ALLOWED)
					break;
			}

			table.setSelectedColumnCount(i);

			// Set the actual total number of columns that the table has. This
			// is
			// for the case that the label contains too many columns to be
			// processed/displayed
			table.setTableTotalColumnCount(i);
			
			// only for TableCharacter and TableDelimited tables
			if (obj instanceof TableCharacter || obj instanceof TableDelimited) {

				// set rows to sample to detect format if not specified
				if (obj instanceof TableCharacter)
					table.setRows(getDataPreview(columnDefs, table));
				else if (obj instanceof TableDelimited)
					table.setRows(getDelimitedDataPreview(columnDefs, table));

				// use sample values in rows to set column formats on dates
				for (SliceColumn column : table.getColumns()) {

					if (column.getDataType().equalsIgnoreCase("ASCII_REAL")) {
						String rowValue = this.getSampleValue(column, table);
						// if value of first item has e or E in it
						if (rowValue.contains("e") || rowValue.contains("E"))
							column.setIsSciNotation(true);
					}

					// check to see if the value is a date or time type
					if (TabularPDS4DataUtils.isDateTimeType(column.getDataType())) {

						String rowValue = this.getSampleValue(column, table);

						// set date string format
						String format = null;
						format = DateUtils.getPattern(rowValue);
						if (format == null) {
							column.setDataType("CHARACTER");
						} else {
							column.setDateFormat(format);
						}
					}
				}				
			}
		}
		
		return table;
	}
	
	/**
	 * retrieve values to determine format of date strings and reals
	 * 
	 * @param columnDefs
	 * @param tabularDataContainer
	 * @return
	 */
	protected static List<Row> getDataPreview(
			List<ColumnInfo> columnDefs,
			TabularDataContainer tabularDataContainer) {
		
		long totalRowsToSample = TOTAL_ROWS_TO_SAMPLE;

		// if the table's total number of rows is less than the minimum
		// (TOTAL_ROWS_TO_SAMPLE),
		// then just sample up to the total number of rows in the table
		if (tabularDataContainer.getTotalRows() < TOTAL_ROWS_TO_SAMPLE)
			totalRowsToSample = tabularDataContainer.getTotalRows();

		URL tabFileUrl = tabularDataContainer.getTabFileURL();
		long startByte = tabularDataContainer.getOffset();
		
		TabularData tabularData = new TabularData(tabFileUrl, columnDefs,
				startByte, totalRowsToSample);

		// TODO Need some kind of test of validity of tabularData
		// before moving on

		List<Row> rowsCopy = new ArrayList<Row>();

		// pull N rows of tabular data for preview
		for (int i = 0; i < totalRowsToSample; i++) {
			List<Element> elementsCopy = new ArrayList<Element>();
			Row row = tabularData.getRows().get(i);
			for (Element element : row.getElements()) {
				elementsCopy.add(element.clone());
			}
			rowsCopy.add(new Row(elementsCopy));
		}
		return rowsCopy;
	}
	
	
	/**
	 * retrieve values to determine format of date strings and reals
	 * 
	 * @param columnDefs
	 * @param tabularDataContainer
	 * @return
	 */
	protected static List<Row> getDelimitedDataPreview(
			List<ColumnInfo> columnDefs,
			TabularDataContainer tabularDataContainer) {
		
		long totalRowsToSample = TOTAL_ROWS_TO_SAMPLE;

		// if the table's total number of rows is less than the minimum
		// (TOTAL_ROWS_TO_SAMPLE),
		// then just sample up to the total number of rows in the table
		if (tabularDataContainer.getTotalRows() < TOTAL_ROWS_TO_SAMPLE)
			totalRowsToSample = tabularDataContainer.getTotalRows();

		URL tabFileUrl = tabularDataContainer.getTabFileURL();
		long startByte = tabularDataContainer.getOffset();
		
		//convert to right delimiter type (e.g. COMMA = ,)
		String fieldDelimeter = TabularPDS4DataUtils
				.getDelimeterASCIIFormat(tabularDataContainer
						.getFieldDelimiter());
		int fieldsCount = tabularDataContainer.getFieldsCount();
		
		TabularData tabularData = new TabularData(tabFileUrl, columnDefs,
				startByte, totalRowsToSample, fieldDelimeter, fieldsCount);

		List<Row> rowsCopy = new ArrayList<Row>();

		// pull N rows of tabular data for preview
		for (int i = 0; i < totalRowsToSample; i++) {
			List<Element> elementsCopy = new ArrayList<Element>();
			Row row = tabularData.getRows().get(i);
			for (Element element : row.getElements()) {
				elementsCopy.add(element.clone());
			}
			rowsCopy.add(new Row(elementsCopy));
		}
		return rowsCopy;
	}
	
	/*
	 * returns first sample value for the column in the N preview rows of the
	 * tabular data which is not equal to the missing constant, or if all N rows
	 * are equal to missing constant, simply returns first row's value where
	 * N=TOTAL_ROWS_TO_SAMPLE or total number of rows in the table
	 */
	private String getSampleValue(SliceColumn column, TabularDataContainer table) {
		String rowValue = null;
		String missingConstant = column.getMissingConstant();

		// loop through N values (N=TOTAL_ROWS_TO_SAMPLE or
		// total number of rows in the table)
		int totalNumRowsToSample = TOTAL_ROWS_TO_SAMPLE;

		if (table.getTotalRows() < TOTAL_ROWS_TO_SAMPLE)
			totalNumRowsToSample = table.getTotalRows();

		// if missing_constant exists for the column
		if (missingConstant != null && missingConstant.length() > 0) {
			// loop through N values in preview rows
			for (int k = 0; k > totalNumRowsToSample; k++) {
				// if value != missingConstant, use it for format
				if (!table.getRows().get(k).getElements()
						.get(column.getIndex() - 1).getValue().toString()
						.equals(missingConstant)) {
					rowValue = table.getRows().get(k).getElements()
							.get(column.getIndex() - 1).getValue().toString();
					break;
				}
			}
		}
		if (rowValue == null) {
			// if here, either all of the first ten rows all had missing
			// constant or no missing constant was provided, use first
			// row value
			if (table.getRows().size() != 0)
				rowValue = table.getRows().get(0).getElements()
						.get(column.getIndex() - 1).getValue().toString();
			if (rowValue == null) {
				rowValue = ""; //$NON-NLS-1$
			}
		}
		return rowValue;
	}
	
	/**
	 * Function called recursively to get all the nested fields from nested group fields
	 * 
	 * @param fieldAndgroupFieldBinaries
	 * @return
	 */
	private List<FieldBinary> getFieldsBinaryFromGroupFieldsBinary(
			List<Object> fieldAndgroupFieldBinaries, int repetitions,
			StringBuilder rootGroupID, StringBuilder rootParentHierarchy,
			int groupLocation, int rootRepetitionID) {
		
		// return null if list empty
		if (fieldAndgroupFieldBinaries.size() == 0)
			return null;

		StringBuilder parentHierarchy;
		StringBuilder groupID;
		ArrayList<FieldBinary> fieldBinaries = new ArrayList<FieldBinary>();
		int totalNumberOfRepetitions = repetitions;
		int repetitionID = rootRepetitionID;
		int nextRepeatedGroupLocation = groupLocation;

		// in order to avoid duplicate column names, a unique ID will be
		// appended to the name of the field
		// repeat fields and groups, if any
		while (totalNumberOfRepetitions > 0) {
			
			// initialize and reset name for next repetition
			parentHierarchy = new StringBuilder(rootParentHierarchy);
			groupID = new StringBuilder(rootGroupID);
			
			// use a new group ID
			int groupIDInt = Integer.parseInt(groupID.toString());
			++groupIDInt;
			groupID.setLength(0);
			groupID.append(groupIDInt);

			// Cast to right type object
			for (Object obj : fieldAndgroupFieldBinaries) {

				// FieldBinary object
				if (obj.getClass().equals(FieldBinary.class)) {

					// Create a new copy of the FieldDelimited object
					FieldBinary fDObjectCopy = TabularPDS4DataUtils.CopyFields
							.getFieldBinaryCopy(FieldBinary.class.cast(obj));

					// Append to existing field name a unique ID in order to
					// avoid duplicate columns
					fDObjectCopy.setName(fDObjectCopy.getName() + " "
							+ parentHierarchy + "-R"
							+ Integer.toString(repetitionID));
					
					// Set the FieldBinary's field_location = start byte to be
					// its location + group's location
					fDObjectCopy.getFieldLocation().setValue(
							nextRepeatedGroupLocation);

					fieldBinaries.add(fDObjectCopy);
					
					// point to the next start byte location
					nextRepeatedGroupLocation = nextRepeatedGroupLocation
							+ fDObjectCopy.getFieldLength().getValue();

					// GroupFieldBinary object
				} else if (obj.getClass().equals(GroupFieldBinary.class)) {

					parentHierarchy.append("-G");
					parentHierarchy.append(groupID.toString());

					List<FieldBinary> fieldBinaryList = getFieldsBinaryFromGroupFieldsBinary(
							GroupFieldBinary.class.cast(obj)
									.getFieldBinariesAndGroupFieldBinaries(),
							GroupFieldBinary.class.cast(obj).getRepetitions(),
							groupID, parentHierarchy,
							nextRepeatedGroupLocation,repetitionID);

					// add only encountered FieldDelimiteds
					if (fieldBinaryList != null && fieldBinaryList.size() != 0)
						fieldBinaries.addAll(fieldBinaryList);
				}
			}
			repetitionID++;
			totalNumberOfRepetitions--;
		}

		return fieldBinaries;
	}
	
	/**
	 * Function called recursively to get all the nested fields from nested group fields
	 * 
	 * @param fieldAndgroupFieldDelimiteds
	 * @return
	 */
	private List<FieldDelimited> getFieldsDelimitedFromGroupFieldsDelimited(
			List<Object> fieldAndgroupFieldDelimiteds, int repetitions,
			StringBuilder rootGroupID, StringBuilder rootParentHierarchy, int rootRepetitionID) {

		// return null if list empty
		if (fieldAndgroupFieldDelimiteds.size() == 0)
			return null;

		StringBuilder parentHierarchy;
		StringBuilder groupID;
		ArrayList<FieldDelimited> fieldDelimiteds = new ArrayList<FieldDelimited>();
		int totalNumberOfRepetitions = repetitions;
		int repetitionID = rootRepetitionID;

		// in order to avoid duplicate column names, a unique ID will be
		// appended to the name of the field
		// repeat fields and groups, if any
		while (totalNumberOfRepetitions > 0) {

			// initialize and reset name for next repetition
			parentHierarchy = new StringBuilder(rootParentHierarchy);
			groupID = new StringBuilder(rootGroupID);
			
			// use a new group ID
			int groupIDInt = Integer.parseInt(groupID.toString());
			++groupIDInt;
			groupID.setLength(0);
			groupID.append(groupIDInt);
			
			// Cast to right type object
			for (Object obj : fieldAndgroupFieldDelimiteds) {

				// FieldDelimited object
				if (obj.getClass().equals(FieldDelimited.class)) {

					// Create a new copy of the FieldDelimited object
					FieldDelimited fDObjectCopy = TabularPDS4DataUtils.CopyFields
							.getFieldDelimitedCopy(FieldDelimited.class
									.cast(obj));

					// Append to existing field name a unique ID in order to
					// avoid duplicate columns
					fDObjectCopy.setName(fDObjectCopy.getName() + " "
							+ parentHierarchy + "-R"
							+ Integer.toString(repetitionID));
					
					fieldDelimiteds.add(fDObjectCopy);

					// GroupFieldDelimited object
				} else if (obj.getClass().equals(GroupFieldDelimited.class)) {

					parentHierarchy.append("-G");
					parentHierarchy.append(groupID.toString());

					List<FieldDelimited> fieldDelimitedList = getFieldsDelimitedFromGroupFieldsDelimited(
							GroupFieldDelimited.class
									.cast(obj)
									.getFieldDelimitedsAndGroupFieldDelimiteds(),
							GroupFieldDelimited.class.cast(obj)
									.getRepetitions(), groupID, parentHierarchy,repetitionID);

					// add only encountered FieldDelimiteds
					if (fieldDelimitedList != null
							&& fieldDelimitedList.size() != 0)
						fieldDelimiteds.addAll(fieldDelimitedList);
				}
			}
			repetitionID++;
			totalNumberOfRepetitions--;
		}
		
		return fieldDelimiteds;
	}
	
	
	/**
	 * Function called recursively to get all the nested fields from nested group fields
	 * 
	 * @param fieldAndgroupFieldCharacters
	 * @return
	 */
	private List<FieldCharacter> getFieldsCharacterFromGroupFieldsCharacter(
			List<Object> fieldAndgroupFieldCharacters, int repetitions,
			StringBuilder rootGroupID, StringBuilder rootParentHierarchy,
			int groupLocation, int rootRepetitionID) {

		// return null if list empty
		if (fieldAndgroupFieldCharacters.size() == 0)
			return null;

		StringBuilder parentHierarchy;
		StringBuilder groupID;
		ArrayList<FieldCharacter> fieldCharacters = new ArrayList<FieldCharacter>();
		int totalNumberOfRepetitions = repetitions;
		int repetitionID = 1;
		int nextRepeatedGroupLocation = groupLocation;

		// in order to avoid duplicate column names, a unique ID will be
		// appended to the name of the field
		// repeat fields and groups, if any
		while (totalNumberOfRepetitions > 0) {
			
			// initialize and reset name for next repetition
			parentHierarchy = new StringBuilder(rootParentHierarchy);
			groupID = new StringBuilder(rootGroupID);
			
			// use a new group ID
			int groupIDInt = Integer.parseInt(groupID.toString());
			++groupIDInt;
			groupID.setLength(0);
			groupID.append(groupIDInt);

			// Cast to right type object
			for (Object obj : fieldAndgroupFieldCharacters) {

				// FieldCharacter object
				if (obj.getClass().equals(FieldCharacter.class)) {

					// Create a new copy of the FieldCharacter object
					FieldCharacter fDObjectCopy = TabularPDS4DataUtils.CopyFields
							.getFieldCharacterCopy(FieldCharacter.class
									.cast(obj));

					// Append to existing field name a unique ID in order to
					// avoid duplicate columns
					fDObjectCopy.setName(fDObjectCopy.getName() + " "
							+ parentHierarchy + "-R"
							+ Integer.toString(repetitionID));
					
					// Set the FieldBinary's field_location = start byte to be
					// its location + group's location
					fDObjectCopy.getFieldLocation().setValue(
							nextRepeatedGroupLocation);

					fieldCharacters.add(fDObjectCopy);
					
					// point to the next start byte location, plus 1 to skip
					// the white space between fields
					nextRepeatedGroupLocation = nextRepeatedGroupLocation
							+ fDObjectCopy.getFieldLength().getValue()+1;

					// GroupFieldCharacter object
				} else if (obj.getClass().equals(GroupFieldCharacter.class)) {

					parentHierarchy.append("-G");
					parentHierarchy.append(groupID.toString());

					List<FieldCharacter> fieldCharacterList = getFieldsCharacterFromGroupFieldsCharacter(
							GroupFieldCharacter.class
									.cast(obj)
									.getFieldCharactersAndGroupFieldCharacters(),
							GroupFieldCharacter.class.cast(obj)
									.getRepetitions(), groupID, parentHierarchy,
									nextRepeatedGroupLocation,repetitionID);

					// add only encountered FieldDelimiteds
					if (fieldCharacterList != null
							&& fieldCharacterList.size() != 0)
						fieldCharacters.addAll(fieldCharacterList);
				}
			}
			repetitionID++;
			totalNumberOfRepetitions--;
		}
		return fieldCharacters;
	}
	
	/**
	 * Build Column Definitions for each field specified in the PDS4 label.
	 * 
	 * For FieldBinary tables:
	 * 
	 * 	- A column of type Complex is divided into two independent columns,
	 * 		   one for the real part and the other for the imaginary part
	 * 	- Each FieldBit is defined in its own independent column
	 * 
	 * @param fieldAndGroupFields
	 * @param tableType
	 * @return list of ColumnInfo objects for each of the fields
	 */
	private List<ColumnInfo> buildColumnDefinitions(
			List<Object> fieldAndGroupFields, TableType tableType) {
		
		List<ColumnInfo> columnDefs = new ArrayList<ColumnInfo>();
		
		// using StringBuilder to preserve groudID recursively
		StringBuilder groupID = new StringBuilder("0");
	
		// Table Character
		if (tableType.equals(TableType.FIXED_TEXT)) {
			
			// A field number may be provided in the label (optional).
			// However, in order to keep the consistency of fields
			// as they appear, the index of each individual field
			// and the ones defined in a group, will be sequential.
			int index = 1;
			
			for (Object obj : fieldAndGroupFields) {

				// FieldCharacter object
				if (obj.getClass().equals(FieldCharacter.class)) {

					ColumnInfo col = new ColumnInfo(
							FieldCharacter.class.cast(obj));

					col.setIndex(index);
					index++;

					columnDefs.add(col);
					

				} else if (obj.getClass().equals(GroupFieldCharacter.class)) {

					GroupFieldCharacter groupFieldCharacter = GroupFieldCharacter.class
							.cast(obj);

					int repetitions = groupFieldCharacter.getRepetitions();
					int groupLocation = groupFieldCharacter.getGroupLocation().getValue();

					// this is done recursively since multiple nested groups and
					// fields can occur, therefore not using these attributes as
					// of 9-11-14
					//int numberOfGroups = groupFieldDelimited.getGroups();
					//int numberOfFields = groupFieldDelimited.getFields();
					//Integer groupNumber = groupFieldDelimited.getGroupNumber();

					List<Object> fieldAndgroupFieldCharacters = groupFieldCharacter
							.getFieldCharactersAndGroupFieldCharacters();

					// use a new group ID
					int groupIDInt = Integer.parseInt(groupID.toString());
					++groupIDInt;
					groupID.setLength(0);
					groupID.append(groupIDInt);
					
					// call recursive function to get all the FieldDelimiteds
					// from the groups
					List<FieldCharacter> fieldsCharacterList = getFieldsCharacterFromGroupFieldsCharacter(
							fieldAndgroupFieldCharacters, repetitions, groupID,
							new StringBuilder("G" + groupID.toString()),groupLocation,1);

					// null if the group does not contain any fields or
					// groupFields
					if (fieldsCharacterList != null) {

						// add all the FieldCharacter from all the groups or
						// fields in groups
						for (FieldCharacter fieldCharacter : fieldsCharacterList) {

							ColumnInfo col = new ColumnInfo(fieldCharacter);

							col.setIndex(index);
							index++;

							columnDefs.add(col);
						}
					}
				}
			}
			
		// Table Delimited	
		} else if (tableType.equals(TableType.DELIMITED)) {

			// A field number may be provided in the label (optional).
			// However, in order to keep the consistency of fields
			// as they appear, the index of each individual field
			// and the ones defined in a group, will be sequential.
			int index = 1;
			
			for (Object obj : fieldAndGroupFields) {

				// FieldDelimited object
				if (obj.getClass().equals(FieldDelimited.class)) {

					ColumnInfo col = new ColumnInfo(
							FieldDelimited.class.cast(obj));
					
					col.setIndex(index);
					index++;

					columnDefs.add(col);

				} else if (obj.getClass().equals(GroupFieldDelimited.class)) {

					GroupFieldDelimited groupFieldDelimited = GroupFieldDelimited.class
							.cast(obj);

					int repetitions = groupFieldDelimited.getRepetitions();

					// this is done recursively since multiple nested groups and
					// fields can occur, therefore not using these attributes as
					// of 9-11-14
					//int numberOfGroups = groupFieldDelimited.getGroups();
					//int numberOfFields = groupFieldDelimited.getFields();
					//Integer groupNumber = groupFieldDelimited.getGroupNumber();

					List<Object> fieldAndgroupFieldDelimiteds = groupFieldDelimited
							.getFieldDelimitedsAndGroupFieldDelimiteds();

					// use a new group ID
					int groupIDInt = Integer.parseInt(groupID.toString());
					++groupIDInt;
					groupID.setLength(0);
					groupID.append(groupIDInt);
					
					// call recursive function to get all the FieldDelimiteds
					// from the groups
					List<FieldDelimited> fieldsDelimitedList = getFieldsDelimitedFromGroupFieldsDelimited(
							fieldAndgroupFieldDelimiteds, repetitions, groupID, new StringBuilder("G"+groupID.toString()),1);

					// null if the group does not contain any fields or
					// groupFields
					if (fieldsDelimitedList != null) {

						// add all the FieldDelimited from all the groups or
						// fields
						// in groups
						for (FieldDelimited fieldDelimited : fieldsDelimitedList) {

							ColumnInfo col = new ColumnInfo(fieldDelimited);
							
							col.setIndex(index);
							index++;
							
							columnDefs.add(col);
						}
					}
				}
			}

		// Table Binary
		} else if (tableType.equals(TableType.FIXED_BINARY)) {

			
			// A field number may be provided in the label (optional).
			// However, in order to keep the consistency of fields
			// as they appear, the index of each individual field
			// and the ones defined in a group, will be sequential.
			int index = 1;
			
			for (Object obj : fieldAndGroupFields) {

				// FieldBinary object
				if (obj.getClass().equals(FieldBinary.class)) {

					ColumnInfo col = new ColumnInfo(
							FieldBinary.class.cast(obj));
					
					// check for fieldBits
					PackedDataFields packedDataFields = FieldBinary.class.cast(
							obj).getPackedDataFields();

					// This FieldBinary contains FieldBits. Therefore, a column
					// for the parent is not created
					if (packedDataFields != null) {

						List<FieldBit> fieldBits = packedDataFields
								.getFieldBits();

						// add a bit columns for each of the FieldBits
						if (!fieldBits.isEmpty()) {
							for (FieldBit fieldBit : fieldBits) {
								
								// Add to the FieldBit name, its parent name in order
								// to identify where it comes from and append the
								// description of its parent as well
								ColumnInfo bitCol = new ColumnInfo(fieldBit);
								bitCol.setName(col.getName()+" - "+bitCol.getName());
								bitCol.setDescription(col.getDescription()+" : "+bitCol.getDescription());
								
								// use the parent's startByte and number of bytes
								bitCol.setBytes(col.getBytes());
								bitCol.setStartByte(col.getStartByte());
								
								bitCol.setIndex(index);
								index++;

								columnDefs.add(bitCol);
							}
						}
						// A regular FieldBinary field
					} else {

						//IEEE Complex numbers consist of two IEEE REAL format numbers of the same precision,
						//contiguous in memory. The first number represents the real part and the second the
						//imaginary part of the complex value
						if (TabularPDS4DataUtils.isComplexType(col.getType())) {

							//If it is an 8 byte IEEE_COMPLEX number, create a column for the real part
							//and a separate column for the imaginary type. Each of the components is 4 bytes. 
							//So need to reset start byte for the second column and number of bytes from 8 to 4 
							//for both
							if(col.getBytes() == 8) {

								//Create column in which to store the real part
								col = new ColumnInfo(FieldBinary.class.cast(obj));
								StringBuilder rpNameToAppend = new StringBuilder(" (Real Part)");
								col.appendName(rpNameToAppend.toString());
								col.setBytes(4);
								
								col.setIndex(index);
								index++;
								
								columnDefs.add(col);

								//Create column in which to store the imaginary part
								col = new ColumnInfo(FieldBinary.class.cast(obj));
								StringBuilder imNameToAppend = new StringBuilder(" (Imaginary Part)");
								col.appendName(imNameToAppend.toString());
								col.setStartByte(col.getStartByte()+4);
								col.setBytes(4);
								
								col.setIndex(index);
								index++;
								
								columnDefs.add(col);

							} else if(col.getBytes() == 16) {

								//Create column in which to store the real part
								col = new ColumnInfo(FieldBinary.class.cast(obj));
								StringBuilder rpNameToAppend = new StringBuilder(" (Real Part)");
								col.appendName(rpNameToAppend.toString());
								col.setBytes(8);
								
								col.setIndex(index);
								index++;
								
								columnDefs.add(col);

								//Create column in which to store the imaginary part
								col = new ColumnInfo(FieldBinary.class.cast(obj));
								StringBuilder imNameToAppend = new StringBuilder(" (Imaginary Part)");
								col.appendName(imNameToAppend.toString());
								col.setStartByte(col.getStartByte()+8);
								col.setBytes(8);
								
								col.setIndex(index);
								index++;
								
								columnDefs.add(col);
							}

							// just a FieldBinary
						} else {
							
							col.setIndex(index);
							index++;
							
							columnDefs.add(col);
						}
					}
				} else if (obj.getClass().equals(GroupFieldBinary.class)) {

					GroupFieldBinary groupFieldBinary = GroupFieldBinary.class
							.cast(obj);

					int repetitions = groupFieldBinary.getRepetitions();
					int groupLocation = groupFieldBinary.getGroupLocation().getValue();

					// this is done recursively since multiple nested groups and
					// fields can occur, therefore not using these attributes as
					// of 9-16-14
					//int numberOfGroups = groupFieldDelimited.getGroups();
					//int numberOfFields = groupFieldDelimited.getFields();
					//Integer groupNumber = groupFieldDelimited.getGroupNumber();

					List<Object> fieldAndgroupFieldBinaries = groupFieldBinary
							.getFieldBinariesAndGroupFieldBinaries();

					// use a new group ID
					int groupIDInt = Integer.parseInt(groupID.toString());
					++groupIDInt;
					groupID.setLength(0);
					groupID.append(groupIDInt);
					
					// call recursive function to get all the FieldBinary objects
					// from the groups
					List<FieldBinary> fieldsBinaryList = getFieldsBinaryFromGroupFieldsBinary(
							fieldAndgroupFieldBinaries, repetitions, groupID, 
							new StringBuilder("G"+groupID.toString()),groupLocation,1);

					// if FieldBinary objects found, process them
					if (fieldsBinaryList != null) {

						// add all the FieldBinary from all the groups or
						// fields in groups
						for (FieldBinary fieldBinary : fieldsBinaryList) {

							ColumnInfo col = new ColumnInfo(fieldBinary);
							
							// check for fieldBits
							PackedDataFields packedDataFields = fieldBinary
									.getPackedDataFields();
							
							if (packedDataFields != null) {

								List<FieldBit> fieldBits = packedDataFields
										.getFieldBits();

								// add a bit columns for each of the FieldBits
								if (!fieldBits.isEmpty()) {
									for (FieldBit fieldBit : fieldBits) {
										
										// Add to the FieldBit name, its parent name in order
										// to identify where it comes from and append the
										// description of its parent as well
										ColumnInfo bitCol = new ColumnInfo(fieldBit);
										bitCol.setName(col.getName()+" - "+bitCol.getName());
										bitCol.setDescription(col.getDescription()+" : "+bitCol.getDescription());
										
										// use the parent's startByte and number of bytes
										bitCol.setBytes(col.getBytes());
										bitCol.setStartByte(col.getStartByte());
										
										bitCol.setIndex(index);
										index++;
										
										columnDefs.add(bitCol);
									}
								}
							} else {

								// if complex type, need to create separate columns, one for the
								// real part and the other for the imaginary part
								if (TabularPDS4DataUtils.isComplexType(col
										.getType())) {

									//If it is an 8 byte IEEE_COMPLEX number, create a column for the real part
									//and column for the imaginary type. Each of the components is 4 bytes. So need
									//to reset start byte for the second column and number of bytes from 8 to 4 for
									//both
									if(col.getBytes() == 8) {

										//Create column in which to store the real part
										col = new ColumnInfo(FieldBinary.class.cast(obj));
										StringBuilder rpNameToAppend = new StringBuilder(" (Real Part)");
										col.appendName(rpNameToAppend.toString());
										col.setBytes(4);

										col.setIndex(index);
										index++;

										columnDefs.add(col);

										//Create column in which to store the imaginary part
										col = new ColumnInfo(FieldBinary.class.cast(obj));
										StringBuilder imNameToAppend = new StringBuilder(" (Imaginary Part)");
										col.appendName(imNameToAppend.toString());
										col.setStartByte(col.getStartByte()+4);
										col.setBytes(4);

										col.setIndex(index);
										index++;

										columnDefs.add(col);

									} else if(col.getBytes() == 16) {

										//Create column in which to store the real part
										col = new ColumnInfo(FieldBinary.class.cast(obj));
										StringBuilder rpNameToAppend = new StringBuilder(" (Real Part)");
										col.appendName(rpNameToAppend.toString());
										col.setBytes(8);

										col.setIndex(index);
										index++;

										columnDefs.add(col);

										//Create column in which to store the imaginary part
										col = new ColumnInfo(FieldBinary.class.cast(obj));
										StringBuilder imNameToAppend = new StringBuilder(" (Imaginary Part)");
										col.appendName(imNameToAppend.toString());
										col.setStartByte(col.getStartByte()+8);
										col.setBytes(8);

										col.setIndex(index);
										index++;

										columnDefs.add(col);
									}

								} else {

									col.setIndex(index);
									index++;

									columnDefs.add(col);
								}
							}
						}
					}
				}
			}
		}	
		return columnDefs;
	}

	/**
	 * 
	 * @param tabularDataContainer
	 * @return
	 * @throws CancelledException
	 */
	@Override
	public int readPartialData(TabularDataContainer tabularDataContainer)
			throws CancelledException {
		
		int existStatus = 0;
		
		try {
			
			//if table does not exist, create table and partially fill it
			if(!tableExists(tabularDataContainer)) {
				createDataTable(tabularDataContainer);
				readPartialFile(tabularDataContainer);
				existStatus = 1;
			//table exists and has been completely populated
			}else if(tableExists(tabularDataContainer) && tabularDataContainer.getTablePopulatedStatus()) {
				existStatus = 2;
			//table exists and has not been completely populated
			}else {
				existStatus = 3;
			}
		} catch (Exception e) {
			e.printStackTrace();
			updateStatus("Unable to continue processing due to a sytem error: "
					+ e.getMessage(), false);

			// LAB 9/22/09 give user easy way to start over
		}
		return existStatus;
	}

	
	
	/**
	 * 
	 * @param tabularDataContainer
	 * @throws Exception
	 */
	private void readPartialFile(TabularDataContainer tabularDataContainer)
			throws Exception {

		try {
			fillPartialDataTable(tabularDataContainer);

		} catch (IOException e) {
			// this.errors.add(e);
			e.printStackTrace();
			throw new RuntimeException(
					"IO Exception while retrieving TABLE pointer" //$NON-NLS-1$
							+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);

		}
	}


	/**
	 * Insert column information for each of the column objects in the 'columns' table
	 * 
	 */
	@Override
	protected void insertColumns(TabularDataContainer tabularDataContainer,
			String labelURLString) throws Exception {
		
		String insertSql = "INSERT INTO columns SET column_name = ?,"
                + " data_type = ?, bytes = ?, bits = ?, start_byte = ?, start_bit = ?, "
                + " items = ?, item_bits = ?, item_bytes = ?, item_offset = ?, field_number = ?, "
                + " description = ?, format = ?, missing_constant = ?, stop_bit = ?, column_number = ?, session_id = ?, "
                + "tabular_container_id = ?, label = ?, selected = ?";

		
		Connection connection = DBManager.getConnection();
		PreparedStatement pstmt = null;
		
		try {
            pstmt = connection.prepareStatement(insertSql);

            for (Column column : tabularDataContainer.getColumns()) {
                
                pstmt.setString(1, column.getName());
                
                //Data type can only be null for bit column with items
                if(column.getDataType() != null)
                    pstmt.setString(2, column.getDataType());
                else
                    pstmt.setNull(2, java.sql.Types.VARCHAR);
                
                //The following fields can be null or not. Set to null if they are already null
                //to ensure consistency
                if(column.getBytes() != null)
                    pstmt.setInt(3, column.getBytes());
                else
                    pstmt.setNull(3, java.sql.Types.VARCHAR);

                if(column.getBits() != null)
                    pstmt.setInt(4, column.getBits());
                else
                    pstmt.setNull(4, java.sql.Types.VARCHAR);

                if(column.getStartByte() != null)
                    pstmt.setInt(5, column.getStartByte());
                else
                    pstmt.setNull(5, java.sql.Types.VARCHAR);
                
                if(column.getStartBit() != null)
                    pstmt.setInt(6, column.getStartBit());
                else
                    pstmt.setNull(6, java.sql.Types.VARCHAR);
                
                if(column.getNumItems() != null)
                    pstmt.setInt(7, column.getNumItems());
                else
                    pstmt.setNull(7, java.sql.Types.VARCHAR);
                
                if(column.getItemBits() != null)
                    pstmt.setInt(8, column.getItemBits());
                else
                    pstmt.setNull(8, java.sql.Types.VARCHAR);
                
                if(column.getItemBytes() != null)
                    pstmt.setInt(9, column.getItemBytes());
                else
                    pstmt.setNull(9, java.sql.Types.VARCHAR);
                
                if(column.getItemOffset() != null)
                    pstmt.setInt(10, column.getItemOffset());
                else
                    pstmt.setNull(10, java.sql.Types.VARCHAR);
                
                if(column.getFieldNumber() != null)
                    pstmt.setInt(11, column.getFieldNumber());
                else
                    pstmt.setNull(11, java.sql.Types.VARCHAR);
                
                pstmt.setString(12, column.getDescription());
                pstmt.setString(13, column.getFormat());
                pstmt.setString(14, column.getMissingConstant());
                
                if(column.getStopBit() != null)
                    pstmt.setInt(15, column.getStopBit());
                else
                    pstmt.setNull(15, java.sql.Types.VARCHAR);
                
                pstmt.setInt(16, column.getIndex());
                pstmt.setString(17, this.sessionId);
                pstmt.setString(18, tabularDataContainer.getId());
                pstmt.setString(19, labelURLString);
                pstmt.setBoolean(20, true);
                pstmt.execute();
            }

        } catch (SQLException sqlException) {
			sqlException.printStackTrace();

			throw new Exception(sqlException);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();

			} catch (SQLException ignored) {
				ignored.printStackTrace();
				// do nothing
			}
		}
		connection.close();

	}

	@Override
	public void readData(TabularDataContainer tabularDataContainer)
			throws CancelledException {
		
		updateStatus("Building tabular data table (may take a few moments)");
		
		try {
			createDataTable(tabularDataContainer);
			updateStatus("Data table has been created");
			readFile(tabularDataContainer);
			this.status.setDone();
			
		} catch (Exception e) {
			e.printStackTrace();
			updateStatus("Unable to continue processing due to a sytem error: "
					+ e.getMessage(), false);

			// LAB 9/22/09 give user easy way to start over
		}
	}
	

	/**
	 * Call fillDataTable() to populate the temporary table and set the
	 * table populated status to true once the table has been filled
	 * 
	 * @param tabularDataContainer
	 * @throws Exception
	 */
	private void readFile(TabularDataContainer tabularDataContainer)
			throws Exception {
		try {

			fillDataTable(tabularDataContainer);
			
			//at this point, the entire table has been populated
			tabularDataContainer.setTablePopulatedStatus(true);

		} catch (IOException e) {
			// this.errors.add(e);
			e.printStackTrace();
			throw new RuntimeException(
					"IO Exception while retrieving TABLE pointer" //$NON-NLS-1$
							+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);

		}
	}
	
	/**
	 * Create temporary data table. At this point, each FieldBit or Complex part (imaginary or real)
	 * has its own column
	 * 
	 */
	@Override
	protected void createDataTable(TabularDataContainer tabularDataContainer)
			throws Exception {
		
		String columnQuerySQL = "SELECT column_name, data_type, bytes, format FROM columns WHERE tabular_container_id = '"
				+ tabularDataContainer.getId() + "';";
		
		Connection connection = DBManager.getConnection();
		Statement queryStmt = null;
		ResultSet rs = null;
		Statement createStmt = null;
		String table;
		
		try {
			queryStmt = connection.createStatement();
			rs = queryStmt.executeQuery(columnQuerySQL);
			
			if (!rs.next()) {
				throw new Exception("No records returned by column query");
			}
			
			// StringBuilder to hold SQL statement
			StringBuilder createTableString = new StringBuilder();
			
			// build table name from session and tabularDataContainer IDs
			table = TabularDataUtils.getTabDataTableName(this.sessionId,
					tabularDataContainer.getId());
			tabularDataContainer.setTableName(table);
			createTableString.append("CREATE TABLE IF NOT EXISTS `" + table + "` (");
			createTableString
					.append("`row_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,");

			// reset to beginning
			rs.beforeFirst();
			
			// for each result in result set of query correlate data type to db
			// column type
			while (rs.next()) {

				// column name
				createTableString.append("`" + rs.getString("column_name") + "` ");
				
				// ASCII_Boolean
				if (TabularPDS4DataUtils.isASCIIBoolean(rs.getString("data_type"))) {
					createTableString.append("BOOLEAN");
					
				// all date, time and dateTime types
				} else if (TabularPDS4DataUtils.isDateTimeType(rs.getString("data_type"))) {
					createTableString.append("TINYTEXT");
					
				// ASCII_Integer or ASCII_NonNegative_Integer
				} else if (TabularPDS4DataUtils.isASCIIIntegerType(rs.getString("data_type"))) {
					createTableString.append("BIGINT");
					
				// ASCII_Real
				} else if (TabularPDS4DataUtils.isASCIIRealType(rs.getString("data_type"))) {
					
					// format specified
					if (rs.getString("format") != null
							&& rs.getString("format").length() != 0) {

						// parse the field format
						String tableString = TabularPDS4DataUtils.parseFieldFormat(
								rs.getString("format"), "ASCII_Real", rs);
			
						// append the format specified in the label
						if(tableString != null) {
							createTableString.append(tableString);
							// it should reach the else statement, but just in case
						} else {
							createTableString.append("DECIMAL(65, 30)");	
						}

						// no format specified so extract from the sampling data
					} else {
						
						int scaleInt = 0;
						int numOfBytes = rs.getInt("bytes");

						//Iterate through the first 100 rows to figure out the scientific notation
						//or decimal point places
						for (int i = 0; i < tabularDataContainer.getRows().size(); i++) {

							Row sampleRow = tabularDataContainer.getRows().get(i);

							// get the element at the index of the column
							String sampleElementValue = sampleRow.getElements().get(
									tabularDataContainer.getColumns().indexOf(
											tabularDataContainer.getColumn(rs
													.getString("column_name"))))
													.getValue().toString();
							
							//if scientific notation used, no need to find scale
							if (sampleElementValue.contains("e")
									|| sampleElementValue.contains("E")) {
								scaleInt = -1;
								break;
							}

							//Remove any space character, tab characters, etc
							sampleElementValue = sampleElementValue.replaceAll("\\s+","");

							//Search for the decimal point (-1 = no decimal point (e.g., -9999))
							if (sampleElementValue.indexOf(".") != -1) {

								int tempScaleInt = sampleElementValue.substring(
										sampleElementValue.indexOf(".") + 1).length();

								//Find the maximum scale of the first 100 row values ignoring
								//non-decimal values
								if(tempScaleInt > scaleInt)
									scaleInt = tempScaleInt;
							}	
							
							// if format is not specified, need to calculate the M,
							// the maximum number of digits
							int tempNumOfBytes = sampleElementValue.length();
							
							if (tempNumOfBytes > numOfBytes)
								numOfBytes = tempNumOfBytes;
							
						}

						//if value is in scientific e notation
						if (scaleInt == -1) {
							// make decimal the largest it can be
							createTableString.append("DECIMAL(65, 30)");
						} else {
							String scale = String.valueOf(scaleInt);

							createTableString.append("DECIMAL("
									+ numOfBytes + "," + scale + ")");
						}
					}	
					
				// ASCII_Numeric_BaseN where N=2,8,16
				} else if (TabularPDS4DataUtils.isASCIINumericBaseNType(rs.getString("data_type"))) {
					createTableString.append("CHAR(255)");
					
				// ASCII_MD5_Checksum
				} else if (TabularPDS4DataUtils.isMD5ChecksumType(rs.getString("data_type"))) {
					createTableString.append("CHAR(32)");
					
				// string and character data types
				} else if (TabularPDS4DataUtils.isStringType(rs.getString("data_type"))) {
					createTableString.append("TEXT");
					
				// Binary Signed, Unsigned LSB or MSB Integer
				} else if (TabularPDS4DataUtils.isBinaryInteger(rs.getString("data_type")))	{
					
					// unsigned integers
					if (TabularPDS4DataUtils.isUnsignedInteger(rs
							.getString("data_type"))) {

						// 1 byte
						if (TabularPDS4DataUtils.is1ByteBinaryInteger(rs
								.getString("data_type"))) {

							createTableString.append("TINYINT UNSIGNED");

							// 2 bytes
						} else if (TabularPDS4DataUtils.is2ByteBinaryInteger(rs
								.getString("data_type"))) {

							createTableString.append("SMALLINT UNSIGNED");

							// 4 bytes
						} else if (TabularPDS4DataUtils.is4ByteBinaryInteger(rs
								.getString("data_type"))) {

							createTableString.append("INT UNSIGNED");

							// 8 bytes
						} else if (TabularPDS4DataUtils.is8ByteBinaryInteger(rs
								.getString("data_type"))) {

							createTableString.append("BIGINT UNSIGNED");

							// anything else. This is set to null
						} else {
							createTableString.append("SMALLINT");
						}
						
					// signed integers
					} else {
						
						// 1 byte
						if (TabularPDS4DataUtils.is1ByteBinaryInteger(rs
								.getString("data_type"))) {
						
							createTableString.append("TINYINT");
							
						// 2 bytes 
						} else if (TabularPDS4DataUtils.is2ByteBinaryInteger(rs
								.getString("data_type"))) {

							createTableString.append("SMALLINT");

						// 4 bytes
						} else if (TabularPDS4DataUtils.is4ByteBinaryInteger(rs
								.getString("data_type"))) {

							createTableString.append("INT");

						// 8 bytes
						} else if (TabularPDS4DataUtils.is8ByteBinaryInteger(rs
								.getString("data_type"))) {

							createTableString.append("BIGINT");

						// anything else. This is set to null
						} else {
							createTableString.append("SMALLINT");
						}
					}
				
				// Real types, LSB AND MSB, double and single
				} else if (TabularPDS4DataUtils.isRealType(rs.getString("data_type")))	{
					
					if(TabularPDS4DataUtils.isSingleRealType(rs.getString("data_type"))) {
						createTableString.append("FLOAT");
					} else if(TabularPDS4DataUtils.isDoubleRealType(rs.getString("data_type"))) {
						createTableString.append("DOUBLE");
					} else {
						createTableString.append("DOUBLE");
					}

				// Complex types
				} else if (TabularPDS4DataUtils.isComplexType(rs.getString("data_type"))) {
					
					//IEEE_COMPLEX is separated into the real and the imaginary parts.
                    //Each part has already been separated during the building of the
					//column definitions, so only looking for 4 or 8 bytes
                    if(rs.getString("bytes").equals("4")) {
                        
                        createTableString.append("FLOAT");
   
                    } else if(rs.getString("bytes").equals("8")) {
                       
                        createTableString.append("DOUBLE");
                        
                    } else {
                        createTableString.append("DOUBLE");
                    }

                // SignedBitString and UnsignedBitString
				} else if (TabularPDS4DataUtils.isBitStringType(rs.getString("data_type"))) {
				
					createTableString.append("MEDIUMTEXT");

				} else {
					createTableString.append("LONGTEXT");
				}
				
				if (!rs.isLast()) {
					createTableString.append(",");
				}
			}// end while

			createTableString.append(");");

			String SQL = createTableString.toString();
			createStmt = connection.createStatement();
			createStmt.execute(SQL);

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			throw new Exception(sqlException);

		} finally {
			try {
				if (queryStmt != null)
					queryStmt.close();
				if (createStmt != null)
					createStmt.close();
				if (rs != null)
					rs.close();

			} catch (SQLException ignored) {
				ignored.printStackTrace();
				// do nothing
			}
		}
		connection.close();
	}

	/**
	 * Fills the remainder of the table [101 - N number of rows], updating
	 * the status as the table is filled
	 * 
	 * @param tabularDataContainer
	 */
	@Override
	protected void fillDataTable(TabularDataContainer tabularDataContainer) throws Exception {
		
		String columnQuerySQL = "SELECT column_name, data_type, start_byte, bytes, start_bit, stop_bit, format FROM columns WHERE tabular_container_id = '"
				+ tabularDataContainer.getId() + "';";
		
		Connection connection = DBManager.getConnection();
		Statement queryStmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// get start byte and URL of tabular data
		long dataStartByte = tabularDataContainer.getOffset();
		URL tabFileUrl = tabularDataContainer.getTabFileURL();
		
		try {
			queryStmt = connection.createStatement();
			rs = queryStmt.executeQuery(columnQuerySQL);
			
			if (rs.wasNull()) {
				throw new RuntimeException(
						"No records returned by column query");
			}
			
			updateStatus("Populating data table");
			
			// create SQL for prepared statement to insert data into
			// table
			StringBuilder insertString = new StringBuilder();
			insertString.append("INSERT INTO `"
					+ tabularDataContainer.getTableName() + "` SET ");

			//Build insert statement
			while (rs.next()) {

				// for unsigned types, cast to unsigned in order to get full range of values
				if (TabularPDS4DataUtils
						.isUnsignedInteger(rs
								.getString("data_type"))) {
				
					insertString.append("`" + rs.getString("column_name") + "` " + " = CAST(? AS UNSIGNED)");
					
				} else {

					insertString.append("`" + rs.getString("column_name") + "` " + " = ?");
				}
				
				if (!rs.isLast()) {
					insertString.append(",");
				}
			}

			//Make prepared statement connection
			pstmt = connection.prepareStatement(insertString.toString());
			
			//Table type = FIXED_BINARY
			if (TabularPDS4DataUtils.isBinaryTable(tabularDataContainer.getFormat())) {
				
				// create input stream
				InputStream in = new BufferedInputStream(tabFileUrl
						.openStream());
				
				in = new BufferedInputStream(tabFileUrl.openStream());
				
				//Total number of bytes for a single row
				int rowBytes = tabularDataContainer.getRowBytes();
				
				//Total number of bytes to skip 
				long rowsDisplayOffset = this.ROWS_DISPLAY_LIMIT*rowBytes;
				
				//Need to start at the beginning of the table pointer
				//TODO:Do a check to make sure the table is in the same file
				//with all other tables and not on its own file. Otherwise,
				//there is not need to skip
				in.skip(dataStartByte+rowsDisplayOffset);
				
				// make a byte array as large as one row
				byte[] data = new byte[rowBytes];
				
				// create counters to loop through rows
				int rowNumber = this.ROWS_DISPLAY_LIMIT+1;
				int bytesRead = 0;
				
				//offset at which to start storing bytes in byte array data. This
				//should always be 0, the beginning of the array
				int offset = 0;
				
				//read(...) automatically advances the pointer. So no need to skip()
				while (Long.valueOf(tabularDataContainer.getTotalRows()) == null
						|| tabularDataContainer.getTotalRows() >= rowNumber
						&& (bytesRead = in.read(data, offset, rowBytes)) != -1) {

					updateStatus(
							"Processing: "
									+ (int) Math.floor(((float) rowNumber / tabularDataContainer
											.getTotalRows()) * 100)
									+ "% completed", false);

					//Read remaining bytes that were not read to ensure the data buffer contains all rowBytes
					while(bytesRead != rowBytes) {
						
						try {
						
							int extraBytesRead = in.read(data, bytesRead, rowBytes-bytesRead);
							
							//Cannot read the file anymore
							if (extraBytesRead <= 0) {
								throw new Exception("Unable to continue processing due to not been able to read the data file");
							}
							
							bytesRead = bytesRead + extraBytesRead;

						//IOException occurs if first byte cannot be read for any reason, or if the input stream has been
						//closed for any reasons, or other I/O error occurs
						}catch(IOException e) {
							e.printStackTrace();
							throw new RuntimeException(
									"IO Exception while reading data file" + e.getMessage());
						}
					}
					
					int parameterIndex = 1;
					// reset resultset to beginning
					rs.beforeFirst();
					
					// entire row of data is already in the byte array
					// for each column entry in rs
					while (rs.next()) {

						int startIndex = rs.getInt("start_byte") - 1;
						
						int columnLength = 0;
						columnLength = rs.getInt("bytes");
						
						// create a byte array to hold column data
						byte[] bytes = new byte[columnLength];
						System.arraycopy(data, startIndex, bytes, 0,
								columnLength);

						// Update PreparedStatement
						updatedBinaryTablePreparedStatement(bytes, rs, pstmt,
								parameterIndex);
						
						//Next Column
						parameterIndex++;

					}// end while(rs.next())
					pstmt.executeUpdate();
					rowNumber++;
					
				}// end while(bytesRead...
			}// end if format = BINARY
			
			// Table type = FIXED_TEXT or DELIMITED
			else if (TabularPDS4DataUtils.isASCIITable(tabularDataContainer.getFormat())) {
				
				pstmt = connection.prepareStatement(insertString.toString());

				final BufferedReader br = new BufferedReader(
						new InputStreamReader(tabFileUrl.openStream()));
				
				//Total number of bytes for a single row
				int rowBytes = tabularDataContainer.getRowBytes();
				
				//Total number of bytes to skip 
				long rowsDisplayOffset = this.ROWS_DISPLAY_LIMIT*rowBytes;
				
				//skip to the nth line
				br.skip(dataStartByte+rowsDisplayOffset);
				
				String currLine = null;
				
				//start at line nth+1
				int lineNumber = ROWS_DISPLAY_LIMIT+1;
				
				
				// 'A line is considered to be terminated by any one of a line feed ('\n'), 
				// a carriage return ('\r'), or a carriage return followed immediately by a linefeed.'
				// Therefore, the delimiter for a record is <CR><LF>, the default for readLine()
				//String recordDelimiter = tabularDataContainer.getRecordDelimiter();
				
				String fieldDelimiter = TabularPDS4DataUtils
						.getDelimeterASCIIFormat(tabularDataContainer.getFieldDelimiter());
				
				// if null, use the most common delimiter: comma
				if(fieldDelimiter == null)
					fieldDelimiter = ",";
				
				while ((currLine = br.readLine()) != null
						&& (Long.valueOf(tabularDataContainer.getTotalRows()) == null || tabularDataContainer
								.getTotalRows() >= lineNumber)) {
					
					updateStatus(
							"Processing: "
									+ (int) Math.floor(((float) lineNumber / tabularDataContainer
											.getTotalRows()) * 100)
									+ "% completed", false);  

					// parameter index for prepared statement
					int parameterIndex = 1;
					
					// index and storage for delimited data
					int rowFieldIndex = 0;
					String[] rowField = null;
					

					// split line into fields using field delimiter
					if (TabularPDS4DataUtils
							.isTableTypeDelimited(tabularDataContainer
									.getFormat())) {

						// tabularDataContainer.getFieldsCount(); This field is not used since the columns
						// were build recursively
						int expectedFieldsCount = tabularDataContainer.getTableTotalColumnCount();
						rowField = currLine.split(fieldDelimiter,expectedFieldsCount);
					}
					
					// reset resultset to beginning
					rs.beforeFirst();
					
					while (rs.next()) {

						try {
							// read data for each column
							String data;
							int startIndex, endIndex;

							// if table is delimited, use the delimiter. Otherwise, use
							// bytes
							if (TabularPDS4DataUtils
									.isTableTypeDelimited(tabularDataContainer
											.getFormat()) && rowField != null) {
								
								data = rowField[rowFieldIndex];
								data = data.replaceAll(fieldDelimiter, "");//remove any delimiters, if any
								rowFieldIndex++;			
							}else {
							
								startIndex = rs.getInt("start_byte") - 1;
								endIndex = rs.getInt("bytes") + startIndex;
								data = currLine.substring(startIndex, endIndex);
								data = data.replaceAll(",","");//remove all commas
							}
							
							data = data.replaceAll("\\s+","");//remove all white spaces and non visible characters
							data = data.trim();//ensure all leading and trailing spaces are not there to avoid error

							// update prepared statement
							updateASCIITablePreparedStatement(data, rs,
									pstmt, parameterIndex);

							parameterIndex++;
	
						} catch (StringIndexOutOfBoundsException e) {
							// missing values in row at end probably
							e.printStackTrace();
							throw new Exception(e);
						}

					}// end while(rs.next())
					pstmt.executeUpdate();

					lineNumber++;
				}// end while(currLine...

			}// end if format = ASCII
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem reading source file:"
					+ tabFileUrl.toString());

		} catch (SQLException SQLException) {
			SQLException.printStackTrace();
			// TODO: LAB 09/23/09 if this fails, how should it be handled?

			throw new Exception("Failure inserting tabular data in database "
					+ SQLException.getMessage());

		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (queryStmt != null)
					queryStmt.close();
				if (rs != null)
					rs.close();

			} catch (SQLException ignored) {
				ignored.printStackTrace();
				// do nothing
			}
		}
		connection.close();

	}

	
	
	/**
	 * Fill the temporary table partially
	 * 
	 * TabularDataContainer contains all the information 
	 */
	@Override
	protected void fillPartialDataTable(
			TabularDataContainer tabularDataContainer) throws Exception {
		
		String columnQuerySQL = "SELECT column_name, data_type, start_byte, bytes, start_bit, stop_bit, format FROM columns WHERE tabular_container_id = '"
				+ tabularDataContainer.getId() + "';";
		
		Connection connection = DBManager.getConnection();
		Statement queryStmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// get start of table and data file URL
		long dataStartByte = tabularDataContainer.getOffset();
		URL tabFileUrl = tabularDataContainer.getTabFileURL();
		
		try {
			queryStmt = connection.createStatement();
			rs = queryStmt.executeQuery(columnQuerySQL);
			
			if (rs.wasNull()) {
				throw new RuntimeException(
						"No records returned by column query");
			}
			
			// create SQL for prepared statement to insert data into
			// table
			StringBuilder insertString = new StringBuilder();
			insertString.append("INSERT INTO `"
					+ tabularDataContainer.getTableName() + "` SET ");

			//Build insert statement
			while (rs.next()) {

				// for unsigned types, cast to unsigned in order to get full range of values
				if (TabularPDS4DataUtils
						.isUnsignedInteger(rs
								.getString("data_type"))) {
				
					insertString.append("`" + rs.getString("column_name") + "` " + " = CAST(? AS UNSIGNED)");
					
				} else {

					insertString.append("`" + rs.getString("column_name") + "` " + " = ?");
				}
				
				if (!rs.isLast()) {
					insertString.append(",");
				}
			}

			//Make prepared statement connection
			pstmt = connection.prepareStatement(insertString.toString());
			
			//Table type = FIXED_BINARY
			if (TabularPDS4DataUtils.isBinaryTable(tabularDataContainer.getFormat())) {
				
				// create input stream
				InputStream in = new BufferedInputStream(tabFileUrl
						.openStream());
			
				in = new BufferedInputStream(tabFileUrl.openStream());
									
				//Need to start at the beginning of the table
				in.skip(dataStartByte);
				
				//Total number of bytes for a single row
				int rowBytes = tabularDataContainer.getRowBytes();
				
				// make a byte array as large as one row
				byte[] data = new byte[rowBytes];
				
				// create counters to loop through rows
				int rowNumber = 1;
				int bytesRead = 0;
			
				//offset at which to start storing bytes in byte array data. This
				//should always be 0, the beginning of the array
				int offset = 0;
				
				//read(...) automatically advances the pointer. So no need to skip()
				while (Long.valueOf(tabularDataContainer.getTotalRows()) == null
						|| (this.ROWS_DISPLAY_LIMIT >= rowNumber && tabularDataContainer.getTotalRows() >= rowNumber)
						&& (bytesRead = in.read(data, offset, rowBytes)) != -1) {

					//Read remaining bytes that were not read to ensure the data buffer contains all rowBytes
					while(bytesRead != rowBytes) {
						
						try {
						
							int extraBytesRead = in.read(data, bytesRead, rowBytes-bytesRead);
							
							//Cannot read the file anymore
							if (extraBytesRead <= 0) {
								throw new Exception("Unable to continue processing due to not been able to read the data file");
							}
							
							bytesRead = bytesRead + extraBytesRead;

						//IOException occurs if first byte cannot be read for any reason, or if the input stream has been
						//closed for any reasons, or other I/O error occurs
						}catch(IOException e) {
							e.printStackTrace();
							throw new RuntimeException(
									"IO Exception while reading data file" + e.getMessage());
						}
					}
					
					int parameterIndex = 1;
					// reset resultset to beginning
					rs.beforeFirst();
					
					// entire row of data is already in the byte array
					// for each column entry in rs
					while (rs.next()) {

						int startIndex = rs.getInt("start_byte") - 1;
						
						int columnLength = 0;
						columnLength = rs.getInt("bytes");
						
						// create a byte array to hold column data
						byte[] bytes = new byte[columnLength];
						System.arraycopy(data, startIndex, bytes, 0,
								columnLength);
	
						// Update PreparedStatement
						updatedBinaryTablePreparedStatement(bytes, rs, pstmt,
								parameterIndex);

						//Next Column
						parameterIndex++;

					}// end while(rs.next())
					pstmt.executeUpdate();
					rowNumber++;
					
				}// end while(bytesRead...
			}// end if format = BINARY
			
			// Table type = FIXED_TEXT or DELIMITED
			else if (TabularPDS4DataUtils.isASCIITable(tabularDataContainer.getFormat())) {

				pstmt = connection.prepareStatement(insertString.toString());

				// Buffer the tabular data
				final BufferedReader br = new BufferedReader(
						new InputStreamReader(tabFileUrl.openStream()));
			
				//skip to the start byte
				br.skip(dataStartByte);
				
				String currLine = null;
				int lineNumber = 1;
				
				// 'A line is considered to be terminated by any one of a line feed ('\n'), 
				// a carriage return ('\r'), or a carriage return followed immediately by a linefeed.'
				// Therefore, the delimiter for a record is <CR><LF>, the default for readLine()
				//String recordDelimiter = tabularDataContainer.getRecordDelimiter();
				
				String fieldDelimiter = TabularPDS4DataUtils
						.getDelimeterASCIIFormat(tabularDataContainer.getFieldDelimiter());

				// if null, use the most common delimiter: comma
				if(fieldDelimiter == null)
					fieldDelimiter = ",";
				
				// read line by line until ROWS_DISPLAY_LIMIT or the total number of rows if less than ROWS_DISPLAY_LIMIT
				while ((currLine = br.readLine()) != null
						&& (Long.valueOf(tabularDataContainer.getTotalRows()) == null 
						|| (this.ROWS_DISPLAY_LIMIT >= lineNumber && tabularDataContainer.getTotalRows() >= lineNumber))) {

					// Parameter index for prepared statement
					int parameterIndex = 1;
					
					// index and storage for delimited data
					int rowFieldIndex = 0;
					String[] rowField = null;
	
					// split line into fields using field delimiter
					if (TabularPDS4DataUtils
							.isTableTypeDelimited(tabularDataContainer
									.getFormat())) {
						
						// tabularDataContainer.getFieldsCount(); This field is not used since the columns
						// were build recursively
						int expectedFieldsCount = tabularDataContainer.getTableTotalColumnCount();
						rowField = currLine.split(fieldDelimiter,expectedFieldsCount);
					}

					// reset resultset to beginning before starting
					rs.beforeFirst();
					
					while (rs.next()) {

						try {
							// read data for each column
							String data;
							int startIndex, endIndex;
							
							// if table is delimited, use the delimiter. Otherwise, use
							// bytes
							if (TabularPDS4DataUtils
									.isTableTypeDelimited(tabularDataContainer
											.getFormat()) && rowField != null) {
								
								data = rowField[rowFieldIndex];
								data = data.replaceAll(fieldDelimiter, "");//remove any delimiters, if any
								rowFieldIndex++;			
							}else {
							
								startIndex = rs.getInt("start_byte") - 1;
								endIndex = rs.getInt("bytes") + startIndex;
								data = currLine.substring(startIndex, endIndex);
								data = data.replaceAll(",","");//remove all commas
							}
							
							data = data.replaceAll("\\s+","");//remove all white spaces and non visible characters
							data = data.trim();//ensure all leading and trailing spaces are not there to avoid error

							// update prepared statement
							updateASCIITablePreparedStatement(data, rs,
									pstmt, parameterIndex);
									
							parameterIndex++;
							
						} catch (StringIndexOutOfBoundsException e) {
							// missing values in row at end probably
							e.printStackTrace();
							throw new Exception(e);
						}

					}// end while(rs.next())
					pstmt.executeUpdate();

					lineNumber++;
				}// end while(currLine...

			}// end if format = ASCII
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem reading source file:"
					+ tabFileUrl.toString());

		} catch (SQLException SQLException) {
			SQLException.printStackTrace();
			// TODO: LAB 09/23/09 if this fails, how should it be handled?

			throw new Exception("Failure inserting tabular data in database "
					+ SQLException.getMessage());

		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (queryStmt != null)
					queryStmt.close();
				if (rs != null)
					rs.close();

			} catch (SQLException ignored) {
				ignored.printStackTrace();
				// do nothing
			}
		}
		connection.close();
	}
	
	
	/**
	 * Updates PreparedStatement given the data bytes, ResultSet and parameterIndex
	 * 
	 * @param bytes
	 * @param rs
	 * @param pstmt
	 * @param parameterIndex
	 * @throws Exception
	 */
	private void updatedBinaryTablePreparedStatement(byte[] bytes, ResultSet rs,
			PreparedStatement pstmt, int parameterIndex) throws Exception {

		// convert bytes to ASCII
		String value = new String(bytes);
		
		// Boolean = ASCII_Boolean
		if (TabularPDS4DataUtils.isASCIIBoolean(rs
				.getString("data_type"))) {

			// permitted values - true and false or 1 (true) or
			// 0 (false)
			if (value.length() == 1) {

				if (Integer.parseInt(value) == 1)
					pstmt.setBoolean(parameterIndex, Boolean.TRUE);
				else
					pstmt.setBoolean(parameterIndex, Boolean.FALSE);

			} else {
				pstmt.setBoolean(parameterIndex, Boolean.parseBoolean(value));
			}

			// ASCII - Date and Time data types
		} else if (TabularPDS4DataUtils.isDateTimeType(rs
				.getString("data_type"))) {

			// empty if the bytes array size = 0
			if(value.isEmpty())
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			else
				pstmt.setString(parameterIndex, value);
			
			// ASCII_Integer and ASCII_NonNegative_Integer
		} else if (TabularPDS4DataUtils.isASCIIIntegerType(rs
				.getString("data_type"))) {

			// empty if the bytes array size = 0
			if(value.isEmpty())
				pstmt.setNull(parameterIndex,
						java.sql.Types.BIGINT);
			else
				pstmt.setInt(parameterIndex,
						Integer.valueOf(value));
		
			// ASCII_Real
		} else if (TabularPDS4DataUtils.isASCIIRealType(rs.getString("data_type"))) {

			if (value.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.DECIMAL);
			} else {

				if (value.contains("E") || value.contains("e")) {
					pstmt.setBigDecimal(parameterIndex,
							new BigDecimal(value));
				} else {
		
					// format has been specified
					if (rs.getString("format") != null || !rs.getString("format").isEmpty()) {
					
						String[] formulationRules = TabularPDS4DataUtils
								.parseFieldFormat(rs.getString("format"));
						
						//extract formulation rules
						if(formulationRules != null) {
						
							// extract precision from format_field
							// formulationRules[2] = precision
							if(formulationRules[2] != null && !formulationRules[2].isEmpty()) {
								String precision = formulationRules[2];
								pstmt.setBigDecimal(parameterIndex,
										new BigDecimal(value,new MathContext(Integer.parseInt(precision))));
								
								//standard format
							} else {
								pstmt.setBigDecimal(parameterIndex,
										new BigDecimal(value));
							}
						} else {
							pstmt.setBigDecimal(parameterIndex,
									new BigDecimal(value));
						}
					} else {
					
						pstmt.setBigDecimal(parameterIndex,
								new BigDecimal(value));
					}
				}	
			}
			// Numeric_Base
		} else if (TabularPDS4DataUtils.isASCIIRealType(rs.getString("data_type"))) {
		
			if (value.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			} else {
				pstmt.setString(parameterIndex, value);
			}
		
			// MD5 Checksum
		} else if (TabularPDS4DataUtils.isMD5ChecksumType(rs.getString("data_type"))) {

			if (value.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			} else {
				pstmt.setString(parameterIndex, value);
			}
			
		// String and character data types
		} else if (TabularPDS4DataUtils.isStringType(rs.getString("data_type"))) {
			
			if (value.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			} else {
				pstmt.setString(parameterIndex, value);
			}

		// MSB Signed Integer
		} else if (TabularPDS4DataUtils.isMSBSignedInteger(rs
				.getString("data_type"))) {
       
			// 2 bytes
			if (TabularPDS4DataUtils
					.is2ByteBinaryInteger(rs
							.getString("data_type"))) {

				pstmt.setShort(parameterIndex,
						BinaryPDS4ConversionUtils.convertByteArrayToShort(bytes));
				
				// 4 bytes
			} else if (TabularPDS4DataUtils
					.is4ByteBinaryInteger(rs
							.getString("data_type"))) {

				pstmt.setInt(parameterIndex,
						BinaryPDS4ConversionUtils.convertByteArrayToInt(bytes));

				// 8 bytes
			} else if (TabularPDS4DataUtils
					.is8ByteBinaryInteger(rs
							.getString("data_type"))) {

				pstmt.setLong(parameterIndex,
						BinaryPDS4ConversionUtils.convertByteArrayToLong(bytes));
				
				// anything else
			} else {
				pstmt.setNull(parameterIndex, java.sql.Types.SMALLINT);
			}

		// LSB Signed Integer
		} else if (TabularPDS4DataUtils.isLSBSignedInteger(rs
				.getString("data_type"))) {
			
			long valueLongType = BinaryPDS4ConversionUtils.convertSignedLSBIntegers(
					bytes, bytes.length);
			pstmt.setLong(parameterIndex, valueLongType);
			
			if (TabularPDS4DataUtils
			.is1ByteBinaryInteger(rs
					.getString("data_type"))) {
			
			
			// 2 bytes
			} else if (TabularPDS4DataUtils
					.is2ByteBinaryInteger(rs
							.getString("data_type"))) {

				pstmt.setLong(parameterIndex,
						valueLongType);

				// 4 bytes
			} else if (TabularPDS4DataUtils
					.is4ByteBinaryInteger(rs
							.getString("data_type"))) {
				
				pstmt.setLong(parameterIndex,
						valueLongType);

				// 8 bytes
			} else if (TabularPDS4DataUtils
					.is8ByteBinaryInteger(rs
							.getString("data_type"))) {
				
				pstmt.setLong(parameterIndex,
						valueLongType);
			
			} else {
				pstmt.setNull(parameterIndex, java.sql.Types.SMALLINT);
			}
		
		// MSB Unsigned Integer
		} else if (TabularPDS4DataUtils.isMSBUnsignedInteger(rs
				.getString("data_type"))) {
			
			long valueLongType = BinaryPDS4ConversionUtils
					.convertUnsignedMSBIntegers(bytes, bytes.length);
				
				// 2 bytes
			if (TabularPDS4DataUtils
					.is2ByteBinaryInteger(rs
							.getString("data_type"))) {

				pstmt.setLong(parameterIndex,
						valueLongType);

				// 4 bytes
			} else if (TabularPDS4DataUtils
					.is4ByteBinaryInteger(rs
							.getString("data_type"))) {
				
				pstmt.setLong(parameterIndex,
						valueLongType);

				// 8 bytes
			} else if (TabularPDS4DataUtils
					.is8ByteBinaryInteger(rs
							.getString("data_type"))) {
				
				pstmt.setLong(parameterIndex,
						valueLongType);
				
			} else {
				pstmt.setNull(parameterIndex, java.sql.Types.SMALLINT);
			}
			
		// Unsigned Byte
		} else if (TabularPDS4DataUtils.isUnsignedByte(rs
				.getString("data_type"))) {
			
			long valueLongType = BinaryPDS4ConversionUtils
					.convertByte(bytes, bytes.length);
			
			pstmt.setLong(parameterIndex, valueLongType);
			
		// Signed Byte
		} else if (TabularPDS4DataUtils.isSignedByte(rs
				.getString("data_type"))) {
			
			long valueLongType = BinaryPDS4ConversionUtils
					.convertByte(bytes, bytes.length);
			
			pstmt.setLong(parameterIndex, valueLongType);
		
		// Unsigned LSB Integer
		} else if (TabularPDS4DataUtils.isLSBUnsignedInteger(rs
				.getString("data_type"))) {
			
			long valueLongType = BinaryPDS4ConversionUtils
					.convertUnsignedLSBIntegers(bytes, bytes.length);
		
			// 2 bytes
			if (TabularPDS4DataUtils
					.is2ByteBinaryInteger(rs
							.getString("data_type"))) {

				pstmt.setLong(parameterIndex,
						valueLongType);

				// 4 bytes
			} else if (TabularPDS4DataUtils
					.is4ByteBinaryInteger(rs
							.getString("data_type"))) {
				
				pstmt.setLong(parameterIndex,
						valueLongType);

				// 8 bytes
			} else if (TabularPDS4DataUtils
					.is8ByteBinaryInteger(rs
							.getString("data_type"))) {
				
				pstmt.setLong(parameterIndex,
						valueLongType);
			
			} else {
				pstmt.setNull(parameterIndex, java.sql.Types.SMALLINT);
			}

		// Reals - IEEE floating-point representation of real numbers
		} else if (TabularPDS4DataUtils.isRealType(rs.getString("data_type"))) {

			// MSB Real type uses the IEEE 754 encoding
			if (TabularPDS4DataUtils.isMSBRealType(rs.getString("data_type"))) {
				
				// IEEE754MSBSingle
				if (TabularPDS4DataUtils.isSingleRealType(rs.getString("data_type"))) {
					
					int intValue = BinaryPDS4ConversionUtils
                            .convertByteArrayToInt(bytes);
                    float convertedFloatBits = Float.intBitsToFloat(intValue);

                    //NaN
                    if(Float.isNaN(convertedFloatBits))
                        pstmt.setNull(parameterIndex, java.sql.Types.FLOAT);
                    
                    //+Inf
                    else if( Float.isInfinite(convertedFloatBits) && ( (intValue >> 31) == 0))
                        pstmt.setFloat(parameterIndex, Float.intBitsToFloat(BinaryPDS4ConversionUtils
                                .convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));
                    //-Inf
                    else if( Float.isInfinite(convertedFloatBits))
                        pstmt.setFloat(parameterIndex, Float.intBitsToFloat(BinaryPDS4ConversionUtils
                                .convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));
                    
                    else
                        pstmt.setFloat(parameterIndex, convertedFloatBits);
                    
					
					// IEEE754MSBDouble
				} else if (TabularPDS4DataUtils.isDoubleRealType(rs.getString("data_type"))) {
					
					long longValue = BinaryPDS4ConversionUtils.convertByteArrayToLong(bytes);
                    double convertedDoubleBits = Double.longBitsToDouble(longValue);
                    
                    //Check for NAN, +-Infinity. Since mysql does not handle these limits, the following values
                    //will be used to denote these limits
                    //NAN - null - The cell appears empty
                    //-Infinity -
                    //+Infinity
                    
                    //NaN
                    if(Double.isNaN(convertedDoubleBits))
                        pstmt.setNull(parameterIndex, java.sql.Types.DOUBLE);
                    //+Inf
                    else if(Double.isInfinite(convertedDoubleBits) && ( (longValue >> 63) == 0))
                        pstmt.setDouble(parameterIndex, Double.longBitsToDouble(BinaryPDS4ConversionUtils
                                .convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
                                        (byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
                    //-Inf
                    else if(Double.isInfinite(convertedDoubleBits))
                        pstmt.setDouble(parameterIndex, Double.longBitsToDouble(BinaryPDS4ConversionUtils
                                .convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
                                        (byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
                    //Double within range
                    else
                        pstmt.setDouble(parameterIndex, convertedDoubleBits); 
                    
				} else {
					pstmt.setNull(parameterIndex, java.sql.Types.FLOAT);
				}

				// LSB Real type - Need to put it in IEEE 754 MSB format
			} else {
				
				// IEEE754LSBSingle
				if (TabularPDS4DataUtils.isSingleRealType(rs.getString("data_type"))) {

					int ieeeSingle = BinaryPDS4ConversionUtils.IEEE754LSBToIEEE754MSBSingle(bytes);
					float convertedFloatBits = Float.intBitsToFloat(ieeeSingle);

					//NaN
					if(Float.isNaN(convertedFloatBits))
						pstmt.setNull(parameterIndex, java.sql.Types.FLOAT);

					//+Inf
					else if( Float.isInfinite(convertedFloatBits) && ( (ieeeSingle >> 31) == 0))
						pstmt.setFloat(parameterIndex, Float.intBitsToFloat(BinaryPDS4ConversionUtils
								.convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

					//-Inf
					else if( Float.isInfinite(convertedFloatBits))
						pstmt.setFloat(parameterIndex, Float.intBitsToFloat(BinaryPDS4ConversionUtils
								.convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

					else
						pstmt.setFloat(parameterIndex, convertedFloatBits);
					
					// IEEE754LSBDouble
				} else if (TabularPDS4DataUtils.isDoubleRealType(rs.getString("data_type"))) {
					
					long ieeeValue = BinaryPDS4ConversionUtils.IEEE754LSBToIEEE754MSBDouble(bytes);
                    double convertedDoubleBits = Double.longBitsToDouble(ieeeValue);
                    
                    //NaN
                    if(Double.isNaN(convertedDoubleBits))
                        pstmt.setNull(parameterIndex, java.sql.Types.DOUBLE);
                    //+Inf
                    else if(Double.isInfinite(convertedDoubleBits) && ( (ieeeValue >> 63) == 0))
                        pstmt.setDouble(parameterIndex, Double.longBitsToDouble(BinaryPDS4ConversionUtils
                                .convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
                                        (byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
                    //-Inf
                    else if(Double.isInfinite(convertedDoubleBits))
                        pstmt.setDouble(parameterIndex, Double.longBitsToDouble(BinaryPDS4ConversionUtils
                                .convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
                                        (byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
                    //Double within range
                    else
                        pstmt.setDouble(parameterIndex, convertedDoubleBits);
					
				} else {
					pstmt.setNull(parameterIndex, java.sql.Types.FLOAT);
				}	
			}
				
		// Complex - Consist of two IEEE-format real numbers of the same precision
		} else if (TabularPDS4DataUtils.isComplexType(rs.getString("data_type"))) {
			
			// MSB Complex type
			if (TabularPDS4DataUtils.isMSBComplexType(rs.getString("data_type"))) {
				
				// Complex 8-byte
				if (TabularPDS4DataUtils.isComplex8ByteType(rs.getString("data_type"))) {
					
					int intValue = BinaryPDS4ConversionUtils
                            .convertByteArrayToInt(bytes);
                    float convertedFloatBits = Float.intBitsToFloat(intValue);

                    //NaN
                    if(Float.isNaN(convertedFloatBits))
                        pstmt.setNull(parameterIndex, java.sql.Types.FLOAT);
                    
                    //+Inf
                    else if( Float.isInfinite(convertedFloatBits) && ( (intValue >> 31) == 0))
                        pstmt.setFloat(parameterIndex, Float.intBitsToFloat(BinaryPDS4ConversionUtils
                                .convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));
                    //-Inf
                    else if( Float.isInfinite(convertedFloatBits))
                        pstmt.setFloat(parameterIndex, Float.intBitsToFloat(BinaryPDS4ConversionUtils
                                .convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));
                    
                    else
                        pstmt.setFloat(parameterIndex, convertedFloatBits);
                    
					// Complex 16-byte
				} else if (TabularPDS4DataUtils.isComplex16ByteType(rs.getString("data_type"))) {
					
					long longValue = BinaryPDS4ConversionUtils.convertByteArrayToLong(bytes);
                    double convertedDoubleBits = Double.longBitsToDouble(longValue);
                    
                    //Check for NAN, +-Infinity. Since mysql does not handle these limits, the following values
                    //will be used to denote these limits
                    //NAN - null - The cell appears empty
                    //-Infinity -
                    //+Infinity
                    
                    //NaN
                    if(Double.isNaN(convertedDoubleBits))
                        pstmt.setNull(parameterIndex, java.sql.Types.DOUBLE);
                    //+Inf
                    else if(Double.isInfinite(convertedDoubleBits) && ( (longValue >> 63) == 0))
                        pstmt.setDouble(parameterIndex, Double.longBitsToDouble(BinaryPDS4ConversionUtils
                                .convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
                                        (byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
                    //-Inf
                    else if(Double.isInfinite(convertedDoubleBits))
                        pstmt.setDouble(parameterIndex, Double.longBitsToDouble(BinaryPDS4ConversionUtils
                                .convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
                                        (byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
                    //Double within range
                    else
                        pstmt.setDouble(parameterIndex, convertedDoubleBits); 
					
				} else {
					pstmt.setNull(parameterIndex, java.sql.Types.FLOAT);
				}

				// LSB Complex type
			} else {
				
				// Complex 8-byte
				if (TabularPDS4DataUtils.isComplex8ByteType(rs.getString("data_type"))) {
					
					int ieeeSingle = BinaryPDS4ConversionUtils.IEEE754LSBToIEEE754MSBSingle(bytes);
					float convertedFloatBits = Float.intBitsToFloat(ieeeSingle);

					//NaN
					if(Float.isNaN(convertedFloatBits))
						pstmt.setNull(parameterIndex, java.sql.Types.FLOAT);

					//+Inf
					else if( Float.isInfinite(convertedFloatBits) && ( (ieeeSingle >> 31) == 0))
						pstmt.setFloat(parameterIndex, Float.intBitsToFloat(BinaryPDS4ConversionUtils
								.convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

					//-Inf
					else if( Float.isInfinite(convertedFloatBits))
						pstmt.setFloat(parameterIndex, Float.intBitsToFloat(BinaryPDS4ConversionUtils
								.convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

					else
						pstmt.setFloat(parameterIndex, convertedFloatBits);
					
					// Complex 16-byte
				} else if (TabularPDS4DataUtils.isComplex16ByteType(rs.getString("data_type"))) {
					
					long ieeeValue = BinaryPDS4ConversionUtils.IEEE754LSBToIEEE754MSBDouble(bytes);
                    double convertedDoubleBits = Double.longBitsToDouble(ieeeValue);
                    
                    //NaN
                    if(Double.isNaN(convertedDoubleBits))
                        pstmt.setNull(parameterIndex, java.sql.Types.DOUBLE);
                    //+Inf
                    else if(Double.isInfinite(convertedDoubleBits) && ( (ieeeValue >> 63) == 0))
                        pstmt.setDouble(parameterIndex, Double.longBitsToDouble(BinaryPDS4ConversionUtils
                                .convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
                                        (byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
                    //-Inf
                    else if(Double.isInfinite(convertedDoubleBits))
                        pstmt.setDouble(parameterIndex, Double.longBitsToDouble(BinaryPDS4ConversionUtils
                                .convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
                                        (byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
                    //Double within range
                    else
                        pstmt.setDouble(parameterIndex, convertedDoubleBits);
						
				} else {
					pstmt.setNull(parameterIndex, java.sql.Types.FLOAT);
				}
			}
			
		// Bit String
		} else if (TabularPDS4DataUtils.isBitStringType(rs.getString("data_type"))) {
			
			ByteArrayBitIterable singleBits = new ByteArrayBitIterable(bytes);
			
			// if start_bit and stop_bits are null, then SignedBitString and
			// UnsignedBitString do not consist of FieldBits. Just treat the
			// string as a single unit
			if (!TabularPDS4DataUtils.isNull(rs
					.getString("start_bit"))
					&& !TabularPDS4DataUtils.isNull(rs
							.getString("stop_bit"))) {

				int startBit = Integer.parseInt(rs
						.getString("start_bit"));
				int stopBit = Integer.parseInt(rs
						.getString("stop_bit"));

				singleBits.setStartBit(startBit);
				singleBits.setStopBit(stopBit);
			}
			
			if (!singleBits.isEmpty()) {

				StringBuilder bitString = new StringBuilder();
				long finalValue = 0;
				
				try {

					// byte - 1 byte
					if (singleBits.sizeInBits() <= 8) {

						// signed
						if (TabularPDS4DataUtils
								.isSignedBitStringType(rs
										.getString("data_type"))) {
							finalValue = (byte) Short.parseShort(singleBits.getStringofBits(), 2);

							// unsigned
						} else {
							finalValue = Short.parseShort(singleBits.getStringofBits(), 2);
						}

						bitString.append(finalValue);
						
						// short = 2 bytes
					} else if (singleBits.sizeInBits() <= 16) {

						// signed
						if (TabularPDS4DataUtils
								.isSignedBitStringType(rs
										.getString("data_type"))) {
							finalValue = (short)Integer.parseInt(singleBits.getStringofBits(),2);

							//unsigned
						}else{
							finalValue = Integer.parseInt(singleBits.getStringofBits(),2);
						}

						bitString.append(finalValue);
						
						// int - 4 bytes
					} else if(singleBits.sizeInBits() <= 32) {

						// signed
						if (TabularPDS4DataUtils
								.isSignedBitStringType(rs
										.getString("data_type"))) {
							finalValue = (int) Long.parseLong(singleBits.getStringofBits(), 2);

							// unsigned
						} else {
							finalValue = Long.parseLong(singleBits.getStringofBits(), 2);
						}

						bitString.append(finalValue);
						
						// long - 8 bytes
					} else if(singleBits.sizeInBits() <= 64) {

						// signed
						if (TabularPDS4DataUtils
								.isSignedBitStringType(rs
										.getString("data_type"))) {

							StringBuilder sb = new StringBuilder(singleBits.getStringofBits());
							sb.replace(0, 1, "-");
							finalValue = Long.parseLong(sb.toString(), 2);

							// unsigned
						} else {
							StringBuilder sb = new StringBuilder(singleBits.getStringofBits());
							sb.replace(0, 1, "0");
							finalValue = Long.parseLong(sb.toString(), 2);
						}

						bitString.append(finalValue);
						
						// longer than 8 bytes not supported
					} else {
						//do not append the decimal value of this string since it is too big
					}

					bitString.append(" ("
							+ singleBits.getStringofBits()
							+ ")");
					pstmt.setString(parameterIndex,
							bitString.toString());
				
				} catch (Exception e) {
					pstmt.setNull(parameterIndex,java.sql.Types.CHAR);
				}
			} else {
				pstmt.setNull(parameterIndex,java.sql.Types.CHAR);
			}
				
		} else {
			
			if (value.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			} else {
				pstmt.setString(parameterIndex, value);
			}	
		}
	}
	
	/**
	 * Updates PreparedStatement given the string data, ResultSet and parameterIndex
	 * for a TableDelimited or TableCharacter
	 * 
	 * 
	 * @param data
	 * @param rs
	 * @param pstmt
	 * @param parameterIndex
	 * @throws Exception
	 */
	private void updateASCIITablePreparedStatement(String data, ResultSet rs,
			PreparedStatement pstmt, int parameterIndex) throws Exception {
		
		// ASCII_Boolean type
		if (TabularPDS4DataUtils.isASCIIBoolean(rs.getString("data_type"))) {
			
			if (data == null || data.isEmpty()) {
				pstmt.setNull(parameterIndex, java.sql.Types.BOOLEAN);
				
			} else {
				// the value is either 1 or 0, use correct boolean type
				// if integer 1 or 0 values used
				if(data.length() == 1) {
					
					if(Integer.parseInt(data) == 1)
						pstmt.setBoolean(parameterIndex, Boolean.TRUE);
					else
						pstmt.setBoolean(parameterIndex, Boolean.FALSE);
					
				}else {
					pstmt.setBoolean(parameterIndex, Boolean.parseBoolean(data));
				}
			}
			
		// ASCII_DATE_TIME variants
		} else if (TabularPDS4DataUtils.isDateTimeType(rs.getString("data_type"))) {
			
			if (data == null || data.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
				
			} else {
				pstmt.setString(parameterIndex, data);
			}
			
		// ASCII_Integer type
		} else if (TabularPDS4DataUtils.isASCIIIntegerType(rs.getString("data_type"))) {

			if (data == null || data.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.BIGINT);
			} else {
				
				//remove the + sign
				if (data.startsWith("+"))
					data = data.replace("+", "");
				
				pstmt.setInt(parameterIndex,
						Integer.valueOf(data));
			}

		// ASCII_Real
		} else if (TabularPDS4DataUtils.isASCIIRealType(rs.getString("data_type"))) {

			if (data == null || data.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.DECIMAL);
			} else {
				
				
				//remove the + sign
				if (data.startsWith("+"))
					data = data.replace("+", "");
			
				pstmt.setBigDecimal(parameterIndex,
						new BigDecimal(data));
			}
			
		// ASCII_Numeric Base
		} else if (TabularPDS4DataUtils.isASCIINumericBaseNType(rs.getString("data_type"))) {
			
			if (data == null || data.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			} else {
				pstmt.setString(parameterIndex, data);
			}	
			
		// MD5 Checksum
		} else if (TabularPDS4DataUtils.isMD5ChecksumType(rs.getString("data_type"))) {

			if (data == null || data.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			} else {
				pstmt.setString(parameterIndex, data);
			}	

		// String and character data types
		} else if (TabularPDS4DataUtils.isStringType(rs.getString("data_type"))) {
			
			if (data == null || data.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			} else {
				pstmt.setString(parameterIndex, data);
			}
			
		// Anything else
		} else {
			
			if (data == null || data.isEmpty()) {
				pstmt.setNull(parameterIndex,
						java.sql.Types.CHAR);
			} else {
				pstmt.setString(parameterIndex, data);
			}
		}
		
	}

	/**
	 * Defines an enumeration for the different table types that can be
	 * extracted. Holds a readable description of the table type.
	 */
	private static enum TableType {

		/** A fixed-width binary table. */
		FIXED_BINARY("fixed-width binary table"),

		/** A fixed-width text table. */
		FIXED_TEXT("fixed-width character table"),

		/** A delimited table. */
		DELIMITED("delimited table");

		private String readableType;

		private TableType(String readableType) {
			this.readableType = readableType;
		}
		/**
		 * Return shorthand string representation of TableType
		 * 
		 * @return
		 */
		public String getStringTypeRepresentation(){
			
			String pds3Representation = null;
			
			switch (this){
			
				//TableCharacter
				case FIXED_TEXT:
					pds3Representation = "CHAR";
				break;
			
				//TableDelimited
				case DELIMITED:
					pds3Representation = "DELIM";
				break;
			
				//TableBinary
				case FIXED_BINARY:
					pds3Representation = "BINARY";
				break;
			
				default:
				break;
			}
			
			return pds3Representation;
		}

		/**
		 * Gets the readable name for the table type.
		 *
		 * @return the name of the table type
		 */
		@SuppressWarnings("unused")
		public String getReadableType() {
			return readableType;
		}
	}
	
	public String getPDSLabelType() {
		return this.PDS_LABEL_TYPE;
	}
	
	/**
	 * Inner class to make a byte array iterable. Adds the ability
	 * to iterate through the ByteArrayBitIterable and get a range
	 * of bits
	 * 
	 * For FieldBits, the start and stop bits start are integers
	 * from >=1 and <=2147483647. Therefore, the start and stop
	 * bits are automatically decremented by one
	 * 
	 * Implements Iterable so that it can be used in for loop format
	 * 
	 * @author German H. Flores
	 *
	 */
	public class ByteArrayBitIterable implements Iterable<Boolean>{
	    private final byte[] array;
	    private int arrayStartBitIndex;
	    private int arrayStartByteIndex;
	    private int arrayStopBitIndex;
	    private int arrayStopByteIndex;
	    private int startBitIndex;

	    public ByteArrayBitIterable(byte[] array) {
	        this.array = array;
	        this.arrayStartBitIndex = 0;
	        this.arrayStartByteIndex = 0;
	        this.arrayStopByteIndex = array.length;
	        this.arrayStopBitIndex = array.length*8;
	        this.startBitIndex = 0;
	    }

		@Override
	    public Iterator<Boolean> iterator() {
	    	return new ByteIterator();
	    }
	    
	    public int sizeInBits(){
	    	return this.array.length*8;
	    }
	    
	    public int sizeInBytes(){
	    	return this.array.length;
	    }
	    
	    public boolean isEmpty() {
	    	if (array == null || this.array.length == 0)
	    		return true;
	    	return false;
	    }
	    
	    public void setStartBit(int startBit) {
	    	
	    	// start at 0
	    	startBit--;
	    	
	    	int index = startBit;
	    	int byteIndex = 0;
	    	
	    	//Calculate index of a bit in the byte array and the 
	    	//corresponding Byte number
	    	while(index > 7) {
	    		index = index - 8;
	    		byteIndex++;
	    	}
	    	
	    	this.arrayStartBitIndex = index;
	    	this.arrayStartByteIndex = byteIndex;
	    	this.startBitIndex = startBit;
	    }
	    
	    public void setStopBit(int stopBit) {
	    	
	    	// start at 0
	    	stopBit--;
	    	
	    	if(stopBit > this.array.length*8)
	    		throw new UnsupportedOperationException();
	    	
	    	int index = stopBit;
	    	int byteIndex = 0;
	    	
	    	while(index > 7) {
	    		index = index - 8;
	    		byteIndex++;
	    	}
	    	
	    	this.arrayStopBitIndex = stopBit;
	    	this.arrayStopByteIndex = byteIndex+1;
	    }
	    
	    public String getStringofBits() {
	    	
	    	StringBuilder stringOfBits = new StringBuilder("");
	    	
	    	for (boolean bit : this){
				bitToString(stringOfBits, bit);
			}
	    	
	    	return stringOfBits.toString();
	    	
	    }
	    
	    public String getHexStringOfBits() {
	    	
	    	StringBuilder stringOfBits = new StringBuilder("");
	    	
	    	for (boolean bit : this){
				bitToString(stringOfBits, bit);
			}
	    	return Long.toHexString(Long.parseLong(stringOfBits.toString()));
	    }
	    
	    private void bitToString(StringBuilder byteValue, boolean bit) {
			
			if(bit)
				byteValue.append(1);
			else
				byteValue.append(0);
		}
	   
	    private class ByteIterator implements Iterator<Boolean> {
	    	
	    	private int bitIndex = arrayStartBitIndex;//Indicates individual bits in a byte
	    	private int bitArrayIndex = startBitIndex;//Indicated individual bits in the array
	    	private int arrayIndex = arrayStartByteIndex;//Indicates the byte index

			@Override
			public boolean hasNext() {        	   
				return ((arrayIndex < arrayStopByteIndex) && (bitArrayIndex <= arrayStopBitIndex));
			}

			@Override
			public Boolean next() {
				Boolean val = (array[arrayIndex] >> (7 - bitIndex) & 1) == 1;
				bitIndex++;
				bitArrayIndex++;
               
				if (bitIndex == 8) {
					bitIndex = 0;
					arrayIndex++;
				}
				return val;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
	    }
	}

	@Override
	protected void fillPartialDataTable(
			TabularDataContainer tabularDataContainer, URL tabFileUrl,
			long dataStartByte) throws Exception {		
	}

	@Override
	protected void fillDataTable(TabularDataContainer tabularDataContainer,
			URL tabFileUrl, long dataStartByte) throws Exception {		
	}
	
	@Override
	public void parseLabel() {
	}
	
	@Override
	public TabularDataContainer buildTabularDataContainer(
			ObjectStatement statement, PointerStatement pointer)
			throws Exception {
		return null;
	}


}
