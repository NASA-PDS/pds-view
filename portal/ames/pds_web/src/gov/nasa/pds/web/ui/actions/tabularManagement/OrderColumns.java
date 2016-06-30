package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Allows user to rearrange order of columns
 * 
 * @author Laura Baalman
 */

public class OrderColumns extends Display {

	private static final long serialVersionUID = 1L;
	protected List<String> columnDisplay = new ArrayList<String>();
	private String reorderedColumns;
	private String columnsString;

	public List<String> getColumnDisplay() {
		return this.columnDisplay;
	}

	public void setReorderedColumns(String reorderedColumns) {
		this.reorderedColumns = reorderedColumns;
	}

	public String getColumnsString() {
		return this.columnsString;
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();
		setTitle("orderColumns.title"); //$NON-NLS-1$
		this.tabularDataContainer = this.slice.getActiveTabularData();
		// if reordered columns list has been passed in, use it as columns
		// display
		if (this.reorderedColumns != null && this.reorderedColumns.length() > 0) {
			// copy string into ArrayList
			StringTokenizer st = new StringTokenizer(this.reorderedColumns, ","); //$NON-NLS-1$
			while (st.hasMoreTokens()) {
				this.columnDisplay.add(st.nextToken());
			}
			// retain reordering in variable used in links on form
			this.columnsString = this.reorderedColumns;
		} else {
			// otherwise, use column order of process object to order
			// create duplicate array of selected columns
			List<String> columnHolder = new ArrayList<String>();

			for (SliceColumn column : this.tabularDataContainer.getColumns()) {
				// add name to array list
				columnHolder.add(column.getName());
			}

			// create shallow copy of holder array to rearrange
			this.columnDisplay = new ArrayList<String>(columnHolder);

			for (String name : columnHolder) {
				// move to the correct index
				this.columnDisplay.set(this.tabularDataContainer
						.getColumn(name).getOrderIndex(), name);
			}
			for (String name : columnHolder) {
				if (!this.tabularDataContainer.getColumn(name).isSelected()) {
					this.columnDisplay.remove(name);
				}
			}
			columnHolder = null;
		}
		// create stringbuilder to hold all selected column names in order
		StringBuilder sb = new StringBuilder();
		// append to string builder
		for (Iterator<?> it = this.columnDisplay.iterator(); it.hasNext();) {
			sb.append(it.next());
			if (it.hasNext())
				sb.append(","); //$NON-NLS-1$
		}
		// transfer string builder value to variable used in links on form
		this.columnsString = sb.toString();
		sb = null;

		return INPUT;

	}
}
