package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.arc.pds.xml.generated.SpecialConstants;
import gov.nasa.pds.web.ui.actions.BaseViewAction;
import gov.nasa.pds.web.ui.containers.Option;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.utils.TabularDataUtils;
import gov.nasa.pds.web.ui.utils.TabularPDS4DataUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Provides basis for most View actions of Data Slicer including process
 * retrieval and preview data queried from database.
 * 
 * @ author Laura Baalman
 */
public abstract class Display extends BaseViewAction {

	private static final long serialVersionUID = 1L;
	protected TabularDataProcess tabularDataProcess;
	protected SliceContainer slice;
	protected TabularDataContainer tabularDataContainer;
	
	protected ArrayList<ArrayList<String>> previewRows = new ArrayList<ArrayList<String>>();
	protected ArrayList<String> previewColumns = new ArrayList<String>();
	//using LinkedHashMap to preserve order of insertion
	protected Map<String,String> previewTable = new LinkedHashMap<String,String>();
	protected ArrayList<String> previewDescription = new ArrayList<String>();
	
	protected int startRow;
	protected int numRowsPerPage = 100;
	protected int endRow;
	private List<Option> jumpToValues = new ArrayList<Option>();
	private List<Option> perPageValues = new ArrayList<Option>();

	public TabularDataProcess getTabularDataProcess() {
		return this.tabularDataProcess;
	}

	public void setTabularDataProcess(TabularDataProcess tabularDataProcess) {
		this.tabularDataProcess = tabularDataProcess;
	}

	public SliceContainer getSlice() {
		return this.slice;
	}

	public TabularDataContainer getTabularDataContainer() {
		return this.tabularDataContainer;
	}
	
	public ArrayList<String> getPreviewColumns() {
		return this.previewColumns;
	}

	public ArrayList<ArrayList<String>> getPreviewRows() {
		return this.previewRows;
	}
	
	public ArrayList<String> getPreviewDescription() {
		return this.previewDescription;
	}
	
	public Map<String,String> getPreviewTable() {
		return this.previewTable;
	}

	public int getStartRow() {
		return this.startRow;
	}

	public int getNumRowsPerPage() {
		return this.numRowsPerPage;
	}

	public int getEndRow() {
		return this.endRow;
	}

	public List<Option> getJumpToValues() {
		return this.jumpToValues;
	}

	public List<Option> getPerPageValues() {
		return this.perPageValues;
	}

	@Override
	protected String executeInner() throws Exception {
		this.tabularDataProcess = (TabularDataProcess) getProcess();
		this.slice = this.tabularDataProcess.getSlice();
		setProcId(this.tabularDataProcess.getID());
		this.tabularDataContainer = this.slice.getActiveTabularData();
		return null;
	}

