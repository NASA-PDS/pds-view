package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.managers.DBManager;
import gov.nasa.pds.web.ui.utils.HTTPUtils;
import gov.nasa.pds.web.ui.utils.TabularDataUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.opensymphony.xwork2.Action;

public class LoadDataTest extends BaseTestAction {

	private LoadData loadData;
	private String loadDataResult;
	private String testLabelURL;
	private SliceContainer slice;
	private TabularDataContainer table;

	/*
	 * retrieves tabularData directory in test directory
	 * 
	 * @return File file object representing tabularDataTest directory
	 */
	private File getTabularDataPath() {
		return new File(getTestDataDirectory(), "tabularData");//$NON-NLS-1$ 
	}

	/*
	 * builds loadLabel object based on test file
	 * 
	 * @param labelFileName name of label file in test directory to be read
	 */
	private void retrieveData(String labelFileName) throws Exception {
		LoadLabel loadLabel = createAction(LoadLabel.class);
		File testLabel = new File(getTabularDataPath(), labelFileName);
		// store the URL value for comparisons
		this.testLabelURL = testLabel.toURI().toURL().toString();
		loadLabel.setLabelURLString(this.testLabelURL);
		String loadLabelResult = loadLabel.execute();
		assertEquals(Action.SUCCESS, loadLabelResult);
		final String procId = loadLabel.getProcId();
		this.loadData = createAction(LoadData.class);
		this.loadData.blocking = true;
		this.loadData.setProcId(procId);
		this.loadDataResult = this.loadData.execute();
		assertEquals(Action.SUCCESS, this.loadDataResult);
		TabularDataProcess process = (TabularDataProcess) this.loadData
				.getProcess();
		this.slice = process.getSlice();
		this.table = this.slice.getActiveTabularData();
	}

	/*
	 * Tests ASCII Table file with rows that have missing constant values
	 */
	@SuppressWarnings("nls")
	public void testMissingConstants() throws Exception {
		retrieveData("24color.lbl");
		// test table name in object is correct
		String tablename = TabularDataUtils.getTabDataTableName(HTTPUtils
				.getSessionId(), this.table.getId());
		assertEquals(tablename, this.table.getTableName());

		// test data table has been created
		Connection connection = DBManager.getConnection();
		Statement queryStmt = connection.createStatement();
		ResultSet rs = queryStmt.executeQuery("SELECT COUNT(*) from "
				+ tablename);
		assertEquals(true, rs.next());
		// test we have the same number of rows in table as expected
		assertEquals(rs.getInt(1), this.table.getTotalRows());

		// test the expected date format has been saved in the DB
		assertEquals("yyyy-MM-dd", this.table.getColumn("REF_DATE")
				.getDateFormat());
		// test various rows that have the missing_constant value in the tab
		// file is saved as null in db
		queryStmt = null;
		queryStmt = connection.createStatement();
		rs = null;
		rs = queryStmt.executeQuery("SELECT REF_DATE from " + tablename
				+ " WHERE AST_NUMBER = '1865'");
		if (rs.next()) {
			rs.getInt("REF_DATE");
			assertEquals(true, rs.wasNull());
		}
		queryStmt = null;
		queryStmt = connection.createStatement();
		rs = null;
		rs = queryStmt.executeQuery("SELECT REFL_1 from " + tablename
				+ " WHERE AST_NUMBER = '4'");
		if (rs.next()) {
			rs.getBigDecimal("REFL_1");
			assertEquals(true, rs.wasNull());
		}

		connection.close();

	}

	@SuppressWarnings("nls")
	public void testBinaryTable() throws Exception {
		// retrieve a label that defines a binary table
		retrieveData("UVVSFC_EAC_04240_220646_HDR.LBL");
		// test slice container fields are read
		assertEquals("UVVSFC_EAC_04240_220646_HDR_DAT", this.slice
				.getProductId());
		assertEquals("MESS-E/V/H-MASCS-3-UVVS-CDR-CALDATA-V1.0", this.slice
				.getDataSetId());
		assertEquals(true, this.slice.getLabelURLString().contains(
				"UVVSFC_EAC_04240_220646_HDR.LBL"));
		// test there is only one tabularDataObject
		assertEquals(1, this.slice.getTabularDataObjects().size());
		// because there is only 1 tabularDataObject the active index should be
		// 0
		assertEquals(0, this.slice.getActiveTabDataIndex());

	}

