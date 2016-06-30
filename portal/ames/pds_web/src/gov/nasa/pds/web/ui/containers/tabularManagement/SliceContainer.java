package gov.nasa.pds.web.ui.containers.tabularManagement;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.containers.BaseContainerInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SliceContainer implements Serializable, BaseContainerInterface {

	// update this if you make non-compatible change to class
	private static final long serialVersionUID = 1351499389220615757L;
	private String id;
	private long dbId;
	private String productId;
	private String dataSetId;
	private String labelFileName;
	private String labelURLString;
	private String pdsLabelType;

	private int activeTabDataIndex;
	private List<TabularDataContainer> tabularDataObjects = new ArrayList<TabularDataContainer>();

	// LAB 05/27/10 the following 4 vars could be in tabularDataContainer
	private String fileType = TabularManagementConstants.FileType.TAB
			.toString();
	private boolean includeHeaders = true;
	private int startRow = 1;
	private int numRowsPerPage = 100;

	// private boolean useCommas = true;
	// private boolean quoteStrings = true;

	public String getId() {
		return this.id;
	}

	public long getDbId() {
		return this.dbId;
	}

	public void setDbId(long dbId) {
		this.dbId = dbId;
	}

	public SliceContainer() {
		this.id = UUID.randomUUID().toString();

	}

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getDataSetId() {
		return this.dataSetId;
	}

	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}

	public String getLabelURLString() {
		return this.labelURLString;
	}

	public void setLabelURLString(String labelURLString) {
		this.labelURLString = labelURLString;
		this.labelFileName = StrUtils.getURLFilename(labelURLString);
	}

	public String getPDSLabelType() {
		return this.pdsLabelType;
	}
	
	public void setPDSLabelType(String pdsLabelType){
		this.pdsLabelType = pdsLabelType;
	}
	
	public String getLabelFileName() {
		return this.labelFileName;
	}

	public void setLabelFileName(String labelFileName) {
		this.labelFileName = labelFileName;
	}

	public int getActiveTabDataIndex() {
		return this.activeTabDataIndex;
	}

	public void setActiveTabDataIndex(int id) {
		this.activeTabDataIndex = id;
	}

	public List<TabularDataContainer> getTabularDataObjects() {
		return this.tabularDataObjects;
	}

	public void setTabularDataObjects(
			List<TabularDataContainer> tabularDataObjects) {
		this.tabularDataObjects = tabularDataObjects;
	}

	public void addTabularDataObject(TabularDataContainer tabularDataObject) {
		this.tabularDataObjects.add(tabularDataObject);
	}

	public TabularDataContainer getActiveTabularData() {
		// need to catch if nothing is in tabularDataObjects
		if (this.tabularDataObjects.size() > 0)
			return this.tabularDataObjects.get(this.activeTabDataIndex);
		return null;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Boolean getIncludeHeaders() {
		return this.includeHeaders;
	}

	public Boolean includeHeaders() {
		return this.includeHeaders;
	}

	public void setIncludeHeaders(Boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}

	public int getStartRow() {
		return this.startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getNumRowsPerPage() {
		return this.numRowsPerPage;
	}

	public void setNumRowsPerPage(int numRowsPerPage) {
		this.numRowsPerPage = numRowsPerPage;
	}
	/*
	 * public void setUseCommas(boolean useCommas) { this.useCommas = useCommas;
	 * 
	 * }
	 * 
	 * public boolean useCommas() { return this.useCommas; }
	 * 
	 * public void setQuoteStrings(boolean quoteStrings) { this.quoteStrings =
	 * quoteStrings; }
	 * 
	 * public boolean quoteStrings() { return this.quoteStrings; }
	 */
}