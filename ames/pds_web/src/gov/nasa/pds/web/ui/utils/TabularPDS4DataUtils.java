package gov.nasa.pds.web.ui.utils;

import gov.nasa.arc.pds.tools.util.URLUtils;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.FieldLength;
import gov.nasa.arc.pds.xml.generated.FieldLocation;
import gov.nasa.arc.pds.xml.generated.FieldStatistics;
import gov.nasa.arc.pds.xml.generated.MaximumFieldLength;
import gov.nasa.arc.pds.xml.generated.PackedDataFields;
import gov.nasa.arc.pds.xml.generated.SpecialConstants;
import gov.nasa.pds.web.ui.actions.tabularManagement.RowCriteria;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants.Condition;
import gov.nasa.pds.web.ui.containers.tabularData.Column;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabularPDS4DataUtils {
	
	// pattern to extract field_format attribute
	private final static Pattern pattern = Pattern
			.compile("%*(\\+|\\-)*%*(\\d+)(.\\d)*([dDoOxXfFeEsS]*)");

	/**
	 * Is the data type an ASCII boolean
	 * 
	 * @param dataType - the type to check
	 * @return true if the data type is the expected type.
	 * 			false otherwise.
	 */
	public static boolean isASCIIBoolean(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Boolean"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type a numeric type, binary or otherwise
	 * 
	 * @param column
	 *            the column to check data type of
	 * 
	 * @return is the column the expected type
	 */
	public static boolean isColumnNumericType(Column column) {
		if (isASCIIIntegerType(column.getDataType())) 
			return true;
		if (isASCIIRealType(column.getDataType()))
			return true;
		if (isBinaryInteger(column.getDataType()))
			return true;
		if (isRealType(column.getDataType()))
			return true;
		if (isComplexType(column.getDataType()))
			return true;
		return false;
	}
	
	/**
	 * Is the column a string
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the column the expected type
	 */
	public static boolean isColumnStringType(Column column) {
		if (isStringType(column.getDataType()))
			return true;
		if (isASCIINumericBaseNType(column.getDataType()))
			return true;
		if (isMD5ChecksumType(column.getDataType()))
			return true;
		if (isBitStringType(column.getDataType()))
			return true;
		return false;

	}
	
	/**
	 * Is the column a date time
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the column the expected type
	 */
	public static boolean isColumnDateTime(Column column) {
		if (isDateTimeType(column.getDataType())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Is the column data type a boolean
	 * 
	 * @param column
	 *            the column to check the type of
	 * 
	 * @return is the column the expected type
	 */
	public static boolean isColumnBooleanType(Column column) {
		if (isASCIIBoolean(column.getDataType())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Is the data type a date time
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isDateTimeType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Date")
				|| dataType.equalsIgnoreCase("ASCII_Date_DOY")
				|| dataType.equalsIgnoreCase("ASCII_Date_YMD"))
			return true;
		
		if (dataType.equalsIgnoreCase("ASCII_Date_Time") 
				|| dataType.equalsIgnoreCase("ASCII_Date_Time_DOY")
				|| dataType.equalsIgnoreCase("ASCII_Date_Time_UTC")
				|| dataType.equalsIgnoreCase("ASCII_Date_Time_YMD")
				|| dataType.equalsIgnoreCase("ASCII_Time"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type a date
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isDateType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Date")
				|| dataType.equalsIgnoreCase("ASCII_Date_DOY")
				|| dataType.equalsIgnoreCase("ASCII_Date_YMD"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type a time
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isTimeType(String dataType) {
		
		if (dataType.equalsIgnoreCase("ASCII_Date_Time") 
				|| dataType.equalsIgnoreCase("ASCII_Date_Time_DOY")
				|| dataType.equalsIgnoreCase("ASCII_Date_Time_UTC")
				|| dataType.equalsIgnoreCase("ASCII_Date_Time_YMD")
				|| dataType.equalsIgnoreCase("ASCII_Time"))
			return true;
		return false;
	}
		
	
	/**
	 * Is the data type an ASCII Integer (signed)
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIISignedIntegerType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Integer"))
			return true;
		return false;
	}
		
	
	/**
	 * Is the data type an ASCII non-negative (unsigned) Integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIIUnsignedIntegerType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_NonNegative_Integer"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an ASCII Integer (signed or unsigned)
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIIIntegerType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_NonNegative_Integer"))
			return true;
		if (dataType.equalsIgnoreCase("ASCII_Integer"))
			return true;
		return false;
	}	
	
	/**
	 * Is the data type an ASCII Real
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIIRealType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Real"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an ASCII numeric base 2
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIINumericBase2Type(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Numeric_Base2"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type an ASCII numeric base 8
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIINumericBase8Type(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Numeric_Base8"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an ASCII numeric base 16
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIINumericBase16Type(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Numeric_Base16"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type an ASCII numeric base N (2, 8, or 16)
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIINumericBaseNType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_Numeric_Base16"))
			return true;
		if (dataType.equalsIgnoreCase("ASCII_Numeric_Base8"))
			return true;
		if (dataType.equalsIgnoreCase("ASCII_Numeric_Base2"))
			return true;
		return false;
	}

	
	/**
	 * Is the data type an ASCII MD5 Checksum
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isMD5ChecksumType(String dataType) {
		if (dataType.equalsIgnoreCase("MD5_Checksum"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type a string type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isStringType(String dataType) {
		// URI
		if (dataType.equalsIgnoreCase("ASCII_AnyURI"))
			return true;
		
		// Identifiers
		if (dataType.equalsIgnoreCase("ASCII_DOI")
				|| dataType.equalsIgnoreCase("ASCII_LID")
				|| dataType.equalsIgnoreCase("ASCII_LIDVID")
				|| dataType.equalsIgnoreCase("ASCII_LIDVID_LID")
				|| dataType.equalsIgnoreCase("ASCII_VID"))
			return true;
		
		// All other string text
		if (dataType.equalsIgnoreCase("ASCII_Directory_Path_Name")
				|| dataType.equalsIgnoreCase("ASCII_File_Name")
				|| dataType.equalsIgnoreCase("ASCII_File_Specification_Name")
				|| dataType.equalsIgnoreCase("ASCII_Short_String_Collapsed")
				|| dataType.equalsIgnoreCase("ASCII_Short_String_Preserved")
				|| dataType.equalsIgnoreCase("ASCII_Text_Collapsed")
				|| dataType.equalsIgnoreCase("ASCII_Text_Preserved")
				|| dataType.equalsIgnoreCase("UTF8_Short_String_Collapsed")
				|| dataType.equalsIgnoreCase("UTF8_Short_String_Preserved")
				|| dataType.equalsIgnoreCase("UTF8_Text_Collapsed")
				|| dataType.equalsIgnoreCase("UTF8_Text_Preserved")
				)
			return true;
		
		// string text specified as character data type
		if (dataType.equalsIgnoreCase("ASCII_String")
				|| dataType.equalsIgnoreCase("UTF8_String"))
			return true;
		
		return false;
	}
	
	/**
	 * Is the data type an LSB signed integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isLSBSignedInteger(String dataType) {

		if (dataType.equalsIgnoreCase("SignedLSB8"))
			return true;
		if (dataType.equalsIgnoreCase("SignedLSB4"))
			return true;
		if (dataType.equalsIgnoreCase("SignedLSB2"))
			return true;
		if (dataType.equalsIgnoreCase("SignedByte"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type a Signed Byte
	 * 
	 * @param dataType
	 * @return
	 */
	public static boolean isSignedByte(String dataType) {
		if (dataType.equalsIgnoreCase("SignedByte"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type an LSB unsigned integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isLSBUnsignedInteger(String dataType) {
		if (dataType.equalsIgnoreCase("UnsignedLSB8"))
			return true;
		if (dataType.equalsIgnoreCase("UnsignedLSB4"))
			return true;
		if (dataType.equalsIgnoreCase("UnsignedLSB2"))
			return true;
		if (dataType.equalsIgnoreCase("UnsignedByte"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an MSB signed integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isMSBSignedInteger(String dataType) {
		if (dataType.equalsIgnoreCase("SignedMSB8"))
			return true;
		if (dataType.equalsIgnoreCase("SignedMSB4"))
			return true;
		if (dataType.equalsIgnoreCase("SignedMSB2"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an MSB unsigned integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isMSBUnsignedInteger(String dataType) {
		if (dataType.equalsIgnoreCase("UnsignedMSB8"))
			return true;
		if (dataType.equalsIgnoreCase("UnsignedMSB4"))
			return true;
		if (dataType.equalsIgnoreCase("UnsignedMSB2"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an unsigned byte
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isUnsignedByte(String dataType) {
		if(dataType.equalsIgnoreCase("UnsignedByte"))
			return true;
		return false;
	}
	
	
	public static boolean isUnsignedInteger(String dataType) {

		if (       dataType.equalsIgnoreCase("UnsignedMSB8")
				|| dataType.equalsIgnoreCase("UnsignedMSB4")
				|| dataType.equalsIgnoreCase("UnsignedMSB2")
				|| dataType.equalsIgnoreCase("UnsignedLSB8")
				|| dataType.equalsIgnoreCase("UnsignedLSB4")
				|| dataType.equalsIgnoreCase("UnsignedLSB2")
				|| dataType.equalsIgnoreCase("UnsignedByte"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an integer type
	 * 
	 * @param dataType
	 * 			 the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isBinaryInteger(String dataType) {
		
		if (dataType.equalsIgnoreCase("UnsignedMSB8")
				||dataType.equalsIgnoreCase("UnsignedMSB4")
				||dataType.equalsIgnoreCase("UnsignedMSB2"))
			return true;
		
		if (dataType.equalsIgnoreCase("UnsignedLSB8")
				||dataType.equalsIgnoreCase("UnsignedLSB4")
				||dataType.equalsIgnoreCase("UnsignedLSB2")
				||dataType.equalsIgnoreCase("UnsignedByte"))
			return true;
	
		if (dataType.equalsIgnoreCase("SignedMSB8")
				|| dataType.equalsIgnoreCase("SignedMSB4")
				|| dataType.equalsIgnoreCase("SignedMSB2"))
			return true;

		if (dataType.equalsIgnoreCase("SignedLSB8")
				|| dataType.equalsIgnoreCase("SignedLSB4")
				|| dataType.equalsIgnoreCase("SignedLSB2")
				|| dataType.equalsIgnoreCase("SignedByte"))
			return true;
	
		return false;
	}
	
	/**
	 * Is the data type an 1 byte integer type
	 * 
	 * @param dataType
	 * 			 the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean is1ByteBinaryInteger(String dataType) {
		if (dataType.equalsIgnoreCase("SignedByte")
				|| dataType.equalsIgnoreCase("UnsignedByte"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an 2 byte integer type
	 * 
	 * @param dataType
	 * 			 the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean is2ByteBinaryInteger(String dataType) {
		if (dataType.equalsIgnoreCase("UnsignedMSB2")
				||dataType.equalsIgnoreCase("UnsignedLSB2")
				|| dataType.equalsIgnoreCase("SignedMSB2")
				|| dataType.equalsIgnoreCase("SignedLSB2"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an 4 byte integer type
	 * 
	 * @param dataType
	 * 			 the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean is4ByteBinaryInteger(String dataType) {
		if (dataType.equalsIgnoreCase("UnsignedMSB4")
				||dataType.equalsIgnoreCase("UnsignedLSB4")
				|| dataType.equalsIgnoreCase("SignedMSB4")
				|| dataType.equalsIgnoreCase("SignedLSB4"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an 8 byte integer type
	 * 
	 * @param dataType
	 * 			 the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean is8ByteBinaryInteger(String dataType) {
		if (dataType.equalsIgnoreCase("UnsignedMSB8")
				||dataType.equalsIgnoreCase("UnsignedLSB8")
				|| dataType.equalsIgnoreCase("SignedMSB8")
				|| dataType.equalsIgnoreCase("SignedLSB8"))
			return true;
		return false;
	}
	
	public static boolean is8ByteUnsignedMSBInteger(String dataType) {
		if (dataType.equalsIgnoreCase("UnsignedMSB8"))
				return true;
		return false;
	}
	
	
	/**
	 * Is the data type an LSB Real
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isLSBRealType(String dataType) {
		if (dataType.equalsIgnoreCase("IEEE754LSBDouble"))
			return true;
		if (dataType.equalsIgnoreCase("IEEE754LSBSingle"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type an MSB Real
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isMSBRealType(String dataType) {
		if (dataType.equalsIgnoreCase("IEEE754MSBDouble"))
			return true;
		if (dataType.equalsIgnoreCase("IEEE754MSBSingle"))
			return true;
		return false;

	}	
	
	/**
	 * Is the data type a Real, MSB or LSB
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isRealType(String dataType) {
		if (dataType.equalsIgnoreCase("IEEE754LSBDouble")
				|| dataType.equalsIgnoreCase("IEEE754LSBSingle")
				|| dataType.equalsIgnoreCase("IEEE754MSBDouble")
				|| dataType.equalsIgnoreCase("IEEE754MSBSingle"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type a single real
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isSingleRealType(String dataType) {
		if (dataType.equalsIgnoreCase("IEEE754MSBSingle")
				||dataType.equalsIgnoreCase("IEEE754LSBSingle"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type a double real
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isDoubleRealType(String dataType) {
		if (dataType.equalsIgnoreCase("IEEE754MSBDouble")
				||dataType.equalsIgnoreCase("IEEE754LSBDouble"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type a LSB complex type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isLSBComplexType(String dataType) {
		if (dataType.equalsIgnoreCase("ComplexLSB16"))
			return true;
		if (dataType.equalsIgnoreCase("ComplexLSB8"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type a MSB complex type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isMSBComplexType(String dataType) {
		if (dataType.equalsIgnoreCase("ComplexMSB16"))
			return true;
		if (dataType.equalsIgnoreCase("ComplexMSB8"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type a MSB 8-byte complex type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isComplex8ByteType(String dataType) {
		if (dataType.equalsIgnoreCase("ComplexLSB8"))
			return true;
		if (dataType.equalsIgnoreCase("ComplexMSB8"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type a MSB 16-byte complex type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isComplex16ByteType(String dataType) {
		if (dataType.equalsIgnoreCase("ComplexLSB16"))
			return true;
		if (dataType.equalsIgnoreCase("ComplexMSB16"))
			return true;
		return false;
	}	
	
	/**
	 * Is the data type a complex type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isComplexType(String dataType) {
		if (dataType.equalsIgnoreCase("ComplexMSB16"))
			return true;
		if (dataType.equalsIgnoreCase("ComplexMSB8"))
			return true;
		if (dataType.equalsIgnoreCase("ComplexLSB16"))
			return true;
		if (dataType.equalsIgnoreCase("ComplexLSB8"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type a Bit String type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isBitStringType(String dataType) {
		if (dataType.equalsIgnoreCase("SignedBitString"))
			return true;
		if (dataType.equalsIgnoreCase("UnsignedBitString"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type null
	 * 
	 * @param dataType
	 * 			the type to check
	 * 
	 * @return is the data type null
	 */
	public static boolean isNull(String dataType) {
		
		if (dataType == null)
			return true;
		return false;
	}
	
	/**
	 * Is the data type a signed Bit String type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isSignedBitStringType(String dataType) {
		if (dataType.equalsIgnoreCase("SignedBitString"))
			return true;
		return false;
	}

	
	/**
	 * Is the data type an unsigned Bit String type
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isUnsignedBitStringType(String dataType) {
		if (dataType.equalsIgnoreCase("UnsignedBitString"))
			return true;
		return false;
	}
	
	
	/**
	 * Is the data type a ASII table
	 * 
	 * @param dataType
	 * 			the type to check
	 * 
	 * @return true, if the data type is the expected type
	 */
	public static boolean isASCIITable(String dataType) {
		
		if(dataType.equalsIgnoreCase("CHAR"))
			return true;
		if(dataType.equalsIgnoreCase("DELIM"))
			return true;
		return false;
	}
	
	/**
	 * Is the data type Table Delimited
	 * 
	 * @param dataType
	 * 			the type to check
	 * 
	 * @return true, if the data type is the expected type
	 */
	public static boolean isTableTypeDelimited(String dataType) {
		if(dataType.equalsIgnoreCase("DELIM"))
			return true;
		return false;
	}
	
	
	
	/**
	 * Is the data type a binary table
	 * 
	 * @param dataType
	 * 			the type to check
	 * 
	 * @return true, if the data type is the expected type
	 */
	public static boolean isBinaryTable(String dataType) {
		if(dataType.equalsIgnoreCase("BINARY"))
			return true;
		return false;
	}
	

	/**
	 * 
	 * @param delimeterType
	 * 				the type to check
	 * @return 
	 */
	public static String getDelimeterASCIIFormat(String delimiterType) {
		
		if(delimiterType == null)
			return null;
			
		if(delimiterType.equalsIgnoreCase("comma"))
			return ",";
		else if(delimiterType.equalsIgnoreCase("horizontal tab"))
			return "\t";
		else if(delimiterType.equalsIgnoreCase("semicolon"))
			return ";";
		else if(delimiterType.equalsIgnoreCase("vertical bar"))
			return "|";
		else
			return null;
	}
	
	public static String getRecordDelimiterACIIFormat(String delimiterName) {
		
		if(delimiterName.equalsIgnoreCase("Carriage-Return Line-Feed"))
			return "\n";
		
		return null;
	}
	
	/**
	 * Build a query to retrieve table data from the db
	 * 
	 * @param table
	 *            table config data
	 * @param countOnly
	 *            flag to indicate whether to just get a count or all data
	 * 
	 * @return the query string
	 */
	@SuppressWarnings({ "nls" })
	public static String buildSqlQuery(TabularDataContainer table,
			boolean countOnly) {
		StringBuilder queryString = new StringBuilder("SELECT ");
		StringBuilder whereClause = new StringBuilder();
		StringBuilder orderByClause = new StringBuilder();

		if (countOnly) {
			queryString.append("COUNT(row_id)");
		} else {
			// add column name in correct order to the query string
			// create list to hold all column names
			ArrayList<String> columnHolder = new ArrayList<String>();
			for (Iterator<?> it = table.getColumns().iterator(); it.hasNext();) {
				SliceColumn column = (SliceColumn) it.next();
				
				if(column.getDataType() != null) {
					
					//Check for LSB, MSB, VAX bit string types.
					if(!TabularDataUtils.isBinaryBitStringType(column.getDataType()))
						columnHolder.add(column.getName());
				}else{
					columnHolder.add(column.getName());	
				}
			}
					
			// create list to be rearranged according to order index
			ArrayList<String> columnArray = new ArrayList<String>(columnHolder);
			
			for (String column : columnHolder) {
				int orderIndex = table.getColumn(column).getOrderIndex();
				columnArray.set(orderIndex, column);
			}
			
			for (Iterator<?> it = columnArray.iterator(); it.hasNext();) {
				String columnName = (String) it.next();
				if (table.getColumn(columnName).isSelected()) {
					queryString.append("`" + columnName + "`");

					if (it.hasNext())
						queryString.append(", ");
				}
			}
			
			// if comma is at end of line, remove it two because of space after
			// comma
			if (queryString.lastIndexOf(",") == queryString.length() - 2) {
				queryString.deleteCharAt(queryString.length() - 2);
			}
		}
		
		// FROM CLAUSE
		queryString.append(" FROM " + table.getTableName() + " ");

		// Create where clause using conditions
		for (RowCriteria criteria : table.getConditions()) {
			String sql = null;
			// loop through condition enum to get type and mysql string
			for (Condition condition : Condition.values()) {
				if (criteria.getCondition().equalsIgnoreCase(condition.name())) {
					// set condition.mySqlString() to a string to be used next
					sql = condition.getMySqlPattern();
				}
			}

			// add to the where clause
			// if not the first addition, add AND/OR to separate
			if (whereClause.length() > 0) {
				whereClause.append(" " + table.getQueryMode() + " ");
			}
			if (sql != null) {
				// append criteria column
				whereClause.append(sql.replaceAll("column",
						criteria.getColumn().getName()).replaceAll("value",
						criteria.getValue()));
			}

		}
		// Create order by clause
		for (RowCriteria criteria : table.getSorts()) {
			String sql = null;
			// loop through condition enum to get type and mysql string
			for (Condition condition : Condition.values()) {
				if (criteria.getCondition().equalsIgnoreCase(condition.name())) {
					// set condition.mySqlString() to a string to be used next
					sql = condition.getMySqlPattern();
				}
			}

			// if not the first addition, add comma to separate
			if (orderByClause.length() > 0) {
				orderByClause.append(", ");
			}

			if (sql != null) {
				// add sort order column and direction
				orderByClause.append(sql.replaceAll("column",
						criteria.getColumn().getName()).replaceAll("value",
						criteria.getValue()));
			}

		}

		if (whereClause.length() > 0) {
			whereClause.insert(0, " WHERE ");
		}
		if (orderByClause.length() > 0) {
			orderByClause.insert(0, " ORDER BY ");
		}

		return queryString.toString() + whereClause.toString()
				+ orderByClause.toString();
	}

	/**
	 * Get the number of results returned from a table query
	 * 
	 * @param tabularDataContainer
	 *            the table config info
	 * 
	 * @return the count
	 */
	public static int countResultsReturned(
			TabularDataContainer tabularDataContainer) {
		// return count
		Connection connection = DBManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(TabularDataUtils.buildSqlQuery(
					tabularDataContainer, true));
			if (rs.next()) {
				int returnVal = rs.getInt(1);
				connection.close();
				return returnVal;
			}
			connection.close();
			return 0;

		} catch (SQLException sqle) {
			throw new RuntimeException("SQL Exception: " + sqle.getMessage()); //$NON-NLS-1$
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();

			} catch (SQLException ignored) {
				// do nothing
			}
		}

	}

	/**
	 * Get a portion of tabular data for preview.
	 * 
	 * @param table
	 *            the table config data
	 */
	public static ResultSet retrievePreview(TabularDataContainer table) {
		// return count
		Connection connection = DBManager.getConnection();
		Statement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = connection.createStatement();

			resultSet = stmt.executeQuery(TabularPDS4DataUtils.buildSqlQuery(table,
					false));
			// + " LIMIT 20");
			return resultSet;

		} catch (SQLException sqle) {
			throw new RuntimeException("SQL Exception: " + sqle.getMessage()); //$NON-NLS-1$
		}

	}

	
	/**
	 * Get the url of a file at the same level of a given url
	 * 
	 * NOTE: context and the file must be in the same directory
	 * 
	 * @param url
	 *            - base url
	 * @param file
	 *            if file to be made into a url
	 * 
	 * @return the found url
	 */
	// LAB 09/23/09 only used by TabularDataLoader.buildTabularContainer(), so
	// perhaps should be moved to that class?
	public static URL getURLofSameLevelFile(URL url, File file)
			throws MalformedURLException {
		String fileName = file.getPath();
		fileName = fileName
				.substring(fileName.lastIndexOf(File.separatorChar) + 1);
		URL tabFileUrl = new URL(
				url.getProtocol(),
				url.getHost(),
				-1,
				url
						.getPath()
						.substring(0, url.getPath().lastIndexOf("/") + 1).concat(fileName)); //$NON-NLS-1$
		// test url can be reached, if not swap case of file name
		if (!URLUtils.exists(tabFileUrl)) {
			Character character = fileName.toString().charAt(0);
			if (Character.isLowerCase(character)) {
				tabFileUrl = new URL(
						url.getProtocol(),
						url.getHost(),
						url
								.getPath()
								.substring(0,
										url.getPath().lastIndexOf("/") + 1)); //$NON-NLS-1$
			} else {

				tabFileUrl = new URL(
						url.getProtocol(),
						url.getHost(),
						url
								.getPath()
								.substring(0,
										url.getPath().lastIndexOf("/") + 1)); //$NON-NLS-1$

			}

		}
		return tabFileUrl;
	}
	
	
	
	/**
	 * Find structure file, if it exists, for a given target location.
	 * 
	 * Looks up each level in search of structure file in a LABEL folder.
	 * 
	 * @param fileUrl
	 *            url for the location of the format file to try
	 */
	public static URL findStructureFile(URL fileUrl) {
		if (URLUtils.exists(fileUrl)) {
			return fileUrl;
		}
		List<String> list = new ArrayList<String>();
		for (String folder : fileUrl.getPath().split("/")) { //$NON-NLS-1$
			list.add(folder);
		}
		// remove empty value at beginning
		list.remove(0);

		String label = "LABEL"; //$NON-NLS-1$
		String labelLC = "label"; //$NON-NLS-1$
		for (int i = 0; i <= list.size(); i++) {
			// remove second to last item (last folder)
			list.remove(list.size() - 2);
			// add "label" before last element
			list.add(list.size() - 1, label);

			URL structureUrl;
			try {
				StringBuffer buffer = new StringBuffer();
				for (String string : list) {
					buffer.append("/"); //$NON-NLS-1$
					buffer = buffer.append(string);
				}
				// build new url based on fileUrl but with new path
				structureUrl = new URL(fileUrl.getProtocol(),
						fileUrl.getHost(), fileUrl.getPort(), buffer.toString());
				if (URLUtils.exists(structureUrl)) {
					return structureUrl;
				}

				// try lower case filename

				File lcFile = new File(list.get(list.size() - 1).toLowerCase());
				if (URLUtils
						.exists(getURLofSameLevelFile(structureUrl, lcFile))) {
					return getURLofSameLevelFile(structureUrl, lcFile);
				}

				// try lower case LABEL

				list.remove(list.size() - 2);
				list.add(list.size() - 1, labelLC);

				// clean out the entire buffer
				buffer.delete(0, buffer.length());
				// refill with new values
				for (String string : list) {
					buffer.append("/"); //$NON-NLS-1$
					buffer = buffer.append(string);
				}
				// build new url based on fileUrl but with new path
				structureUrl = new URL(fileUrl.getProtocol(),
						fileUrl.getHost(), fileUrl.getPort(), buffer.toString());
				if (URLUtils.exists(structureUrl)) {
					return structureUrl;
				}

				// try lower case filename
				lcFile = new File(list.get(list.size() - 1).toLowerCase());
				if (URLUtils
						.exists(getURLofSameLevelFile(structureUrl, lcFile))) {
					return getURLofSameLevelFile(structureUrl, lcFile);
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			// remove label folder
			list.remove(list.size() - 2);

		}

		return null;

	}
	
	
	
	/**
	 * builds tablename - prefixed with
	 * TabularManagementConstants.TABULARDATA_TABLE_PREFIX followed by up to
	 * first 5 letters of sessionId
	 * 
	 * @param sessionId
	 *            the session id to be used in creation of table name
	 * 
	 * @return String the table name
	 */
	public static String getTabDataTablePrefix(String sessionId) {
		String prefix = (sessionId.length() > 5) ? TabularManagementConstants.TABULARDATA_TABLE_PREFIX
				.concat(sessionId.substring(0, 4))
				: TabularManagementConstants.TABULARDATA_TABLE_PREFIX
						.concat(sessionId);
		return prefix;
	}

	/**
	 * Get the name of a data table being used for the given process.
	 * 
	 * @param sessionId
	 *            the current session id
	 * @param tabularDataContainerId
	 *            the current tabular data container id
	 * 
	 * @return the table name in the db
	 */
	@SuppressWarnings("nls")
	public static String getTabDataTableName(String sessionId,
			String tabularDataContainerId) {
		String tableString = (tabularDataContainerId.length() > 5) ? tabularDataContainerId
				.replaceAll("-", "").substring(0, 4)
				: tabularDataContainerId.replaceAll("-", "");
		String tableName = TabularDataUtils.getTabDataTablePrefix(sessionId)
				+ tableString;
		return tableName;
	}

	/**
	 * Extracts the contents of the field format and returns a string
	 * with the format according to the type
	 * 
	 * @param format
	 * @param type
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static String parseFieldFormat(String format, String type, ResultSet rs)
			throws SQLException {

		String sign = null;
		String width = null;
		String precision = null;
		String specifier = null;
		String createTableString = null;

		if (type.equalsIgnoreCase("ASCII_Real")) {

			Matcher matcher = pattern.matcher(format);

			while (matcher.find()) {
				sign = matcher.group(1);
				width = matcher.group(2);
				precision = matcher.group(3);
				precision = precision.replace(".", "");//remove the period
				specifier = matcher.group(4);
			}

			// floating point
			if(specifier.equalsIgnoreCase("f")) {
				createTableString = "DECIMAL(" + rs.getInt("bytes") + ","
						+ precision + ")";
				
				// exponential form. Keep all the information and
				// format it correctly before display
			} else if(specifier.equalsIgnoreCase("e")) {
				createTableString = "DECIMAL(65, 30)";
			} else {
				createTableString = "DECIMAL(65, 30)";
			}
		}
		
		return createTableString;
	}
	
	/**
	 * Extract the formulation rules
	 * 
	 * @param format
	 * @return
	 * 		formulationRules[0] = sign
	 * 		formulationRules[1] = width
	 * 		formulationRules[2] = precision
	 * 		formulationRules[3] = specifier
	 * 	
	 * @throws SQLException
	 */
	public static String[] parseFieldFormat(String format)
			throws SQLException {

		if (format == null)
			return null;
		
		String[] formulationRules = new String[4];
		Matcher matcher = pattern.matcher(format);

		while (matcher.find()) {
			formulationRules[0] = matcher.group(1);
			formulationRules[1] = matcher.group(2);
			formulationRules[2] = matcher.group(3);
			formulationRules[2] = formulationRules[2].replace(".", "");// remove the period
			formulationRules[3] = matcher.group(4);
		}

		return formulationRules;
	}
	
	/**
	 * Defines an enumeration for the different table types that can be
	 * extracted. Holds a readable description of the table type.
	 */
	private static enum TableType {

		/** A fixed-width binary table. */
		FIXED_BINARY("fixed-width binary table"),

		/** A fixed-width text table. */
		FIXED_TEXT("fixed-width character table"),

		/** A delimited table. */
		DELIMITED("delimited table");
		
		private String readableType;

		private TableType(String readableType) {
			this.readableType = readableType;
		}

		/**
		 * Gets the readable name for the table type.
		 *
		 * @return the name of the table type
		 */
		@SuppressWarnings("unused")
		public String getReadableType() {
			return readableType;
		}
	}
	
	/**
	 * Deep copy Field utility
	 * 
	 * @author ghflores
	 */
	public static class CopyFields {
		
		
		public static FieldBinary getFieldBinaryCopy(FieldBinary fieldToCopy) {
			
			// if not null, create a deep copy
			if (fieldToCopy != null) {
				
				FieldBinary fieldBinary = new FieldBinary();
				
				// copy all the elements
				fieldBinary.setDataType(fieldToCopy.getDataType());
				fieldBinary.setDescription(fieldToCopy.getDescription());
				fieldBinary.setFieldFormat(fieldToCopy.getFieldFormat());
				fieldBinary.setFieldNumber(fieldToCopy.getFieldNumber());
				fieldBinary.setUnit(fieldToCopy.getUnit());
				fieldBinary.setValueOffset(fieldToCopy.getValueOffset());
				fieldBinary.setName(fieldToCopy.getName());
				fieldBinary.setScalingFactor(fieldToCopy.getScalingFactor());
				
				// copy FieldStatistics object elements
				if (fieldToCopy.getFieldStatistics() != null) {
					FieldStatistics newFieldStatistics = new FieldStatistics();

					newFieldStatistics.setDescription(fieldToCopy
							.getFieldStatistics().getDescription());
					newFieldStatistics.setLocalIdentifier(fieldToCopy
							.getFieldStatistics().getLocalIdentifier());
					newFieldStatistics.setMaximum(fieldToCopy
							.getFieldStatistics().getMaximum());
					newFieldStatistics.setMean(fieldToCopy.getFieldStatistics()
							.getMean());
					newFieldStatistics.setMedian(fieldToCopy
							.getFieldStatistics().getMedian());
					newFieldStatistics.setMinimum(fieldToCopy
							.getFieldStatistics().getMinimum());
					newFieldStatistics.setStandardDeviation(fieldToCopy
							.getFieldStatistics().getStandardDeviation());
					fieldBinary.setFieldStatistics(newFieldStatistics);
				} else {
					fieldBinary.setFieldStatistics(fieldToCopy
							.getFieldStatistics());
				}
				
				// copy SpecialConstants object elements
				if (fieldToCopy.getSpecialConstants() != null) {
					SpecialConstants newSpecialConstants = new SpecialConstants();

					newSpecialConstants.setErrorConstant(fieldToCopy
							.getSpecialConstants().getErrorConstant());
					newSpecialConstants.setHighInstrumentSaturation(fieldToCopy
							.getSpecialConstants()
							.getHighInstrumentSaturation());
					newSpecialConstants
							.setHighRepresentationSaturation(fieldToCopy
									.getSpecialConstants()
									.getHighRepresentationSaturation());
					newSpecialConstants.setInvalidConstant(fieldToCopy
							.getSpecialConstants().getInvalidConstant());
					newSpecialConstants
							.setLowInstrumentSaturation(fieldToCopy
									.getSpecialConstants()
									.getLowInstrumentSaturation());
					newSpecialConstants
							.setLowRepresentationSaturation(fieldToCopy
									.getSpecialConstants()
									.getLowRepresentationSaturation());
					newSpecialConstants.setMissingConstant(fieldToCopy
							.getSpecialConstants().getMissingConstant());
					newSpecialConstants.setNotApplicableConstant(fieldToCopy
							.getSpecialConstants().getNotApplicableConstant());
					newSpecialConstants.setSaturatedConstant(fieldToCopy
							.getSpecialConstants().getSaturatedConstant());
					newSpecialConstants.setUnknownConstant(fieldToCopy
							.getSpecialConstants().getUnknownConstant());
					newSpecialConstants.setValidMaximum(fieldToCopy
							.getSpecialConstants().getValidMaximum());
					newSpecialConstants.setValidMinimum(fieldToCopy
							.getSpecialConstants().getValidMinimum());
					fieldBinary.setSpecialConstants(newSpecialConstants);
				} else {
					fieldBinary.setSpecialConstants(fieldToCopy
							.getSpecialConstants());
				}
				
				// copy FieldLength object elements
				if (fieldToCopy.getFieldLength() != null) {
					
					FieldLength newFieldLength = new FieldLength();
					newFieldLength.setUnit(fieldToCopy.getFieldLength().getUnit());
					newFieldLength.setValue(fieldToCopy.getFieldLength().getValue());
					fieldBinary.setFieldLength(newFieldLength);
					
				} else {
					fieldBinary.setFieldLength(fieldToCopy.getFieldLength());
				}
				
				// copy FieldLocation object elements
				if (fieldToCopy.getFieldLocation() != null) {
					
					FieldLocation newFieldLocation = new FieldLocation();
					
					newFieldLocation.setUnit(fieldToCopy.getFieldLocation().getUnit());
					newFieldLocation.setValue(fieldToCopy.getFieldLocation().getValue());
					fieldBinary.setFieldLocation(newFieldLocation);
					
				} else {
					fieldBinary.setFieldLocation(fieldToCopy.getFieldLocation());
				}

				if (fieldBinary.getPackedDataFields() != null) {
					
					PackedDataFields newPackedDataFields = new PackedDataFields();
					
					newPackedDataFields.setBitFields(fieldToCopy.getPackedDataFields().getBitFields());
					newPackedDataFields.setDescription(fieldToCopy.getPackedDataFields().getDescription());
					fieldBinary.setPackedDataFields(newPackedDataFields);
					
				} else {
					fieldBinary.setPackedDataFields(fieldToCopy.getPackedDataFields());
				}

				return fieldBinary;
				
			} else {
				return null;
			}
		}
		
		
		/**
		 * Create deep copy of FieldCharacter field object
		 * 
		 * @param fieldToCopy
		 * 			FieldCharacter to copy
		 * @return
		 */
		public static FieldCharacter getFieldCharacterCopy(FieldCharacter fieldToCopy) {
			
			// if not null, create a deep copy
			if (fieldToCopy != null) {
				
				FieldCharacter fieldCharacter = new FieldCharacter();
				
				// copy all the elements
				fieldCharacter.setDataType(fieldToCopy.getDataType());
				fieldCharacter.setDescription(fieldToCopy.getDescription());
				fieldCharacter.setFieldFormat(fieldToCopy.getFieldFormat());
				fieldCharacter.setFieldNumber(fieldToCopy.getFieldNumber());
				fieldCharacter.setUnit(fieldToCopy.getUnit());
				fieldCharacter.setValueOffset(fieldToCopy.getValueOffset());
				fieldCharacter.setName(fieldToCopy.getName());
				fieldCharacter.setScalingFactor(fieldToCopy.getScalingFactor());
				
				// copy FieldStatistics object elements
				if (fieldToCopy.getFieldStatistics() != null) {
					FieldStatistics newFieldStatistics = new FieldStatistics();

					newFieldStatistics.setDescription(fieldToCopy
							.getFieldStatistics().getDescription());
					newFieldStatistics.setLocalIdentifier(fieldToCopy
							.getFieldStatistics().getLocalIdentifier());
					newFieldStatistics.setMaximum(fieldToCopy
							.getFieldStatistics().getMaximum());
					newFieldStatistics.setMean(fieldToCopy.getFieldStatistics()
							.getMean());
					newFieldStatistics.setMedian(fieldToCopy
							.getFieldStatistics().getMedian());
					newFieldStatistics.setMinimum(fieldToCopy
							.getFieldStatistics().getMinimum());
					newFieldStatistics.setStandardDeviation(fieldToCopy
							.getFieldStatistics().getStandardDeviation());
					fieldCharacter.setFieldStatistics(newFieldStatistics);
				} else {
					fieldCharacter.setFieldStatistics(fieldToCopy
							.getFieldStatistics());
				}
				
				// copy SpecialConstants object elements
				if (fieldToCopy.getSpecialConstants() != null) {
					SpecialConstants newSpecialConstants = new SpecialConstants();

					newSpecialConstants.setErrorConstant(fieldToCopy
							.getSpecialConstants().getErrorConstant());
					newSpecialConstants.setHighInstrumentSaturation(fieldToCopy
							.getSpecialConstants()
							.getHighInstrumentSaturation());
					newSpecialConstants
							.setHighRepresentationSaturation(fieldToCopy
									.getSpecialConstants()
									.getHighRepresentationSaturation());
					newSpecialConstants.setInvalidConstant(fieldToCopy
							.getSpecialConstants().getInvalidConstant());
					newSpecialConstants
							.setLowInstrumentSaturation(fieldToCopy
									.getSpecialConstants()
									.getLowInstrumentSaturation());
					newSpecialConstants
							.setLowRepresentationSaturation(fieldToCopy
									.getSpecialConstants()
									.getLowRepresentationSaturation());
					newSpecialConstants.setMissingConstant(fieldToCopy
							.getSpecialConstants().getMissingConstant());
					newSpecialConstants.setNotApplicableConstant(fieldToCopy
							.getSpecialConstants().getNotApplicableConstant());
					newSpecialConstants.setSaturatedConstant(fieldToCopy
							.getSpecialConstants().getSaturatedConstant());
					newSpecialConstants.setUnknownConstant(fieldToCopy
							.getSpecialConstants().getUnknownConstant());
					newSpecialConstants.setValidMaximum(fieldToCopy
							.getSpecialConstants().getValidMaximum());
					newSpecialConstants.setValidMinimum(fieldToCopy
							.getSpecialConstants().getValidMinimum());
					fieldCharacter.setSpecialConstants(newSpecialConstants);
				} else {
					fieldCharacter.setSpecialConstants(fieldToCopy
							.getSpecialConstants());
				}
				
				// copy FieldLength object elements
				if (fieldToCopy.getFieldLength() != null) {
					
					FieldLength newFieldLength = new FieldLength();
					newFieldLength.setUnit(fieldToCopy.getFieldLength().getUnit());
					newFieldLength.setValue(fieldToCopy.getFieldLength().getValue());
					fieldCharacter.setFieldLength(newFieldLength);
					
				} else {
					fieldCharacter.setFieldLength(fieldToCopy.getFieldLength());
				}
				
				// copy FieldLocation object elements
				if (fieldToCopy.getFieldLocation() != null) {
					
					FieldLocation newFieldLocation = new FieldLocation();
					
					newFieldLocation.setUnit(fieldToCopy.getFieldLocation().getUnit());
					newFieldLocation.setValue(fieldToCopy.getFieldLocation().getValue());
					fieldCharacter.setFieldLocation(newFieldLocation);
					
				} else {
					fieldCharacter.setFieldLocation(fieldToCopy.getFieldLocation());
				}
				
				return fieldCharacter;
			} else {
				return null;
			}
		}
		
		
		/**
		 * Create deep copy of FieldDelimited field object
		 * 
		 * @param fieldToCopy
		 * 				FieldDelimited to copy
		 * @return
		 */
		public static FieldDelimited getFieldDelimitedCopy(FieldDelimited fieldToCopy) {
			
			// if not null, create a deep copy
			if (fieldToCopy != null) {

				FieldDelimited fieldDelimited = new FieldDelimited();

				// copy all the elements
				fieldDelimited.setDataType(fieldToCopy.getDataType());
				fieldDelimited.setDescription(fieldToCopy.getDescription());
				fieldDelimited.setFieldFormat(fieldToCopy.getFieldFormat());
				fieldDelimited.setFieldNumber(fieldToCopy.getFieldNumber());
				fieldDelimited.setUnit(fieldToCopy.getUnit());
				fieldDelimited.setValueOffset(fieldToCopy.getValueOffset());
				fieldDelimited.setName(fieldToCopy.getName());
				fieldDelimited.setScalingFactor(fieldToCopy.getScalingFactor());

				// copy inner object elements
				if (fieldToCopy.getFieldStatistics() != null) {
					FieldStatistics newFieldStatistics = new FieldStatistics();

					newFieldStatistics.setDescription(fieldToCopy
							.getFieldStatistics().getDescription());
					newFieldStatistics.setLocalIdentifier(fieldToCopy
							.getFieldStatistics().getLocalIdentifier());
					newFieldStatistics.setMaximum(fieldToCopy
							.getFieldStatistics().getMaximum());
					newFieldStatistics.setMean(fieldToCopy.getFieldStatistics()
							.getMean());
					newFieldStatistics.setMedian(fieldToCopy
							.getFieldStatistics().getMedian());
					newFieldStatistics.setMinimum(fieldToCopy
							.getFieldStatistics().getMinimum());
					newFieldStatistics.setStandardDeviation(fieldToCopy
							.getFieldStatistics().getStandardDeviation());
					fieldDelimited.setFieldStatistics(newFieldStatistics);
				} else {
					fieldDelimited.setFieldStatistics(fieldToCopy
							.getFieldStatistics());
				}

				if (fieldToCopy.getMaximumFieldLength() != null) {
					MaximumFieldLength newMaximumFieldLength = new MaximumFieldLength();

					newMaximumFieldLength.setUnit(fieldToCopy
							.getMaximumFieldLength().getUnit());
					newMaximumFieldLength.setValue(fieldToCopy
							.getMaximumFieldLength().getValue());
					fieldDelimited.setMaximumFieldLength(newMaximumFieldLength);
				} else {
					fieldDelimited.setMaximumFieldLength(fieldToCopy
							.getMaximumFieldLength());
				}

				if (fieldToCopy.getSpecialConstants() != null) {
					SpecialConstants newSpecialConstants = new SpecialConstants();

					newSpecialConstants.setErrorConstant(fieldToCopy
							.getSpecialConstants().getErrorConstant());
					newSpecialConstants.setHighInstrumentSaturation(fieldToCopy
							.getSpecialConstants()
							.getHighInstrumentSaturation());
					newSpecialConstants
							.setHighRepresentationSaturation(fieldToCopy
									.getSpecialConstants()
									.getHighRepresentationSaturation());
					newSpecialConstants.setInvalidConstant(fieldToCopy
							.getSpecialConstants().getInvalidConstant());
					newSpecialConstants
							.setLowInstrumentSaturation(fieldToCopy
									.getSpecialConstants()
									.getLowInstrumentSaturation());
					newSpecialConstants
							.setLowRepresentationSaturation(fieldToCopy
									.getSpecialConstants()
									.getLowRepresentationSaturation());
					newSpecialConstants.setMissingConstant(fieldToCopy
							.getSpecialConstants().getMissingConstant());
					newSpecialConstants.setNotApplicableConstant(fieldToCopy
							.getSpecialConstants().getNotApplicableConstant());
					newSpecialConstants.setSaturatedConstant(fieldToCopy
							.getSpecialConstants().getSaturatedConstant());
					newSpecialConstants.setUnknownConstant(fieldToCopy
							.getSpecialConstants().getUnknownConstant());
					newSpecialConstants.setValidMaximum(fieldToCopy
							.getSpecialConstants().getValidMaximum());
					newSpecialConstants.setValidMinimum(fieldToCopy
							.getSpecialConstants().getValidMinimum());
					fieldDelimited.setSpecialConstants(newSpecialConstants);
				} else {
					fieldDelimited.setSpecialConstants(fieldToCopy
							.getSpecialConstants());
				}

				return fieldDelimited;
			} else {
				return null;
			}
		}
	}
	
	
}
