package gov.nasa.pds.web.ui.containers.tabularData;

import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.FieldStatistics;
import gov.nasa.arc.pds.xml.generated.SpecialConstants;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.web.ui.containers.ColumnInfo;

import java.util.ArrayList;
import java.util.List;

public class Column {

	//Data elements in general
	private String name;
	private int index;
	private final List<Element> elements = new ArrayList<Element>();
	private final String description;
	private final Integer itemOffset;
	private final String missingConstant;
	private ObjectStatement columnDef;
	
	//Data elements for Column Data Objects
	private String dataType;
	private final Integer bytes;
	private final Integer startByte;
	private final String format;
	private final Integer numItems;
	private final Integer itemBytes;

	//Data elements for Bit column elements
	private final Integer bits;
	private final Integer startBit;
	private final Integer itemBits;
	
	//Data element for Field
	private final Integer fieldNumber;
	
	// PDS4 specific
	private final String unit;
	private final Double scalingFactor;
	private final Double valueOffset;
	private final SpecialConstants specialConstants;
	private final FieldStatistics fieldStatistics;
	private final FieldCharacter fieldCharacter;
	private final FieldDelimited fieldDelimited;
	private final FieldBinary fieldBinary;
	private final FieldBit fieldBit;
	private final Integer stopBit;

	public Column(final int index) {
		this.index = index;
		this.name = null;
		this.dataType = null;
		this.description = null;
		this.bytes = null;
		this.startByte = null;
		this.format = null;
		this.numItems = null; 
		this.itemBytes = null;
		this.missingConstant = null;
		this.itemOffset = null;
		this.bits = null;
		this.startBit = null;
		this.itemBits = null;
		this.fieldNumber = null;
		this.unit = null;
		this.scalingFactor = null;
		this.valueOffset = null;
		this.specialConstants = null;
		this.fieldStatistics = null;
		this.fieldCharacter = null;
		this.fieldDelimited = null;
		this.fieldBinary = null;
		this.fieldBit = null;
		this.stopBit = null;
	}

	public Column(final ColumnInfo columnInfo, final int currentSize) {
		this.columnDef = columnInfo.getColumnDef();
		this.index = columnInfo.getIndex() == null ? currentSize : columnInfo
				.getIndex();
		this.name = columnInfo.getName();
		this.dataType = columnInfo.getType();
		this.description = columnInfo.getDescription();
		this.bytes = columnInfo.getBytes();
		this.startByte = columnInfo.getStartByte();
		this.format = columnInfo.getFormat();
		this.numItems = columnInfo.getNumItems();
		this.itemBytes = columnInfo.getItemBytes();
		this.missingConstant = columnInfo.getMissingConstant();
		this.itemOffset = columnInfo.getItemOffset();
		this.bits = columnInfo.getBits();
		this.startBit = columnInfo.getStartBit();
		this.itemBits = columnInfo.getItemBits();
		this.fieldNumber = columnInfo.getFieldNumber();
		this.unit = columnInfo.getUnit();
		this.scalingFactor = columnInfo.getScalingFactor();
		this.valueOffset = columnInfo.getValueOffset();
		this.specialConstants = columnInfo.getSpecialConstants();
		this.fieldStatistics = columnInfo.getFieldStatistics();
		this.fieldCharacter = columnInfo.getFieldCharacter();
		this.fieldDelimited = columnInfo.getFieldDelimited();
		this.fieldBinary = columnInfo.getFieldBinary();
		this.fieldBit = columnInfo.getFieldBit();
		this.stopBit = columnInfo.getStopBit();
	}

	public Column() {
		this(0);
	}

	private Column(final Column column) {
		this.columnDef = column.getColumnDef();
		this.index = column.getIndex();
		this.name = column.getName();
		this.dataType = column.getDataType();
		this.description = column.getDescription();
		this.bytes = column.getBytes();
		this.startByte = column.getStartByte();
		this.format = column.getFormat();
		this.numItems = column.getNumItems();
		this.itemBytes = column.getItemBytes();
		this.missingConstant = column.getMissingConstant();
		this.itemOffset = column.getItemOffset();
		this.bits = column.getBits();
		this.startBit = column.getStartBit();
		this.itemBits = column.getItemBits();
		this.fieldNumber = column.getFieldNumber();
		this.unit = column.getUnit();
		this.scalingFactor = column.getScalingFactor();
		this.valueOffset = column.getValueOffset();
		this.specialConstants = column.getSpecialConstants();
		this.fieldStatistics = column.getFieldStatistics();
		this.fieldCharacter = column.getFieldCharacter();
		this.fieldDelimited = column.getFieldDelimited();
		this.fieldBinary = column.getFieldBinary();
		this.fieldBit = column.getFieldBit();
		this.stopBit = column.getStopBit();
	}

	@Override
	public Column clone() {
		return new Column(this);
	}

	protected void addElement(final Element element) {
		this.elements.add(element);
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	public String getDescription() {
		return this.description;
	}

	public List<Element> getElements() {
		return this.elements;
	}

	public Integer getBytes() {
		return this.bytes;
	}

	public Integer getStartByte() {
		return this.startByte;
	}

	public String getFormat() {
		return this.format;
	}

	public Integer getNumItems() {
		return this.numItems;
	}

	public Integer getItemBytes() {
		return this.itemBytes;
	}

	public Integer getItemOffset() {
		return this.itemOffset;
	}

	public String getMissingConstant() {
		return this.missingConstant;
	}
	
	public Integer getBits() {
		return this.bits;
	}
	
	public Integer getStartBit() {
		return this.startBit;
	}
	
	public Integer getItemBits() {
		return this.itemBits;
	}
	
	public Integer getFieldNumber() {
		return this.fieldNumber;
	}
	
	protected ObjectStatement getColumnDef() {
		return this.columnDef;
	}
	
	public String getUnit() {
		return this.unit;
	}

	public Double getScalingFactor() {
		return this.scalingFactor;
	}

	public Double getValueOffset() {
		return this.valueOffset;
	}

	public SpecialConstants getSpecialConstants() {
		return this.specialConstants;
	}

	public FieldStatistics getFieldStatistics() {
		return this.fieldStatistics;
	}

	public FieldCharacter getFieldCharacter() {
		return this.fieldCharacter;
	}
	
	public FieldDelimited getFieldDelimited() {
		return this.fieldDelimited;
	}

	public FieldBinary getFieldBinary(){
		return this.fieldBinary;
	}
	
	public FieldBit getFieldBit() {
		return this.fieldBit;
	}
	
	public Integer getStopBit() {
		return this.stopBit;
	}
}