	@SuppressWarnings("nls")
	public void testASCIITable() throws Exception {
		// retrieve label that defines a valid simple ASCII table
		retrieveData("DIONE.LBL");
		// test all sliceContainer fields
		// test product id is
		assertEquals("DIONE.TAB", this.slice.getProductId());
		// test data_set_id
		assertEquals("HST-S-WFPC2-4-ASTROM2002-V1.0", this.slice.getDataSetId());
		// test labelURLString
		assertEquals(true, this.slice.getLabelURLString().contains("DIONE.LBL"));
		// test labelFileName
		assertEquals("DIONE.LBL", this.slice.getLabelFileName());
		// test there is only one tabularDataObject
		assertEquals(1, this.slice.getTabularDataObjects().size());
		// because there is only 1 tabularDataObject the active index should be
		// 0
		assertEquals(0, this.slice.getActiveTabDataIndex());

		// test there are 6 columns, all of which are also in selected columns
		// and the selected column count is 6
		assertEquals(6, this.table.getColumns().size());
		assertEquals(6, this.table.getSelectedColumns().size());
		assertEquals(6, this.table.getSelectedColumnCount());

		// test some values in the columns
		assertEquals("WFPC2 IMAGE NAME", this.table.getColumns().get(0)
				.getName().toString());

		// test there are the same number of columns in db as label
		Connection connection = DBManager.getConnection();

		Statement stmt = connection.createStatement();
		ResultSet rset = stmt
				.executeQuery("SELECT COUNT(*) FROM columns WHERE tabular_container_id = '"
						+ this.table.getId() + "'");
		assertEquals(true, rset.next());
		assertEquals(rset.getInt(1), this.table.getColumns().size());

		// TODO could test some actual values in the db

		// test table values
		assertEquals("DIONE.TAB", this.table.getDataFile().getName());
		assertEquals(true, this.table.getTabFileUrl().contains("DIONE.TAB"));
		assertEquals("DIONE.TAB", this.table.getTabFileName());

		// test totalRows is 14
		assertEquals(14, this.table.getTotalRows());
		// test tablename is correct
		// test table name in object is correct
		String tablename = TabularDataUtils.getTabDataTableName(HTTPUtils
				.getSessionId(), this.table.getId());
		assertEquals(tablename, this.table.getTableName());

		// test data table has been created
		Statement queryStmt = connection.createStatement();
		ResultSet rs = queryStmt.executeQuery("SELECT COUNT(*) from "
				+ tablename);
		assertEquals(true, rs.next());
		assertEquals(rs.getInt(1), this.table.getTotalRows());

		// test type is table
		assertEquals("TABLE", this.table.getType());
		// test delimiter value is null because we have table obj not
		// spreadsheet
		assertEquals(null, this.table.getDelimiter());

		connection.close();

		// TODO test values in the data table in DB
	}

