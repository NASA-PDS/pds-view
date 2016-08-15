package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceColumn;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.utils.HTTPUtils;
import gov.nasa.pds.web.ui.utils.TabularDataLoader;
import gov.nasa.pds.web.ui.utils.TabularPDS3DataLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.opensymphony.xwork2.Action;

public class SelectColumnsTest extends BaseTestAction {
	private LoadLabel loadLabel;
	private String loadLabelResult;
	private SelectColumns selectColumns;
	private String selectColumnsResult;
	private SaveSelectedColumns saveSelectedColumns;
	private String saveSelectedColumnsResult;

	private File getTabularDataPath() {
		return new File(getTestDataDirectory(), "tabularData");//$NON-NLS-1$ 
	}

	/*
	 * helper function to create test data
	 */
	private void retrieveData(String labelFileName) throws Exception {
		this.loadLabel = createAction(LoadLabel.class);
		final File testLabel = new File(getTabularDataPath(), labelFileName);
		final String testLabelURL = testLabel.toURI().toURL().toString();
		this.loadLabel.setLabelURLString(testLabelURL);
		this.loadLabelResult = this.loadLabel.execute();
		assertEquals(Action.SUCCESS, this.loadLabelResult);

		final String procId = this.loadLabel.getProcId();

		// create loader from slice in process and call readData
		TabularDataProcess tabularDataProcess = (TabularDataProcess) this.loadLabel
				.getProcess();
		SliceContainer slice = tabularDataProcess.getSlice();
		StatusContainer status = new StatusContainer();
		// create loader
		TabularDataLoader tabularDataLoader = new TabularPDS3DataLoader(
				tabularDataProcess.getSlice(), status, HTTPUtils.getSessionId());
		tabularDataLoader.readData(slice.getActiveTabularData());
		tabularDataProcess.setSlice(tabularDataLoader.getSlice());

		this.selectColumns = createAction(SelectColumns.class);
		this.selectColumns.setProcId(procId);
		this.selectColumnsResult = this.selectColumns.execute();
		assertEquals(Action.INPUT, this.selectColumnsResult);
	}

	private ArrayList<String> buildColumnString(
			TabularDataProcess tabularDataProcess) {
		ArrayList<String> checkedColumns = new ArrayList<String>();
		for (SliceColumn column : tabularDataProcess.getSlice()
				.getActiveTabularData().getColumns()) {
			checkedColumns.add(column.getName());
		}
		return checkedColumns;
	}

	/*
	 * Tests selection of columns by passing in test collection of strings
	 * representing column names in the test data also tests
	 * SaveSelectedColumns.pushBackUserInput()
	 */
	@SuppressWarnings("nls")
	@Test
	public void testSaveSelectedColumns() throws Exception {
		retrieveData("DIONE.LBL");
		final String procId = this.selectColumns.getProcId();
		this.saveSelectedColumns = createAction(SaveSelectedColumns.class);
		// create test collection to simulate checked boxes in UI
		List<String> checkedColumns = new ArrayList<String>();
		// grab name and Id of columns from sample data
		TabularDataContainer table = this.selectColumns.tabularDataProcess
				.getSlice().getActiveTabularData();
		String columnId0 = String.valueOf(table.getColumns().get(0)
				.getColumnId());
		String columnName0 = table.getColumns().get(0).getName();
		String columnId1 = String.valueOf(table.getColumns().get(1)
				.getColumnId());

		checkedColumns.add(columnId0);
		checkedColumns.add(columnId1);
		this.saveSelectedColumns.setCheckedColumns(checkedColumns);
		this.saveSelectedColumns.setProcId(procId);
		// set action string (simulates next button pressed in UI)
		this.saveSelectedColumns.setSave("actionstring");
		this.saveSelectedColumnsResult = this.saveSelectedColumns.execute();
		assertEquals(Action.SUCCESS, this.saveSelectedColumnsResult);
		// compare size of test collection and table's selectedColumns
		// collection
		assertEquals(checkedColumns.size(), table.getSelectedColumns().size());
		// test a specific entry from the test collection exists in table's
		// selectedColumns

		assertEquals(true, table.getSelectedColumns().contains(
				table.getColumn(columnName0)));

		// test one of the selected column's property isSelected = true
		assertEquals(true, table.getColumn(columnName0).isSelected());
	}

