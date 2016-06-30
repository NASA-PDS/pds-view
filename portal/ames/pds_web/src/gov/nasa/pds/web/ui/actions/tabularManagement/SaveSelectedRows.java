package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants.Condition;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.utils.DateUtils;
import gov.nasa.pds.web.ui.utils.TabularDataUtils;

/**
 * Saves the row criteria to the process and checks if query produces no results
 * 
 * @author Laura Baalman
 * @version 2
 */
public class SaveSelectedRows extends Save {

	private static final long serialVersionUID = 1L;
	public static final String ADD_CONDITION = "addCondition"; //$NON-NLS-1$
	public static final String REMOVE_RULE = "removeRule"; //$NON-NLS-1$
	private String selectedColumn;
	private String condition;
	private String value;
	private String columnToRemove;
	private String conditionToRemove;
	private String valueToRemove;
	private String queryMode; // all (and) or any (or)
	private int startRow;
	private int numRowsPerPage;

	// setter for submit button type not provided by Save or BaseSubmitAction
	public void setAddCondition(@SuppressWarnings("unused") final String action) {
		this.actionString = ADD_CONDITION;
	}

	// need getters and setters for values passed back during building of form
	public String getSelectedColumn() {
		return this.selectedColumn;
	}

	public void setSelectedColumn(final String selectedColumn) {
		this.selectedColumn = selectedColumn;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(final String condition) {
		this.condition = condition;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getColumnToRemove() {
		return this.columnToRemove;
	}

	public void setColumnToRemove(String columnToRemove) {
		this.columnToRemove = columnToRemove;
		this.actionString = REMOVE_RULE;
	}

	public String getConditionToRemove() {
		return this.conditionToRemove;
	}

	public void setConditionToRemove(String conditionToRemove) {
		this.conditionToRemove = conditionToRemove;
	}

	public String getValueToRemove() {
		return this.valueToRemove;
	}

	public void setValueToRemove(String valueToRemove) {
		this.valueToRemove = valueToRemove;
	}

	// public String getQueryMode() {
	// return this.queryMode;
	// }

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}

	// public int getStartRow() {
	// return this.startRow;
	// }

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	// public int getNumRowsPerPage() {
	// return this.numRowsPerPage;
	// }

	public void setNumRowsPerPage(int numRowsPerPage) {
		this.numRowsPerPage = numRowsPerPage;
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();

		SliceContainer slice = this.tabularDataProcess.getSlice();
		TabularDataContainer table = slice.getActiveTabularData();
		
		// get the total number of rows per page and query
		this.numRowsPerPage = table.getNumRowsPerPage();
		this.queryMode = table.getQueryMode();

		// when submitted by changing column or condition selection (part of
		// building a rule), or changing any/all, or jump to, or show per page
		// save values from form and pass back to same page using input
		if (this.actionString == null) {

			// if numrowsperpage does not change, save start row passed in
			if (slice.getNumRowsPerPage() == this.numRowsPerPage) {
				slice.setStartRow(this.startRow);
				slice.setNumRowsPerPage(this.numRowsPerPage);
			} else {
				// if numRowsPerPage changes saved it and reset startRow to 1
				slice.setStartRow(1);
				slice.setNumRowsPerPage(this.numRowsPerPage);
			}

			// if query mode changes, requery, reset startRow
			if (!table.getQueryMode().equalsIgnoreCase(this.queryMode)) {
				table.setQueryMode(this.queryMode);
				slice.setStartRow(1);
				table.setRowsReturned(TabularDataUtils
						.countResultsReturned(table));
			}
			return INPUT;
		}

		if (this.actionString.equals(SaveSelectedRows.REMOVE_RULE)) {
			// TODO - test all three have been handed in
			table.removeRowCriteria(this.columnToRemove,
					this.conditionToRemove, this.valueToRemove);
			table.setRowsReturned(TabularDataUtils.countResultsReturned(table));
			// reset startrow to 1 because result set has changed
			slice.setStartRow(1);
		}

		if (this.actionString.equals(SaveSelectedRows.ADD_CONDITION)) {
			SliceColumn column = table.getColumn(this.selectedColumn);
			if (TabularDataUtils.isColumnDateTime(column)) {
				// turn date value into a long
				this.value = String.valueOf(DateUtils.toEpochDate(this.value));

			}
			// add the condition
			table.addConditions(table.getColumn(this.selectedColumn),
					this.condition, this.value);

			table.setRowsReturned(TabularDataUtils.countResultsReturned(table));
			// clear out values
			this.selectedColumn = null;
			this.condition = null;
			this.value = null;
			// reset startrow to 1 because result set has changed, start row
			// could be higher than rows returned
			slice.setStartRow(1);
		}

		if (this.actionString.equals(BaseSubmitAction.NEXT))
			return NEXT;

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// TODO push all values into the session that may have been invalid
	}

	@SuppressWarnings("nls")
	@Override
	protected void validateUserInput() {
		if (this.actionString != null) {
			if (this.actionString.equals(SaveSelectedRows.ADD_CONDITION)) {
				if (this.selectedColumn.length() < 1) {
					addError("selectRows.error.noColumnSelected");
				}
				if (this.condition.length() < 1) {
					addError("selectRows.error.noCondition");
				}
				// if condition is IS or IS_NOT, value can be null, otherwise
				// not
				if (!this.condition
						.equals(TabularManagementConstants.Condition.IS.name())
						&& !this.condition
								.equals(TabularManagementConstants.Condition.IS_NOT
										.name())) {
					if (this.value.length() < 1) {
						addError("selectRows.error.noValueEntered");
					}
				}
				if (this.value.length() > 0) {
					for (Condition rowCondition : TabularManagementConstants.Condition
							.values()) {
						if (this.condition.equals(rowCondition.name())) {
							if (rowCondition
									.getType()
									.equals(
											TabularManagementConstants.ConditionType.NUMERIC)
									& !StrUtils.isNumber(this.value)) {
								addError("selectRows.error.numericOnly");
							}

							if (rowCondition
									.getType()
									.equals(
											TabularManagementConstants.ConditionType.DATE_TIME)) {
								// try creating pds date out of value
								if (DateUtils.getPattern(this.value) == null) {
									addError("selectRows.error.unrecognizedDateFormat");
								}
							}
						}
					}
				}
			}
		}
	}
}