	/*
	 * tests a PDS ASCII Spectrum can be read. Compares various values in
	 * TabularDataContainer with known values.
	 */
	@SuppressWarnings("nls")
	public void testASCIISpectrum() throws Exception {
		// retrieve label that defines an ASCII Spectrum object
		retrieveData("spectrum.lbl");
		// test all sliceContainer fields
		// test product id is
		assertEquals("FL14SR01.SPC", this.slice.getProductId());
		// test data_set_id
		assertEquals("CLEM1-L-RSS-5-BSR-V1.0", this.slice.getDataSetId());
		// test process's slice has correct URL
		assertEquals(true, this.slice.getLabelURLString().contains(
				"spectrum.lbl"));
		assertEquals("spectrum.lbl", this.slice.getLabelFileName());
		assertEquals(1, this.slice.getTabularDataObjects().size());
		assertEquals(0, this.slice.getActiveTabDataIndex());

		// test table values
		// test tabular file name has been saved
		assertEquals("FL14SR01.SPC", this.table.getDataFile().getName());
		assertEquals(true, this.table.getTabFileUrl().contains("FL14SR01.SPC"));
		// actual file is named in lower case, check it gets saved that way
		assertEquals("FL14SR01.SPC", this.table.getTabFileName());

		// test there is 1 column
		assertEquals(1, this.table.getColumns().size());
		assertEquals(1, this.table.getSelectedColumns().size());
		assertEquals(1, this.table.getSelectedColumnCount());
		// test totalRows is 129
		assertEquals(129, this.table.getTotalRows());
		// test table name is saved correctly
		assertEquals(TabularDataUtils.getTabDataTableName(HTTPUtils
				.getSessionId(), this.table.getId()), this.table.getTableName());
		// test type
		assertEquals("SPECTRUM", this.table.getType());
		// test delimiter value is null because we have spectrum obj not
		// spreadsheet
		assertEquals(null, this.table.getDelimiter());

		// test values in the DB
		Connection connection = DBManager.getConnection();

		Statement stmt = connection.createStatement();
		ResultSet rset = stmt
				.executeQuery("SELECT COUNT(*) FROM columns WHERE tabular_container_id = '"
						+ this.table.getId() + "'");
		assertEquals(true, rset.next());
		// test there is 1 row in column table
		assertEquals(rset.getInt(1), this.table.getColumns().size());

		// test the actual values in column table are correct
		// /column_id column_name session_id tabular_container_id label

		Statement colStmt = connection.createStatement();
		ResultSet colrs = colStmt
				.executeQuery("SELECT column_id, column_name, session_id, tabular_container_id, label FROM columns WHERE tabular_container_id = '"
						+ this.table.getId() + "'");

		assertEquals(true, colrs.next());
		// lab 04/05/10 may not need this depending on how culumns toble will be
		// difened
		// assertEquals(colrs.getString(1), this.table.getColumns().get(0)
		// .getIndex());
		assertEquals(colrs.getString(2), this.table.getColumns().get(0)
				.getName());
		assertEquals(colrs.getString(3), HTTPUtils.getSessionId());
		assertEquals(colrs.getString(4), this.table.getId());
		assertEquals(true, colrs.getString(5).contains(
				this.slice.getLabelFileName()));
		// in above statement - db has
		// file:/Users/lbaalman/Documents/workspace/pds/pds_web/test-data/tabularData/spectrum.lbl
		// but this.slice.getLabelFileName() = spectrum.lbl

		// test table name in object is correct
		String tablename = TabularDataUtils.getTabDataTableName(HTTPUtils
				.getSessionId(), this.table.getId());
		assertEquals(tablename, this.table.getTableName());
		// test data table has been created
		Statement queryStmt = connection.createStatement();
		ResultSet rs = queryStmt.executeQuery("SELECT COUNT(*) from "
				+ tablename);
		assertEquals(true, rs.next());
		assertEquals(rs.getInt(1), this.table.getTotalRows());

		// TODO: test various values in db table

		// test some values in the columns of the table object
		assertEquals("RCP NOISE SPECTRUM", this.table.getColumns().get(0)
				.getName().toString());

		// test preview rows read correctly
		assertEquals("0.0371", this.table.getRows().get(3).getElements().get(0)
				.getValue().toString().trim());

		connection.close();

	}

