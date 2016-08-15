package gov.nasa.pds.web.ui.actions;

/**
 * Base action for dynamically generated javascript. Especially useful in locale
 * specific code such as calendars. Currently unnused and incomplete.
 * 
 * @author jagander
 */
public class JavaScriptAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Set locale in case javascript is locale dependent
	 * 
	 * @param locale
	 *            user's locale
	 */
	// TODO: implement
	public void setL(String locale) {
		// stub
	}

	/**
	 * method to generate javascript
	 * 
	 * @return result
	 */
	@Override
	public String execute() {
		return SUCCESS;
	}
}
