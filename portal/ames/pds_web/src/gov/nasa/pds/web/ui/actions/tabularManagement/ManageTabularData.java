package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants.FileType;
import gov.nasa.pds.web.ui.containers.Option;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.utils.TabularDataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Defines primary UI for data slicer containing links to select and order both
 * columns and rows as well as define and down load output file.
 * 
 * @author Laura Baalman
 */
public class ManageTabularData extends Display {

	private static final long serialVersionUID = 1L;
	private List<Option> fileTypeOptions = new ArrayList<Option>();
	private String fileType;
	private boolean includeHeaders = true;
	private List<RowCriteria> conditionsDisplay = new ArrayList<RowCriteria>();

	public List<Option> getFileTypeOptions() {
		return this.fileTypeOptions;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setIncludeHeaders(boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}

	public boolean includeHeaders() {
		return this.includeHeaders;
	}

	public boolean getIncludeHeaders() {
		return this.includeHeaders;
	}

	private String queryMode; // and / or

	public String getQueryMode() {
		return this.queryMode;
	}

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}

	public List<RowCriteria> getConditionsDisplay() {
		return this.conditionsDisplay;
	}

	public void addConditionsDisplay(SliceColumn column,
			String conditionString, String valueString) {
		this.conditionsDisplay.add(new RowCriteria(column, conditionString,
				valueString));
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();
		setTitle("manageTabularData.title"); //$NON-NLS-1$

		getPreviewData(); // method of parent class (Display)

		if (this.tabularDataProcess.getSlice().getFileType() != null
				&& this.tabularDataProcess.getSlice().getFileType().length() > 0)
			this.fileType = this.tabularDataProcess.getSlice().getFileType();
		else
			this.fileType = TabularManagementConstants.FileType.TAB.toString();

		// build file format options
		for (FileType type : TabularManagementConstants.FileType.values()) {
			// if this type applies to the format of the table (or "both")
			if (type.getApplication().equalsIgnoreCase(
					this.tabularDataContainer.getFormat())
					|| type.getApplication().equalsIgnoreCase("both")) { //$NON-NLS-1$
				this.fileTypeOptions.add(new Option(type.name(), getUIManager()
						.getTxt(type.getKey())));
			}
		}
		Option.setSelected(this.fileTypeOptions, this.fileType);

		// create display duplicate of TabularDataProcess.rows
		for (RowCriteria criteria : this.tabularDataContainer.getConditions()) {
			String dateString = null;
			if (TabularDataUtils.isColumnDateTime(criteria.getColumn())) {
				// apply format to date string
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						criteria.getColumn().getDateFormat());
				Date date = new Date(Long.valueOf(criteria.getValue()
						.toString()));
				dateString = simpleDateFormat.format(date);
				this.addConditionsDisplay(criteria.getColumn(), criteria
						.getCondition(), dateString);
			} else {
				this.addConditionsDisplay(criteria.getColumn(), criteria
						.getCondition(), criteria.getValue());
			}

		}
		return INPUT;
	}
}