	// @SuppressWarnings("nls")
	// public void testACIISeriesWithStructure() throws Exception {
	// retrieveData("T92025.LBL");
	//
	// // test product id is
	// assertEquals("T92025.TAB", this.slice.getProductId());
	// // test data_set_id
	// assertEquals("ULY-J-COSPIN-LET-3-RDR-FLUX-32SEC-V1.0", this.slice
	// .getDataSetId());
	// // test labelURLString
	// assertEquals(true, this.slice.getLabelURLString().contains(
	// "T92025.LBL"));
	// // test labelFileName
	// assertEquals("T92025.LBL", this.slice.getLabelFileName());
	// // test there is only one tabularDataObject
	// assertEquals(1, this.slice.getTabularDataObjects().size());
	// // because there is only 1 tabularDataObject the active index should be
	// // 0
	// assertEquals(0, this.slice.getActiveTabDataIndex());
	//
	// // test table values
	// // dataFile
	// assertEquals("T92025.TAB", this.table.getDataFile().getName());
	// assertEquals(true, this.table.getTabFileUrl().contains(
	// "T92025.TAB"));
	// assertEquals("T92025.TAB", this.table.getTabFileName());
	//
	// // test there are 6 columns, all of which are also in selected columns
	// // and the selected column count is correct
	//
	// // LAB 09/27/09 this fails because object has double the number of
	// // columns but works when run via the browser
	// assertEquals(39, this.table.getColumns().size());
	// assertEquals(39, this.table.getSelectedColumns().size());
	// assertEquals(39, this.table.getSelectedColumnCount());
	// // test there are the same number of columns in db as label
	// Connection connection = DBManager.getConnection();
	//
	// Statement stmt = connection.createStatement();
	// ResultSet rset = stmt
	// .executeQuery("SELECT COUNT(*) FROM columns WHERE tabular_container_id = '"
	// + this.table.getId() + "'");
	// assertEquals(true, rset.next());
	// assertEquals(rset.getInt(1), this.table.getColumns().size());
	// // test totalRows i
	// assertEquals(1787, this.table.getTotalRows());
	//
	// // test tablename is correct
	// assertEquals(TabularDataUtils.getTabDataTableName(HTTPUtils
	// .getSessionId(), this.table.getId()), this.table.getTableName());
	//
	// // test type is correct
	// assertEquals(true, this.table.getType().endsWith("SERIES"));
	// // test delimiter value is null because we have table obj not
	// // spreadsheet
	// assertEquals(null, this.table.getDelimiter());
	//
	// // test table name in object is correct
	// String tablename = TabularDataUtils.getTabDataTableName(HTTPUtils
	// .getSessionId(), this.table.getId());
	// assertEquals(tablename, this.table.getTableName());
	// // test data table has been created
	// Statement queryStmt = connection.createStatement();
	// ResultSet rs = queryStmt.executeQuery("SELECT COUNT(*) from "
	// + tablename);
	// assertEquals(true, rs.next());
	// assertEquals(rs.getInt(1), this.table.getTotalRows());
	//
	// // test some values in the columns
	// assertEquals("TIME", this.table.getColumns().get(0).getName()
	// .toString());
	//
	// // TODO USE VALUES HERE TO TEST SCIENTIFIC NOTATION IS CORRECCTLY
	// // HANDLED IN BOTH DB AND PREVIEW ROWS
	//
	// }

	public void testTableInFileWithStructure() throws Exception {
		//	retrieveData("structure.lbl"); //$NON-NLS-1$
		// test some values
		// assertEquals(1, this.slice.getTabularDataObjects().size());
		// TODO test correct number of columns have been created
	}

	/*
	 * header - start of table defined with row id
	 */
	@SuppressWarnings("nls")
	public void testTableWithHeader() throws Exception {
		retrieveData("TableWithHeader.lbl");
		// test product id is
		assertEquals("PHX_TAU451_030_20081203A", this.slice.getProductId());
		// test data_set_id
		assertEquals("PHX-M-SSI-5-ATMOS-OPACITY-V1.0", this.slice
				.getDataSetId());
		// test labelURLString
		assertEquals(true, this.slice.getLabelURLString().contains(
				"TableWithHeader.lbl"));
		// test labelFileName
		assertEquals("TableWithHeader.lbl", this.slice.getLabelFileName());
		// test there is only one tabularDataObject
		assertEquals(1, this.slice.getTabularDataObjects().size());
		// because there is only 1 tabularDataObject the active index should be
		// 0
		assertEquals(0, this.slice.getActiveTabDataIndex());

		// test table values
		// dataFile
		assertEquals("PHX_TAU451_030_20081203A.TAB", this.table.getDataFile()
				.getName());
		assertEquals(true, this.table.getTabFileUrl().contains(
				"PHX_TAU451_030_20081203A.TAB"));
		assertEquals("PHX_TAU451_030_20081203A.TAB", this.table
				.getTabFileName());

		assertEquals(8, this.table.getColumns().size());
		assertEquals(8, this.table.getSelectedColumns().size());
		assertEquals(8, this.table.getSelectedColumnCount());
		// test there are the same number of columns in db as label
		Connection connection = DBManager.getConnection();

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT COUNT(*) FROM columns WHERE tabular_container_id = '"
						+ this.table.getId() + "'");
		assertEquals(true, rs.next());
		assertEquals(rs.getInt(1), this.table.getColumns().size());
		// test totalRows i
		assertEquals(51, this.table.getTotalRows());