	@SuppressWarnings("nls")
	@Test
	public void testSortUI() throws Exception {
		retrieveData("DIONE.LBL");

		// apply a sort
		this.selectColumns.setSort(SaveSelectedColumns.DATATYPE_ASC);
		this.selectColumnsResult = this.selectColumns.execute();
		// test the first item in columnDisplay to determine if sort had desired
		// affect, putting data types in alpha order
		assertEquals("ASCII_REAL", this.selectColumns.getColumnCheckboxes()
				.get(0).getDatatype());

		// apply a sort
		this.selectColumns.setSort(SaveSelectedColumns.DATATYPE_DESC);
		this.selectColumnsResult = this.selectColumns.execute();
		// test the first item in columnDisplay to determine if sort had desired
		// affect, putting data types in alpha order
		assertEquals("CHARACTER", this.selectColumns.getColumnCheckboxes().get(
				0).getDatatype());

		// apply a sort
		this.selectColumns.setSort(SaveSelectedColumns.NAME_ASC);
		this.selectColumnsResult = this.selectColumns.execute();
		// test the first item in columnDisplay to determine if sort had desired
		// affect, putting data types in alpha order
		assertEquals("DEC OFFSET", this.selectColumns.getColumnCheckboxes()
				.get(0).getLabel());

		// apply a sort NAME_DESC
		this.selectColumns.setSort(SaveSelectedColumns.NAME_DESC);
		this.selectColumnsResult = this.selectColumns.execute();
		// test the first item in columnDisplay to determine if sort had desired
		// affect, putting data types in alpha order
		assertEquals("WFPC2 IMAGE NAME", this.selectColumns
				.getColumnCheckboxes().get(0).getLabel());

	}

	@SuppressWarnings("nls")
	public void testSortButtons() throws Exception {
		retrieveData("DIONE.LBL");
		final String procId = this.selectColumns.getProcId();
		this.saveSelectedColumns = createAction(SaveSelectedColumns.class);
		this.saveSelectedColumns.setProcId(procId);
		this.saveSelectedColumns
				.setCheckedColumns(buildColumnString(this.selectColumns.tabularDataProcess));
		this.saveSelectedColumns.setSavedSort("");
		this.saveSelectedColumns.setSortColumn("name");
		this.saveSelectedColumnsResult = this.saveSelectedColumns.execute();
		assertEquals(SaveSelectedColumns.NAME_ASC, this.saveSelectedColumns
				.getSort());
		assertEquals(SaveSelectedColumns.SORT, this.saveSelectedColumnsResult);
		this.saveSelectedColumns.setSavedSort(SaveSelectedColumns.NAME_ASC);
		this.saveSelectedColumns.setSortColumn("name");
		this.saveSelectedColumnsResult = this.saveSelectedColumns.execute();
		assertEquals(SaveSelectedColumns.NAME_DESC, this.saveSelectedColumns
				.getSort());
		assertEquals(SaveSelectedColumns.SORT, this.saveSelectedColumnsResult);

		this.saveSelectedColumns.setSortColumn("datatype");
		this.saveSelectedColumnsResult = this.saveSelectedColumns.execute();
		assertEquals(SaveSelectedColumns.DATATYPE_ASC, this.saveSelectedColumns
				.getSort());
		assertEquals(SaveSelectedColumns.SORT, this.saveSelectedColumnsResult);
		this.saveSelectedColumns.setSavedSort(SaveSelectedColumns.DATATYPE_ASC);
		this.saveSelectedColumns.setSortColumn("datatype");
		this.saveSelectedColumnsResult = this.saveSelectedColumns.execute();
		assertEquals(SaveSelectedColumns.DATATYPE_DESC,
				this.saveSelectedColumns.getSort());
		assertEquals(SaveSelectedColumns.SORT, this.saveSelectedColumnsResult);
	}

	/** *** Exception tests **** */

	/*
	 * test SaveSelectedColumns.validateUserInput() using checkedColumns List
	 * with no items to simulate none selected in UI
	 */
	public void testNoColumnsSelected() throws Exception {
		retrieveData("DIONE.LBL"); //$NON-NLS-1$
		final String procId = this.selectColumns.getProcId();
		this.saveSelectedColumns = createAction(SaveSelectedColumns.class);
		List<String> checkedColumns = new ArrayList<String>();
		this.saveSelectedColumns.setCheckedColumns(checkedColumns);
		this.saveSelectedColumns.setProcId(procId);
		this.saveSelectedColumns.setSave("filler"); //$NON-NLS-1$
		this.saveSelectedColumnsResult = this.saveSelectedColumns.execute();
		assertEquals(Action.INPUT, this.saveSelectedColumnsResult);
	}

	@Override
	protected void clearAction() {
		this.loadLabel = null;
		this.selectColumns = null;
		this.saveSelectedColumns = null;

	}

}
