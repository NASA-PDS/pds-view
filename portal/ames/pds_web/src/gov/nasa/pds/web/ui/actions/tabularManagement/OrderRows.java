package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.constants.TabularManagementConstants.Condition;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants.ConditionType;
import gov.nasa.pds.web.ui.containers.Option;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * allows user to order row output
 * 
 * @author Laura Baalman
 * @version 2
 */

public class OrderRows extends Display {

	private static final long serialVersionUID = 1L;
	private List<Option> columnOptions = new ArrayList<Option>();
	private List<Option> orderOptions = new ArrayList<Option>();
	private String column;
	private String order;
	private List<RowCriteria> conditionsDisplay = new ArrayList<RowCriteria>();

	public List<Option> getColumnOptions() {
		return this.columnOptions;
	}

	public List<Option> getOrderOptions() {
		return this.orderOptions;
	}

	public String getColumn() {
		return this.column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrder() {
		return this.order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<RowCriteria> getConditionsDisplay() {
		return this.conditionsDisplay;
	}

	public void addConditionsDisplay(SliceColumn aColumn,
			String conditionString, String valueString) {
		this.conditionsDisplay.add(new RowCriteria(aColumn, conditionString,
				valueString));
	}

	@SuppressWarnings("nls")
	@Override
	protected String executeInner() throws Exception {
		super.executeInner();
		setTitle("orderRows.title"); //$NON-NLS-1$
		getPreviewData();

		// set empty strings passed in through form to null
		if (this.order != null && this.order.length() == 0)
			this.order = null;
		if (this.column != null && this.column.length() == 0)
			this.column = null;

		// build column order options

		this.columnOptions.add(new Option(null, null));
		for (SliceColumn col : this.tabularDataContainer.getColumns()) {
			this.columnOptions.add(new Option(col.getName(), col.getName()
					.concat(" - (" + col.getDataType() + ")"))); //$NON-NLS-1$

		}
		// Option.sort(this.columnOptions);
		if (this.column != null) {
			Option.setSelected(this.columnOptions, this.column);
		}

		// set up order options
		this.orderOptions.add(new Option(null, null));
		for (Condition rowCondition : Condition.values()) {
			if (rowCondition.getType().equals(ConditionType.ORDER)) {
				this.orderOptions.add(new Option(rowCondition.name(),
						getUIManager().getTxt(rowCondition.getKey())));
			}
		}

		return INPUT;
	}
}
