package gov.nasa.pds.web.ui.actions.tabularManagement;


/*
 * Saves user selections related to file format, directs processing from buttons
 * on manageTabularData.jsp
 * 
 * @author Laura Baalman
 */
public class SaveDownloadFormat extends Save {

	private static final long serialVersionUID = 1L;
	private String fileType;
	private boolean includeHeaders;
	public static final String SELECT_COLUMNS = "selectColumns"; //$NON-NLS-1$
	public static final String ORDER_COLUMNS = "orderColumns"; //$NON-NLS-1$
	public static final String SELECT_ROWS = "selectRows"; //$NON-NLS-1$
	public static final String ORDER_ROWS = "orderRows"; //$NON-NLS-1$
	public static final String UPDATE_PREVIEW = "updatePreview"; //$NON-NLS-1$
	private int startRow;
	private int numRowsPerPage;

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setIncludeHeaders(boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}

	public void setSelectColumns(@SuppressWarnings("unused") final String action) {
		this.actionString = SELECT_COLUMNS;
	}

	public void setOrderColumns(@SuppressWarnings("unused") final String action) {
		this.actionString = ORDER_COLUMNS;
	}

	public void setSelectRows(@SuppressWarnings("unused") final String action) {
		this.actionString = SELECT_ROWS;
	}

	public void setOrderRows(@SuppressWarnings("unused") final String action) {
		this.actionString = ORDER_ROWS;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public void setNumRowsPerPage(int numRowsPerPage) {
		this.numRowsPerPage = numRowsPerPage;
	}

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();

		// assign values from the form to the slice in the tabularDataProcess
		this.tabularDataProcess.getSlice().setFileType(this.fileType);
		this.tabularDataProcess.getSlice().setIncludeHeaders(
				this.includeHeaders);
		// if numrowsperpage does not change, save start row passed in
		if (this.tabularDataProcess.getSlice().getNumRowsPerPage() == this.numRowsPerPage) {
			this.tabularDataProcess.getSlice().setStartRow(this.startRow);

		} else {
			// if numRowsPerPage changes, reset startRow to 1
			this.tabularDataProcess.getSlice().setStartRow(1);
			this.tabularDataProcess.getSlice().setNumRowsPerPage(
					this.numRowsPerPage);
		}
		if (this.actionString != null && this.actionString.length() > 0)
			return this.actionString;

		return SUCCESS;

	}

	@Override
	protected void pushBackUserInput() {
		this.tabularDataProcess = (TabularDataProcess) getProcess();
		this.tabularDataProcess.getSlice().setIncludeHeaders(
				this.includeHeaders);
	}

	@Override
	protected void validateUserInput() {
		if (this.actionString != null
				&& this.actionString.equals(Save.DOWNLOAD)) {

			if (this.fileType.length() == 0) {
				addError("enterDownloadFormat.error.noFileType"); //$NON-NLS-1$
			}
		}
	}

}
