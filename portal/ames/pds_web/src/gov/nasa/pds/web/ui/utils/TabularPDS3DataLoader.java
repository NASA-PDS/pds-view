package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.containers.FileReference;
import gov.nasa.pds.tools.dict.DictIdentifier;
import gov.nasa.pds.tools.dict.parser.DictIDFactory;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.Numeric;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.web.applets.CancelledException;
import gov.nasa.pds.web.ui.containers.ColumnInfo;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.tabularData.Column;
import gov.nasa.pds.web.ui.containers.tabularData.Element;
import gov.nasa.pds.web.ui.containers.tabularData.Row;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularLabelContainer;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipInputStream;

/*
 * Reads PDS label and its associated tabular data files into a SliceContainer
 * object.
 * 
 * @author Laura Baalman
 * 
 * @author German H. Flores
 */
public class TabularPDS3DataLoader extends TabularDataLoader {

	/**
	 *	Defines type of label this loader processes
	 */
	private final String PDS_LABEL_TYPE = "pds3";
	private Label label = null;

	public TabularPDS3DataLoader(final String labelURLString) {// should use file
		super(labelURLString);

		// create label and labelContainer objects
		parseLabel();
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

	public TabularPDS3DataLoader(final String labelURLString,
			final StatusContainer status, final String sessionId) {
		super(labelURLString,status,sessionId);

		// create label and labelContainer objects
		parseLabel();
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
	 * sliceContainer is passed in
	 */
	public TabularPDS3DataLoader(SliceContainer slice,
			final StatusContainer status, final String sessionId) {
		super(slice,status,sessionId);

		// create label and labelContainer objects
		parseLabel();

	}

	/**
	 * return the type of this data loader
	 * @return
	 */
	public  String getDataType() {
		return this.PDS_LABEL_TYPE;
	}


	/*
	 * reads label from labelURLString and creates TabularLabelContainer and
	 * label objects from it for use by rest of class. called by constructors.
	 */
	@Override
	public void parseLabel() {
		URL labelUrl = null;

		try {
			labelUrl = new URL(this.slice.getLabelURLString());
		} catch (MalformedURLException URLException) {
			URLException.printStackTrace();
			// TODO no op? throw new exception?
			// update status to error? then exit?

		}
		this.labelContainer = new TabularLabelContainer(labelUrl, null,
				DataSetValidator.getMasterDictionary());
		if (this.labelContainer.isValid()) {
			this.label = this.labelContainer.getLabelObj();
		}
		// TODO else? can labelContainer creation or getLabelObj fail?
	}

	/**
	 * Extract table and file information to display
	 * 
	 * @param label
	 * @param tabularDataContainer
	 */
	private void extractTableFileInformation(Label label,
			TabularDataContainer tabularDataContainer) {

		List<gov.nasa.pds.tools.label.Statement> statements = label
				.getStatements();
		StringBuilder tableInformation = new StringBuilder();
		StringBuilder fileInformation = new StringBuilder();

		for (gov.nasa.pds.tools.label.Statement statement : statements) {

			try {

				DictIdentifier objId = DictIDFactory
						.createObjectDefId(statement.getIdentifier().getId());

				if (objId != null) {

					// only show statements that are not blank
					if (!statement.getIdentifier().getId().isEmpty()
							&& !statement.getLabel()
							.getAttribute(objId.toString())
							.getValue().toString().isEmpty()) {


						if (objId.toString().equalsIgnoreCase("FILE_RECORDS")
								|| objId.toString().equalsIgnoreCase("RECORD_BYTES")
								|| objId.toString().equalsIgnoreCase("RECORD_TYPE")) {


							fileInformation.append("- "
									+ statement.getIdentifier().getId() + ": ");
							fileInformation.append(statement.getLabel()
									.getAttribute(objId.toString()).getValue());
							fileInformation.append("\n");

						} else {


							tableInformation.append("- "
									+ statement.getIdentifier().getId() + ": ");
							tableInformation.append(statement.getLabel()
									.getAttribute(objId.toString()).getValue());
							tableInformation.append("\n");

						}
					}
				}
			} catch (Exception e) {
				// skip statements with errors
			}
		}

		if (tableInformation.length() == 0)
			tabularDataContainer
			.setTableInformation("No information available");
		else
			tabularDataContainer.setTableInformation(tableInformation
					.toString());

		if (fileInformation.length() == 0)
			tabularDataContainer.setFileInformation("No information available");
		else
			tabularDataContainer.setFileInformation(fileInformation.toString());
	}

	/*
	 * gets labelcontainer's tabularPointers and passes them and the
	 * ObjectStatement for each pointer, including external structures, to
	 * BuildTabularDataContainer, which creates a TabularDataContainer out of
	 * it, which is then added to the slice. also inserts columns for the object
	 * statements into column table for persistence.
	 */
	private void readSupportedObjects() throws Exception {

		// get tabular pointers (most common)
		List<PointerStatement> pointers = this.labelContainer
				.getTabularPointers();

		// look for pointers within other objects (e.g., FILE)
		if (pointers.isEmpty())
			pointers = this.labelContainer.getPointers();

		if (!pointers.isEmpty()) {

			for (PointerStatement pointerStatement : pointers) {

				// get all object statements for each pointer
				// get table object id
				DictIdentifier objId = DictIDFactory
						.createObjectDefId(pointerStatement.getIdentifier()
								.getId());
				List<ObjectStatement> objectStatements = this.labelContainer
						.getAllObjects(objId);
				// .getAllFileObjects(objId);
				for (ObjectStatement objectStatement : objectStatements) {
					// build TabularDataContainer with object and pointer
					// statements
					TabularDataContainer tabularDataContainer = buildTabularDataContainer(
							objectStatement, pointerStatement);

					// extract table and file information that will be shown
					// to the user as they hover over the label and data file
					// Hyperlink
					extractTableFileInformation(this.label,tabularDataContainer);

					this.slice.addTabularDataObject(tabularDataContainer);
					insertColumns(tabularDataContainer,
							this.slice.getLabelURLString());
				}
			}
		} else {
			throw new Exception("No data object pointers found in the label.");
		}
	}

	/*
	 * Reads external structure files in the LABEL folder and adds object
	 * statements to the passed in object statement
	 * 
	 * @param statement ObjectStatement for a tabular PDS object (TABLE, SERIES,
	 * SPREADSHEET, SPECTRUM)
	 */
	public ObjectStatement addStructures(ObjectStatement statement) {
		// get pointers for this object statement
		final List<PointerStatement> pointers = statement.getPointers();
		// loop through
		for (final PointerStatement secondLevelPointer : pointers) {
			// find structure pointers
			if (secondLevelPointer.getIdentifier().toString().equals(
					"STRUCTURE")) { //$NON-NLS-1$
				// assuming one file
				@SuppressWarnings("unused")
				FileReference file = secondLevelPointer.getFileRefs().get(0);
				DefaultLabelParser parser = new DefaultLabelParser(
						new ManualPathResolver());
				try {

					// read label fragment
					URL fragmentURL = this.labelContainer.getFirstURI(
							secondLevelPointer).toURL();
					fragmentURL = TabularDataUtils
							.findStructureFile(fragmentURL);
					Label fragment = parser.parsePartial(fragmentURL,
							this.label);
					// loop through objects, adding them to statement
					for (ObjectStatement column : fragment.getObjects()) {
						statement.addStatement(column);
					}

				} catch (LabelParserException e) {
					// ?
					e.printStackTrace();
					throw new RuntimeException(
							"unable to parse STRUCTURE pointer" //$NON-NLS-1$
							+ e.getMessage());

				} catch (IOException e) {
					// this.errors.add(e);
					e.printStackTrace();
					throw new RuntimeException(
							"IO Exception while retrieving STRUCTURE pointer" //$NON-NLS-1$
							+ e.getMessage());

				}
			}
		}// end for loop reading pointers
		return statement;
	}

	/*
	 * Creates a TabularDataContainer, sets type, delimiter, as well as creating
	 * the object's column objects. Calls get getDataPreview to retrieve sample
	 * date time formats. Sets total rows, product_id, data_set_id.
	 * 
	 * @param statement ObjectStatement for a tabular PDS object (TABLE, SERIES,
	 * SPREADSHEET, SPECTRUM) to be used as the basis for the creation of a
	 * tabularDataContainer
	 * 
	 * @param pointer PointerStatement for a tabular PDS object to be used as
	 * the basis for the creation of a TabularDataContainer
	 */
	@SuppressWarnings("nls")
	@Override
	public TabularDataContainer buildTabularDataContainer(
			ObjectStatement statement, PointerStatement pointer)
					throws Exception {

		TabularDataContainer table = new TabularDataContainer();

		// set the type this TabularDataContainer holds
		table.setPDSType(this.PDS_LABEL_TYPE);

		table.setType(statement.getIdentifier().getId());
		// LAB 04/28/10 spreadsheets currently blocked
		if (table.getType().endsWith("SPREADSHEET")) {
			// translate DELIMITER TO actual character
			String delimiterName = statement.getAttribute("FIELD_DELIMITER")
					.getValue().toString();
			String delimiterValue;
			if (delimiterName.equalsIgnoreCase("COMMA"))
				delimiterValue = ",";
			else if (delimiterName.equalsIgnoreCase("SEMICOLON"))
				delimiterValue = ";";
			else if (delimiterName.equalsIgnoreCase("TAB"))
				delimiterValue = "\t";
			else if (delimiterName.equalsIgnoreCase("VERTICAL_BAR"))
				delimiterValue = "|";
			else
				delimiterValue = ",";

			// COMMA, SEMICOLON, TAB, or VERTICAL_BAR
			table.setDelimiter(delimiterValue);
		}

		// create column objects from tabdataStatement Columns or Fields
		// and add to table object
		List<ColumnInfo> columnDefs = new ArrayList<ColumnInfo>();

		//Get all pointers, column and container statements
		final List<PointerStatement> pointers = statement.getPointers();
		List<ObjectStatement> colStatements;
		List<ObjectStatement> conStatements;//Container statements

		if (table.getType().endsWith("SPREADSHEET")) {
			colStatements = statement.getObjects("FIELD");
		} else {
			colStatements = statement.getObjects("COLUMN");
		}
		// if no column statements, then the label must have structures that
		//may be in different folders. If not then, there is something wrong with
		//the label or the format is not a supported one
		if (colStatements.size() == 0 && pointers.size() > 0) {
			addStructures(statement);

			if (table.getType().endsWith("SPREADSHEET")) {
				colStatements = statement.getObjects("FIELD");
			} else {
				colStatements = statement.getObjects("COLUMN");
			}
		} else if (colStatements.size() == 0 && pointers.size() == 0) {
			throw new Exception("No columns or structures found in the label.");
		}

		//Check to see if it this label has containers, which will have columns and possibly more containers
		conStatements = statement.getObjects("CONTAINER");

		//This label has containers so need to search for Structure pointers and get the columns
		if(conStatements.size() != 0) {

			//Search through all the containers
			for (ObjectStatement containerStatement : conStatements) {

				//Add structure statements to the container statement
				//If all the labels and data are in the same directory, there is no need to add
				//structures. Otherwise, the data will be duplicated
				if(containerStatement.getObjects("COLUMN").size() == 0)
					addStructures(containerStatement);

				colStatements.add(containerStatement);
			}
		}


		// using StringBuilder to preserve indexID recursively
		// the indexID will be sequential and unique
		StringBuilder indexID = new StringBuilder("1");

		//Create ColumnDefs that will be used to create columns.
		//At this point, all the columns that will be inserted into the temporary
		//table must be defined. Therefore, ITEMS, REPETITIONS, BIT_COLUMNS must be
		//expanded and each of the parents and children must be created as columns
		//ID and start byte must initially be set to null
		columnDefs = buildColumnDefinitions(colStatements, null, null, indexID);

		//Set the grand total number of column definitions. This includes all the
		//columns extracted and expanded from the label(s)
		table.setLabelTotalColumnCount(columnDefs.size());

		// Keep track of the column names to check for duplicates
		StringBuilder columnNameList = new StringBuilder("; ;");
		int duplicateID = 1;

		// loop through columnsDefs and create column objects
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
			SliceColumn c = new SliceColumn(columnInfo, columnDefs
					.indexOf(columnInfo), true, columnDefs.indexOf(columnInfo));
			// add to column collections
			table.getColumns().add(c);
			table.getSelectedColumns().add(c);
			i++;

			//If there are too many columns, cap at NUMBER_OF_COLUMNS_ALLOWED since mysql is only able
			//to handle about 574 columns. Do not process/look at any column > 500
			//TODO: Find a way to display more than 500 columns. When so, remove this if statement!
			if(i==NUMBER_OF_COLUMNS_ALLOWED)
				break;
		}
		table.setSelectedColumnCount(i);

		//Set the actual total number of columns that the table has. This is for the
		//case that the label contains too many columns to be processed/displayed
		table.setTableTotalColumnCount(i);

		// need to set format to binary or ascii to control how to go
		// forward

		table.setFormat(statement.getAttribute("INTERCHANGE_FORMAT").getValue()
				.toString());
		// record the table value for ROW_BYTES
		table.setRowBytes(Integer.valueOf(statement.getAttribute("ROW_BYTES")
				.getValue().toString()));

		int numRows = Integer.valueOf(
				statement.getAttribute("ROWS").getValue().toString())
				.intValue();
		table.setTotalRows(numRows);

		// LAB 9/23/09 errors out here for spreadsheets
		if (table.getFormat().equalsIgnoreCase("ASCII")) {
			table.setRows(getDataPreview(this.labelContainer, table));
		}

		// use sample values in rows to set column formats on dates
		for (SliceColumn column : table.getColumns()) {

			// fix the data type for some labels that omit underscore symbol
			// if a space was used rather than underscore, replace the blank
			// space with underscore
			String dataType = column.getDataType();
			if(dataType.contains(" ")) {
				dataType = dataType.replace(" ", "_");
				column.setDataType(dataType);
			}

			if (column.getDataType().equalsIgnoreCase("ASCII_REAL")) {
				String rowValue = this.getSampleValue(column, table);
				// if value of first item has e or E in it
				if (rowValue.contains("e") || rowValue.contains("E"))
					column.setIsSciNotation(true);
			}

			if (column.getDataType().equalsIgnoreCase("DATE")
					|| column.getDataType().equalsIgnoreCase("TIME")) {
				String rowValue = this.getSampleValue(column, table);
				// set datestring format
				String format = null;
				format = DateUtils.getPattern(rowValue);
				if (format == null) {
					column.setDataType("CHARACTER");
				} else {
					column.setDateFormat(format);
				}
			}
		}

		AttributeStatement prodIDStmnt = this.label.getAttribute("PRODUCT_ID");
		if (prodIDStmnt != null) {
			this.slice.setProductId(prodIDStmnt.getValue().toString());
		}

		AttributeStatement datasetIDStmnt = this.label
				.getAttribute("DATA_SET_ID");
		if (datasetIDStmnt != null) {
			this.slice.setDataSetId(this.label.getAttribute("DATA_SET_ID")
					.getValue().toString());
		}

		URL url = this.labelContainer.getFirstURI(pointer).toURL();
		if (url != null) {
			File file = new File(url.getFile());
			table.setDataFile(file);
			table.setTabFileUrl(TabularDataUtils.getURLofSameLevelFile(
					this.labelContainer.getLabelUrl(), file).toString());
		}

		return table;
	}


	/**
	 * Builds column definitions of type ColumnInfo
	 *
	 * @param colStatements - The object statements from which to gather information about
	 *                           the possible column. Not all object statements are made into
	 *                           columns. For example, CONTAINER objects are not
	 *                           
	 * @param startingByte - The reference byte from which a container must start with. Initially is null.
	 * 						 However, if the label contains containers, the start byte for all the columns
	 * 						 within a container must start from where the parent label left off, not 1
	 * 
	 * @param repetitionID - The ID number that identifies an individual repetition set. Initially is set
	 * 						 to null. If containers found, the repetition ID is used
	 * 
	 * @return A list of column Info objects
	 */
	private List<ColumnInfo> buildColumnDefinitions(
			List<ObjectStatement> colStatements, Integer startingByte,
			Integer repetitionID, StringBuilder indexID) {

		List<ColumnInfo> columnDefs = new ArrayList<ColumnInfo>();
		Integer startByte = startingByte;
		Integer id = repetitionID;	        

		//This statement will probably never be executed since the for loop is the one that terminates
		//the recursive (Once it has finished iterating through all the columns). Left here just in case
		if(colStatements.size() == 0)
			return columnDefs;

		//Iterate through all the column statements and store their column info in a list
		for (ObjectStatement column : colStatements) {

			//Check to see if this column is composed of bit columns. Cannot break bit columns
			//down any further. So cannot have items or repetitions
			if (column.getObjects("BIT_COLUMN").size() > 0) {

				ColumnInfo col = new ColumnInfo(column);
				int rootStartByte = col.getStartByte();
				int rootBytes = col.getBytes();

				List<ObjectStatement> bitColumnStatements = column.getObjects("BIT_COLUMN");

				for (ObjectStatement bitColumn: bitColumnStatements)
				{
					ColumnInfo bitColumnInfo = new ColumnInfo(bitColumn);
					bitColumnInfo.setStartByte(rootStartByte);
					bitColumnInfo.setBytes(rootBytes);
					bitColumnInfo.setStopBit((bitColumnInfo.getStartBit()-1)+bitColumnInfo.getBits());

					// use of the unused fields to mark this column as a bit column
					// field_number is only used for PDS4
					bitColumnInfo.setFieldNumber(1);

					// set index
					int index = Integer.parseInt(indexID.toString());
					bitColumnInfo.setIndex(index);
					index++;
					indexID.setLength(0);
					indexID.append(index);

					columnDefs.add(bitColumnInfo);
				}

			}
			///////Check to see if this is a container
			//If it is, then it already has all the column definitions, so need to
			//iterate through them and create column info objects and add them to
			//the list of column definitions
			//If this column is an object, then col has the information we need
			//to extract such as repetitions, bytes, etc... However, we are not going
			//insert the container object itself into the column definitions list
			else if(column.getIdentifier().getId().equals("CONTAINER")) {

				//Get all the objects, including any more containers and columns
				List<ObjectStatement> conColStatements = column.getObjects();

				//Get attributes needed to set start byte and repetitions
				int repetitions = Integer.parseInt(column.getAttribute("REPETITIONS").getValue().toString());
				int containerStartByte = Integer.parseInt(column.getAttribute("START_BYTE").getValue().toString());
				int containerBytes = Integer.parseInt(column.getAttribute("BYTES").getValue().toString());

				//Expand the repetitions
				for(int i=0; i<repetitions; i++) {

					List<ColumnInfo> columnDefsFromContainer = new ArrayList<ColumnInfo>();

					//Keep calling recursively if more containers are found since each container can
					//have more containers or more columns
					columnDefsFromContainer = buildColumnDefinitions(conColStatements, containerStartByte, i+1, indexID);

					//Increment to next group of bytes
					containerStartByte = containerStartByte + containerBytes;

					//Add all the definitions to the main column definitions list
					columnDefs.addAll(columnDefsFromContainer);
				}

				//At this point, the columns can either have items, be of complex type, or simply be a column
			}else {

				ColumnInfo col = new ColumnInfo(column);
				int bytes = 0;

				if(startByte != null) {
					col.setStartByte(startByte);
					bytes = col.getBytes();
				}

				//IEEE Complex numbers consist of two IEEE REAL format numbers of the same precision,
				//contiguous in memory. The first number represents the real part and the second the
				//imaginary part of the complex value
				if(TabularDataUtils.isBinaryComplexType(col.getType())) {

					//If it is an 8 byte IEEE_CONPLEX number, create a column for the real part
					//and column for the imaginary type. Each of the components is 4 bytes. So need
					//to reset start byte for the second column and number of bytes from 8 to 4 for
					//both
					if(col.getBytes() == 8) {

						//Create column in which to store the real part
						col = new ColumnInfo(column);
						StringBuilder rpNameToAppend = new StringBuilder(" (Real Part)");
						col.appendName(rpNameToAppend.toString());
						col.setBytes(4);

						// set index
						int index = Integer.parseInt(indexID.toString());
						col.setIndex(index);
						index++;
						columnDefs.add(col);

						//Create column in which to store the imaginary part
						col = new ColumnInfo(column);
						StringBuilder imNameToAppend = new StringBuilder(" (Imaginary Part)");
						col.appendName(imNameToAppend.toString());
						col.setStartByte(col.getStartByte()+4);
						col.setBytes(4);

						// set index
						col.setIndex(index);
						index++;
						indexID.setLength(0);
						indexID.append(index);
						columnDefs.add(col);

					} else if(col.getBytes() == 16) {

						//Create column in which to store the real part
						col = new ColumnInfo(column);
						StringBuilder rpNameToAppend = new StringBuilder(" (Real Part)");
						col.appendName(rpNameToAppend.toString());
						col.setBytes(8);

						// set index
						int index = Integer.parseInt(indexID.toString());
						col.setIndex(index);
						index++;
						columnDefs.add(col);

						//Create column in which to store the imaginary part
						col = new ColumnInfo(column);
						StringBuilder imNameToAppend = new StringBuilder(" (Imaginary Part)");
						col.appendName(imNameToAppend.toString());
						col.setStartByte(col.getStartByte()+8);
						col.setBytes(8);

						// set index
						col.setIndex(index);
						index++;
						indexID.setLength(0);
						indexID.append(index);
						columnDefs.add(col);

						//Just add the column if any other byte size
					}else {

						// set index
						int index = Integer.parseInt(indexID.toString());
						col.setIndex(index);
						index++;
						indexID.setLength(0);
						indexID.append(index);

						columnDefs.add(col);
					}

				}
				//Check to see if column is a column with items. Column with Items could either be defined by ITEM_BYTES or BYTES
				//and the number of ITEMS
				else if ( ((col.getNumItems() != null) && (col.getItemBytes() != null)) ||
						((col.getNumItems() != null) && (col.getBytes() != null)) ) {

					//Save initial start byte and itemBytes in order to calculate the
					//next start bytes for the items
					Integer itemsStartByte = col.getStartByte();
					Integer itemBytes;

					//ITEMS which is optional could be used rather than ITEM_BYTES
					if(col.getItemBytes() == null) {
						itemBytes = col.getBytes();
					} else {
						itemBytes = col.getItemBytes();
					}

					//Generate items
					for(int i=0; i<col.getNumItems(); i++) {

						StringBuilder nameToAppend = null;

						if(repetitionID == null) {
							//Append item + an item number for each item in order to avoid duplicate column names
							nameToAppend = new StringBuilder(" - Item " + Integer.toString(i+1));
						}else {
							//Create new name for each repeated item
							nameToAppend = new StringBuilder(" - Item " + Integer.toString(i+1) + " - " + id);
						}

						//Create a new item column
						col = new ColumnInfo(column);
						col.appendName(nameToAppend.toString());
						col.setStartByte(itemsStartByte);

						// set index
						int index = Integer.parseInt(indexID.toString());
						col.setIndex(index);
						index++;
						indexID.setLength(0);
						indexID.append(index);

						//Add column
						columnDefs.add(col);

						//Increment to the next start byte for the next item
						itemsStartByte = itemsStartByte + itemBytes;
					}

				}
				//It is a regular column
				else {
					if(repetitionID == null) {
						// set index
						int index = Integer.parseInt(indexID.toString());
						col.setIndex(index);
						index++;
						indexID.setLength(0);
						indexID.append(index);

						columnDefs.add(col);//Column with no items
					}else {
						StringBuilder nameToAppend = new StringBuilder(" - " + Integer.toString(id));
						col.appendName(nameToAppend.toString());
						col.setStartByte(startByte);

						// set index
						int index = Integer.parseInt(indexID.toString());
						col.setIndex(index);
						index++;
						indexID.setLength(0);
						indexID.append(index);

						columnDefs.add(col);//Column with no items or bit column parent
						startByte = startByte + bytes;
					}

				}
			}
		}

		return columnDefs;
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

	/*
	 * inserts rows into columns table based on values from the
	 * tabularDataContainer, as well as the current sessionid, and labelURL
	 * 
	 * @param tabularDataContainer the cantainer from which to create rows for
	 * column table
	 * 
	 * @param labelURL the string representation of the label's URL
	 */
	@SuppressWarnings("nls")
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

	/** readData(TabularDataContainer)
	 * 
	 * Creates MySQL table, read data file, and populate table. If the table already exists,
	 * the table is not recreated
	 * @param tabularDataContainer
	 * @throws CancelledException
	 */
	@SuppressWarnings("nls")
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

	/** readPartialData(TabularDataContainer)
	 * 
	 * Creates MySQL table if it does not exists, read data file, and partially populates the table. 
	 * A status error is returned according if the table already exists and the state of the the 
	 * table (partially or completely filled)
	 * @param tabularDataContainer
	 * @return 	0 - there was an error
	 * 			1 - table does not exists, so table created and partially filled
	 * 			2 - table exists and has already been completely populated
	 * 			3 - table exists and has not been completely populated
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

	/*
	 * retrieve values to determine format of date strings
	 */
	protected static List<Row> getDataPreview(
			TabularLabelContainer labelContainerObj,
			TabularDataContainer tabularDataContainer) {
		// this errors out if label does not point to a tabular data
		// file (LAB 9/23/09 this may no longer be the case)

		int totalRowsToSample = TOTAL_ROWS_TO_SAMPLE;

		// if the table's total number of rows is less than the minimum (TOTAL_ROWS_TO_SAMPLE), 
		// then just sample up to the total number of rows in the table
		if (tabularDataContainer.getTotalRows() < TOTAL_ROWS_TO_SAMPLE)
			totalRowsToSample = tabularDataContainer.getTotalRows();

		TabularData tabularData = labelContainerObj.getTabularData(
				tabularDataContainer.getType(), totalRowsToSample);
		// TODO LAB 09/15/09 need some kind of test of validity of tabularData
		// before moving on

		List<Row> rowsCopy = new ArrayList<Row>();

		// pull first 10 rows of tabular data for preview
		for (int i = 0; i < totalRowsToSample; i++) {
			List<Element> elementsCopy = new ArrayList<Element>();
			Row row = tabularData.getRows().get(i);
			for (Element element : row.getElements()) {
				// NOTE: (JAG) changed to clone rather than creating new element
				// here
				elementsCopy.add(element.clone());
			}
			rowsCopy.add(new Row(elementsCopy));
		}
		return rowsCopy;

	}

	/*
	 * creates a table in the database to hold tabular data
	 * 
	 * @param tabularDataContainer the container to be used as a basis for a
	 * table
	 */
	// LAB 9/23/09 something similar exist in TabularDataUtils - consolidate or
	// remove one
	@SuppressWarnings("nls")
	@Override
	protected void createDataTable(TabularDataContainer tabularDataContainer)
			throws Exception {

		String columnQuerySQL = "SELECT column_name, data_type, bytes, items, item_bytes, start_bit, stop_bit, bits, format, field_number FROM columns WHERE tabular_container_id = '"
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

			// create stringbuilders to hold sql
			StringBuilder createTableString = new StringBuilder();
			// build table name from session and tabularDataContainer ids
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

				createTableString.append("`" + rs.getString("column_name") + "` ");

				//Bit columns are displayed as TINYTEXT (max length of 255 characters) in the format "int (binary value)". 
				//Example format: 12 (1100) since we don't exactly know what the user wants to see
				// bit columns are found by checking the field_number field since PDS3 labels does not use it
				if(elementType(rs).equals("BITCOLUMN")) {
					createTableString.append("TINYTEXT");
				} else {

					// figure out what data type to append to string
					// date/time
					if (TabularDataUtils.isDateTimeType(rs.getString("data_type"))) {
						createTableString.append("BIGINT");
					}
					// boolean
					else if (TabularDataUtils.isBooleanType(rs
							.getString("data_type"))) {
						createTableString.append("BOOLEAN");
					}
					// ascii string
					else if (TabularDataUtils.isASCIIStringType(rs
							.getString("data_type"))) {
						//Total number of bytes = Up to 3 characters (each character is 1 byte) in decimal (Eg. ASCII decimal 122 = z) + commas 
						//                        + brackets (2) + number of bytes
						//This is used rather than TINYTEXT since in the worst case, the max number of bytes used is
						//the total number of bytes specified above
						createTableString.append("VARCHAR(" + (rs.getInt("bytes")*3+(rs.getInt("bytes")-1)+2+rs.getInt("bytes")) + ")");
					}
					// ascii and binary integer
					else if (TabularDataUtils.isIntegerType(rs
							.getString("data_type"))) {
						// mysql int can go up to 4,294,967,295
						// if type is binary - pds only accepts up to 4 bytes
						// 4 byte unsigned >= 4,294,967,295
						// use bytes to determine if ascii number may be too large
						// for integer type

						//COLUMN DATA TYPE
						if(elementType(rs).equals("COLUMN")) {
							//TODO: Integers, signed and unsigned, in PDS3 can be at most 4 bytes.
							//Therefore, 10 bytes will never be reached. Need to look for
							//a label that meets this condition
							if (Integer.valueOf(rs.getString("bytes")) >= 10) {
								createTableString.append("BIGINT");
							} else {

								//Unsigned integers are handled as long type in order to be able to reach
								//the upper limit value of 4,294,967,295 due to the sign bit.
								//Therefore, these should be stores as BIGINT's
								if(TabularDataUtils.isBinarySignedIntegerType(rs
										.getString("data_type")))
									createTableString.append("INTEGER");
								else
									createTableString.append("BIGINT");
							}
							//COLUMN WITH ITEMS
						} else if(elementType(rs).equals("COLUMNWITHITEMS")) {

							if (Integer.valueOf(rs.getString("item_bytes")) >= 10) {
								createTableString.append("BIGINT");
							} else {

								//Unsigned integers are handled as long type in order to be able to reach
								//the upper limit value of 4,294,967,295 due to the sign bit.
								//Therefore, these should be stores as BIGINT's
								if(TabularDataUtils.isBinarySignedIntegerType(rs
										.getString("data_type")))
									createTableString.append("INTEGER");
								else
									createTableString.append("BIGINT");
							}
						}
						// FORMAT = "I5"

					}// decimal ascii
					else if (TabularDataUtils.isASCIIRealType(rs
							.getString("data_type"))) {
						String scale;
						if (rs.getString("format") != null
								&& rs.getString("format").length() != 0) {
							if (rs.getString("format").contains("E")) {
								// FORMAT = "E11.5"
								createTableString.append("DECIMAL(65, 30)");

							} else {
								// get scale from format column
								// FORMAT = "F6.3"
								scale = rs.getString("format").substring(
										rs.getString("format").indexOf(".") + 1);
								createTableString.append("DECIMAL("
										+ rs.getInt("bytes") + "," + scale + ")");
							}
						} else {

							//The following is for the cases in which no MISSING_CONSTANT or FORMAT
							//fields were provided and the rows in the ASCII_REAL column contains
							//values other than real values (e.g., -9999)
							// TODO: non-real values are displayed with a decimal 
							int scaleInt = 0;

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
							}

							//if value is in scientific e notation
							if (scaleInt == -1) {
								// make decimal the largest it can be
								createTableString.append("DECIMAL(65, 30)");
							} else {
								scale = String.valueOf(scaleInt);

								createTableString.append("DECIMAL("
										+ rs.getInt("bytes") + "," + scale + ")");
							}
						}
					}
					// decimal/floatingpoint/real binary numbers
					else if (TabularDataUtils.isBinaryFloatingPointType(rs
							.getString("data_type"))) {

						//Allocate correct size according to the size of bytes used
						if(rs.getString("bytes").equals("4"))
							createTableString.append("FLOAT");
						else if(rs.getString("bytes").equals("8"))
							createTableString.append("DOUBLE");
						else
							createTableString.append("DOUBLE");
					}
					// ascii complex numbers - sci notation
					else if (TabularDataUtils.isASCIIComplexType(rs
							.getString("data_type"))) {
						// FORMAT =
						// Ew.d[Ee] Floating point value, displayed in
						// exponential
						// format
						// /TODO: determine correct complex number formatting
						createTableString.append("VARCHAR(" + rs.getInt("bytes")
								+ ")");

					}
					//Binary complex numbers
					else if (TabularDataUtils.isBinaryComplexType(rs
							.getString("data_type"))) {

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

					}
					// other (N/A)
					else if (TabularDataUtils
							.isOtherType(rs.getString("data_type"))) {
						createTableString.append("VARCHAR(" + rs.getInt("bytes")
								+ ")");
					} else {
						// catch any that may have been missed
						//createTableString.append("VARCHAR(" + rs.getInt("bytes")
						//		+ ")");
						createTableString.append("TINYTEXT");
					}
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
	 * 
	 * @param tabularDataContainer
	 * @throws Exception
	 */
	private void readFile(TabularDataContainer tabularDataContainer)
			throws Exception {
		URL tabURL = null;

		List<PointerStatement> pointers = this.label.getPointers();

		//If no top level pointers, look for data object pointers in data objects
		if(pointers.isEmpty()) {
			List<ObjectStatement> objects = this.label.getObjects();
			for (ObjectStatement os : objects) {
				pointers.addAll(os.getPointers());
			}
		}

		for (PointerStatement pointer : pointers) {
			updateStatus("Retrieving tabular data file",false); //$NON-NLS-1$
			if (pointer.getIdentifier().toString().equals(
					tabularDataContainer.getType())) {
				// assumes one and only one FileRef for
				// tabularDataContainer.type
				// this gives NULL - there are no file refs on this pointer
				Numeric startPosition = pointer.getFileRefs().get(0)
						.getStartPosition();
				long dataStartByte = Label.getSkipBytes(this.label,
						startPosition);

				try {
					tabURL = this.labelContainer.getFirstURI(pointer).toURL();
					// TODO need to stop progression if this errors out
					fillDataTable(tabularDataContainer, tabURL, dataStartByte);

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
		}
	}

	/**
	 * 
	 * @param tabularDataContainer
	 * @throws Exception
	 */
	private void readPartialFile(TabularDataContainer tabularDataContainer)
			throws Exception {
		URL tabURL = null;

		List<PointerStatement> pointers = this.label.getPointers();

		//If no top level pointers, look for data object pointers in data objects
		if(pointers.isEmpty()) {
			List<ObjectStatement> objects = this.label.getObjects();
			for (ObjectStatement os : objects) {
				pointers.addAll(os.getPointers());
			}
		}

		for (PointerStatement pointer : pointers) {
			if (pointer.getIdentifier().toString().equals(
					tabularDataContainer.getType())) {
				// assumes one and only one FileRef for
				// tabularDataContainer.type
				// this gives NULL - there are no file refs on this pointer
				Numeric startPosition = pointer.getFileRefs().get(0)
						.getStartPosition();
				long dataStartByte = Label.getSkipBytes(this.label,
						startPosition);

				try {
					tabURL = this.labelContainer.getFirstURI(pointer).toURL();
					// TODO need to stop progression if this errors out
					fillPartialDataTable(tabularDataContainer, tabURL, dataStartByte);

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
		}
	}


	/**
	 * 	Fills the temporary data table. It reads the binary or ASCII data and it begins filling
	 *  the table row by row and inserting each row into the table.
	 * 
	 * @param tabularDataContainer
	 * @param tabFileUrl
	 * @param dataStartByte
	 * @throws Exception
	 */
	@SuppressWarnings("nls")
	@Override
	protected void fillDataTable(TabularDataContainer tabularDataContainer,
			URL tabFileUrl, long dataStartByte) throws Exception {

		String columnQuerySQL = "SELECT column_name, data_type, start_byte, bytes, items, item_bytes, bits, item_bits, missing_constant, start_bit, stop_bit, field_number FROM columns WHERE tabular_container_id = '"
				+ tabularDataContainer.getId() + "';";

		Connection connection = DBManager.getConnection();
		Statement queryStmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

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

				insertString.append("`" + rs.getString("column_name") + "` " + " = ?");

				if (!rs.isLast()) {
					insertString.append(",");
				}
			}

			//Make prepared statement connection
			pstmt = connection.prepareStatement(insertString.toString());

			//INTERCHANGE_FORMAT = BINARY
			if (tabularDataContainer.getFormat().equalsIgnoreCase("BINARY")) {

				// create input stream
				InputStream in = new BufferedInputStream(tabFileUrl
						.openStream());

				//Check to see if the data has been zipped. If it has, open ZipInputStream in order
				//to be able to read the zipped file.
				if(tabFileUrl.toString().endsWith(".zip") || tabFileUrl.toString().endsWith(".ZIP")) {

					ZipInputStream inZip = new ZipInputStream(in);
					inZip.getNextEntry();
					in = (InputStream)inZip;
					inZip.close();

				} else {
					in = new BufferedInputStream(tabFileUrl.openStream());
				}

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

					int i = 1;
					// reset resultset to beginning
					rs.beforeFirst();

					// entire row of data is already in the byte array
					// for each column entry in rs
					while (rs.next()) {

						int startIndex = rs.getInt("start_byte") - 1;
						int columnLength = 0;

						//Depending on the type, get the item_bytes or bytes
						if(elementType(rs).equals("COLUMNWITHITEMS"))
							columnLength = rs.getInt("item_bytes");
						else
							columnLength = rs.getInt("bytes");

						// create a byte array to hold column data
						byte[] bytes = new byte[columnLength];
						System.arraycopy(data, startIndex, bytes, 0,
								columnLength);

						// if the column is a BITCOLUMN, process differently than the other types
						// extract the bits and generate the string format
						if (elementType(rs).equals("BITCOLUMN")) {

							ByteArrayBitIterable singleBits;
							StringBuilder sb = new StringBuilder("");
							StringBuilder finalString = new StringBuilder("");
							int decimal = 0;

							//Need to do byte swapping for LSB_BIT_STRING to convert to correct logical order
							//And create a byte array from bytes in order to be able to iterate through the single bits
							//and get a subset of the byte
							if (TabularDataUtils.isLSBBitStringType(rs
									.getString("data_type"))) {

								byte[] swappedBytes = new byte[Integer.parseInt(rs.getString("bytes"))];
								for(int j=(bytes.length-1); j>=0; j--)
									swappedBytes[(bytes.length-1)-j] = bytes[j];

								singleBits = new ByteArrayBitIterable(swappedBytes);
							} else {//MSB_BIT_STRING
								singleBits = new ByteArrayBitIterable(bytes);
							}

							int startBit = Integer.parseInt(rs.getString("start_bit"));
							int stopBit = Integer.parseInt(rs.getString("stop_bit"));

							//Select the range of bits - offset to the correct start and
							//end bit position
							singleBits.setStartBit(startBit-1);
							singleBits.setStopBit(stopBit-1);

							//Create a string with the range of bits
							for (boolean bit : singleBits){
								BinaryConversionUtils.bitToString(sb, bit);
							}

							//Convert the bits to a decimal/ Base is 2
							decimal = Integer.parseInt(sb.toString(), 2);
							finalString.append(decimal);
							finalString.append(" (" + sb + ")");

							pstmt.setString(i, finalString.toString());

						} else {

							//Boolean
							if(TabularDataUtils.isBooleanType(rs
									.getString("data_type"))) {

								int value = BinaryConversionUtils.convertMSBSigned(
										bytes, bytes.length);

								//All 0 = False. Anything else = True
								if(value == 0)
									pstmt.setBoolean(i, false);
								else
									pstmt.setBoolean(i, true);
							}
							// msb integers
							else if (TabularDataUtils.isMSBBinarySignedInteger(rs
									.getString("data_type"))) {
								int value = BinaryConversionUtils.convertMSBSigned(
										bytes, bytes.length);
								pstmt.setInt(i, value);
							}
							// LSB Signed
							else if (TabularDataUtils.isLSBSignedInteger(rs
									.getString("data_type"))) {
								int value = BinaryConversionUtils.convertLSBSigned(
										bytes, bytes.length);
								pstmt.setInt(i, value);
							}
							// msb unsigned
							else if (TabularDataUtils.isMSBUnsignedInteger(rs
									.getString("data_type"))) {
								long value = BinaryConversionUtils
										.convertMSBUnsigned(bytes, bytes.length);
								pstmt.setLong(i, value);
							}
							// lsb unsigned
							else if (TabularDataUtils.isLSBUnsignedInteger(rs
									.getString("data_type"))) {
								long value = BinaryConversionUtils
										.convertLSBUnsigned(bytes, bytes.length);
								pstmt.setLong(i, value);

							}
							//Convert to right type (float or double) before calling BitsToDouble or Float
							//The same conversions work for Complex times since they are composed of
							//IEEE reals or VAX
							//Special values such as NAN, +-INF are not supported in mysql. Therefore, if one
							//of the data values is
							//TODO: Possibly add support for 10-bytes
							//		and check for Double and Float MAX_VALUE constants. These seem
							//		to work sometimes but sometimes not and an Exception error is
							// 		generated. For now, the highest value - 1
							//		is hard-coded in order to represent +-infinity. So MAX_VALUE - 1 is
							//		used in order to avoid the exception error
							else if (TabularDataUtils.isBinaryFloatingPointType(rs.getString("data_type")) ||
									TabularDataUtils.isBinaryComplexType(rs.getString("data_type"))) {

								//Check to see if it is VAX type either Floating or Complex type
								if(TabularDataUtils.isVAXFloatingPointType(rs.getString("data_type")) ||
										TabularDataUtils.isVAXComplexFDGType(rs.getString("data_type"))){

									//Check to see if it VAXG Floating or Complex type
									if(TabularDataUtils.isVAXGFloatingPointType(rs.getString("data_type")) ||
											TabularDataUtils.isVAXGComplexType(rs.getString("data_type"))) {

										long ieeeValue = BinaryConversionUtils.vaxGTypeToIEEEDouble(bytes);
										double convertedDoubleBits = Double.longBitsToDouble(ieeeValue);

										//NaN
										if(Double.isNaN(convertedDoubleBits))
											pstmt.setNull(i, java.sql.Types.DOUBLE);
										//+Inf
										else if(Double.isInfinite(convertedDoubleBits) && ( (ieeeValue >> 63) == 0))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//-Inf
										else if(Double.isInfinite(convertedDoubleBits))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//Double within range
										else
											pstmt.setDouble(i, convertedDoubleBits);

									} else {//Must be F, D types

										//F type
										if(rs.getString("bytes").equals("4")) {

											int ieeeSingle = BinaryConversionUtils.vaxFTypeToIEEESingle(bytes);
											float convertedFloatBits = Float.intBitsToFloat(ieeeSingle);

											//NaN
											if(Float.isNaN(convertedFloatBits))
												pstmt.setNull(i, java.sql.Types.FLOAT);

											//+Inf
											else if( Float.isInfinite(convertedFloatBits) && ( (ieeeSingle >> 31) == 0))
												pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
														.convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

											//-Inf
											else if( Float.isInfinite(convertedFloatBits))
												pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
														.convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

											else
												pstmt.setFloat(i, convertedFloatBits);
											//D type
										}else if(rs.getString("bytes").equals("8")) {

											long ieeeValue = BinaryConversionUtils.vaxDTypeToIEEEDouble(bytes);
											double convertedDoubleBits = Double.longBitsToDouble(ieeeValue);

											//NaN
											if(Double.isNaN(convertedDoubleBits))
												pstmt.setNull(i, java.sql.Types.DOUBLE);
											//+Inf
											else if(Double.isInfinite(convertedDoubleBits) && ( (ieeeValue >> 63) == 0))
												pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
														.convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
																(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
											//-Inf
											else if(Double.isInfinite(convertedDoubleBits))
												pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
														.convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
																(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
											//Double within range
											else
												pstmt.setDouble(i, convertedDoubleBits);

										}//VAX 10-byte H-Type currently not supported
										else {
											pstmt.setNull(i, java.sql.Types.DOUBLE);
										}
									}    
									//PC_REAL or PC_COMPLEX
								} else if(TabularDataUtils.isPCRealComplexType(rs.getString("data_type"))) {

									//PC_REAL 4 Bytes
									if(rs.getString("bytes").equals("4")) {

										int ieeeSingle = BinaryConversionUtils.pcReal4BTypeToIEEESingle(bytes);
										float convertedFloatBits = Float.intBitsToFloat(ieeeSingle);

										//NaN
										if(Float.isNaN(convertedFloatBits))
											pstmt.setNull(i, java.sql.Types.FLOAT);

										//+Inf
										else if( Float.isInfinite(convertedFloatBits) && ( (ieeeSingle >> 31) == 0))
											pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
													.convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

										//-Inf
										else if( Float.isInfinite(convertedFloatBits))
											pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
													.convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

										else
											pstmt.setFloat(i, convertedFloatBits);

										//PC_REAL 8 bytes
									}else if(rs.getString("bytes").equals("8")) {

										long ieeeValue = BinaryConversionUtils.pcReal8BTypeToIEEEDouble(bytes);
										double convertedDoubleBits = Double.longBitsToDouble(ieeeValue);

										//NaN
										if(Double.isNaN(convertedDoubleBits))
											pstmt.setNull(i, java.sql.Types.DOUBLE);
										//+Inf
										else if(Double.isInfinite(convertedDoubleBits) && ( (ieeeValue >> 63) == 0))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//-Inf
										else if(Double.isInfinite(convertedDoubleBits))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//Double within range
										else
											pstmt.setDouble(i, convertedDoubleBits);

									}//PC_REAL 10-bytes currently not supported
									else {
										pstmt.setNull(i, java.sql.Types.DOUBLE);
									}
								}//IEEE type
								else {

									//IEEE 4-byte (Single Precision)
									if(rs.getString("bytes").equals("4")) {
										int intValue = BinaryConversionUtils
												.convertByteArrayToInt(bytes);
										float convertedFloatBits = Float.intBitsToFloat(intValue);

										//NaN
										if(Float.isNaN(convertedFloatBits))
											pstmt.setNull(i, java.sql.Types.FLOAT);

										//+Inf
										//TODO: Test Float.MAX_VALUE. It seems to work at times but sometimes
										//a truncation error is thrown
										else if( Float.isInfinite(convertedFloatBits) && ( (intValue >> 31) == 0))
											pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
													.convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));
										//pstmt.setFloat(i,Float.MAX_VALUE);
										//-Inf
										else if( Float.isInfinite(convertedFloatBits))
											pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
													.convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

										else
											pstmt.setFloat(i, convertedFloatBits);
									}
									//IEEE 8-byte (Double Precision)
									else if(rs.getString("bytes").equals("8")) {

										long longValue = BinaryConversionUtils.convertByteArrayToLong(bytes);
										double convertedDoubleBits = Double.longBitsToDouble(longValue);

										//Check for NAN, +-Infinity. Since mysql does not handle these limits, the following values
										//will be used to denote these limits
										//NAN - null - The cell appears empty
										//-Infinity -
										//+Infinity

										//NaN
										if(Double.isNaN(convertedDoubleBits))
											pstmt.setNull(i, java.sql.Types.DOUBLE);
										//+Inf
										else if(Double.isInfinite(convertedDoubleBits) && ( (longValue >> 63) == 0))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//-Inf
										else if(Double.isInfinite(convertedDoubleBits))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//Double within range
										else
											pstmt.setDouble(i, convertedDoubleBits);  
									}
									//IEEE 10-byte (Temporary)
									else {
										pstmt.setNull(i, java.sql.Types.DOUBLE);
									}
								}
							}else if (TabularDataUtils.isASCIIStringType(rs
									.getString("data_type"))) {

								//Select the right encoding, ASCII (7-bit) or EBCDIC (8-bit)
								if (rs.getString("data_type").equalsIgnoreCase("CHARACTER"))
									pstmt.setString(i, BinaryConversionUtils.byteArrayToString(bytes,"US-ASCII"));
								else if(rs.getString("data_type").equalsIgnoreCase("EBCDIC_CHARACTER"))
									pstmt.setString(i, BinaryConversionUtils.byteArrayToString(bytes,"Cp1047"));

							} else if (rs.getString("data_type").equalsIgnoreCase("N/A")) {
								pstmt.setNull(i, java.sql.Types.VARCHAR);

								// A column that declares itself a type X_BIT_STRING, where X is
								//MSB or LSB
							} else if(TabularDataUtils.isBinaryBitStringType(rs
									.getString("data_type"))) { 

								ByteArrayBitIterable singleBits;
								StringBuilder sb = new StringBuilder("");
								StringBuilder finalString = new StringBuilder("");
								int decimal = 0;

								//Need to do byte swapping for LSB_BIT_STRING to convert to correct logical order
								//And create a byte array from bytes in order to be able to iterate through the single bits
								//and get a subset of the byte
								if (TabularDataUtils.isLSBBitStringType(rs
										.getString("data_type"))) {

									byte[] swappedBytes = new byte[Integer.parseInt(rs.getString("bytes"))];
									for(int j=(bytes.length-1); j>=0; j--)
										swappedBytes[(bytes.length-1)-j] = bytes[j];

									singleBits = new ByteArrayBitIterable(swappedBytes);
								} else {//MSB_BIT_STRING
									singleBits = new ByteArrayBitIterable(bytes);
								}

								//Create a string with the range of bits
								for (boolean bit : singleBits){
									BinaryConversionUtils.bitToString(sb, bit);
								}

								//Convert the bits to a decimal/ Base is 2
								decimal = Integer.parseInt(sb.toString(), 2);
								finalString.append(decimal);
								finalString.append(" (" + sb + ")");

								pstmt.setString(i, finalString.toString());
							}
							// else case to catch anything else for now, could cause data loss
							else {
								pstmt.setNull(i, java.sql.Types.INTEGER);
							}
						}	
						//Next Column
						i++;

					}// end while(rs.next())
					pstmt.executeUpdate();
					rowNumber++;

				}// end while(bytesRead...
			}// end if format = BINARY
			else if (tabularDataContainer.getFormat().equalsIgnoreCase("ASCII")) {

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

				while ((currLine = br.readLine()) != null
						&& (Long.valueOf(tabularDataContainer.getTotalRows()) == null || tabularDataContainer
						.getTotalRows() >= lineNumber)) {

					updateStatus(
							"Processing: "
									+ (int) Math.floor(((float) lineNumber / tabularDataContainer
											.getTotalRows()) * 100)
											+ "% completed", false);  

					int i = 1;//Column index

					// reset resultset to beginning
					rs.beforeFirst();
					String[] row = null;

					if (tabularDataContainer.getType().equalsIgnoreCase(
							"SPREADSHEET")) {
						row = currLine.split(tabularDataContainer
								.getDelimiter());
					}

					while (rs.next()) {

						try {
							// read data for each column
							String data;
							if (tabularDataContainer.getType()
									.equalsIgnoreCase("SPREADSHEET")
									&& row != null) {
								data = row[i - 1].replaceAll("\"", "");
							} else {
								int startIndex = rs.getInt("start_byte") - 1;
								int endIndex = rs.getInt("bytes") + startIndex;
								data = currLine.substring(startIndex, endIndex);
								data = data.replaceAll(",","");
							}

							// determine type to insert data as
							// date/time
							if (rs.getString("data_type").equalsIgnoreCase(
									"date")
									|| rs.getString("data_type")
									.equalsIgnoreCase("time")) {
								// if date = missing constants, insert null
								if (data.equalsIgnoreCase(rs
										.getString("missing_constant"))) {
									pstmt.setNull(i, java.sql.Types.INTEGER);

								} else {
									boolean validDate;
									long dateLong = 0;
									try {
										dateLong = DateUtils.toEpochDate(data);
										validDate = true;
									} catch (Exception e) {
										e.printStackTrace();
										validDate = false;
									}
									if (validDate) {
										pstmt.setLong(i, dateLong);
									} else {
										pstmt
										.setNull(i,
												java.sql.Types.INTEGER);
										// TODO add status message
										// "unrecognized date format, value set to null for line ###"
										// LAB 09/27/09 if we get here, date
										// format
										// of a sample value was tested in
										// buildTabularData and the column data
										// type
										// set to character if not a valid PDDS
										// date
									}
								}

								// boolean
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("boolean")) {
								pstmt.setBoolean(i, Boolean.valueOf(data));

								// strings
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("character")
									|| rs.getString("data_type")
									.equalsIgnoreCase(
											"ebcdic_character")) {
								// TODO check whether ebcdic_character should be
								// read as ascii or as binary
								pstmt.setString(i, data);

								// numbers
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("ascii_integer")) {
								String trimmedData = data.trim();
								if (trimmedData.length() == 0) {
									pstmt.setNull(i, java.sql.Types.INTEGER);

								} else {
									try {
										pstmt.setInt(i, Integer
												.valueOf(trimmedData));
									} catch (NumberFormatException e) {

										//Parse as long if the number is too big. Otherwise,
										//if an error is thrown, the ascii_integer must
										//have decimals so need to parse as a double first
										//and then typecast to integer type
										try{
											pstmt.setLong(i, Long
													.valueOf(trimmedData));
										} catch (NumberFormatException e2) {

											pstmt.setInt(i,(int)Double
													.parseDouble(trimmedData));
										}
									}
								}
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("ascii_real")) {
								// IF value = missing_constant enter null
								String missing_constant = rs
										.getString("missing_constant");
								if (missing_constant != null
										&& missing_constant.length() > 0
										&& data.equals(missing_constant)) {
									pstmt.setNull(i, java.sql.Types.DECIMAL);
								} else {
									pstmt.setBigDecimal(i, new BigDecimal(data
											.trim()));
								}
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("ascii_complex")) {
								// FORMAT =
								// Ew.d[Ee] Floating point value, displayed in
								// exponential
								// format
								// /TODO: determine correct complex number
								// formatting
								pstmt.setString(i, data);
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("N/A")) {
								pstmt.setString(i, data);

								//else case is for Non-ASCII. Obsolete data types supported by earlier versions of PDS
							} else {
								//Obsolete data types
								//BIT-STRING - Alias for MSB_BIT_STRING
								//COMPLEX - Alias for IEEE_COMPLEX
								//FLOAT - Alias for IEEE_REAL
								//INTEGER - Alias for MSB_INTEGER
								//REAL - Alias for IEEE_REAL
								//UNSIGNED_INTEGER - Alias for MSB_UNSIGNED_INTEGER
								if (rs.getString("data_type").equalsIgnoreCase("bit-string")) {
									pstmt.setString(i, data);

								} else if(rs.getString("data_type").equalsIgnoreCase("complex")) {
									pstmt.setString(i, data);

								} else if(rs.getString("data_type").equalsIgnoreCase("float")) {
									String trimmedData = data.trim();
									if (trimmedData.length() == 0) {
										pstmt.setNull(i, java.sql.Types.FLOAT);
									} else {
										try {
											pstmt.setFloat(i, Float
													.valueOf(trimmedData));
										} catch (NumberFormatException e) {
											pstmt.setLong(i, Long
													.valueOf(trimmedData));
										}
									}
								} else if(rs.getString("data_type").equalsIgnoreCase("integer")) {
									String trimmedData = data.trim();
									if (trimmedData.length() == 0) {
										//Inserts "null" to the query string
										pstmt.setNull(i, java.sql.Types.INTEGER);
									} else {
										//Set as an integer type
										try {
											pstmt.setInt(i, Integer
													.valueOf(trimmedData));
											//Set as a long type
										} catch (NumberFormatException e) {
											pstmt.setLong(i, Long
													.valueOf(trimmedData));
										}
									}
								} else if(rs.getString("data_type").equalsIgnoreCase("real")) {
									String missing_constant = rs.getString("missing_constant");
									if (missing_constant != null
											&& missing_constant.length() > 0
											&& data.equals(missing_constant)) {
										pstmt.setNull(i, java.sql.Types.DECIMAL);
									} else {
										pstmt.setBigDecimal(i, new BigDecimal(data
												.trim()));
									}
								} else if(rs.getString("data_type").equalsIgnoreCase("unsigned_integer")) {
									String trimmedData = data.trim();
									if (trimmedData.length() == 0) {
										//Inserts "null" to the query string
										pstmt.setNull(i, java.sql.Types.INTEGER);
									} else {
										//Set as an integer type
										try {
											pstmt.setInt(i, Integer
													.valueOf(trimmedData));
											//Set as a long type
										} catch (NumberFormatException e) {
											pstmt.setLong(i, Long
													.valueOf(trimmedData));
										}
									}
								} else {
									throw new RuntimeException("Obsolete Data Type used: " + rs.getString("data_type") + ". The current data types supported are: DATE, TIME, BOOLEAN, CHARACTER, ASCII_INTEGER, ASCII_REAL, ASCII_COMPLEX, and N/A");
								}
							}

							i++;
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
	 * 
	 * @param tabularDataContainer
	 * @param tabFileUrl
	 * @param dataStartByte
	 * @throws Exception
	 */
	@SuppressWarnings("nls")
	@Override
	protected void fillPartialDataTable(TabularDataContainer tabularDataContainer,
			URL tabFileUrl, long dataStartByte) throws Exception {

		String columnQuerySQL = "SELECT column_name, data_type, start_byte, bytes, items, item_bytes, bits, item_bits, missing_constant, start_bit, stop_bit, field_number FROM columns WHERE tabular_container_id = '"
				+ tabularDataContainer.getId() + "';";

		Connection connection = DBManager.getConnection();
		Statement queryStmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

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

				insertString.append("`" + rs.getString("column_name") + "` " + " = ?");

				if (!rs.isLast()) {
					insertString.append(",");
				}
			}

			//Make prepared statement connection
			pstmt = connection.prepareStatement(insertString.toString());

			//INTERCHANGE_FORMAT = BINARY
			if (tabularDataContainer.getFormat().equalsIgnoreCase("BINARY")) {

				// create input stream
				InputStream in = new BufferedInputStream(tabFileUrl
						.openStream());

				//Check to see if the data has been zipped. If it has, open ZipInputStream in order
				//to be able to read the zipped file.
				if(tabFileUrl.toString().endsWith(".zip") || tabFileUrl.toString().endsWith(".ZIP")) {

					ZipInputStream inZip = new ZipInputStream(in);
					inZip.getNextEntry();
					in = (InputStream)inZip;
					inZip.close();

				} else {
					in = new BufferedInputStream(tabFileUrl.openStream());
				}

				//Need to start at the beginning of the table pointer
				//TODO:Do a check to make sure the table is in the same file
				//with all other tables and not on its own file. Otherwise,
				//there is not need to skip
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

					int i = 1;
					// reset resultset to beginning
					rs.beforeFirst();

					// entire row of data is already in the byte array
					// for each column entry in rs
					while (rs.next()) {

						int startIndex = rs.getInt("start_byte") - 1;
						int columnLength = 0;

						//Depending on the type, get the item_bytes or bytes
						if(elementType(rs).equals("COLUMNWITHITEMS"))
							columnLength = rs.getInt("item_bytes");
						else
							columnLength = rs.getInt("bytes");

						// create a byte array to hold column data
						byte[] bytes = new byte[columnLength];
						System.arraycopy(data, startIndex, bytes, 0,
								columnLength);

						// if the column is a BITCOLUMN, process differently than the other types
						// extract the bits and generate the string format
						if (elementType(rs).equals("BITCOLUMN")) {

							ByteArrayBitIterable singleBits;
							StringBuilder sb = new StringBuilder("");
							StringBuilder finalString = new StringBuilder("");
							int decimal = 0;

							//Need to do byte swapping for LSB_BIT_STRING to convert to correct logical order
							//And create a byte array from bytes in order to be able to iterate through the single bits
							//and get a subset of the byte
							if (TabularDataUtils.isLSBBitStringType(rs
									.getString("data_type"))) {

								byte[] swappedBytes = new byte[Integer.parseInt(rs.getString("bytes"))];
								for(int j=(bytes.length-1); j>=0; j--)
									swappedBytes[(bytes.length-1)-j] = bytes[j];

								singleBits = new ByteArrayBitIterable(swappedBytes);
							} else {//MSB_BIT_STRING
								singleBits = new ByteArrayBitIterable(bytes);
							}

							int startBit = Integer.parseInt(rs.getString("start_bit"));
							int stopBit = Integer.parseInt(rs.getString("stop_bit"));

							//Select the range of bits and offset to correct start and stop bit
							singleBits.setStartBit(startBit-1);
							singleBits.setStopBit(stopBit-1);

							//Create a string with the range of bits
							for (boolean bit : singleBits){
								BinaryConversionUtils.bitToString(sb, bit);
							}

							//Convert the bits to a decimal/ Base is 2
							decimal = Integer.parseInt(sb.toString(), 2);
							finalString.append(decimal);
							finalString.append(" (" + sb + ")");

							pstmt.setString(i, finalString.toString());

						} else {

							//Boolean
							if(TabularDataUtils.isBooleanType(rs
									.getString("data_type"))) {

								int value = BinaryConversionUtils.convertMSBSigned(
										bytes, bytes.length);

								//All 0 = False. Anything else = True
								if(value == 0)
									pstmt.setBoolean(i, false);
								else
									pstmt.setBoolean(i, true);
							}
							// msb integers
							else if (TabularDataUtils.isMSBBinarySignedInteger(rs
									.getString("data_type"))) {
								int value = BinaryConversionUtils.convertMSBSigned(
										bytes, bytes.length);
								pstmt.setInt(i, value);
							}
							// LSB Signed
							else if (TabularDataUtils.isLSBSignedInteger(rs
									.getString("data_type"))) {
								int value = BinaryConversionUtils.convertLSBSigned(
										bytes, bytes.length);
								pstmt.setInt(i, value);
							}
							// msb unsigned
							else if (TabularDataUtils.isMSBUnsignedInteger(rs
									.getString("data_type"))) {
								long value = BinaryConversionUtils
										.convertMSBUnsigned(bytes, bytes.length);
								pstmt.setLong(i, value);
							}
							// lsb unsigned
							else if (TabularDataUtils.isLSBUnsignedInteger(rs
									.getString("data_type"))) {
								long value = BinaryConversionUtils
										.convertLSBUnsigned(bytes, bytes.length);
								pstmt.setLong(i, value);

							}
							//Convert to right type (float or double) before calling BitsToDouble or Float
							//The same conversions work for Complex times since they are composed of
							//IEEE reals or VAX
							//Special values such as NAN, +-INF are not supported in mysql. Therefore, if one
							//of the data values is
							//TODO: Possibly add support for 10-bytes
							//		and check for Double and Float MAX_VALUE constants. These seem
							//		to work sometimes but sometimes not and an Exception error is
							// 		generated. For now, the highest value - 1
							//		is hard-coded in order to represent +-infinity. So MAX_VALUE - 1 is
							//		used in order to avoid the exception error
							else if (TabularDataUtils.isBinaryFloatingPointType(rs.getString("data_type")) ||
									TabularDataUtils.isBinaryComplexType(rs.getString("data_type"))) {

								//Check to see if it is VAX type either Floating or Complex type
								if(TabularDataUtils.isVAXFloatingPointType(rs.getString("data_type")) ||
										TabularDataUtils.isVAXComplexFDGType(rs.getString("data_type"))){

									//Check to see if it VAXG Floating or Complex type
									if(TabularDataUtils.isVAXGFloatingPointType(rs.getString("data_type")) ||
											TabularDataUtils.isVAXGComplexType(rs.getString("data_type"))) {

										long ieeeValue = BinaryConversionUtils.vaxGTypeToIEEEDouble(bytes);
										double convertedDoubleBits = Double.longBitsToDouble(ieeeValue);

										//NaN
										if(Double.isNaN(convertedDoubleBits))
											pstmt.setNull(i, java.sql.Types.DOUBLE);
										//+Inf
										else if(Double.isInfinite(convertedDoubleBits) && ( (ieeeValue >> 63) == 0))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//-Inf
										else if(Double.isInfinite(convertedDoubleBits))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//Double within range
										else
											pstmt.setDouble(i, convertedDoubleBits);

									} else {//Must be F, D types

										//F type
										if(rs.getString("bytes").equals("4")) {

											int ieeeSingle = BinaryConversionUtils.vaxFTypeToIEEESingle(bytes);
											float convertedFloatBits = Float.intBitsToFloat(ieeeSingle);

											//NaN
											if(Float.isNaN(convertedFloatBits))
												pstmt.setNull(i, java.sql.Types.FLOAT);

											//+Inf
											else if( Float.isInfinite(convertedFloatBits) && ( (ieeeSingle >> 31) == 0))
												pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
														.convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

											//-Inf
											else if( Float.isInfinite(convertedFloatBits))
												pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
														.convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

											else
												pstmt.setFloat(i, convertedFloatBits);
											//D type
										}else if(rs.getString("bytes").equals("8")) {

											long ieeeValue = BinaryConversionUtils.vaxDTypeToIEEEDouble(bytes);
											double convertedDoubleBits = Double.longBitsToDouble(ieeeValue);

											//NaN
											if(Double.isNaN(convertedDoubleBits))
												pstmt.setNull(i, java.sql.Types.DOUBLE);
											//+Inf
											else if(Double.isInfinite(convertedDoubleBits) && ( (ieeeValue >> 63) == 0))
												pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
														.convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
																(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
											//-Inf
											else if(Double.isInfinite(convertedDoubleBits))
												pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
														.convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
																(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
											//Double within range
											else
												pstmt.setDouble(i, convertedDoubleBits);

										}//VAX 10-byte H-Type currently not supported
										else {
											pstmt.setNull(i, java.sql.Types.DOUBLE);
										}
									}    
									//PC_REAL or PC_COMPLEX
								} else if(TabularDataUtils.isPCRealComplexType(rs.getString("data_type"))) {

									//PC_REAL 4 Bytes
									if(rs.getString("bytes").equals("4")) {

										int ieeeSingle = BinaryConversionUtils.pcReal4BTypeToIEEESingle(bytes);
										float convertedFloatBits = Float.intBitsToFloat(ieeeSingle);

										//NaN
										if(Float.isNaN(convertedFloatBits))
											pstmt.setNull(i, java.sql.Types.FLOAT);

										//+Inf
										else if( Float.isInfinite(convertedFloatBits) && ( (ieeeSingle >> 31) == 0))
											pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
													.convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

										//-Inf
										else if( Float.isInfinite(convertedFloatBits))
											pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
													.convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

										else
											pstmt.setFloat(i, convertedFloatBits);

										//PC_REAL 8 bytes
									}else if(rs.getString("bytes").equals("8")) {

										long ieeeValue = BinaryConversionUtils.pcReal8BTypeToIEEEDouble(bytes);
										double convertedDoubleBits = Double.longBitsToDouble(ieeeValue);

										//NaN
										if(Double.isNaN(convertedDoubleBits))
											pstmt.setNull(i, java.sql.Types.DOUBLE);
										//+Inf
										else if(Double.isInfinite(convertedDoubleBits) && ( (ieeeValue >> 63) == 0))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//-Inf
										else if(Double.isInfinite(convertedDoubleBits))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//Double within range
										else
											pstmt.setDouble(i, convertedDoubleBits);

									}//PC_REAL 10-bytes currently not supported
									else {
										pstmt.setNull(i, java.sql.Types.DOUBLE);
									}
								}//IEEE type
								else {

									//IEEE 4-byte (Single Precision)
									if(rs.getString("bytes").equals("4")) {
										int intValue = BinaryConversionUtils
												.convertByteArrayToInt(bytes);
										float convertedFloatBits = Float.intBitsToFloat(intValue);

										//NaN
										if(Float.isNaN(convertedFloatBits))
											pstmt.setNull(i, java.sql.Types.FLOAT);

										//+Inf
										//TODO: Test Float.MAX_VALUE. It seems to work at times but sometimes
										//a truncation error is thrown
										else if( Float.isInfinite(convertedFloatBits) && ( (intValue >> 31) == 0))
											pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
													.convertByteArrayToInt(new byte[]{(byte)0x7F,(byte)0x7F,(byte)0xFF,(byte)0xFE})));
										//pstmt.setFloat(i,Float.MAX_VALUE);
										//-Inf
										else if( Float.isInfinite(convertedFloatBits))
											pstmt.setFloat(i, Float.intBitsToFloat(BinaryConversionUtils
													.convertByteArrayToInt(new byte[]{(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0xFE})));

										else
											pstmt.setFloat(i, convertedFloatBits);
									}
									//IEEE 8-byte (Double Precision)
									else if(rs.getString("bytes").equals("8")) {

										long longValue = BinaryConversionUtils.convertByteArrayToLong(bytes);
										double convertedDoubleBits = Double.longBitsToDouble(longValue);

										//Check for NAN, +-Infinity. Since mysql does not handle these limits, the following values
										//will be used to denote these limits
										//NAN - null - The cell appears empty
										//-Infinity -
										//+Infinity

										//NaN
										if(Double.isNaN(convertedDoubleBits))
											pstmt.setNull(i, java.sql.Types.DOUBLE);
										//+Inf
										else if(Double.isInfinite(convertedDoubleBits) && ( (longValue >> 63) == 0))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0x7F,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//-Inf
										else if(Double.isInfinite(convertedDoubleBits))
											pstmt.setDouble(i, Double.longBitsToDouble(BinaryConversionUtils
													.convertByteArrayToLong(new byte[]{(byte)0xFF,(byte)0xE0,(byte)0x00,(byte)0x00,
															(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00})));
										//Double within range
										else
											pstmt.setDouble(i, convertedDoubleBits);  
									}
									//IEEE 10-byte (Temporary)
									else {
										pstmt.setNull(i, java.sql.Types.DOUBLE);
									}
								}
							}else if (TabularDataUtils.isASCIIStringType(rs
									.getString("data_type"))) {

								//Select the right encoding, ASCII (7-bit) or EBCDIC (8-bit)
								if (rs.getString("data_type").equalsIgnoreCase("CHARACTER"))
									pstmt.setString(i, BinaryConversionUtils.byteArrayToString(bytes,"US-ASCII"));
								else if(rs.getString("data_type").equalsIgnoreCase("EBCDIC_CHARACTER"))
									pstmt.setString(i, BinaryConversionUtils.byteArrayToString(bytes,"Cp1047"));

							} else if (rs.getString("data_type").equalsIgnoreCase("N/A")) {
								pstmt.setNull(i, java.sql.Types.VARCHAR);

								// A column that declares itself a type X_BIT_STRING, where X is
								//MSB or LSB
							} else if(TabularDataUtils.isBinaryBitStringType(rs
									.getString("data_type"))) { 

								ByteArrayBitIterable singleBits;
								StringBuilder sb = new StringBuilder("");
								StringBuilder finalString = new StringBuilder("");
								int decimal = 0;

								//Need to do byte swapping for LSB_BIT_STRING to convert to correct logical order
								//And create a byte array from bytes in order to be able to iterate through the single bits
								//and get a subset of the byte
								if (TabularDataUtils.isLSBBitStringType(rs
										.getString("data_type"))) {

									byte[] swappedBytes = new byte[Integer.parseInt(rs.getString("bytes"))];
									for(int j=(bytes.length-1); j>=0; j--)
										swappedBytes[(bytes.length-1)-j] = bytes[j];

									singleBits = new ByteArrayBitIterable(swappedBytes);
								} else {//MSB_BIT_STRING
									singleBits = new ByteArrayBitIterable(bytes);
								}

								//Create a string with the range of bits
								for (boolean bit : singleBits){
									BinaryConversionUtils.bitToString(sb, bit);
								}

								//Convert the bits to a decimal/ Base is 2
								decimal = Integer.parseInt(sb.toString(), 2);
								finalString.append(decimal);
								finalString.append(" (" + sb + ")");

								pstmt.setString(i, finalString.toString());

								// else case to catch anything else for now, could cause data loss
							} else {
								pstmt.setNull(i, java.sql.Types.INTEGER);
							}
						}
						//Next Column
						i++;

					}// end while(rs.next())
					pstmt.executeUpdate();
					rowNumber++;

				}// end while(bytesRead...
			}// end if format = BINARY
			else if (tabularDataContainer.getFormat().equalsIgnoreCase("ASCII")) {

				pstmt = connection.prepareStatement(insertString.toString());

				final BufferedReader br = new BufferedReader(
						new InputStreamReader(tabFileUrl.openStream()));

				//skip to the start byte
				br.skip(dataStartByte);

				String currLine = null;
				int lineNumber = 1;

				while ((currLine = br.readLine()) != null
						&& (Long.valueOf(tabularDataContainer.getTotalRows()) == null 
						|| (this.ROWS_DISPLAY_LIMIT >= lineNumber && tabularDataContainer.getTotalRows() >= lineNumber))) {

					int i = 1;//Column index

					// reset resultset to beginning
					rs.beforeFirst();
					String[] row = null;

					if (tabularDataContainer.getType().equalsIgnoreCase(
							"SPREADSHEET")) {
						row = currLine.split(tabularDataContainer
								.getDelimiter());
					}

					while (rs.next()) {

						try {
							// read data for each column
							String data;
							if (tabularDataContainer.getType()
									.equalsIgnoreCase("SPREADSHEET")
									&& row != null) {
								data = row[i - 1].replaceAll("\"", "");
							} else {
								int startIndex = rs.getInt("start_byte") - 1;
								int endIndex = rs.getInt("bytes") + startIndex;
								data = currLine.substring(startIndex, endIndex);
								data = data.replaceAll(",","");
							}

							// determine type to insert data as
							// date/time
							if (rs.getString("data_type").equalsIgnoreCase(
									"date")
									|| rs.getString("data_type")
									.equalsIgnoreCase("time")) {
								// if date = missing constants, insert null
								if (data.equalsIgnoreCase(rs
										.getString("missing_constant"))) {
									pstmt.setNull(i, java.sql.Types.INTEGER);

								} else {
									boolean validDate;
									long dateLong = 0;
									try {
										dateLong = DateUtils.toEpochDate(data);
										validDate = true;
									} catch (Exception e) {
										e.printStackTrace();
										validDate = false;
									}
									if (validDate) {
										pstmt.setLong(i, dateLong);
									} else {
										pstmt
										.setNull(i,
												java.sql.Types.INTEGER);
										// TODO add status message
										// "unrecognized date format, value set to null for line ###"
										// LAB 09/27/09 if we get here, date
										// format
										// of a sample value was tested in
										// buildTabularData and the column data
										// type
										// set to character if not a valid PDDS
										// date
									}
								}

								// boolean
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("boolean")) {
								pstmt.setBoolean(i, Boolean.valueOf(data));

								// strings
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("character")
									|| rs.getString("data_type")
									.equalsIgnoreCase(
											"ebcdic_character")) {
								// TODO check whether ebcdic_character should be
								// read as ascii or as binary
								pstmt.setString(i, data);

								// numbers
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("ascii_integer")) {
								String trimmedData = data.trim();
								if (trimmedData.length() == 0) {
									pstmt.setNull(i, java.sql.Types.INTEGER);

								} else {
									try {
										pstmt.setInt(i, Integer
												.valueOf(trimmedData));
									} catch (NumberFormatException e) {

										//Parse as long if the number is too big. Otherwise,
										//if an error is thrown, the ascii_integer must
										//have decimals so need to parse as a double first
										//and then typecast to integer type
										try{
											pstmt.setLong(i, Long
													.valueOf(trimmedData));
										} catch (NumberFormatException e2) {

											pstmt.setInt(i,(int)Double
													.parseDouble(trimmedData));
										}
									}
								}
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("ascii_real")) {
								// IF value = missing_constant enter null
								String missing_constant = rs
										.getString("missing_constant");
								if (missing_constant != null
										&& missing_constant.length() > 0
										&& data.equals(missing_constant)) {
									pstmt.setNull(i, java.sql.Types.DECIMAL);
								} else {
									pstmt.setBigDecimal(i, new BigDecimal(data
											.trim()));
								}
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("ascii_complex")) {
								// FORMAT =
								// Ew.d[Ee] Floating point value, displayed in
								// exponential
								// format
								// /TODO: determine correct complex number
								// formatting
								pstmt.setString(i, data);
							} else if (rs.getString("data_type")
									.equalsIgnoreCase("N/A")) {
								pstmt.setString(i, data);

								//else case is for Non-ASCII. Obsolete data types supported by earlier versions of PDS
							} else {
								//Obsolete data types
								//BIT-STRING - Alias for MSB_BIT_STRING
								//COMPLEX - Alias for IEEE_COMPLEX
								//FLOAT - Alias for IEEE_REAL
								//INTEGER - Alias for MSB_INTEGER
								//REAL - Alias for IEEE_REAL
								//UNSIGNED_INTEGER - Alias for MSB_UNSIGNED_INTEGER
								if (rs.getString("data_type").equalsIgnoreCase("bit-string")) {
									pstmt.setString(i, data);

								} else if(rs.getString("data_type").equalsIgnoreCase("complex")) {
									pstmt.setString(i, data);

								} else if(rs.getString("data_type").equalsIgnoreCase("float")) {
									String trimmedData = data.trim();
									if (trimmedData.length() == 0) {
										pstmt.setNull(i, java.sql.Types.FLOAT);
									} else {
										try {
											pstmt.setFloat(i, Float
													.valueOf(trimmedData));
										} catch (NumberFormatException e) {
											pstmt.setLong(i, Long
													.valueOf(trimmedData));
										}
									}
								} else if(rs.getString("data_type").equalsIgnoreCase("integer")) {
									String trimmedData = data.trim();
									if (trimmedData.length() == 0) {
										//Inserts "null" to the query string
										pstmt.setNull(i, java.sql.Types.INTEGER);
									} else {
										//Set as an integer type
										try {
											pstmt.setInt(i, Integer
													.valueOf(trimmedData));
											//Set as a long type
										} catch (NumberFormatException e) {
											pstmt.setLong(i, Long
													.valueOf(trimmedData));
										}
									}
								} else if(rs.getString("data_type").equalsIgnoreCase("real")) {
									String missing_constant = rs.getString("missing_constant");
									if (missing_constant != null
											&& missing_constant.length() > 0
											&& data.equals(missing_constant)) {
										pstmt.setNull(i, java.sql.Types.DECIMAL);
									} else {
										pstmt.setBigDecimal(i, new BigDecimal(data
												.trim()));
									}
								} else if(rs.getString("data_type").equalsIgnoreCase("unsigned_integer")) {
									String trimmedData = data.trim();
									if (trimmedData.length() == 0) {
										//Inserts "null" to the query string
										pstmt.setNull(i, java.sql.Types.INTEGER);
									} else {
										//Set as an integer type
										try {
											pstmt.setInt(i, Integer
													.valueOf(trimmedData));
											//Set as a long type
										} catch (NumberFormatException e) {
											pstmt.setLong(i, Long
													.valueOf(trimmedData));
										}
									}
								} else {
									throw new RuntimeException("Obsolete Data Type used: " + rs.getString("data_type") + ". The current data types supported are: DATE, TIME, BOOLEAN, CHARACTER, ASCII_INTEGER, ASCII_REAL, ASCII_COMPLEX, and N/A");
								}
							}

							i++;
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
	 * Checks to see if the object is one of the following:
	 * 		COLUMN
	 * 		COLUMNWITHITEMS
	 * 		BITCOLUMN
	 * 		BITCOUMNSWIHTITEMS
	 * 
	 * @param rs - The result set data objects
	 * @return
	 * @throws SQLException
	 */
	public String elementType(ResultSet rs) throws SQLException {
		String dataType = null;

		try {
			// COLUMN or COLUMN WITH ITEMS (This is the case when item_bytes are
			// not specified. Meaning that bytes are specified
			// and all the ITEMS use the same number of bytes
			if ((rs.getString("bytes") != null && rs.getString("items") == null && rs
					.getString("field_number") == null)
					|| (rs.getString("items") != null
					&& rs.getString("bytes") != null
					&& rs.getString("item_bytes") == null && rs
					.getString("field_number") == null)) {
				dataType = "COLUMN";
				// COLUMN WITH ITEMS
			} else if (rs.getString("item_bytes") != null
					&& rs.getString("items") != null) {
				dataType = "COLUMNWITHITEMS";
				// BIT COLUMN
			} else if (rs.getString("bits") != null
					&& rs.getString("field_number") != null) {
				dataType = "BITCOLUMN";
				// BIT COLUMN WITH ITEMS
			} else if (rs.getString("item_bits") != null
					&& rs.getString("items") != null) {
				dataType = "BITCOLUMNWITHITEMS";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException("Data Type not set correctly in database");
		}

		return dataType;
	}

	/**
	 * Inner class to make a byte array iterable. Adds the ability
	 * to iterate through the ByteArrayBitIterable and get a range
	 * of bits
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

		public void setStartBit(int startBit) {

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
			TabularDataContainer tabularDataContainer) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fillDataTable(TabularDataContainer tabularDataContainer)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
