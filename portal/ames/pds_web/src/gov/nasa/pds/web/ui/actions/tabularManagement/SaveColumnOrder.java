package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * saves the column order to the process object
 * 
 * @author Laura Baalman
 * @version 3
 */
public class SaveColumnOrder extends Save {

	private static final long serialVersionUID = 1L;
	private String columnsString;

	public String getColumnString() {
		return this.columnsString;
	}

	public void setColumnsString(String columnsString) {
		this.columnsString = columnsString;
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();

		if (this.actionString.equals(SAVE)) {
			// add each comma separated value in reorderedColumns to array
			StringTokenizer st = new StringTokenizer(this.columnsString, ","); //$NON-NLS-1$
			ArrayList<String> colArray = new ArrayList<String>();
			while (st.hasMoreTokens()) {
				colArray.add(st.nextToken());
			}

			int i = colArray.size();
			TabularDataContainer table = this.tabularDataProcess.getSlice()
					.getActiveTabularData();

			for (SliceColumn column : table.getColumns()) {
				if (column.isSelected()) {
					// save selected at new index
					column.setOrderIndex(colArray.indexOf(column.getName()));
				} else {
					// stick unselected at the end, out of the way
					column.setOrderIndex(i);
					i++;
				}
			}
		}
		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// noop
	}

	@Override
	protected void validateUserInput() {
		// noop
	}
}
