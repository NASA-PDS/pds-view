package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.containers.tabularManagement.ColumnCheckbox;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.utils.Comparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * allows user to select which columns to include, and sort by column name or
 * data type
 * 
 * @author Laura Baalman
 * @version 3
 */

public class SelectColumns extends Display {

	private static final long serialVersionUID = 1L;
	private List<ColumnCheckbox> columnCheckboxes = new ArrayList<ColumnCheckbox>();
	private String sort;
	private String checkedColumns;
	private String changedColumns;
	// inErrorState used in jsp to decide not check the checkAll box
	// if error condition exists because the only error condition for this
	// action is if there are no columns selected
	private boolean inErrorState;

	public boolean getInErrorState() {
		return this.inErrorState;
	}

	public void setCheckedColumns(String checkedColumns) {
		this.checkedColumns = checkedColumns;
	}

	public List<ColumnCheckbox> getColumnCheckboxes() {
		return this.columnCheckboxes;
	}

	public String getSort() {
		return this.sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getChangedColumns() {
		return this.changedColumns;
	}

	public void setChangedColumns(String changedColumns) {
		this.changedColumns = changedColumns;
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();
		setTitle("selectColumns.title"); //$NON-NLS-1$
		// hold hasErrors value to be used in test in jsp
		this.inErrorState = this.hasErrors();

		for (SliceColumn column : this.getTabularDataContainer().getColumns()) {
			Boolean checked;
			// check if checkedColumns have been passed in
			if (this.checkedColumns != null && this.checkedColumns.length() > 0) {
				// if so, use contains to get boolean for checked
				checked = this.checkedColumns.contains(String.valueOf(column
						.getColumnId()));
			} else {
				// otherwise, use the process' column's value for checked
				checked = column.isSelected();
			}
			// create checkbox collection for display
			this.columnCheckboxes.add(new ColumnCheckbox(column.getColumnId(),
					column.getName(), checked, column.getDataType(), column
							.getDescription()));
		}

		// apply sort if this.sort is set
		if (this.sort != null && this.sort.length() > 0) {
			if (this.sort.equalsIgnoreCase(SaveSelectedColumns.NAME_ASC)) {
				Collections.sort(this.columnCheckboxes,
						Comparators.COLUMNCHECKBOX_LABEL_COMPARATOR);
			}
			if (this.sort.equalsIgnoreCase(SaveSelectedColumns.NAME_DESC)) {
				Collections.sort(this.columnCheckboxes,
						Comparators.COLUMNCHECKBOX_LABEL_COMPARATOR);
				Collections.reverse(this.columnCheckboxes);
			}
			if (this.sort.equalsIgnoreCase(SaveSelectedColumns.DATATYPE_ASC)) {
				Collections.sort(this.columnCheckboxes,
						Comparators.COLUMNCHECKBOX_DATATYPE_COMPARATOR);
			}
			if (this.sort.equalsIgnoreCase(SaveSelectedColumns.DATATYPE_DESC)) {
				Collections.sort(this.columnCheckboxes,
						Comparators.COLUMNCHECKBOX_DATATYPE_COMPARATOR);
				Collections.reverse(this.columnCheckboxes);

			}

		} else {
			this.sort = null;
		}
		return INPUT;
	}

}
