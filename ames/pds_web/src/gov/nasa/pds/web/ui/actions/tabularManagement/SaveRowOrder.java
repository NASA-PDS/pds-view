package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.utils.TabularDataUtils;

/**
 * Saves the row sort to the process and checks if query produces no results
 * 
 * @author Laura Baalman
 * @version 2
 */
public class SaveRowOrder extends Save {

	private static final long serialVersionUID = 1L;
	public static final String ADD_ORDER = "addOrder"; //$NON-NLS-1$
	public static final String REMOVE_RULE = "removeRule"; //$NON-NLS-1$
	private String selectedColumn;
	private String order;
	private String columnToRemove;
	private String conditionToRemove;
	private int startRow;
	private int numRowsPerPage;

	public void setAddOrder(@SuppressWarnings("unused") final String action) {
		this.actionString = ADD_ORDER;
	}

	public String getSelectedColumn() {
		return this.selectedColumn;
	}

	public void setSelectedColumn(final String selectedColumn) {
		this.selectedColumn = selectedColumn;
	}

	public String getOrder() {
		return this.order;
	}

	public void setOrder(final String order) {
		this.order = order;
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

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public void setNumRowsPerPage(int numRowsPerPage) {
		this.numRowsPerPage = numRowsPerPage;
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();
		SliceContainer slice = this.tabularDataProcess.getSlice();
		TabularDataContainer table = slice.getActiveTabularData();
		// when submitted by changing column selection
		// or jump to, or show per page
		// save values from form and pass back to same page using input
		if (this.actionString == null) {
			// assign values from the form to the slice in the
			// tabularDataProcess
			// if numrowsperpage does not change, save start row passed in
			if (slice.getNumRowsPerPage() == this.numRowsPerPage) {
				slice.setStartRow(this.startRow);

			} else {
				// if numRowsPerPage changes saved it and reset startRow to 1
				slice.setStartRow(1);
				slice.setNumRowsPerPage(this.numRowsPerPage);
			}
			return INPUT;
		}

		if (this.actionString.equals(SaveRowOrder.REMOVE_RULE)) {
			// TODO - test all three have been handed in?
			table.removeRowCriteria(this.columnToRemove,
					this.conditionToRemove, ""); //$NON-NLS-1$
			table.setRowsReturned(TabularDataUtils.countResultsReturned(table));
			// reset startrow to 1 to be similar to saveSelecteRows behavior,
			// though not strictly necessary
			slice.setStartRow(1);
		}

		if (this.actionString.equals(SaveRowOrder.ADD_ORDER)) {
			// add the sort order
			table.addSorts(table.getColumn(this.selectedColumn), this.order);
			// clear out values
			this.selectedColumn = null;
			this.order = null;
			// reset startrow to 1 to be similar to saveSelecteRows behavior,
			// though not strictly necessary
			slice.setStartRow(1);
		}

		if (this.actionString.equals(BaseSubmitAction.NEXT)) {
			return NEXT;
		}

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// 
	}

	@SuppressWarnings("nls")
	@Override
	protected void validateUserInput() {
		if (this.actionString != null) {
			if (this.actionString.equals(SaveRowOrder.ADD_ORDER)) {
				if (this.selectedColumn.length() < 1) {
					addError("orderRows.error.noOrderColumnSelected");
				}
				if (this.order.length() < 1) {
					addError("orderRows.error.noOrderDirection");
				}
			}

		}
	}
}
