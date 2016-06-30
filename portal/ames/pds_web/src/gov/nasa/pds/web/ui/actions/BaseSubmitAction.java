package gov.nasa.pds.web.ui.actions;

/**
 * Base action for anything that accepts user input.
 * 
 * @author jagander
 */
public abstract class BaseSubmitAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	/*
	 * Common button names to determine process user is attempting to complete.
	 * If only one path, these are useful to suppress struts complaining about
	 * setter not being found.
	 */

	/**
	 * Save process requested
	 */
	public final static String SAVE = "save"; //$NON-NLS-1$

	/**
	 * Next step in a process requested
	 */
	public final static String NEXT = "next"; //$NON-NLS-1$

	/**
	 * Returning one step in a process requested
	 */
	public final static String BACK = "back"; //$NON-NLS-1$

	/**
	 * Last step in a process requested
	 */
	public final static String LAST = "last"; //$NON-NLS-1$

	/**
	 * Last step in a process requested
	 */
	public final static String SUBMIT = "submit"; //$NON-NLS-1$

	/**
	 * Last step in a process requested
	 */
	public final static String CANCEL = "cancel"; //$NON-NLS-1$

	/**
	 * Last step in a process requested
	 */
	public final static String UPDATE = "update"; //$NON-NLS-1$

	/**
	 * Last step in a process requested
	 */
	public final static String DELETE = "delete"; //$NON-NLS-1$

	/**
	 * Last step in a process requested
	 */
	public final static String EDIT = "edit"; //$NON-NLS-1$

	/**
	 * String value of requested process
	 */
	protected String actionString;

	/**
	 * Referrer for request
	 */
	protected String referrer;

	/**
	 * Set referring URL
	 * 
	 * @param referrer
	 *            referring URL
	 */
	public void setReferrer(final String referrer) {
		this.referrer = referrer;
	}

	/**
	 * Get referring URL
	 * 
	 * @return referring URL
	 */
	public String getReferrer() {
		return this.referrer;
	}

	/*
	 * Setters for button names to determine which process was requested
	 */

	@SuppressWarnings("unused")
	public void setSubmit(final String action) {
		this.actionString = SUBMIT;
	}

	@SuppressWarnings("unused")
	public void setCancel(final String action) {
		this.actionString = CANCEL;
	}

	@SuppressWarnings("unused")
	public void setUpdate(final String action) {
		this.actionString = UPDATE;
	}

	@SuppressWarnings("unused")
	public void setDelete(final String action) {
		this.actionString = DELETE;
	}

	@SuppressWarnings("unused")
	public void setEdit(final String action) {
		this.actionString = EDIT;
	}

	@SuppressWarnings("unused")
	public void setSave(final String action) {
		this.actionString = SAVE;
	}

	@SuppressWarnings("unused")
	public void setNext(final String action) {
		this.actionString = NEXT;
	}

	@SuppressWarnings("unused")
	// TODO: duplicate of "last" value?
	public void setBack(final String action) {
		this.actionString = BACK;
	}

	@SuppressWarnings("unused")
	public void setLast(final String action) {
		this.actionString = LAST;
	}

	/**
	 * Primary execution method for submissions
	 */
	@Override
	public String execute() {
		try {

			// validate the user input before attempting to inner execution,
			// prevents bad input from being used in a process and corrupting
			// data
			validateUserInput();

			// if errors were found, push back any user input and send the user
			// to the defined input result. Typically this will be the referring
			// page with the bad data re-populated.
			if (hasErrors()) {
				pushBackUserInput();
				return INPUT;
			}

			// do the inner execution and get the result
			String resultVal = executeInner();

			// check the result for being input and push the input back if
			// necessary
			if (resultVal.equals(INPUT)) {
				pushBackUserInput();
			}

			return resultVal;
		} catch (Exception e) {

			// if uncaught exception in the inner execution, set it for
			// retrieval on the error page
			setException(e);

			return ERROR;
		}
	}

	/**
	 * Push user input back. Basically this amounts to pushing appropriate user
	 * input into the session temporarily so that it may be pulled for
	 * re-population of the referring page.
	 */
	protected abstract void pushBackUserInput();

	/**
	 * Do user input validation. This may range from checking user input for
	 * being null or empty to using ids that don't exist in the database.
	 */
	protected abstract void validateUserInput();

	/**
	 * Do the implementing action execution
	 */
	protected abstract String executeInner() throws Exception;
}
