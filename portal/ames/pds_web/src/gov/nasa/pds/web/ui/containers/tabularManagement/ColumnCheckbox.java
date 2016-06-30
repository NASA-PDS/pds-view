package gov.nasa.pds.web.ui.containers.tabularManagement;

import gov.nasa.pds.web.ui.containers.Checkbox;

public class ColumnCheckbox extends Checkbox {
	private String datatype;
	private String description;

	// TODO LAB 08/06/09 add unit, maybe others

	public String getDatatype() {
		return this.datatype;
	}

	public String getDescription() {
		return this.description;
	}

	public ColumnCheckbox(Object value, Object label, boolean isChecked,
			String datatype, String description) {
		super(value, label, isChecked);
		this.datatype = datatype;
		this.description = description;

	}
}