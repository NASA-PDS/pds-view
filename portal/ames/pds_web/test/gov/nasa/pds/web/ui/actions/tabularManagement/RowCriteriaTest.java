package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.HTTPUtils;
import gov.nasa.pds.web.ui.utils.TabularDataLoader;
import gov.nasa.pds.web.ui.utils.TabularPDS3DataLoader;

import java.io.File;
import java.util.List;

import com.opensymphony.xwork2.Action;

public class RowCriteriaTest extends BaseTestAction {

	private SelectRows enterRowCriteria;

	private String enterRowCriteriaResult;

	private SaveSelectedRows saveSelectedRows;

	private String saveSelectedRowsResult;

	private File getTabularDataPath() {
		return new File(getTestDataDirectory(), "tabularData");//$NON-NLS-1$ 
	}

	private void retrieveData(String labelFileName) throws Exception {
		// get test data
		LoadLabel loadLabel = createAction(LoadLabel.class);
		final File testLabel = new File(getTabularDataPath(), labelFileName);
		final String testLabelURL = testLabel.toURI().toURL().toString();
		loadLabel.setLabelURLString(testLabelURL);
		loadLabel.execute();
		final String procId = loadLabel.getProcId();
		TabularDataProcess process = (TabularDataProcess) loadLabel
				.getProcess();
		SliceContainer slice = process.getSlice();
		StatusContainer status = new StatusContainer();
		TabularDataLoader tabularDataLoader = new TabularPDS3DataLoader(slice,
				status, HTTPUtils.getSessionId());

		tabularDataLoader.readData(slice.getActiveTabularData());
		LogManager.logTabulardata(slice);

		process.setSlice(slice);

		this.enterRowCriteria = createAction(SelectRows.class);
		this.enterRowCriteria.setProcId(procId);
		this.enterRowCriteriaResult = this.enterRowCriteria.execute();
		assertEquals(Action.INPUT, this.enterRowCriteriaResult);
		this.saveSelectedRows = createAction(SaveSelectedRows.class);
		this.saveSelectedRows.setProcId(procId);
	}

