package gov.nasa.pds.web.ui.containers;

import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.FieldStatistics;
import gov.nasa.arc.pds.xml.generated.SpecialConstants;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.ObjectStatement;

import java.io.Serializable;

public class ColumnInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Common to PDS3 and PDS4 labels
	private String name;
	private final String type;
	private String description;
	private Integer index;
	private Integer bytes;
	private final String format;
	private final String missingConstant;

	// PDS3 specific
	private Integer startByte;
	private final Integer numItems;
	private final Integer itemBytes;
	private final Integer itemOffset;
	private final ObjectStatement columnDef;
	private final Integer bits;
	private final Integer startBit;
	private final Integer itemBits;
	private Integer fieldNumber;

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
	private Integer stopBit;
	
	/**
	 * Constructor used for PDS3 labels. All unused PDS4 fields are set to null
	 * 
	 * @param columnDef
	 */
	@SuppressWarnings("nls")
	public ColumnInfo(ObjectStatement columnDef) {
		
		// required
		final AttributeStatement nameAttrib = columnDef.getAttribute("NAME");
		this.name = nameAttrib.getValue().toString();

		// optional
		// Data type is treated the same for BIT_COLUMNS and COLUMNS since they
		// both share the same type
		final AttributeStatement bitTypeAttrib = columnDef
				.getAttribute("BIT_DATA_TYPE");
		final AttributeStatement typeAttrib = columnDef
				.getAttribute("DATA_TYPE");
		this.type = typeAttrib == null ? bitTypeAttrib.getValue().toString()
				: typeAttrib.getValue().toString();

		final AttributeStatement bytesAttrib = columnDef.getAttribute("BYTES");
		this.bytes = bytesAttrib == null ? null : Integer.valueOf(bytesAttrib
				.getValue().toString());

		final AttributeStatement descriptionAttrib = columnDef
				.getAttribute("DESCRIPTION");
		this.description = descriptionAttrib == null ? null : descriptionAttrib
				.getValue().toString();

		final AttributeStatement indexParam = columnDef
				.getAttribute("COLUMN_NUMBER");
		this.index = indexParam == null ? null : Integer.valueOf(indexParam
				.getValue().toString());

		final AttributeStatement formatAttrib = columnDef
				.getAttribute("FORMAT");
		this.format = formatAttrib == null ? null : formatAttrib.getValue()
				.toString();

		final AttributeStatement missingConstantAttrib = columnDef
				.getAttribute("MISSING_CONSTANT");
		this.missingConstant = missingConstantAttrib == null ? null
				: missingConstantAttrib.getValue().toString();

		this.columnDef = columnDef;

		final AttributeStatement bitsAttrib = columnDef.getAttribute("BITS");
		this.bits = bitsAttrib == null ? null : Integer.valueOf(bitsAttrib
				.getValue().toString());

		final AttributeStatement startBitAttrib = columnDef
				.getAttribute("START_BIT");
		this.startBit = startBitAttrib == null ? null : Integer
				.valueOf(startBitAttrib.getValue().toString());

		final AttributeStatement itemBitsAttrib = columnDef
				.getAttribute("ITEM_BITS");
		this.itemBits = itemBitsAttrib == null ? null : Integer
				.valueOf(itemBitsAttrib.getValue().toString());

		final AttributeStatement startByteAttrib = columnDef
				.getAttribute("START_BYTE");
		this.startByte = startByteAttrib == null ? null : Integer
				.valueOf(startByteAttrib.getValue().toString());

		final AttributeStatement numItemsAttrib = columnDef
				.getAttribute("ITEMS");
		this.numItems = numItemsAttrib == null ? null : Integer
				.valueOf(numItemsAttrib.getValue().toString());

		final AttributeStatement itemBytesAttrib = columnDef
				.getAttribute("ITEM_BYTES");
		this.itemBytes = itemBytesAttrib == null ? null : Integer
				.valueOf(itemBytesAttrib.getValue().toString());

		final AttributeStatement itemOffsetAttrib = columnDef
				.getAttribute("ITEM_OFFSET");
		this.itemOffset = itemOffsetAttrib == null ? null : Integer
				.valueOf(itemOffsetAttrib.getValue().toString());

		final AttributeStatement fieldNumberAttrib = columnDef
				.getAttribute("FIELD_NUMBER");
		this.fieldNumber = fieldNumberAttrib == null ? null : Integer
				.valueOf(fieldNumberAttrib.getValue().toString());

		// Unused PDS4 elements set to null
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
	
	/**
	 * Constructor for type FieldCharacter used by PDS4 labels. 
	 * All unused fields are set to null
	 * 
	 * @param fieldCharacter
	 */
	public ColumnInfo(FieldCharacter fieldCharacter) {

		// required
		this.fieldCharacter = fieldCharacter;
		this.name = fieldCharacter.getName();
		this.bytes = fieldCharacter.getFieldLength().getValue();
		this.type = fieldCharacter.getDataType();
		
		// optional
		this.index = fieldCharacter.getFieldNumber() == null ? null
				: fieldCharacter.getFieldNumber();
		
		this.description = fieldCharacter.getDescription() == null ? null
				: fieldCharacter.getDescription();
	
		this.format = fieldCharacter.getFieldFormat() == null ? null 
				: fieldCharacter.getFieldFormat();
		
		this.missingConstant = fieldCharacter.getSpecialConstants() == null ? null 
				: fieldCharacter.getSpecialConstants()
						.getMissingConstant();

		this.unit = fieldCharacter.getUnit() == null ? null : fieldCharacter
				.getUnit();

		this.scalingFactor = fieldCharacter.getScalingFactor() == null ? null
				: fieldCharacter.getScalingFactor();

		this.valueOffset = fieldCharacter.getValueOffset() == null ? null
				: fieldCharacter.getValueOffset();
		
		this.specialConstants = fieldCharacter.getSpecialConstants() == null ? null
				: fieldCharacter.getSpecialConstants();
		
		this.fieldStatistics = fieldCharacter.getFieldStatistics() == null ? null
				: fieldCharacter.getFieldStatistics();
		
		this.startByte = fieldCharacter.getFieldLocation() == null ? null
				: fieldCharacter.getFieldLocation().getValue();
		
		// unused
		this.numItems = null;
		this.itemBytes = null;
		this.itemOffset = null;
		this.columnDef = null;
		this.bits = null;
		this.startBit = null;
		this.stopBit = null;
		this.itemBits = null;
		this.fieldNumber = null;
		this.fieldDelimited = null;
		this.fieldBinary = null;
		this.fieldBit = null;
	}
	
	/**
	 * Constructor for type FieldDelimited used by PDS4 labels. 
	 * All unused fields are set to null
	 * 
	 * @param fieldDelimited
	 */
	public ColumnInfo(FieldDelimited fieldDelimited) {

		// required
		this.fieldDelimited = fieldDelimited;
		this.name = fieldDelimited.getName();
		this.type = fieldDelimited.getDataType();
		
		// optional
		this.bytes = fieldDelimited.getMaximumFieldLength() == null ? null
				: fieldDelimited.getMaximumFieldLength().getValue();
		
		this.index = fieldDelimited.getFieldNumber() == null ? null
				: fieldDelimited.getFieldNumber();
		
		this.description = fieldDelimited.getDescription() == null ? null
				: fieldDelimited.getDescription();
	
		this.format = fieldDelimited.getFieldFormat() == null ? null 
				: fieldDelimited.getFieldFormat();
		
		this.missingConstant = fieldDelimited.getSpecialConstants() == null ? null 
				: fieldDelimited.getSpecialConstants()
						.getMissingConstant();

		this.unit = fieldDelimited.getUnit() == null ? null : fieldDelimited
				.getUnit();

		this.scalingFactor = fieldDelimited.getScalingFactor() == null ? null
				: fieldDelimited.getScalingFactor();

		this.valueOffset = fieldDelimited.getValueOffset() == null ? null
				: fieldDelimited.getValueOffset();
		
		this.specialConstants = fieldDelimited.getSpecialConstants() == null ? null
				: fieldDelimited.getSpecialConstants();
		
		this.fieldStatistics = fieldDelimited.getFieldStatistics() == null ? null
				: fieldDelimited.getFieldStatistics();
		
		// unused
		this.startByte = null;
		this.numItems = null;
		this.itemBytes = null;
		this.itemOffset = null;
		this.columnDef = null;
		this.bits = null;
		this.startBit = null;
		this.stopBit = null;
		this.itemBits = null;
		this.fieldNumber = null;
		this.fieldCharacter = null;
		this.fieldBinary = null;
		this.fieldBit = null;
	}

	
	/**
	 * Constructor for type FieldBinary used by PDS4 labels. 
	 * All unused fields are set to null
	 * 
	 * @param fieldBinary
	 */
	public ColumnInfo(FieldBinary fieldBinary) {

		// required
		this.fieldBinary = fieldBinary;
		this.name = fieldBinary.getName();
		this.bytes = fieldBinary.getFieldLength().getValue();
		this.type = fieldBinary.getDataType();
		this.startByte = fieldBinary.getFieldLocation().getValue();
		
		// optional
		this.index = fieldBinary.getFieldNumber() == null ? null
				: fieldBinary.getFieldNumber();
		
		this.description = fieldBinary.getDescription() == null ? null
				: fieldBinary.getDescription();
	
		this.format = fieldBinary.getFieldFormat() == null ? null 
				: fieldBinary.getFieldFormat();
		
		this.missingConstant = fieldBinary.getSpecialConstants() == null ? null 
				: fieldBinary.getSpecialConstants()
						.getMissingConstant();

		this.unit = fieldBinary.getUnit() == null ? null : fieldBinary
				.getUnit();

		this.scalingFactor = fieldBinary.getScalingFactor() == null ? null
				: fieldBinary.getScalingFactor();

		this.valueOffset = fieldBinary.getValueOffset() == null ? null
				: fieldBinary.getValueOffset();
		
		this.specialConstants = fieldBinary.getSpecialConstants() == null ? null
				: fieldBinary.getSpecialConstants();
		
		this.fieldStatistics = fieldBinary.getFieldStatistics() == null ? null
				: fieldBinary.getFieldStatistics();
		
		// unused
		this.numItems = null;
		this.itemBytes = null;
		this.itemOffset = null;
		this.columnDef = null;
		this.bits = null;
		this.startBit = null;
		this.stopBit = null;
		this.itemBits = null;
		this.fieldNumber = null;
		this.fieldCharacter = null;
		this.fieldDelimited = null;
		this.fieldBit = null;
	}
	
	
	
	/**
	 * Constructor for type FieldBinary used by PDS4 labels. 
	 * All unused fields are set to null
	 * 
	 * @param fieldBinary
	 */
	public ColumnInfo(FieldBit fieldBit) {
		
		// required
		this.fieldBit = fieldBit;
		this.name = fieldBit.getName();
		this.startBit = fieldBit.getStartBit();
		this.stopBit = fieldBit.getStopBit();
		this.type = fieldBit.getDataType();//data_type
		
		// optional
		this.index = fieldBit.getFieldNumber() == null ? null
				: fieldBit.getFieldNumber();
		
		this.format = fieldBit.getFieldFormat() == null ? null
				: fieldBit.getFieldFormat();
		
		this.unit = fieldBit.getUnit() == null ? null
				: fieldBit.getUnit();
		
		this.scalingFactor = fieldBit.getScalingFactor() == null ? null
				: fieldBit.getScalingFactor();
		
		this.valueOffset = fieldBit.getValueOffset() == null ? null
				: fieldBit.getValueOffset();
	
		this.description = fieldBit.getDescription() == null ? null
				: fieldBit.getDescription();
		
		this.specialConstants = fieldBit.getSpecialConstants() == null ? null
				: fieldBit.getSpecialConstants();
		
		this.fieldNumber = fieldBit.getFieldNumber() == null ? null
				: fieldBit.getFieldNumber();
		
		this.missingConstant = fieldBit.getSpecialConstants() == null ? null 
				: fieldBit.getSpecialConstants()
						.getMissingConstant();
		
		// unused
		this.numItems = null;
		this.itemBytes = null;
		this.itemOffset = null;
		this.columnDef = null;
		this.bits = null;
		this.itemBits = null;
		this.fieldCharacter = null;
		this.fieldDelimited = null;
		this.fieldBinary = null;
		this.fieldStatistics = null;
		
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getIndex() {
		return this.index;
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

	public ObjectStatement getColumnDef() {
		return this.columnDef;
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
	
	public FieldBit getFieldBit() {
		return this.fieldBit;
	}
	
	public Integer getStopBit() {
		return this.stopBit;
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
	
	 public void setName(String newName) {
		 this.name = newName;
	}
	    
	public void appendName(String nameToAppend) {
		this.name = this.name + nameToAppend;   
	}
	
	public void setStartByte(Integer newStartByte) {   
		this.startByte = newStartByte;
	}
	
	public void setBytes(Integer newBytes) {
		this.bytes = newBytes;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setIndex(Integer index) {
		this.index = index;
	}
	    
	public void setStopBit(int stopBit) {
		this.stopBit = stopBit;
	}
	
	public void setFieldNumber(Integer fieldNumber) {
		this.fieldNumber = fieldNumber;
	}
}
