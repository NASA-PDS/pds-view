package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * accepts string of all columns, name of specific column and direction to move
 * it. outputs a string of all columns in new order.
 * 
 * @author Laura Baalman
 * @version 2
 */
public class MoveColumn extends Save {

	private static final long serialVersionUID = 1L;
	private String direction;
	private String column;
	private String columnsString;
	private String reorderedColumns;

	public void setColumn(String column) {
		this.column = column;
	}

	public String getColumn() {
		return this.column;
	}

	public String getDirection() {
		return this.direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getColumnString() {
		return this.columnsString;
	}

	public void setColumnsString(String columnsString) {
		this.columnsString = columnsString;
	}

	public String getReorderedColumns() {
		return this.reorderedColumns;
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();

		// convert string to array
		StringTokenizer st = new StringTokenizer(this.columnsString, ","); //$NON-NLS-1$
		ArrayList<String> colArray = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			colArray.add(st.nextToken());
		}
		// iterate through columns to compare with one submitted to
		// be changed
		TabularDataContainer table = this.tabularDataProcess.getSlice()
				.getActiveTabularData();
		for (SliceColumn sliceColumn : table.getColumns()) {
			// only selected columns might be passed in from form
			if (sliceColumn.isSelected()) {

				// compare column with the one submitted for change via link
				if (sliceColumn.getName().equals(this.column)) {
					// get current index
					int currentIndex = colArray.indexOf(sliceColumn.getName());
					// create variables for changes to index
					int newIndex = 0;
					int highIndex = colArray.size() - 1;
					// set new index based on direction and current index -
					// prevent out of index range errors
					if (this.direction.equalsIgnoreCase("up")) { //$NON-NLS-1$
						// if already at start, can't move further up
						if (currentIndex == 0) {
							newIndex = currentIndex;
						} else {
							newIndex = currentIndex - 1;
						}
					} else if (this.direction.equalsIgnoreCase("down")) { //$NON-NLS-1$
						// if already at high end, can't move further
						// down
						if (currentIndex == highIndex) {
							newIndex = currentIndex;
						} else {
							newIndex = currentIndex + 1;
						}
					}
					Collections.swap(colArray, currentIndex, newIndex);
					break;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (Iterator<?> it = colArray.iterator(); it.hasNext();) {
			sb.append(it.next());
			if (it.hasNext())
				sb.append(","); //$NON-NLS-1$
		}
		this.reorderedColumns = sb.toString();
		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// 
	}

	@Override
	protected void validateUserInput() {
		//
	}
}
