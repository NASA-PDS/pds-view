package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.AppVersionInfo;

/**
 * first step of Data slicing tool provides user interface to enter URL of PDS
 * label file
 * 
 * @author Laura Baalman
 */
public class ImportTabularData extends Display {

	private static final long serialVersionUID = 1L;

	private String labelURLString;

	public void setLabelURLString(String labelURLString) {
		this.labelURLString = labelURLString;
	}

	public String getLabelURLString() {
		return this.labelURLString;
	}

	public AppVersionInfo getAppVersionInfo() {
		return new AppVersionInfo();
	}

	@Override
	protected String executeInner() throws Exception {
		setTitle("importTabularData.title"); //$NON-NLS-1$
		return INPUT;
	}

}