	/*
	 * Retrieves data using TabularDataUtils.retrievePreview(this.table) to fill
	 * priviewColumns with column names and previewData with 10 rows of data
	 */
	protected void getPreviewData() throws Exception {

		ResultSet rs = null;
		try {

			this.tabularDataContainer.setRowsReturned(TabularDataUtils
					.countResultsReturned(this.tabularDataContainer));
			this.startRow = this.slice.getStartRow();
			this.numRowsPerPage = this.slice.getNumRowsPerPage();
			
			// set end row to be displayed in page
			if (this.tabularDataContainer.getRowsReturned() < (this.numRowsPerPage + this.startRow)) {
				this.endRow = this.tabularDataContainer.getRowsReturned();
			} else {
				this.endRow = this.startRow + this.numRowsPerPage - 1;
			}
			
			// numRowsPerPage and startRow cannot be 0
			// default to 1 for the startRow and 100 for numRowsPerPage
			if (this.numRowsPerPage <= 0)
				this.numRowsPerPage = 100;
			
			if (this.startRow <= 0)
				this.startRow = 1;

			// build drop down strings
			// 1 to 10
			for (int i = 0; (i * this.numRowsPerPage) < this.tabularDataContainer
					.getRowsReturned(); i++) {
				String string = ((i * this.numRowsPerPage) + 1)
						+ " to " + ((i + 1) * this.numRowsPerPage); //$NON-NLS-1$
				// /this.jumpToValues.add(string);
				// build file format options

				this.jumpToValues.add(new Option(
						((i * this.numRowsPerPage) + 1), string));

			}
			Option.setSelected(this.jumpToValues, this.startRow);

			// build per page drop down strings
			int[] arrayOfInts = { 10, 50, 100 };
			for (int i = 0; i < arrayOfInts.length; i++) {
				this.perPageValues.add(new Option(arrayOfInts[i],
						arrayOfInts[i] + " per page")); //$NON-NLS-1$
				// jump out of loop if i is more than rowsReturned
				if (arrayOfInts[i] > this.tabularDataContainer
						.getRowsReturned())
					break;
			}
			Option.setSelected(this.perPageValues, this.numRowsPerPage);

			// save total number of rows per page
			this.tabularDataContainer.setNumRowsPerPage(this.numRowsPerPage);


			rs = TabularDataUtils.retrievePreview(this.tabularDataContainer);
			// build column name list
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rs.getMetaData().getColumnCount();
			
			StringBuilder columnToolTipDescription = new StringBuilder();
			String columnName = "";

			//Populate column titles and tool tip information
			for (int i = 1; i < columnCount + 1; i++) {
				
				// used Filter and Sort Rows pages
				this.previewColumns.add(rsmd.getColumnName(i));
				
				SliceColumn column = this.tabularDataContainer.getColumn(rsmd
						.getColumnName(i));

				if(column.getDataType() != null
						&& !column.getDataType().isEmpty()) {
					columnToolTipDescription.append("- Data type: "+column.getDataType());
					columnToolTipDescription.append("\n");
				}
				
				SpecialConstants specialConstants = column
						.getSpecialConstants();
				
				//Get missing constant - for PDS3 labels only
				String missingConstant = column.getMissingConstant();
				if(missingConstant != null
						&& !missingConstant.isEmpty()
						&& specialConstants == null) {
					columnToolTipDescription.append("- Missing Constant: "+missingConstant);
					columnToolTipDescription.append("\n");
					
					//PDS4 label SpecialConstant
				} else {

					StringBuilder specialConstantsList = new StringBuilder();
					// add special constants, if any
					if (specialConstants != null) {

						if (specialConstants.getErrorConstant() != null
								&& !specialConstants.getErrorConstant().isEmpty()) {
							specialConstantsList.append("\t- Error: "
									+ specialConstants.getErrorConstant());
							specialConstantsList.append("\n");
						}

						if (specialConstants.getInvalidConstant() != null
								&& !specialConstants.getInvalidConstant().isEmpty()) {
							specialConstantsList.append("\t- Invalid: "
									+ specialConstants.getInvalidConstant());
							specialConstantsList.append("\n");
						}

						if (specialConstants.getMissingConstant() != null
								&& !specialConstants.getMissingConstant().isEmpty()) {
							specialConstantsList.append("\t- Missing: "
									+ specialConstants.getMissingConstant());
							specialConstantsList.append("\n");
						}

						if (specialConstants.getNotApplicableConstant() != null
								&& !specialConstants.getNotApplicableConstant() 
								.isEmpty()) {
							specialConstantsList.append("\t- Not Applicable: "
									+ specialConstants.getNotApplicableConstant());
							specialConstantsList.append("\n");
						}

						if (specialConstants.getSaturatedConstant() != null
								&& !specialConstants.getSaturatedConstant()
								.isEmpty()) {
							specialConstantsList.append("\t- Saturated: "
									+ specialConstants.getSaturatedConstant());
							specialConstantsList.append("\n");
						}

						if (specialConstants.getUnknownConstant() != null
								&& !specialConstants.getUnknownConstant().isEmpty()) {
							specialConstantsList.append("\t- Unknown: "
									+ specialConstants.getUnknownConstant());
						}

						// only add to tool tip if there is at least one constant
						if (specialConstantsList.length() != 0) {
							columnToolTipDescription
							.append("- Special Constants:\n");
							columnToolTipDescription.append(specialConstantsList
									.toString());
						}
					}
				}
				
				// add description, if any
				if (column.getDescription() != null
						&& !column.getDescription().isEmpty()) {
					columnToolTipDescription.append("- Description: "+column.getDescription());
					columnToolTipDescription.append("\n");
				}
				
				columnName = rsmd.getColumnName(i);
				this.previewDescription.add(column.getDescription());
				this.previewTable.put(columnName, columnToolTipDescription.toString());
				
				columnToolTipDescription.setLength(0);
				columnName = "";
			}

			if (rs.next()) {

				// start at row startRow
				rs.absolute(this.startRow);

				// loop through numRowsPerPage times
				for (int j = 0; j < this.numRowsPerPage; j++) {
					ArrayList<String> row = new ArrayList<String>();

					for (int i = 1; i < columnCount + 1; i++) {
						Object value = rs.getObject(i);
						
						SliceColumn column = this.tabularDataContainer
								.getColumn(rsmd.getColumnName(i));
						if (rs.wasNull() || value == null) {
							
							// value = null, then just show an asterisk
							row.add("*");

						} else {
						
							// format scientific notation
							if (column.isSciNotation()) {
				
								String[] formulationRules = TabularPDS4DataUtils
										.parseFieldFormat(column.getFormat());
								
								//extract formulation rules
								if(formulationRules != null) {
								
									// extract precision from format_field
									// formulationRules[2] = precision
									if(formulationRules[2] != null && !formulationRules[2].isEmpty()) {
										int precision = Integer.parseInt(formulationRules[2]);

										StringBuilder decimalFormat = new StringBuilder();
										decimalFormat.append("0.");
										for(int repeatCount = 0; repeatCount < precision; repeatCount++) {
											decimalFormat.append("#");
										}
										decimalFormat.append("E0");
										
										NumberFormat formatter = new DecimalFormat(
												decimalFormat.toString());
										row.add(formatter.format(Double.valueOf(value
												.toString())));
										
										//standard format
									} else {
										
										String sciValue = value.toString();
										sciValue = sciValue.toLowerCase();
										
										// contains a period and E (e.g., 1.2E5)
										if(sciValue.contains(".")
											&& (sciValue.contains("e") || sciValue.contains("E") )) {
										
											// build the right format
											StringBuilder decimalFormat = new StringBuilder();
											int dotIndex = sciValue.indexOf(".");
											int eIndex = sciValue.indexOf("e");
											
											for(int index=0; index<dotIndex; index++)
												decimalFormat.append("0");
											decimalFormat.append(".");
											for(int index=0; index<(eIndex-dotIndex)-1; index++)
												decimalFormat.append("0");
											decimalFormat.append("E");
											for(int index=0; index<(sciValue.length()-eIndex)-1; index++)
												decimalFormat.append("0");
												
											NumberFormat formatter = new DecimalFormat(
													decimalFormat.toString());
											row.add(formatter.format(Double.valueOf(value
													.toString())));
												
											//does not contain a period (e.g., 1E5)
										} else if(!sciValue.contains(".")
												&& (sciValue.contains("e") || sciValue.contains("E") )) {
											
											// build the right format
											StringBuilder decimalFormat = new StringBuilder();
											int eIndex = sciValue.indexOf("e");
											
											for(int index=0; index<eIndex; index++)
												decimalFormat.append("0");
											decimalFormat.append("E");
											for(int index=0; index<(sciValue.length()-eIndex)-1; index++)
												decimalFormat.append("0");
											
											NumberFormat formatter = new DecimalFormat(
													decimalFormat.toString());
											row.add(formatter.format(Double.valueOf(value
													.toString())));
											
										} else {
											// format
											NumberFormat formatter = new DecimalFormat(
													"0.#####E0");
											row.add(formatter.format(Double.valueOf(value
													.toString())));	
										}
									}
								} else {
									
									String sciValue = value.toString();
									sciValue = sciValue.toLowerCase();
									
									// contains a period and E (e.g., 1.2E5)
									if(sciValue.contains(".")
										&& (sciValue.contains("e") || sciValue.contains("E") )) {
									
										// build the right format
										StringBuilder decimalFormat = new StringBuilder();
										int dotIndex = sciValue.indexOf(".");
										int eIndex = sciValue.indexOf("e");
										
										for(int index=0; index<dotIndex; index++)
											decimalFormat.append("0");
										decimalFormat.append(".");
										for(int index=0; index<(eIndex-dotIndex)-1; index++)
											decimalFormat.append("0");
										decimalFormat.append("E");
										for(int index=0; index<(sciValue.length()-eIndex)-1; index++)
											decimalFormat.append("0");
											
										NumberFormat formatter = new DecimalFormat(
												decimalFormat.toString());
										row.add(formatter.format(Double.valueOf(value
												.toString())));
											
										//does not contain a period (e.g., 1E5)
									} else if(!sciValue.contains(".")
											&& (sciValue.contains("e") || sciValue.contains("E") )) {
										
										// build the right format
										StringBuilder decimalFormat = new StringBuilder();
										int eIndex = sciValue.indexOf("e");
										
										for(int index=0; index<eIndex; index++)
											decimalFormat.append("0");
										decimalFormat.append("E");
										for(int index=0; index<(sciValue.length()-eIndex)-1; index++)
											decimalFormat.append("0");
										
										NumberFormat formatter = new DecimalFormat(
												decimalFormat.toString());
										row.add(formatter.format(Double.valueOf(value
												.toString())));
										
									} else {
										// format
										NumberFormat formatter = new DecimalFormat(
												"0.#####E0");
										row.add(formatter.format(Double.valueOf(value
												.toString())));	
									}
								}

							} else if (column.getDateFormat() != null
									&& column.getDateFormat().length() != 0) {
								// date column is stored as long, need to
								// display in correct date format
								
								// TODO: Format date correctly. For now just store 
								// a string
								/*
								SimpleDateFormat formatter = new SimpleDateFormat(
										column.getDateFormat());

								Date date = new Date(Long.valueOf(value
										.toString()));
								row.add(formatter.format(date));
								*/
								row.add(value.toString());

							} else {
							
								// if the value is equal to the missing constant, let user know
								// that the value is a missing constant by appending an asterisk
								// this is used in PDS3 labels
								if(value.toString().equalsIgnoreCase(column.getMissingConstant())) {
									row.add(value.toString()+"*");
								
								// PDS4 special constants. If the value is the same as the special
								// constant, append an asterisk
								} else if (column.getSpecialConstants() != null) {
									
									SpecialConstants specialConstants = column.getSpecialConstants();
									
									// error constant
									if (specialConstants.getErrorConstant() != null
											&& !specialConstants.getErrorConstant().isEmpty()) {
										
										if (specialConstants.getErrorConstant()
												.equalsIgnoreCase(
														value.toString()))
											row.add(value.toString() + "*");
										else 
											row.add(value.toString());
										
										// invalid constant
									} else if (specialConstants.getInvalidConstant() != null
											&& !specialConstants.getInvalidConstant().isEmpty()) {
										
										if (specialConstants.getInvalidConstant()
												.equalsIgnoreCase(
														value.toString()))
											row.add(value.toString() + "*");
										else 
											row.add(value.toString());
										
										// missing constant
									} else if (specialConstants.getMissingConstant() != null
											&& !specialConstants.getMissingConstant().isEmpty()) {
										
										if (specialConstants.getMissingConstant()
												.equalsIgnoreCase(
														value.toString()))
											row.add(value.toString() + "*");
										else 
											row.add(value.toString());
										
										// not applicable constant
									} else if (specialConstants.getNotApplicableConstant() != null
											&& !specialConstants.getNotApplicableConstant() 
													.isEmpty()) {
										
										if (specialConstants.getNotApplicableConstant()
												.equalsIgnoreCase(
														value.toString()))
											row.add(value.toString() + "*");
										else 
											row.add(value.toString());
										
										// saturated constant
									} else if (specialConstants.getSaturatedConstant() != null
											&& !specialConstants.getSaturatedConstant()
													.isEmpty()) {
										
										if (specialConstants.getSaturatedConstant()
												.equalsIgnoreCase(
														value.toString()))
											row.add(value.toString() + "*");
										else 
											row.add(value.toString());
										
										// unknown constant
									} else if (specialConstants.getUnknownConstant() != null
											&& !specialConstants.getUnknownConstant().isEmpty()) {
										
										if (specialConstants.getUnknownConstant()
												.equalsIgnoreCase(
														value.toString()))
											row.add(value.toString() + "*");
										else 
											row.add(value.toString());
										
										// just add the value
									} else {
										row.add(value.toString());
									}
									// normal value
								} else {
									row.add(value.toString());
								}	
							}
						}
					}
					this.previewRows.add(row);
					// break out if at last record
					if (rs.isLast())
						break;
					rs.next();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("error retrieving preview data "); //$NON-NLS-1$
		} catch (Exception e) {
			setException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ignored) {
				// do nothing
			}
		}
	}

	/*
	 * used in manageTabularData.jsp to format numbers
	 */
	public boolean isNumber(String value) {
		if (StrUtils.isNumber(value))
			return true;
		return false;
	}

}
