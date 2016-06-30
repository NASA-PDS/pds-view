package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.constants.TabularManagementConstants.Condition;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants.ConditionType;
import gov.nasa.pds.web.ui.containers.Option;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.utils.TabularDataUtils;
import gov.nasa.pds.web.ui.utils.TabularPDS4DataUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * allows user to enter filters to select rows by value.
 * 
 * @author Laura Baalman
 * @version 2
 */

public class SelectRows extends Display {

	private static final long serialVersionUID = 1L;
	private List<Option> columnOptions = new ArrayList<Option>();
	private List<Option> conditionOptions = new ArrayList<Option>();
	private String selectedColumn;
	private String condition;
	private String value;
	private String queryMode; // and / or
	private List<RowCriteria> conditionsDisplay = new ArrayList<RowCriteria>();

	public List<Option> getColumnOptions() {
		return this.columnOptions;
	}

	public List<Option> getConditionOptions() {
		return this.conditionOptions;
	}

	public String getSelectedColumn() {
		return this.selectedColumn;
	}

	public void setSelectedColumn(String selectedColumn) {
		this.selectedColumn = selectedColumn;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

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

	@SuppressWarnings("nls")
	@Override
	protected String executeInner() throws Exception {
		super.executeInner();
		setTitle("selectRows.title"); //$NON-NLS-1$

		if (this.tabularDataContainer.getQueryMode() != null
				&& this.tabularDataContainer.getQueryMode().length() > 0) {
			this.queryMode = this.tabularDataContainer.getQueryMode();
		} else {
			this.queryMode = "SET IN SELECT ROW";
		}
		getPreviewData();

		// set empty strings passed in through form to null
		if (this.selectedColumn != null && this.selectedColumn.length() == 0)
			this.selectedColumn = null;
		if (this.condition != null && this.condition.length() == 0)
			this.condition = null;
		if (this.value != null && this.value.length() == 0)
			this.value = null;

		// build column condition options
		this.columnOptions.add(new Option(null, null));
		for (SliceColumn column : this.tabularDataContainer.getColumns()) {
			this.columnOptions.add(new Option(column.getName(), column
					.getName().concat(" - (" + column.getDataType() + ")"))); //$NON-NLS-2$

		}
		// LAB 09/26/09 was sorted alpha, now shows in orig label order
		// Option.sort(this.columnOptions);
		// TODO apply differentiation between selected columns and non-selected
		// in drop down
		// TODO display in view order (whatever user has selected)

		// if a selectedColumn has been set, use type to determine condition
		// options
		if (this.selectedColumn != null) {
			Option.setSelected(this.columnOptions, this.selectedColumn);
			// add blank option
			this.conditionOptions.add(new Option(null, null));
			for (Condition rowCondition : Condition.values()) {
				
				// PDS3 types
				if (this.tabularDataContainer.getPDSType().equalsIgnoreCase("pds3")) {
				
					if (TabularDataUtils
							.isColumnNumericType(this.tabularDataContainer
									.getColumn(this.selectedColumn))
									&& rowCondition.getType().equals(ConditionType.NUMERIC)) {
						this.conditionOptions.add(new Option(rowCondition.name(),
								getUIManager().getTxt(rowCondition.getKey())));
					}
					if (TabularDataUtils
							.isColumnStringType(this.tabularDataContainer
									.getColumn(this.selectedColumn))
									&& rowCondition.getType().equals(ConditionType.STRING)) {
						this.conditionOptions.add(new Option(rowCondition.name(),
								getUIManager().getTxt(rowCondition.getKey())));
					}
					if (TabularDataUtils
							.isColumnBooleanType(this.tabularDataContainer
									.getColumn(this.selectedColumn))
									&& rowCondition.getType().equals(ConditionType.BOOLEAN)) {
						this.conditionOptions.add(new Option(rowCondition.name(),
								getUIManager().getTxt(rowCondition.getKey())));
					}

					if (TabularDataUtils.isColumnDateTime(this.tabularDataContainer
							.getColumn(this.selectedColumn))
							&& rowCondition.getType().equals(
									ConditionType.DATE_TIME)) {
						this.conditionOptions.add(new Option(rowCondition.name(),
								getUIManager().getTxt(rowCondition.getKey())));
					}
				
					// PDS4 types
				} else if (this.tabularDataContainer.getPDSType().equalsIgnoreCase("pds4")) {

					if (TabularDataUtils
							.isColumnNumericType(this.tabularDataContainer
									.getColumn(this.selectedColumn))
									&& rowCondition.getType().equals(ConditionType.NUMERIC)) {
						this.conditionOptions.add(new Option(rowCondition.name(),
								getUIManager().getTxt(rowCondition.getKey())));
					}

					if (TabularPDS4DataUtils
							.isColumnStringType(this.tabularDataContainer
									.getColumn(this.selectedColumn))
									&& rowCondition.getType().equals(ConditionType.STRING)) {
						this.conditionOptions.add(new Option(rowCondition.name(),
								getUIManager().getTxt(rowCondition.getKey())));
					}

					if (TabularDataUtils
							.isColumnBooleanType(this.tabularDataContainer
									.getColumn(this.selectedColumn))
									&& rowCondition.getType().equals(ConditionType.BOOLEAN)) {
						this.conditionOptions.add(new Option(rowCondition.name(),
								getUIManager().getTxt(rowCondition.getKey())));
					}

					//DateTime for PDS4 are treated as strings for now
					//TODO: change this to DATE_TIME
					if (TabularPDS4DataUtils.isColumnDateTime(this.tabularDataContainer
							.getColumn(this.selectedColumn))
							&& rowCondition.getType().equals(
									ConditionType.STRING)) {
						this.conditionOptions.add(new Option(rowCondition.name(),
								getUIManager().getTxt(rowCondition.getKey())));
					}
				} else {
					// default case?
					// it should not reach this
				}
			}
		} else {

			this.conditionOptions.add(new Option(null, null));
			this.conditionOptions.add(new Option("", //$NON-NLS-1$
					getUIManager().getTxt("selectRows.option.selectColumn"))); //$NON-NLS-1$
		}

		if (this.condition != null) {
			Option.setSelected(this.conditionOptions, this.condition);
		}

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
