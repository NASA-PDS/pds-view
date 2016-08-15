package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;

import java.io.File;

import com.opensymphony.xwork2.Action;

public class LoadLabelTest extends BaseTestAction {
	private LoadLabel loadLabel;
	private String loadLabelResult;
	private File testLabel;
	private String testLabelURL;

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
		this.loadLabel = createAction(LoadLabel.class);
		this.testLabel = new File(getTabularDataPath(), labelFileName);
		// store the url value for comparisons
		this.testLabelURL = this.testLabel.toURI().toURL().toString();
		this.loadLabel.setLabelURLString(this.testLabelURL);
		this.loadLabelResult = this.loadLabel.execute();
	}

	public void testSuccess() throws Exception {
		retrieveData("DIONE.LBL"); //$NON-NLS-1$
		assertEquals(Action.SUCCESS, this.loadLabelResult);
		// get process
		TabularDataProcess tabularDataProcess = (TabularDataProcess) this.loadLabel
				.getProcess();
		SliceContainer slice = tabularDataProcess.getSlice();
		// test process's slice has correct URL
		assertEquals(this.testLabelURL, slice.getLabelURLString());
		assertEquals(1, slice.getTabularDataObjects().size());
	}

	@SuppressWarnings("nls")
	public void testNoObjectsInLabel() throws Exception {
		retrieveData("1f128285236edn0000p1001l0m1.img");
		assertEquals(Action.INPUT, this.loadLabelResult);
		assertEquals(true, this.loadLabel.hasErrors());
		assertEquals(true, this.loadLabel.getErrorMessages().get(0).contains(
				this.loadLabel.getUIManager().getTxt(
						"loadLabel.error.noSupportedObjectsFound", true)));
	}

	// TODO when Spreadsheet supported, remove this test, add functional tests
	// to TabularDataLoaderTest
	@SuppressWarnings("nls")
	public void testSpreadsheetNotSupported() throws Exception {
		retrieveData("spreadsheet.lbl");
		assertEquals(true, this.loadLabel.hasErrors());
		assertEquals(true, this.loadLabel.getErrorMessages().get(0).contains(
				this.loadLabel.getUIManager().getTxt(
						"loadLabel.error.noSupportedObjectsFound", true)));
	}

	@SuppressWarnings("nls")
	public void testMissingURL() throws Exception {
		this.loadLabel = createAction(LoadLabel.class);
		this.loadLabel.setLabelURLString("");
		this.loadLabelResult = this.loadLabel.execute();
		assertEquals(Action.INPUT, this.loadLabelResult);
		assertEquals(true, this.loadLabel.hasErrors());
		assertEquals(true, this.loadLabel.getErrorMessages().get(0).contains(
				this.loadLabel.getUIManager().getTxt(
						"loadLabel.error.missingLabelUrl", true)));
	}

	@SuppressWarnings("nls")
	public void testNonPDSURL() throws Exception {
		this.loadLabel = createAction(LoadLabel.class);
		this.loadLabel.setLabelURLString("http://www.google.com/nonexistent-file");
		this.loadLabelResult = this.loadLabel.execute();
		assertEquals(Action.INPUT, this.loadLabelResult);
		assertEquals(true, this.loadLabel.hasErrors());
		assertEquals(true, this.loadLabel.getErrorMessages().size() >= 1);
	}

	@SuppressWarnings("nls")
	public void testMalformedURL() throws Exception {
		this.loadLabel = createAction(LoadLabel.class);
		this.loadLabel.setLabelURLString("localhost/DIONE.LBL");
		this.loadLabelResult = this.loadLabel.execute();
		assertEquals(Action.INPUT, this.loadLabelResult);
		assertEquals(true, this.loadLabel.hasErrors());
		assertEquals(true, this.loadLabel.getErrorMessages().get(0).contains(
				this.loadLabel.getUIManager().getTxt(
						"loadLabel.error.malformedLabelUrl", true)));
	}

	// TODO: determine if this is testing correctly
	@SuppressWarnings("nls")
	public void testUnavailableURL() throws Exception {
		retrieveData("http://localhost/DIONE.LBL");
		assertEquals(true, this.loadLabel.hasErrors());
		assertEquals(true, this.loadLabel.getErrorMessages().get(0).contains(
				this.loadLabel.getUIManager().getTxt(
						"loadLabel.error.urlUnreachable", true)));
	}

	@Override
	protected void clearAction() {

		this.loadLabel = null;
		this.loadLabelResult = null;
		this.testLabel = null;
		this.testLabelURL = null;

	}

}
