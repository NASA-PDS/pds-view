package gov.nasa.pds.web.ui.containers.tabularManagement;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.arc.pds.xml.generated.MaximumRecordLength;
import gov.nasa.arc.pds.xml.generated.ObjectLength;
import gov.nasa.arc.pds.xml.generated.Offset;
import gov.nasa.arc.pds.xml.generated.RecordLength;
import gov.nasa.pds.web.ui.actions.tabularManagement.RowCriteria;
import gov.nasa.pds.web.ui.containers.BaseContainerInterface;
import gov.nasa.pds.web.ui.containers.tabularData.Row;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.Comparators;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TabularDataContainer implements Serializable,
		BaseContainerInterface {

	private static final long serialVersionUID = -2278587525150117971L;
	
	private String id;
	private long dbId;
	
	// PDS3 = INTERCHANGE_FORMAT = ASCII OR BINARY
	// PDS4 = FIXED_TEXT or DELIMITED = ASCII
	//		  FIXED_BINARY = BINARY
	private String format;
	
	// PDS3 = ROW_BYTES
	// PDS4 = record_length
	private int rowBytes;
	
	// PDS3 = FIELD_DELIMITER
	// PDS4 = field_delimiter
	// values = {, | ; \t}
	private String delimiter;
	
	// PDS3 = TABLE or SPREADSHEET
	// PDS4 = FIXED_BINARY or FIXED_TEXT or DELIMETED
	// TODO: check if it is being used
	private String type;
	
	// PDS type = PDS4 or PDS3
	private String pdsType;
	
	// TODO: datafile could replace both tabFileName and TabFileUrl??
	// do we really need to store the FILE object? The URL will suffice
	private File dataFile;
	private String tabFileName;
	private String tabFileUrl;
	private URL tabFileURL;
	
	//Keep track of the total number of columns parsed from the label
	//and the ones processed and inserted into the temporary table. 
	//If the label has more than 500 columns, these values will differ.
	//Otherwise, they will be the same
	private int labelTotalColumnCount;
	private int tableTotalColumnCount;
	
	//status whether the table has been completely populated or not
	private boolean tablePopulatedStatus = false;

	private List<SliceColumn> columns = new ArrayList<SliceColumn>();
	private List<Row> rows = Collections.synchronizedList(new ArrayList<Row>());
	private List<RowCriteria> conditions = new ArrayList<RowCriteria>();
	private List<RowCriteria> sorts = new ArrayList<RowCriteria>();
	private List<SliceColumn> selectedColumns = Collections
			.synchronizedList(new ArrayList<SliceColumn>());
	private int totalRows;
	private String queryMode = "AND";
	private int rowsReturned;
	private String tableName;
	private int selectedColumnCount;
	private int numRowsPerPage;

	// PDS4 specific
	// recordDelimiter and fieldDelimiter used for delimited tables only
	private String recordDelimiter;
	private String fieldDelimiter;
	
	private int offset;
	private int fieldsCount;
	private int groupsCount;
	private String parsing_standard_id;
	private String local_identifier;
	private ObjectLength object_length;
	private RecordLength recordLength;
	private MaximumRecordLength maximumRecordLength;
	private Offset offsetObject;
	private String description;
	private String name;
	
	private String fileInformation;
	private String tableInformation;

	public TabularDataContainer() {
		this.id = UUID.randomUUID().toString();
		this.tablePopulatedStatus = false;
	}

	public String getPDSType() {
		return this.pdsType;
	}
	
	public void setPDSType(String pdsType) {
		this.pdsType = pdsType;
	}
	
	public int getNumRowsPerPage() {
		return this.numRowsPerPage;
	}
	
	public void setNumRowsPerPage(int numRowsPerPage) {
		this.numRowsPerPage = numRowsPerPage;
	}
	
	public void setFileInformation(String fileInformation) {
		this.fileInformation = fileInformation;
	}
	
	public String getFileInformation() {
		return this.fileInformation;
	}
	
	public void setTableInformation(String tableInformation) {
		this.tableInformation = tableInformation;
	}
	
	public String getTableInformation() {
		return this.tableInformation;
	}
	
	public void setTablePopulatedStatus(boolean tableStatus) {
		this.tablePopulatedStatus = tableStatus;
	}
	
	public MaximumRecordLength getMaximumRecordLength() {
		return this.maximumRecordLength;
	}
	
	public void setMaximumRecordLength(MaximumRecordLength maximumRecordLength) {
		this.maximumRecordLength = maximumRecordLength;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOffset(Offset offset) {
		this.offsetObject = offset;
	}
	
	public Offset getOffsetObject() {
		return this.offsetObject;
	}
	
	
	public RecordLength getRecordLength() {
		return this.recordLength;
	}
	
	public void setRecordLength(RecordLength recordLength) {
		this.recordLength = recordLength;
	}
	
	public ObjectLength getObjectLength() {
		return this.object_length;
	}
	
	public void setObjectLength(ObjectLength objectLength) {
		this.object_length = objectLength;
	}
	
	public String getLocalIdentifier() {
		return this.local_identifier;
	}
	public void setLocalIdentifier(String localIdentifier) {
		this.local_identifier = localIdentifier;
	}
	
	public int getFieldsCount() {
		return this.fieldsCount;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setFieldsCount(int fieldsCount) {
		this.fieldsCount = fieldsCount;
	}
	
	public int getGroupsCount() {
		return this.groupsCount;
	}
	
	public void setGroupsCount(int groupsCount) {
		this.groupsCount = groupsCount;
	}
	
	public String getParsingStandardID() {
		return this.parsing_standard_id;
	}
	
	public void setParsingStandardID(String parsingStandardID) {
		this.parsing_standard_id = parsingStandardID;
	}
	
	
	public boolean getTablePopulatedStatus() {
		return this.tablePopulatedStatus;
	}
	
	public String getRecordDelimiter() {
		return this.recordDelimiter;
	}
	
	public void setRecordDelimiter(String recordDelimiter) {
		this.recordDelimiter = recordDelimiter;
	}
	
	public String getFieldDelimiter() {
		return this.fieldDelimiter;
	}
	
	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}
	
	public String getId() {
		return this.id;
	}

	public long getDbId() {
		return this.dbId;
	}

	public void setDbId(long dbId) {
		this.dbId = dbId;
	}

	public File getDataFile() {
		return this.dataFile;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getRowBytes() {
		return this.rowBytes;
	}

	public void setRowBytes(int rowBytes) {
		this.rowBytes = rowBytes;
	}

	public void setDataFile(File file) {
		this.dataFile = file;
	}

	public String getTabFileName() {
		return this.tabFileName;
	}

	public String getTabFileUrl() {
		return this.tabFileUrl;
	}

	public void setTabFileUrl(String tabFileUrl) {
		this.tabFileUrl = tabFileUrl;
		this.tabFileName = StrUtils.getURLFilename(tabFileUrl);
	}
	
	public URL getTabFileURL() {
		return this.tabFileURL;
	}
	
	public void setTabFileURL(URL tabFileURL) {
		this.tabFileURL = tabFileURL;
	}

	public List<SliceColumn> getColumns() {
		return this.columns;
	}

	public void setColumns(List<SliceColumn> columns) {
		this.columns = columns;
	}

	public List<SliceColumn> getSelectedColumns() {
		return this.selectedColumns;
	}

	public void setSelectedColumns(List<SliceColumn> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}

	public int getSelectedColumnCount() {
		return this.selectedColumnCount;
	}

	public void setSelectedColumnCount(int selectedColumnCount) {
		this.selectedColumnCount = selectedColumnCount;
	}

	public List<Row> getRows() {
		return this.rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public int getTotalRows() {
		return this.totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public String getQueryMode() {
		return this.queryMode;
	}

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}

	public void setRowsReturned(int rowsReturned) {
		this.rowsReturned = rowsReturned;
	}

	public int getRowsReturned() {
		return this.rowsReturned;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return this.delimiter;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public void setLabelTotalColumnCount(int columnCount) {
		this.labelTotalColumnCount = columnCount;
	}
	
	public void setTableTotalColumnCount(int columnCount) {
		this.tableTotalColumnCount = columnCount;
	}
	
	public int getLabelTotalColumnCount() {
		return this.labelTotalColumnCount;
	}
	
	public int getTableTotalColumnCount() {
		return this.tableTotalColumnCount;
	}
	
	public List<RowCriteria> getConditions() {
		return this.conditions;
	}

	public void addConditions(SliceColumn column, String condition, String value) {
		this.conditions.add(new RowCriteria(column, condition, value));
		// log the addition of this condition filter
		LogManager.logFilter(this, column.getName(), condition, value, false);

	}

	public List<RowCriteria> getSorts() {
		return this.sorts;
	}

	public void addSorts(SliceColumn column, String condition) {
		this.sorts.add(new RowCriteria(column, condition, "")); //$NON-NLS-1$
		// log the addition of this sort filter
		LogManager.logFilter(this, column.getName(), condition, null, false);
	}

	/*
	 * remove any RowCriteria objects from the list rowCriteria that match the
	 * column names passed in as List of Strings
	 */
	public void removeRowCriteria(String columnToRemove, String condition,
			String value) {
		boolean done = false;
		for (Iterator<?> it = this.conditions.iterator(); it.hasNext();) {
			RowCriteria row = (RowCriteria) it.next();
			if (row.getColumn().getName().equalsIgnoreCase(columnToRemove)
					&& row.getCondition().equalsIgnoreCase(condition)
					&& row.getValue().equalsIgnoreCase(value)) {
				it.remove();
				done = true;
				break;
			}
		}
		if (!done) {
			for (Iterator<?> it = this.sorts.iterator(); it.hasNext();) {
				RowCriteria row = (RowCriteria) it.next();
				if (row.getColumn().getName().equalsIgnoreCase(columnToRemove)
						&& row.getCondition().equalsIgnoreCase(condition)) {
					it.remove();
				}
			}
		}
		// log the removal of this filter
		LogManager.logFilter(this, columnToRemove, condition, value, true);
	}

	/*
	 * retrieve a SliceColumn from columns (the List of SliceColumns) by the
	 * SliceColumn's name as a string
	 */
	public SliceColumn getColumn(String columnName) {
		for (SliceColumn column : this.columns) {
			if (column.getName().equalsIgnoreCase(columnName)) {
				return column;
			}
		}
		return null;
	}

	/*
	 * saves index of each column in arraylist selectedColumns as the column's
	 * OrderIndex
	 */
	public void saveSelectedColumnOrder() {
		for (SliceColumn column : this.selectedColumns) {
			this.getColumn(column.getName()).setOrderIndex(
					this.selectedColumns.indexOf(column));
		}
		// TODO Remove
		reorderRows();
	}

	// TODO remove
	/*
	 * reorders tab data based on the orderIndex of each element's column
	 */
	public void reorderRows() {
		for (Row row : this.rows) {
			Collections.sort(row.getElements(),
					Comparators.ELEMENT_SLICECOLUMN_COMPARATOR);
		}
	}

}