		// test tablename is correct
		assertEquals(TabularDataUtils.getTabDataTableName(HTTPUtils
				.getSessionId(), this.table.getId()), this.table.getTableName());

		// test type is table
		assertEquals("TABLE", this.table.getType());
		// test delimiter value is null because we have table obj not
		// spreadsheet
		assertEquals(null, this.table.getDelimiter());

		// test table name in object is correct
		String tablename = TabularDataUtils.getTabDataTableName(HTTPUtils
				.getSessionId(), this.table.getId());
		assertEquals(tablename, this.table.getTableName());
		// test data table has been created
		stmt = connection.createStatement();
		rs = stmt.executeQuery("SELECT COUNT(*) from " + tablename);
		assertEquals(true, rs.next());
		assertEquals(rs.getInt(1), this.table.getTotalRows());

		// test some values in the columns
		assertEquals("SSI_PRODUCT_ID", this.table.getColumns().get(0).getName()
				.toString());
		assertEquals("CHARACTER", this.table.getColumns().get(0).getDataType()
				.toString());
		assertEquals("2", this.table.getColumns().get(0).getStartByte()
				.toString());
		assertEquals("27", this.table.getColumns().get(0).getBytes().toString());
		assertEquals("1", String.valueOf(this.table.getColumns().get(0)
				.getIndex()));
		// ASCII_REAL
		assertEquals("SOLAR_FLUX", this.table.getColumns().get(5).getName()
				.toString());
		assertEquals("ASCII_REAL", this.table.getColumns().get(5).getDataType()
				.toString());

		// test some values in the DB
		stmt = connection.createStatement();
		rs = null;
		rs = stmt.executeQuery("SELECT SOLAR_DISTANCE from " + tablename
				+ " WHERE SSI_PRODUCT_ID = 'SS009ESF897023993_11AA7L3M1';");

		assertEquals(true, rs.next());
		assertEquals("1.664", String.valueOf(rs.getFloat("SOLAR_DISTANCE")));

		connection.close();
	}

	/*
	 * tests that date formatting is being passed into column definition for a
	 * date with a valid PDS format
	 */
	public void testValidDateFormat() throws Exception {
		retrieveData("sightings.lbl"); //$NON-NLS-1$
		assertEquals("yyyy-MM-dd'T'HH:mm:ss", this.table.getColumn("OBS_TIME") //$NON-NLS-1$ //$NON-NLS-2$
				.getDateFormat());
		// TODO test the date is stored as long representing UNIX epoch with
		// milliseconds in DB

	}

	/*
	 * this file has dates in the format 0000-01-01T00:00:00.000 which is close
	 * to yyyy-MM-dd'T'HH:mm:ss.SSS but does not quite match any pds dates
	 * allowed. columns are therefore redefined as CHARACTER
	 */
	public void testNonStandardDateFormat() throws Exception {
		retrieveData("ra_050627184806_hktm_eng.lbl"); //$NON-NLS-1$

		assertEquals(true, this.table.getTabFileUrl().contains(
				"RA_050627184806_HKTM_ENG.TAB")); //$NON-NLS-1$

		// TODO test for column type change

	}

	// TODO LAB 09/27/09 KNOW FAILURE currently fails because is looking for
	// 99068.sts instead of
	// 99068.STS (filename case issue)

	public void testLarge() throws Exception {
		retrieveData("ast2mass.lbl"); //$NON-NLS-1$
		// TODO TEST that it works, esp at end of file
	}

	/*
	 * test label where case of table pointer value does not match the case of
	 * the data product file can still be read successfully
	 */
	public void testUrlCaseError() throws Exception {
		retrieveData("tabCaseMismatch.LBL"); //$NON-NLS-1$
		// TODO add tests
	}

	@Override
	protected void clearAction() {
		this.loadData = null;
		this.loadDataResult = null;
		this.testLabelURL = null;
		this.slice = null;
		this.table = null;

	}

}
