package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.utils.Comparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * validates if no columns selected, saves the selected columns into the process
 * object, sorts columns for display
 * 
 * @author Laura Baalman
 * @version 3
 */
public class SaveSelectedColumns extends Save {

	private static final long serialVersionUID = 1L;
	public static final String SORT = "sort"; //$NON-NLS-1$
	public static final String NAME_ASC = "nameAsc"; //$NON-NLS-1$
	public static final String NAME_DESC = "nameDesc"; //$NON-NLS-1$
	public static final String DATATYPE_ASC = "dataTypeAsc"; //$NON-NLS-1$
	public static final String DATATYPE_DESC = "dataTypeDesc"; //$NON-NLS-1$
	private List<String> checkedColumns = new ArrayList<String>();
	@SuppressWarnings("unused")
	private String sortColumn;
	private String sort;
	private String savedSort;
	private String changedColumns;
	private String savedChangedColumns;

	public void setSave() {
		this.actionString = SAVE;
	}

	public List<String> getCheckedColumns() {
		return this.checkedColumns;
	}

	public void setCheckedColumns(List<String> checkedColumns) {
		this.checkedColumns = checkedColumns;
	}

	public void setSortColumn(String sortColumn) {
		if (sortColumn.length() > 0) {
			if (sortColumn.equalsIgnoreCase("name")) { //$NON-NLS-1$
				if (this.savedSort.equals(NAME_ASC))
					this.sort = NAME_DESC;
				else
					this.sort = NAME_ASC;
			} else if (sortColumn.equalsIgnoreCase("dataType")) { //$NON-NLS-1$
				if (this.savedSort.equals(DATATYPE_ASC))
					this.sort = DATATYPE_DESC;
				else
					this.sort = DATATYPE_ASC;
			}
			this.actionString = SORT;
		}
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSort() {
		return this.sort;
	}

	public void setSavedSort(String savedSort) {
		this.savedSort = savedSort;
	}

	public String getSavedSort() {
		return this.savedSort;
	}

	public String getChangedColumns() {
		return this.changedColumns;
	}

	public void setChangedColumns(String changedColumns) {
		this.changedColumns = changedColumns;
	}

	public String getSavedChangedColumns() {
		return this.savedChangedColumns;
	}

	public void setSavedChangedColumns(String savedChangedColumns) {
		this.savedChangedColumns = savedChangedColumns;
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();

		// TODO: LAB 8/4/09 this may not be used any longer
		if (this.actionString.equals(SORT)) {
			this.changedColumns = this.savedChangedColumns;
			return SORT;
		}

		if (this.actionString.equals(BaseSubmitAction.SAVE)) {
			this.saveCheckedColumns();
			this.sort = this.savedSort;
		}

		return SUCCESS;
	}

	private void saveCheckedColumns() {
		// LAB 8/4/09 change when multiple objects supported
		TabularDataContainer table = this.tabularDataProcess.getSlice()
				.getActiveTabularData();

		// setSelected to false for all columns
		for (SliceColumn column : table.getColumns()) {
			column.setSelected(false);
		}

		// remove all selected columns
		table.getSelectedColumns().clear();
		// loop through returned collection of checkedColumns
		int i = 0;
		for (String checkedColumn : this.checkedColumns) {
			i++;
			// loop through all columns
			for (SliceColumn column : table.getColumns()) {
				// compare current from selected with current from all
				// columns
				if (checkedColumn.equalsIgnoreCase(String.valueOf(column
						.getColumnId()))) {
					// set selected = true in column list
					column.setSelected(true);
					// TODO REMOVE and test
					// and add to selectedColumns
					table.getSelectedColumns().add(column);
					// exit loop once the current column has been matched
					break;
				}
			}
		}
		table.setSelectedColumnCount(i);
		// reorder selected columns based on index
		Collections.sort(table.getSelectedColumns(),
				Comparators.TABULAR_COLUMN_COMPARATOR);

	}

	@Override
	protected void validateUserInput() {

		if (this.actionString.equals(SAVE) && this.checkedColumns.size() == 0) {
			addError("selectColumns.error.noColumnsSelected"); //$NON-NLS-1$
		}
	}

	@Override
	protected void pushBackUserInput() {
		this.sort = this.savedSort;

	}

}