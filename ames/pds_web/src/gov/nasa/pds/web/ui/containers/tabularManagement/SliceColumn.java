package gov.nasa.pds.web.ui.containers.tabularManagement;

import gov.nasa.pds.web.ui.containers.ColumnInfo;
import gov.nasa.pds.web.ui.containers.tabularData.Column;

import java.io.Serializable;

public class SliceColumn extends Column implements Serializable {

	private static final long serialVersionUID = 1L;

	// might not be used
	private int columnId;

	private boolean isSelected;

	private Integer orderIndex;

	private String dateFormat;

	private boolean isSciNotation;
	
	private int printColumnWidth;

	private boolean isNumber;
		
	public int getPrintColumnWidth() {
		return printColumnWidth;
	}

	public void setPrintColumnWidth(int printColumnWidth) {
		this.printColumnWidth = printColumnWidth;
	}

	public boolean isNumber() {
		return isNumber;
	}

	public void setNumber(boolean isNumber) {
		this.isNumber = isNumber;
	}

	public int getColumnId() {
		return this.columnId;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public Integer getOrderIndex() {
		return this.orderIndex;
	}

	public String getDateFormat() {
		return this.dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean getIsSciNotation() {
		return this.isSciNotation;
	}

	public boolean isSciNotation() {
		return this.isSciNotation;
	}

	public void setIsSciNotation(boolean isSciNotation) {
		this.isSciNotation = isSciNotation;
	}

	public SliceColumn() {
		super();
	}

	public SliceColumn(ColumnInfo columnInfo, int index, boolean isSelected,
			int orderIndex) {
		super(columnInfo, index);
		this.isSelected = isSelected;
		this.columnId = columnInfo.getName().hashCode();
		this.orderIndex = orderIndex;
	}
}
