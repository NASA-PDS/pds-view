package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;

public abstract class Save extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;
	public static final String DOWNLOAD = "download"; //$NON-NLS-1$

	protected TabularDataProcess tabularDataProcess;

	public TabularDataProcess getTabularDataProcess() {
		return this.tabularDataProcess;
	}

	public void setTabularDataProcess(TabularDataProcess tabularDataProcess) {
		this.tabularDataProcess = tabularDataProcess;
	}

	public void setDownload(@SuppressWarnings("unused") final String action) {
		this.actionString = DOWNLOAD;
	}

	@Override
	protected String executeInner() throws Exception {
		this.tabularDataProcess = (TabularDataProcess) getProcess();
		return null;
	}

	@Override
	protected abstract void pushBackUserInput();

	@Override
	protected abstract void validateUserInput();

}
