package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.managers.DBManager;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.opensymphony.xwork2.Action;

public class LoggingTest extends BaseTestAction {
	private String procId;
	private LoadLabel loadLabel;
	private String loadLabelResult;
	private File testLabel;
	private String testLabelURL;
	private LoadData loadData;
	private String loadDataResult;
	private SliceContainer slice;
	private TabularDataContainer table;

	private SelectRows enterRowCriteria;
	private String enterRowCriteriaResult;
	private SaveSelectedRows saveSelectedRows;
	private String saveSelectedRowsResult;

	private Download download;
	private String downloadResult;

	@Override
	protected void clearAction() {
		// TODO Auto-generated method stub
		this.procId = null;
		this.loadLabel = null;
		this.loadLabelResult = null;
		this.loadData = null;
		this.loadDataResult = null;
		this.testLabel = null;
		this.testLabelURL = null;
		this.slice = null;
		this.table = null;
		this.enterRowCriteria = null;
		this.enterRowCriteriaResult = null;
		this.saveSelectedRows = null;
		this.saveSelectedRowsResult = null;
		this.download = null;

	}

	private File getTabularDataPath() {
		return new File(getTestDataDirectory(), "tabularData");//$NON-NLS-1$ 
	}

	/*
	 * builds loadLabel object based on test file
	 * 
	 * @param labelFileName name of label file in test directory to be read
	 */
	private void retrieveLabel(String labelFileName) throws Exception {
		this.loadLabel = createAction(LoadLabel.class);
		this.testLabel = new File(getTabularDataPath(), labelFileName);
		// store the url value for comparisons
		this.testLabelURL = this.testLabel.toURI().toURL().toString();
		this.loadLabel.setLabelURLString(this.testLabelURL);
		this.loadLabelResult = this.loadLabel.execute();
	}

	private void retrieveData(String labelFileName) throws Exception {
		// get test data
		retrieveLabel(labelFileName);

		this.procId = this.loadLabel.getProcId();
		this.loadData = createAction(LoadData.class);
		this.loadData.blocking = true;
		this.loadData.setProcId(this.procId);
		this.loadDataResult = this.loadData.execute();
		assertEquals(Action.SUCCESS, this.loadDataResult);
		TabularDataProcess process = (TabularDataProcess) this.loadData
				.getProcess();
		this.slice = process.getSlice();
		this.table = this.slice.getActiveTabularData();

	}

	@SuppressWarnings("nls")
	public void testLogLabelOccurs() throws Exception {
		retrieveLabel("DIONE.LBL"); //$NON-NLS-1$
		assertEquals(Action.SUCCESS, this.loadLabelResult);
		TabularDataProcess tabularDataProcess = (TabularDataProcess) this.loadLabel
				.getProcess();
		this.slice = tabularDataProcess.getSlice();
		// test db record exists from call to
		// LogManager.logLabel(slice);

		Connection connection = DBManager.getConnection();
		Statement queryStmt = connection.createStatement();
		// use query to get last id and use it to query
		ResultSet rs = queryStmt
				.executeQuery("SELECT MAX(slice_id) FROM slice");
		assertEquals(true, rs.next());
		long id = rs.getLong(1);

		queryStmt = connection.createStatement();
		rs = queryStmt
				.executeQuery("SELECT session_id, label_url, user_ip, time_created FROM slice WHERE slice_id = '"
						+ id + "'");
		assertEquals(true, rs.next());
		// test we have the correct values in the db
		assertEquals(rs.getString(1), HTTPUtils.getSessionId());
		assertEquals(rs.getString(2), this.slice.getLabelURLString());
		assertEquals(rs.getString(3), HTTPUtils.getRequestIP());
		assertEquals(rs.getDate(4) != null, true);

		connection.close();
	}

