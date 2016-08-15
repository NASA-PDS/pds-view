package gov.nasa.pds.web.ui.containers;

import java.util.List;

/*
 * a wrapper class for name/value pairs intended to be associated with a
 * checkbox
 */

public class Checkbox implements BaseContainerInterface {

	protected Object value;
	protected Object label;
	protected boolean isChecked;

	public boolean isChecked() {
		return this.isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@SuppressWarnings("nls")
	public Checkbox(Object value, Object label) {
		this.value = value == null ? "" : value;
		this.label = label == null ? "" : label;
	}

	public Checkbox(Object value, Object label, boolean isChecked) {
		this(value, label);
		this.isChecked = isChecked;
	}

	public Object getLabel() {
		return this.label;
	}

	public String getLabelAsString() {
		return this.label.toString();
	}

	public Object getValue() {
		return this.value;
	}

	public static void setChecked(List<Checkbox> checkboxList, Object value) {
		if (value == null) {
			return;
		}
		for (Checkbox checkbox : checkboxList) {
			if (checkbox.value.equals(value)) {
				checkbox.setChecked(true);
				break;
			}
		}
	}

}
