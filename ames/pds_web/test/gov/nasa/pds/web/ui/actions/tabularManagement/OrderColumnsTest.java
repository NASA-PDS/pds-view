package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.utils.HTTPUtils;
import gov.nasa.pds.web.ui.utils.TabularDataLoader;
import gov.nasa.pds.web.ui.utils.TabularPDS3DataLoader;

import java.io.File;

import com.opensymphony.xwork2.Action;

public class OrderColumnsTest extends BaseTestAction {

	private OrderColumns orderColumns;

	private String orderColumnsResult;

	private MoveColumn sortColumns;

	private String sortColumnsResult;

	private SaveColumnOrder saveColumnOrder;

	private String saveColumnOrderResult;

	private File getTabularDataPath() {
		return new File(getTestDataDirectory(), "tabularData");//$NON-NLS-1$ 
	}

	@SuppressWarnings("nls")
	private String retrieveData() throws Exception {
		// get test data
		LoadLabel loadLabel = createAction(LoadLabel.class);
		final File testLabel = new File(getTabularDataPath(), "DIONE.LBL");
		final String testLabelURL = testLabel.toURI().toURL().toString();
		loadLabel.setLabelURLString(testLabelURL);
		loadLabel.execute();
		final String procId = loadLabel.getProcId();

		// get process
		TabularDataProcess tabularDataProcess = (TabularDataProcess) loadLabel
				.getProcess();
		StatusContainer status = new StatusContainer(11);
		// create loader
		TabularDataLoader tabularDataLoader = new TabularPDS3DataLoader(
				tabularDataProcess.getSlice().getLabelURLString(), status,
				HTTPUtils.getSessionId());
		// save slice into process
		tabularDataProcess.setSlice(tabularDataLoader.getSlice());

		// create order columns action
		this.orderColumns = createAction(OrderColumns.class);
		this.orderColumns.setProcId(procId);
		this.orderColumnsResult = this.orderColumns.execute();
		assertEquals(Action.INPUT, this.orderColumnsResult);
		return procId;

	}

	/*
	 * test move column up one
	 */
	@SuppressWarnings("nls")
	public void testMoveColumnUp() throws Exception {
		final String procId = retrieveData();
		// create sort columns action
		this.sortColumns = createAction(MoveColumn.class);
		this.sortColumns.setProcId(procId);
		this.sortColumns.setColumn("OBSERVATION_TIME");
		this.sortColumns.setDirection("up");
		this.sortColumns
				.setColumnsString("WFPC2 IMAGE NAME,OBSERVATION_TIME,RA*COS(DEC) OFFSET,SIGMA OF RA*COS(DEC) OFFSET,DEC OFFSET,SIGMA OF DEC OFFSET");
		this.sortColumnsResult = this.sortColumns.execute();
		assertEquals(Action.SUCCESS, this.sortColumnsResult);
		assertEquals(
				this.sortColumns.getReorderedColumns(),
				"OBSERVATION_TIME,WFPC2 IMAGE NAME,RA*COS(DEC) OFFSET,SIGMA OF RA*COS(DEC) OFFSET,DEC OFFSET,SIGMA OF DEC OFFSET");
		// create saveColumnOrder action
		this.saveColumnOrder = createAction(SaveColumnOrder.class);
		this.saveColumnOrder.setProcId(procId);
		this.saveColumnOrder
				.setColumnsString("OBSERVATION_TIME,WFPC2 IMAGE NAME,RA*COS(DEC) OFFSET,SIGMA OF RA*COS(DEC) OFFSET,DEC OFFSET,SIGMA OF DEC OFFSET");

		// save button clicked
		this.saveColumnOrder.setSave("filler");
		this.saveColumnOrderResult = this.saveColumnOrder.execute();
		assertEquals(Action.SUCCESS, this.saveColumnOrderResult);
		// test that the order index of the column is 1
		assertEquals(this.saveColumnOrder.tabularDataProcess.getSlice()
				.getActiveTabularData().getColumn("WFPC2 IMAGE NAME")
				.getOrderIndex().intValue(), 1);
		// test that the order index of the column it replaced is 0
		assertEquals(this.saveColumnOrder.tabularDataProcess.getSlice()
				.getActiveTabularData().getColumn("OBSERVATION_TIME")
				.getOrderIndex().intValue(), 0);
	}

	/*
	 * test move column down
	 */
	@SuppressWarnings("nls")
	public void testMoveColumnDown() throws Exception {
		final String procId = retrieveData();
		// create sort columns action
		this.sortColumns = createAction(MoveColumn.class);
		this.sortColumns.setProcId(procId);
		this.sortColumns.setColumn("WFPC2 IMAGE NAME");
		this.sortColumns.setDirection("down");
		this.sortColumns
				.setColumnsString("WFPC2 IMAGE NAME,OBSERVATION_TIME,RA*COS(DEC) OFFSET,SIGMA OF RA*COS(DEC) OFFSET,DEC OFFSET,SIGMA OF DEC OFFSET");
		this.sortColumnsResult = this.sortColumns.execute();
		assertEquals(Action.SUCCESS, this.sortColumnsResult);
		assertEquals(
				this.sortColumns.getReorderedColumns(),
				"OBSERVATION_TIME,WFPC2 IMAGE NAME,RA*COS(DEC) OFFSET,SIGMA OF RA*COS(DEC) OFFSET,DEC OFFSET,SIGMA OF DEC OFFSET");
		// create saveColumnOrder action
		this.saveColumnOrder = createAction(SaveColumnOrder.class);
		this.saveColumnOrder.setProcId(procId);
		this.saveColumnOrder
				.setColumnsString("OBSERVATION_TIME,WFPC2 IMAGE NAME,RA*COS(DEC) OFFSET,SIGMA OF RA*COS(DEC) OFFSET,DEC OFFSET,SIGMA OF DEC OFFSET");

		// save button clicked
		this.saveColumnOrder.setSave("filler");
		this.saveColumnOrderResult = this.saveColumnOrder.execute();
		assertEquals(Action.SUCCESS, this.saveColumnOrderResult);
		// test that the order index of the column is 1
		assertEquals(this.saveColumnOrder.tabularDataProcess.getSlice()
				.getActiveTabularData().getColumn("WFPC2 IMAGE NAME")
				.getOrderIndex().intValue(), 1);
		// test that the order index of the column it replaced is 0
		assertEquals(this.saveColumnOrder.tabularDataProcess.getSlice()
				.getActiveTabularData().getColumn("OBSERVATION_TIME")
				.getOrderIndex().intValue(), 0);

	}

	@Override
	protected void clearAction() {

		this.orderColumns = null;
		this.saveColumnOrder = null;
	}

}