	@SuppressWarnings("nls")
	public void testLogTabulardataOccurs() throws Exception {
		retrieveData("24color.lbl");
		// test values in tabulardata table are correct
		Connection connection = DBManager.getConnection();
		Statement queryStmt = connection.createStatement();
		// use query to get last id and use it to query
		ResultSet rs = queryStmt
				.executeQuery("SELECT MAX(tabulardata_id) FROM tabulardata");
		assertEquals(true, rs.next());
		long id = rs.getLong(1);

		queryStmt = connection.createStatement();
		rs = queryStmt
				.executeQuery("SELECT slice_id, table_url, columns_orig, rows_orig, table_type FROM tabulardata WHERE tabularData_id = '"
						+ id + "'");
		assertEquals(true, rs.next());
		assertEquals(rs.getLong(1), this.slice.getDbId());
		assertEquals(rs.getString(2), this.table.getTabFileUrl());
		assertEquals(rs.getInt(3), this.table.getColumns().size());
		assertEquals(rs.getInt(4), this.table.getTotalRows());
		assertEquals(rs.getString(5), this.table.getType());

		connection.close();

	}

	@SuppressWarnings("nls")
	public void testLogFilterOccured() throws Exception {
		retrieveData("DIONE.LBL");
		this.enterRowCriteria = createAction(SelectRows.class);
		this.enterRowCriteria.setProcId(this.procId);
		this.enterRowCriteriaResult = this.enterRowCriteria.execute();
		assertEquals(Action.INPUT, this.enterRowCriteriaResult);
		this.saveSelectedRows = createAction(SaveSelectedRows.class);
		this.saveSelectedRows.setProcId(this.procId);

		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		// test values for queryMode are passed through
		this.saveSelectedRows.setQueryMode("OR");
		// set other condition values
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.IS
						.toString());
		this.saveSelectedRows.setValue("U46A010ER");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();

		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		// test it processed without error
		assertEquals(this.saveSelectedRows.hasErrors(), false);

		// test logFilter has occured, which is called in in table.addConditions
		Connection connection = DBManager.getConnection();
		Statement queryStmt = connection.createStatement();
		ResultSet rs = queryStmt
				.executeQuery("SELECT column_name, column_type, filter_condition, condition_value, if_deleted FROM filters WHERE tabulardata_id = '"
						+ this.table.getDbId() + "'");
		assertEquals(true, rs.next());
		RowCriteria condition = this.table.getConditions().get(0);

		// test we have the correct values in the db
		assertEquals(rs.getString(1), "WFPC2 IMAGE NAME");
		assertEquals(rs.getString(1), condition.getColumn().getName());
		assertEquals(rs.getString(2), "CHARACTER");
		assertEquals(rs.getString(2), condition.getColumn().getDataType());
		assertEquals(rs.getString(3), TabularManagementConstants.Condition.IS
				.toString());
		assertEquals(rs.getString(3), condition.getCondition());
		assertEquals(rs.getString(4), "U46A010ER");
		assertEquals(rs.getString(4), condition.getValue());
		assertEquals(rs.getBoolean(5), false);

		connection.close();
	}

	@SuppressWarnings("nls")
	public void testLogDownloadOccurs() throws Exception {
		//
		retrieveData("spectrum.lbl");
		this.download = createAction(Download.class);
		this.download.setProcId(this.procId);
		this.download.blocking = true;
		this.downloadResult = this.download.execute();

		assertEquals(Action.SUCCESS, this.downloadResult);
		// test logDownload has occurred
		Connection connection = DBManager.getConnection();
		Statement queryStmt = connection.createStatement();
		ResultSet rs = queryStmt
				.executeQuery("SELECT headers_included, columns_selected, rows_selected, filter_count, file_type, interchange_format FROM downloads WHERE	tabulardata_id = '"
						+ this.table.getDbId() + "'");
		assertEquals(true, rs.next());
		// test we have the correct values in the db
		assertEquals(String.valueOf(rs.getBoolean(1)), String
				.valueOf(this.slice.includeHeaders()));
		assertEquals(rs.getInt(2), this.table.getSelectedColumns().size());
		assertEquals(rs.getInt(3), this.table.getRowsReturned());
		assertEquals(rs.getInt(4), this.table.getConditions().size());
		assertEquals(rs.getString(5), this.slice.getFileType());
		assertEquals(rs.getString(6), this.table.getFormat());

		connection.close();

	}

}
