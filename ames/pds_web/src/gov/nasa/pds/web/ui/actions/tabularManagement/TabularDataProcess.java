package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.BaseProcess;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;

public class TabularDataProcess extends BaseProcess {

	private static final long serialVersionUID = -7912286880757189407L;
	private SliceContainer slice;

	public TabularDataProcess() {
		super();
	}

	public TabularDataProcess(final String id) {
		super(id);
	}

	public SliceContainer getSlice() {
		return this.slice;
	}

	public void setSlice(SliceContainer slice) {
		this.slice = slice;
	}

}
