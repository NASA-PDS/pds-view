package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;

import java.io.Serializable;

public class RowCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private SliceColumn column;

	private String condition;

	private String value;

	public SliceColumn getColumn() {
		return this.column;
	}

	public void setColumn(SliceColumn column) {
		this.column = column;
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

	public RowCriteria() {
		this.column = null;
		this.condition = null;
		this.value = null;
	}

	public RowCriteria(SliceColumn column, String condition, String value) {
		this.column = column;
		this.condition = condition;
		this.value = value;
	}

}
