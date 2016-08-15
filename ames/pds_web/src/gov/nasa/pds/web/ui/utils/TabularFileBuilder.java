package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.web.applets.CancelledException;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

import org.apache.commons.lang.StringUtils;

/**
 * A utility class to generate a tabular data file from the database given user
 * configuration (in the form of a SliceContainer).
 * 
 * @author Laura Baalman
 */
public class TabularFileBuilder extends Observable {

	/**
	 * A status container to allow interaction with another thread.
	 */
	private final StatusContainer status;

	/**
	 * A container for source and output configuration.
	 */
	private SliceContainer slice;

	/**
	 * Output stream for tabular data file.
	 */
	private ByteArrayOutputStream output;

	/**
	 * Get the output stream.
	 * 
	 * @return the outputstream
	 */
	public ByteArrayOutputStream getOutput() {
		return this.output;
	}

	/**
	 * Primary constructor for the tabular file builder.
	 * 
	 * @param slice
	 *            the container for configuration info
	 * @param status
	 *            the status container for communicating progress to an outside
	 *            thread
	 */
	public TabularFileBuilder(final SliceContainer slice,
			final StatusContainer status) {
		this.slice = slice;
		this.status = status;
	}

	/**
	 * Update the status so that the launching thread can display an update.
	 * 
	 * @param key
	 *            message key to pull message from properties file
	 */
	private void updateStatus(final String key) throws CancelledException {
		updateStatus(key, true);
	}

	/**
	 * Update the status so that the launching thread can display an update.
	 * Allows you to set whether a step in the process has incremented.
	 * 
	 * @param key
	 *            message key to pull message from properties file
	 * @param incrementStep
	 *            flag indicating whether to increment a step in the process
	 */
	private void updateStatus(final String key, Boolean incrementStep)
			throws CancelledException {
		if (this.status.isCancelled()) {
			throw new CancelledException();
		}
		if (incrementStep != null && incrementStep.equals(Boolean.TRUE)) {
			this.status.incrementStep();
		}
		this.status.setStatus(key);
		setChanged();
		notifyObservers(this.status);

	}