	/*
	 * test error conditions if parts not selected
	 */
	@SuppressWarnings("nls")
	public void testColumnMissing() throws Exception {
		retrieveData("DIONE.LBL");
		this.saveSelectedRows.setSelectedColumn("");
		this.saveSelectedRows.setValue("");
		this.saveSelectedRows.setCondition("");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.INPUT, this.saveSelectedRowsResult);
		assertEquals(this.saveSelectedRows.hasErrors(), true);
		List<String> errMessages = this.saveSelectedRows.getErrorMessages();
		assertEquals(true, errMessages.get(0).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.noColumnSelected", true)));
		assertEquals(true, errMessages.get(1).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.noCondition", true)));
		assertEquals(true, errMessages.get(2).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.noValueEntered", true)));
	}

	@SuppressWarnings("nls")
	public void testConditionMissing() throws Exception {
		retrieveData("DIONE.LBL");
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		// fill in with blank to prevent null error
		this.saveSelectedRows.setValue("");
		this.saveSelectedRows.setCondition("");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.INPUT, this.saveSelectedRowsResult);
		assertEquals(this.saveSelectedRows.hasErrors(), true);
		List<String> errMessages = this.saveSelectedRows.getErrorMessages();
		assertEquals(true, errMessages.get(0).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.noCondition", true)));
		assertEquals(true, errMessages.get(1).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.noValueEntered", true)));
	}

	@SuppressWarnings("nls")
	public void testValueMissing() throws Exception {
		retrieveData("DIONE.LBL");
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.STARTS_WITH
						.toString());
		this.saveSelectedRows.setValue("");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.INPUT, this.saveSelectedRowsResult);
		assertEquals(this.saveSelectedRows.hasErrors(), true);
		List<String> errMessages = this.saveSelectedRows.getErrorMessages();
		assertEquals(true, errMessages.get(0).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.noValueEntered", true)));

	}

	/*
	 * test non numeric value entered when condition type is numeric
	 */
	@SuppressWarnings("nls")
	public void testNonNumericValue() throws Exception {
		retrieveData("DIONE.LBL");
		this.saveSelectedRows.setSelectedColumn("RA*COS(DEC) OFFSET");
		// set other condition values
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.EQUALS
						.toString());
		this.saveSelectedRows.setValue("a");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.INPUT, this.saveSelectedRowsResult);
		List<String> errMessages = this.saveSelectedRows.getErrorMessages();
		assertEquals(true, errMessages.get(0).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.numericOnly", true)));

		this.saveSelectedRows.setValue("09l/27/2009");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.INPUT, this.saveSelectedRowsResult);
		errMessages = this.saveSelectedRows.getErrorMessages();
		assertEquals(true, errMessages.get(0).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.numericOnly", true)));
	}

	/*
	 * test unrecognized date error
	 */
	@SuppressWarnings("nls")
	public void testUnKnownDateFormat() throws Exception {
		retrieveData("24color.lbl");
		this.saveSelectedRows.setSelectedColumn("REF_DATE");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.IS_BEFORE
						.toString());
		this.saveSelectedRows.setValue("06/20/1979");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.INPUT, this.saveSelectedRowsResult);
		assertEquals(this.saveSelectedRows.hasErrors(), true);
		List<String> errMessages = this.saveSelectedRows.getErrorMessages();
		assertEquals(true, errMessages.get(0).contains(
				this.saveSelectedRows.getUIManager().getTxt(
						"selectRows.error.unrecognizedDateFormat", true)));

	}

	/*
	 * test normal processing for addition of column, condition, and value for
	 */
	@SuppressWarnings("nls")
	public void testAddRowCriteria() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		// test values for queryMode are passed through
		this.saveSelectedRows.setQueryMode("OR");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		// when submitted via change of select column, test the logic sets
		// actionString to BUILD_RULE
		assertEquals(Action.INPUT, this.saveSelectedRowsResult);
		// get table to test value of query mode
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		TabularDataContainer table = process.getSlice().getActiveTabularData();
		assertEquals("OR", table.getQueryMode());
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
		// test the condition was saved in the active tabularDataObject
		assertEquals(1, this.saveSelectedRows.tabularDataProcess.getSlice()
				.getActiveTabularData().getConditions().size());
		// test values were correctly saved in the RowCriteria object
		RowCriteria condition = this.saveSelectedRows.tabularDataProcess
				.getSlice().getActiveTabularData().getConditions().get(0);
		assertEquals("WFPC2 IMAGE NAME", condition.getColumn().getName());
		assertEquals(TabularManagementConstants.Condition.IS.toString(),
				condition.getCondition());
		assertEquals("U46A010ER", condition.getValue());

	}

	@SuppressWarnings("nls")
	public void testRemoveRow() throws Exception {
		retrieveData("DIONE.LBL");
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.INPUT, this.saveSelectedRowsResult);

		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.IS
						.toString());
		this.saveSelectedRows.setValue("U46A010ER");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		assertEquals(this.saveSelectedRows.hasErrors(), false);
		// test condition was added
		assertEquals(1, this.saveSelectedRows.getTabularDataProcess()
				.getSlice().getActiveTabularData().getConditions().size());

		// set values for removal (this mimics the remove links created in the
		// form
		this.saveSelectedRows.setColumnToRemove("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setConditionToRemove(TabularManagementConstants.Condition.IS
						.toString());
		this.saveSelectedRows.setValueToRemove("U46A010ER");

		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		// test the condition list has no entries now
		assertEquals(0, this.saveSelectedRows.tabularDataProcess.getSlice()
				.getActiveTabularData().getConditions().size());
	}

	@SuppressWarnings("nls")
	public void testStringIs() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.IS
						.toString());
		this.saveSelectedRows.setValue("U46A010ER");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(1, process.getSlice().getActiveTabularData()
				.getRowsReturned());
	}

	@SuppressWarnings("nls")
	public void testStringIsNot() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.IS_NOT
						.toString());
		this.saveSelectedRows.setValue("U46A010ER");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(13, process.getSlice().getActiveTabularData()
				.getRowsReturned());
	}

	@SuppressWarnings("nls")
	public void testStringContains() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.CONTAINS
						.toString());
		this.saveSelectedRows.setValue("30");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(8, process.getSlice().getActiveTabularData()
				.getRowsReturned());
	}

	@SuppressWarnings("nls")
	public void testStringDoesNotContain() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.DOES_NOT_CONTAIN
						.toString());
		this.saveSelectedRows.setValue("30");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(6, process.getSlice().getActiveTabularData()
				.getRowsReturned());
	}

	@SuppressWarnings("nls")
	public void testStringStartsWith() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.STARTS_WITH
						.toString());
		this.saveSelectedRows.setValue("U4");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(6, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testStringDoesNotStartWith() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.DOES_NOT_START_WITH
						.toString());
		this.saveSelectedRows.setValue("U4");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(8, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testStringEndsWith() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.ENDS_WITH
						.toString());
		this.saveSelectedRows.setValue("T");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(8, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testStringDoesNotEndWith() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("WFPC2 IMAGE NAME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.DOES_NOT_END_WITH
						.toString());
		this.saveSelectedRows.setValue("T");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(6, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testNumberEquals() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("RA*COS(DEC) OFFSET");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.EQUALS
						.toString());
		this.saveSelectedRows.setValue("-6.797");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(1, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testNumberDoesNotEqual() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("RA*COS(DEC) OFFSET");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.DOES_NOT_EQUAL
						.toString());
		this.saveSelectedRows.setValue("-6.797");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(13, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testNumberGreaterThan() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("RA*COS(DEC) OFFSET");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.GREATER_THAN
						.toString());
		this.saveSelectedRows.setValue("-10");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(6, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testNumberLessThan() throws Exception {
		retrieveData("DIONE.LBL");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("RA*COS(DEC) OFFSET");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.LESS_THAN
						.toString());
		this.saveSelectedRows.setValue("-10");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(8, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	// TODO LAB 09/27/09 currently do not have any test data with a a boolean
	// coulmn
	// @SuppressWarnings("nls")
	public void testBooleanIsTrue() throws Exception {
		// retrieveData("DIONE.LBL");
		// // note: changing select column submits via the form
		// this.saveSelectedRows.setSelectedColumn("RA*COS(DEC) OFFSET");
		// this.saveSelectedRows
		// .setCondition(TabularManagementConstants.Condition.LESS_THAN
		// .toString());
		// this.saveSelectedRows.setValue("-10");
		// this.saveSelectedRows.setAddCondition("filler");
		// this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		// assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		// TabularDataProcess process = (TabularDataProcess)
		// this.saveSelectedRows
		// .getProcess();
		// assertEquals(8, process.getSlice().getActiveTabularData()
		// .getRowsReturned());

	}

	// TODO LAB 09/27/09 currently do not have any test data with a a boolean
	// coulmn
	// @SuppressWarnings("nls")
	public void testBooleanIsFalse() throws Exception {
		// retrieveData("DIONE.LBL");
		// // note: changing select column submits via the form
		// this.saveSelectedRows.setSelectedColumn("RA*COS(DEC) OFFSET");
		// this.saveSelectedRows
		// .setCondition(TabularManagementConstants.Condition.LESS_THAN
		// .toString());
		// this.saveSelectedRows.setValue("-10");
		// this.saveSelectedRows.setAddCondition("filler");
		// this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		// assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		// TabularDataProcess process = (TabularDataProcess)
		// this.saveSelectedRows
		// .getProcess();
		// assertEquals(8, process.getSlice().getActiveTabularData()
		// .getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testDateIsBefore() throws Exception {
		retrieveData("sightings-partial.lbl");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("OBS_TIME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.IS_BEFORE
						.toString());
		this.saveSelectedRows.setValue("1983-05-23T21:35:35");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(6, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testDateIsAfter() throws Exception {
		retrieveData("sightings-partial.lbl");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("OBS_TIME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.IS_AFTER
						.toString());
		this.saveSelectedRows.setValue("1983-05-23T21:35:35");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(3, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testDateIs() throws Exception {
		retrieveData("sightings-partial.lbl");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("OBS_TIME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.DATE_IS
						.toString());
		this.saveSelectedRows.setValue("1983-05-23T21:35:35");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(1, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@SuppressWarnings("nls")
	public void testDateIsNot() throws Exception {
		retrieveData("sightings-partial.lbl");
		// note: changing select column submits via the form
		this.saveSelectedRows.setSelectedColumn("OBS_TIME");
		this.saveSelectedRows
				.setCondition(TabularManagementConstants.Condition.DATE_IS_NOT
						.toString());
		this.saveSelectedRows.setValue("1983-05-23T21:35:35");
		this.saveSelectedRows.setAddCondition("filler");
		this.saveSelectedRowsResult = this.saveSelectedRows.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedRowsResult);
		TabularDataProcess process = (TabularDataProcess) this.saveSelectedRows
				.getProcess();
		assertEquals(9, process.getSlice().getActiveTabularData()
				.getRowsReturned());

	}

	@Override
	protected void clearAction() {
		this.enterRowCriteria = null;
		this.saveSelectedRows = null;
	}

}
