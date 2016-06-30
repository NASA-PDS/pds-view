package gov.nasa.pds.web.ui.actions;

/**
 * This class is the primary class that "view" type Actions should extend. When
 * in doubt, if rendering a "page", this is the correct action to extend.
 * 
 * @author jagander
 */
public abstract class BaseViewAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Page title, used as an override when titles may be dynamic
	 */
	private String title;

	/**
	 * Page title key, used as an override when titles may be dynamic
	 */
	private String titleKey;

	/**
	 * Page title arguments, used when titles are dynamic and depend on
	 * arguments. An example would be "Previewing label file foo.lbl"
	 */
	private Object[] titleArgs = new Object[0];

	/**
	 * Wrapper for the execute method to centralize common functionality across
	 * the execute methods of all "view" actions.
	 * <p>
	 * Currently this wrapper only deals with unhandled exceptions. However, the
	 * "work" done here could and should expand.
	 * <p>
	 * TODO: Note that everything inside of an execute() call should be treated
	 * as a transaction since the data represents a snapshot of the current
	 * server state. This is a low level issue for the server layer as well as
	 * display layer.
	 * 
	 * @return (String)result - the result for this action. This determines what
	 *         to display based on the configuration for the action in
	 *         struts.xml.
	 */
	@Override
	public String execute() {
		try {
			final String returnVal = executeInner();
			try {
				this.title = getUIManager().getTxt(this.titleKey,
						this.titleArgs);
			} catch (Exception e) {
				// noop, not every page requires a distinct title
			}
			return returnVal;
		} catch (Exception e) {
			this.title = getUIManager().getTxt("error.title"); //$NON-NLS-1$
			setException(e);
		}
		return ERROR;
	}

	/**
	 * Specific action's execution
	 */
	protected abstract String executeInner() throws Exception;

	/**
	 * Set title override
	 * 
	 * @param key
	 *            title key
	 * @param arguments
	 *            argument array for title message assembly
	 */
	protected void setTitle(final String key, final Object... arguments) {
		this.titleKey = key;
		this.titleArgs = arguments;
	}

	/**
	 * Get title override
	 * 
	 * @return title override
	 */
	public String getTitle() {
		return this.title;
	}
}