	/**
	 * Build the output file
	 */
	@SuppressWarnings("nls")
	public void buildFile() throws Exception {

		// inform the observer thread that building has begun
		updateStatus("Building file");

		// determine if the file is a tab file
		boolean tab = false;
		String fileType = this.slice.getFileType();
		if (fileType.equalsIgnoreCase(TabularManagementConstants.FileType.TAB
				.toString())) {
			tab = true;
		}

		// get table container from slice
		TabularDataContainer table = this.slice.getActiveTabularData();

		// create buffered output stream
		this.output = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(this.output);

		// get a connection to the database
		Connection connection = DBManager.getConnection();

		Statement stmt = null;
		ResultSet rs = null;
		try {

			// create a sql query including table name from config
			stmt = connection.createStatement();
			rs = stmt
					.executeQuery(TabularDataUtils.buildSqlQuery(table, false));

			// get metainfo from query
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rs.getMetaData().getColumnCount();

			if (rs.wasNull()) {
				throw new RuntimeException("No records returned by query");
			}

			//Get the total number of rows to be written
			int size = 0;
            try {
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            }
            catch(Exception ex) {
                throw new RuntimeException("No records returned by query");
            }    
            
		    final String horizonalPad = " ";		   
				
			// if config stated that headers should be in output, include them
			if (this.slice.includeHeaders()) {
				for (int i = 1; i < columnCount + 1; i++) {					
					String colHeader = rsmd.getColumnName(i);
					if (tab) {
						SliceColumn sliceColumn = table.getColumn(rsmd.getColumnName(i));
						calculateColumnWidth(sliceColumn, colHeader);
						String paddedHeader = padLeftOrRight(colHeader, sliceColumn.getPrintColumnWidth(), sliceColumn.isNumber());						
					    writer.write(paddedHeader);
					    if (i < columnCount) writer.write(horizonalPad); 
					} else {
						writer.write("\"" + colHeader + "\"");					
						if (i < columnCount) writer.write(","); 
					}
				}
				writer.write(System.getProperty("line.separator")); //$NON-NLS-1$
			} else {
				if (tab) calculateColumnWidths(rsmd, table);
			}

			//Keep track of the rows. This is used for update Status
			float rowNumber = 1;
			
			// write each line of data
			while (rs.next()) {
				
				//Display status as percentage
				updateStatus(
						"Processing: "
								+ (int) Math.floor(rowNumber / size * 100)
								+ "% completed ", false);
                rowNumber++;
				
				StringBuilder valueString = new StringBuilder();
				for (int i = 1; i <= columnCount; i++) {					
					// get the value
					Object value = rs.getObject(i);
					// get its column to get type, and missing constant
					SliceColumn sliceColumn = table.getColumn(rsmd.getColumnName(i));

					String baseValue = null;
					// if value is null, insert missing constant
					if (rs.wasNull()) {
						baseValue = sliceColumn.getMissingConstant();
					} else {
						baseValue = value.toString();
					}
					if (TabularDataUtils.isColumnStringType(sliceColumn)) {
						if (tab) {
							valueString.append(padLeftOrRight(baseValue, sliceColumn.getPrintColumnWidth(), sliceColumn.isNumber()));
						} else {
							valueString.append("\"" + baseValue + "\"");
						}					
					} else if (TabularDataUtils.isColumnDateTime(sliceColumn)) {
						String dateTime = null;
						if (rs.wasNull()) {
							dateTime = baseValue;						
						} else {							
							SimpleDateFormat format = new SimpleDateFormat(
									sliceColumn.getDateFormat());
							Date date = new Date(Long.valueOf(baseValue));
							dateTime = format.format(date);
						}
						if (tab) {
							valueString.append(padLeftOrRight(dateTime, sliceColumn.getPrintColumnWidth(), sliceColumn.isNumber()));
						} else {
							valueString.append(dateTime);				
						}						
					} else if (sliceColumn.isSciNotation()) {
						String scientific = new DecimalFormat("0.#####E0").format(Double.valueOf(baseValue.toString()));
						if (tab) {
							valueString.append(padLeftOrRight(scientific, sliceColumn.getPrintColumnWidth(), sliceColumn.isNumber()));	
						} else {
							valueString.append(scientific);	
						}
					} else {
						if (tab) {
							valueString.append(padLeftOrRight(baseValue, sliceColumn.getPrintColumnWidth(), sliceColumn.isNumber()));
						} else {
							valueString.append(baseValue);
						}
					}
					if (i < columnCount) {
						if (tab)
							valueString.append(horizonalPad);
						else
							valueString.append(",");
					}

				}// end for
				// write valueString to the stream
				writer.write(valueString.toString());
				// write line separator
				writer.write(System.getProperty("line.separator")); //$NON-NLS-1$

			}// end while rs.next()

			writer.flush();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw sqle;

		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
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
		connection.close();
		this.status.setDone();

	}

	/** Sets default print attributes for all columns. */
	private void calculateColumnWidths(ResultSetMetaData rsmd, TabularDataContainer table) throws SQLException {
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i < columnCount + 1; i++) {					
			SliceColumn sliceColumn = table.getColumn(rsmd.getColumnName(i));
			sliceColumn.setPrintColumnWidth(sliceColumn.getBytes());			 
		}	
	}

	/** Sets TAB print attributes for a slice column. */
	private void calculateColumnWidth(SliceColumn sliceColumn, String colHeader) throws SQLException {
		int dataWidth = sliceColumn.getBytes();
		int colHeaderWidth = colHeader.length();
		sliceColumn.setPrintColumnWidth(Math.max(dataWidth, colHeaderWidth));  // print width across header and data
		sliceColumn.setNumber(sliceColumn.getDataType().equalsIgnoreCase("ASCII_REAL") ? true : false); // determines padding position
	}

	private String padLeftOrRight(String string, int width, boolean isNumber) {
		return (isNumber) ? StringUtils.leftPad(string, width) : StringUtils.rightPad(string, width);
	}
	
}
