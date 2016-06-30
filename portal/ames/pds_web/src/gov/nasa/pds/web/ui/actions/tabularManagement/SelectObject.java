package gov.nasa.pds.web.ui.actions.tabularManagement;

/*
 * provides user option to select which tabular data object to work with when
 * more than one is defined by a label. if only one object in slice, returns
 * "pass" which skips this action
 * 
 * @author Laura Baalman
 */
public class SelectObject extends Display {

	private static final long serialVersionUID = 1L;

	@Override
	protected String executeInner() throws Exception {
		super.executeInner();
		setTitle("selectObject.title"); //$NON-NLS-1$
		// if there is only one tabularData object, skip this action, go
		// directly to that object
		if (this.slice.getTabularDataObjects().size() == 1) {
			this.tabularDataProcess.getSlice().setActiveTabDataIndex(0);
			return "pass"; //$NON-NLS-1$
		}
		return INPUT;
	}
}
