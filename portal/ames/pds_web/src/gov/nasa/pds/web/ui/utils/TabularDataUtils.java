package gov.nasa.pds.web.ui.utils;

import gov.nasa.arc.pds.tools.util.URLUtils;
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

/**
 * A tabular data general utilities class. However, most methods specific to PDS
 * format.
 * 
 * @author jagander
 * @author lbaalman
 * 
 * @author German H. Flores
 */
public class TabularDataUtils {

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// remove label folder
			list.remove(list.size() - 2);

		}

		return null;

	}

	/**
	 * Is the data type an ASCII Integer
	 * 
	 * @param column
	 *            the column to check type of
	 * 
	 * @return is the column the expected type
	 */
	// TODO convert calls to this to use isASCIIIntegerType(String dataType)
	// directly
	public static boolean isASCIIIntegerType(Column column) {
		if (isASCIIIntegerType(column.getDataType())) {
			return true;
		}

		return false;
	}

	/**
	 * Is the data type an ASCII Integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIIIntegerType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_INTEGER"))//$NON-NLS-1$
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
		if (dataType.equalsIgnoreCase("ASCII_REAL"))//$NON-NLS-1$
			return true;
		return false;
	}

	/**
	 * Is the data type an ASCII Complex
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isASCIIComplexType(String dataType) {
		if (dataType.equalsIgnoreCase("ASCII_COMPLEX"))//$NON-NLS-1$
			return true;
		return false;
	}

	/**
	 * Is the data type an integer numeric type, binary, signed, or otherwise
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isIntegerType(String dataType) {

		if (isASCIIIntegerType(dataType))
			return true;
		if (isBinarySignedIntegerType(dataType))
			return true;
		if (isBinaryUnsignedIntegerType(dataType))
			return true;
		return false;
	}

	/**
	 * Is the data type an complex numeric type, binary or otherwise
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isComplexType(String dataType) {
		if (TabularDataUtils.isASCIIComplexType(dataType))
			return true;
		if (TabularDataUtils.isBinaryComplexType(dataType))
			return true;
		return false;

	}

	/**
	 * Is the data type a real numeric type, binary or otherwise
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isRealType(String dataType) {
		if (TabularDataUtils.isASCIIRealType(dataType))
			return true;
		if (TabularDataUtils.isBinaryFloatingPointType(dataType))
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
	// TODO find uses of this method and convert to use isNumericType(String
	// dataType)
	public static boolean isColumnNumericType(Column column) {
		if (isNumericType(column.getDataType()) == true) {
			return true;
		}
		return false;
	}

	/**
	 * Is the data type a numeric type, binary or otherwise
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isNumericType(String dataType) {
		if (isIntegerType(dataType))
			return true;
		if (isRealType(dataType))
			return true;
		if (isComplexType(dataType))
			return true;
		return false;
	}

	/**
	 * Is the data type a binary signed integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isBinarySignedIntegerType(String dataType) {
		if (isMSBBinarySignedInteger(dataType))
			return true;
		if (isLSBSignedInteger(dataType))
			return true;
		return false;
	}

	/**
	 * Is the data type an LSB integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isLSBSignedInteger(String dataType) {

		if (dataType.equalsIgnoreCase("LSB_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("VAX_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("PC_INTEGER"))
			return true;
		return false;
	}

	/**
	 * Is the data type an MSB integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isMSBBinarySignedInteger(String dataType) {
		if (dataType.equalsIgnoreCase("MSB_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("IBM_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("MAC_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("SUN_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("INTEGER"))// obsolete
			return true;

		return false;
	}

	/**
	 * Is the data type a binary unsigned integer
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isBinaryUnsignedIntegerType(String dataType) {
		if (isMSBUnsignedInteger(dataType))
			return true;
		if (isLSBUnsignedInteger(dataType))
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
	@SuppressWarnings("nls")
	public static boolean isMSBUnsignedInteger(String dataType) {
		if (dataType.equalsIgnoreCase("IBM_UNSIGNED_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("MAC_UNSIGNED_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("MSB_UNSIGNED_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("SUN_UNSIGNED_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("UNSIGNED_INTEGER"))// obsolete
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
	@SuppressWarnings("nls")
	public static boolean isLSBUnsignedInteger(String dataType) {
		if (dataType.equalsIgnoreCase("LSB_UNSIGNED_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("PC_UNSIGNED_INTEGER"))
			return true;
		if (dataType.equalsIgnoreCase("VAX_UNSIGNED_INTEGER"))
			return true;
		return false;
	}

	/**
	 * Is the data type a binary floating point
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isBinaryFloatingPointType(String dataType) {
		if (isMSBFloatingPointType(dataType))
			return true;
		if (isLSBFloatingPointType(dataType))
			return true;
		return false;

	}

	/**
	 * Is the data type an MSB floating point
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isMSBFloatingPointType(String dataType) {
		// these 4 binary floating point types are implemented the same
		// can be 4-, 8- and 10-byte real numbers
		if (dataType.equalsIgnoreCase("IEEE_REAL"))
			return true;
		if (dataType.equalsIgnoreCase("MAC_REAL"))
			return true;
		if (dataType.equalsIgnoreCase("SUN_REAL"))
			return true;
		if (dataType.equalsIgnoreCase("FLOAT"))// obsolete
			return true;
		if (dataType.equalsIgnoreCase("REAL"))// obsolete
			return true;
		return false;

	}

	/**
	 * Is the data type an LSB floating point
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	// TODO determine if these are in fact LSB
	@SuppressWarnings("nls")
	public static boolean isLSBFloatingPointType(String dataType) {
		// TODO PC real is implemented uniquely
		if (dataType.equalsIgnoreCase("PC_REAL"))
			return true;
		// TODO these 3 vax are implemented similarly
		if (dataType.equalsIgnoreCase("VAX_DOUBLE"))
			return true;
		if (dataType.equalsIgnoreCase("VAX_REAL"))
			return true;
		if (dataType.equalsIgnoreCase("VAXG_REAL"))
			return true;
		// TODO determine ibm_real implementation
		if (dataType.equalsIgnoreCase("IBM_REAL"))
			return true;

		return false;
	}

    /**
     * Is the data type a VAX floating point
     *
     * @param dataType
     *            the type to check
     *
     * @return is the data type the expected type
     */
    @SuppressWarnings("nls")
    public static boolean isVAXFloatingPointType(String dataType) {
        
        if (dataType.equalsIgnoreCase("VAX_DOUBLE"))
            return true;
        if (dataType.equalsIgnoreCase("VAX_REAL"))
            return true;
        if (dataType.equalsIgnoreCase("VAXG_REAL"))
            return true;

        return false;
    }
    
    /**
     * Is the data type a VAXG floating point
     *
     * @param dataType
     *            the type to check
     *
     * @return is the data type the expected type
     */
    @SuppressWarnings("nls")
    public static boolean isVAXGFloatingPointType(String dataType) {

        if (dataType.equalsIgnoreCase("VAXG_REAL"))
            return true;

        return false;
    }

	
	
	/**
	 * Is the data type a binary complex number
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	// TODO split into groups based on implementation
	@SuppressWarnings("nls")
	public static boolean isBinaryComplexType(String dataType) {
		// the following 4 are IEEE_REAL complex
		if (dataType.equalsIgnoreCase("IEEE_COMPLEX"))
			return true;
		if (dataType.equalsIgnoreCase("MAC_COMPLEX"))
			return true;
		if (dataType.equalsIgnoreCase("SUN_COMPLEX"))
			return true;
		if (dataType.equalsIgnoreCase("COMPLEX"))// obsolete
			return true;

		// TODO not sure on IBM_REAL implementation
		if (dataType.equalsIgnoreCase("IBM_COMPLEX"))
			return true;
		// TODO PC Complex is handled uniquely
		if (dataType.equalsIgnoreCase("PC_COMPLEX"))
			return true;
		// TODO VAX AND VAXG COMPLEX are handled the same
		if (dataType.equalsIgnoreCase("VAX_COMPLEX"))
			return true;
		if (dataType.equalsIgnoreCase("VAXG_COMPLEX"))
			return true;
		return false;
	}
	
	/**
     * Is the data type a VAX floating point
     *
     * @param dataType
     *            the type to check
     *
     * @return is the data type the expected type
     */
    @SuppressWarnings("nls")
    public static boolean isVAXComplexFDGType(String dataType) {
        
        if (dataType.equalsIgnoreCase("VAX_COMPLEX"))
            return true;
        
        if (dataType.equalsIgnoreCase("VAXG_COMPLEX"))
            return true;

        return false;
    }
    
    /**
     * Is the data type a VAXG floating point
     *
     * @param dataType
     *            the type to check
     *
     * @return is the data type the expected type
     */
    @SuppressWarnings("nls")
    public static boolean isVAXGComplexType(String dataType) {

        if (dataType.equalsIgnoreCase("VAXG_COMPLEX"))
            return true;

        return false;
    }

	/**
	* Is the data type PC_REAL
	*
	* @param dataType
	*            the type to check
	*
	* @return is the data type the expected type
	*/
	// TODO determine if these are in fact LSB
	@SuppressWarnings("nls")
	public static boolean isPCRealComplexType(String dataType) {
	
	if (dataType.equalsIgnoreCase("PC_REAL"))
	return true;
	
	if (dataType.equalsIgnoreCase("PC_COMPLEX"))
	return true;
	
	return false;
	}

	/*
	 * IEEE_REAL Aliases: FLOAT REAL MAC_REAL SUN_REAL
	 * 
	 * see appendix c section c.5 for implementation details
	 */
	/*
	 * IEEE_COMPLEX Aliases: COMPLEX MAC_COMPLEX SUN_COMPLEX
	 * 
	 * IEEE complex numbers consist of two IEEE_REAL format numbers of the same
	 * precision, contiguous in memory. The first number represents the real
	 * part and the second the imaginary part of the complex value.
	 * 
	 * For more information on using IEEE_REAL formats, see appendix c Section
	 * C.5.
	 */

	/*
	 * PC_REAL Aliases: None
	 */

	/*
	 * PC_COMPLEXAliases: None
	 * 
	 * PC complex numbers consist of two PC_REAL format numbers of the same
	 * precision,contiguous in memory. The first number represents the real part
	 * and the second the imaginarypart of the complex value.
	 * 
	 * For more information on using PC_REAL formats, see Section C.7.
	 */

	/*
	 * VAX_REAL, VAXG_REAL Aliases: VAX_DOUBLE for VAX_REAL only. No aliases for
	 * VAXG_REAL
	 */

	/*
	 * VAX_COMPLEX, VAXG_COMPLEX Aliases: None
	 * 
	 * VAX complex numbers consist of two VAX_REAL (or VAXG_REAL) format numbers
	 * of the same precision, contiguous in memory. The first number represents
	 * the real part and the second the imaginary part of the complex value.
	 * 
	 * For more information on using VAX_REAL formats, see Section C.9.
	 */

	/**
	 * Is the column an ascii string
	 * 
	 * @param column
	 *            the column to check
	 * 
	 * @return is the column the expected type
	 */
	public static boolean isColumnASCIIStringType(Column column) {
		if (isASCIIStringType(column.getDataType()))
			return true;

		return false;
	}

	/**
	 * Is the data type an acii string
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isASCIIStringType(String dataType) {
		if (dataType.equalsIgnoreCase("CHARACTER"))
			return true;
		if (dataType.equalsIgnoreCase("EBCDIC_CHARACTER"))
			return true;
		return false;
	}

	/**
	 * Is the data type a binary bit string
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	@SuppressWarnings("nls")
	public static boolean isBinaryBitStringType(String dataType) {

		if (dataType.equalsIgnoreCase("LSB_BIT_STRING"))
			return true;
		if (dataType.equalsIgnoreCase("MSB_BIT_STRING"))
			return true;
		if (dataType.equalsIgnoreCase("VAX_BIT_STRING"))
			return true;
		if (dataType.equalsIgnoreCase("BIT_STRING"))// obsolete
			return true;

		return false;
	}

	/**
	* Is the data type a LSB binary bit string or aliases
	*
	* @param dataType
	*            the type to check
	*
	* @return is the data type the expected type
	*/
	@SuppressWarnings("nls")
	public static boolean isLSBBitStringType(String dataType) {
	
	if (dataType.equalsIgnoreCase("LSB_BIT_STRING"))
	return true;
	
	if (dataType.equalsIgnoreCase("VAX_BIT_STRING"))
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
		if (isBinaryBitStringType(column.getDataType()))
			return true;
		if (isASCIIStringType(column.getDataType()))
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
	// TODO: convert calls to this to use isDateTimeType(String dataType)
	// directly
	public static boolean isColumnDateTime(Column column) {
		if (isDateTimeType(column.getDataType())) {
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
		if (dataType.equalsIgnoreCase("DATE"))
			return true;
		if (dataType.equalsIgnoreCase("TIME"))
			return true;
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
	// TODO: convert calls to this to use isBooleanType(String dataType)
	// directly
	public static boolean isColumnBooleanType(Column column) {
		if (isBooleanType(column.getDataType())) {
			return true;
		}
		return false;
	}

	/**
	 * Is the data type a boolean
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isBooleanType(String dataType) {
		if (dataType.equalsIgnoreCase("BOOLEAN")) //$NON-NLS-1$
			return true;
		return false;
	}

	/**
	 * Is the column data type other (ie NA)
	 * 
	 * @param column
	 *            the column to check the type of
	 * 
	 * @return is the data type the expected type
	 */
	// TODO convert calls to this to use isOtherType(String dataType) directly
	public static boolean isColumnOtherType(Column column) {
		if (isOtherType(column.getDataType())) {
			return true;
		}
		return false;
	}

	/**
	 * Is the data type other (ie NA)
	 * 
	 * @param dataType
	 *            the type to check
	 * 
	 * @return is the data type the expected type
	 */
	public static boolean isOtherType(String dataType) {
		if (dataType.equalsIgnoreCase("N/A")) { //$NON-NLS-1$
			return true;
		}
		return false;
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
	@SuppressWarnings("nls")
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
				
				columnHolder.add(column.getName());
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

			resultSet = stmt.executeQuery(TabularDataUtils.buildSqlQuery(table,
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
						80,
						url
								.getPath()
								.substring(0,
										url.getPath().lastIndexOf("/") + 1).concat(fileName.toUpperCase())); //$NON-NLS-1$
			} else {

				tabFileUrl = new URL(
						url.getProtocol(),
						url.getHost(),
						80,
						url
								.getPath()
								.substring(0,
										url.getPath().lastIndexOf("/") + 1).concat(fileName.toLowerCase())); //$NON-NLS-1$

			}

		}
		return tabFileUrl;
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

}
