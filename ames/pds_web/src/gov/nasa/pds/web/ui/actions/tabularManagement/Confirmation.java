package gov.nasa.pds.web.ui.actions.tabularManagement;

/**
 * Displays of final step in data slicer. corresponding jsp has link to download
 * the file created, go back to make changes and start over.
 * 
 * @author Laura Baalman
 */
public class Confirmation extends Display {

	private static final long serialVersionUID = 1L;

	@Override
	protected String executeInner() throws Exception {
		setTitle("confirmation.title"); //$NON-NLS-1$
		return INPUT;
	}
}
