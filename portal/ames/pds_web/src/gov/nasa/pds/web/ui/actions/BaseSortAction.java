package gov.nasa.pds.web.ui.actions;

/**
 * Base action for doing a sort. Should work with a given process to update the
 * sort of a record set.
 * 
 * Currently this is unused but is left in for when advanced record set
 * manipulation is necessary.
 * 
 * @author jagander
 */
public abstract class BaseSortAction extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Primary sort column key
	 */
	public static final String KEY_PRIMARY_SORT_COLUMN = "primarySortColumn"; //$NON-NLS-1$

	/**
	 * Primary sort column descending/ascending key
	 */
	public static final String KEY_PRIMARY_SORT_DESCENDING = "primarySortDescending"; //$NON-NLS-1$

	/**
	 * Secondary sort column key
	 */
	public static final String KEY_SECONDARY_SORT_COLUMN = "secondarySortColumn"; //$NON-NLS-1$

	/**
	 * Secondary sort column descending/ascending key
	 */
	public static final String KEY_SECONDARY_SORT_DESCENDING = "secondarySortDescending"; //$NON-NLS-1$

	/**
	 * Default sort column
	 */
	public String defaultSortColumn;

	/**
	 * Default sort column descending/ascending
	 */
	public Boolean defaultSortDescending;

	/**
	 * Column being sorted
	 */
	private String column;

	/**
	 * Set column to sort
	 * 
	 * @param column
	 *            column to sort
	 */
	public void setColumn(final String column) {
		this.column = column;
	}

	/**
	 * Set a filter. Currently not implemented.
	 * 
	 * @param key
	 *            filter key
	 * @param value
	 *            filter value
	 */
	public void setFilter(String key, Object value) {
		// stub
		// super.setFilter(getRenderClassName(), key, value);
	}

	/**
	 * Get filter value for filter name
	 * 
	 * @param filterName
	 *            filter name
	 * @param defaultValue
	 *            default value to use if no value set
	 */
	public Object getFilter(String filterName, Object defaultValue) {
		// stub
		// return super.getFilter(getRenderClassName(), filterName,
		// defaultValue);
		return null;
	}

	/**
	 * Do work of sorting. Typically you will not need to override this in your
	 * specific sort action.
	 */
	@Override
	protected String executeInner() throws Exception {
		// Get current sort filters
		String primarySortColumn = (String) getFilter(KEY_PRIMARY_SORT_COLUMN,
				null);
		Boolean primarySortDescending = (Boolean) getFilter(
				KEY_PRIMARY_SORT_DESCENDING, true);

		// If no primary sort set, set to default
		if (primarySortColumn == null) {
			primarySortColumn = getDefaultSortColumn();
			primarySortDescending = Boolean.valueOf(getDefaultSortDescending());

			setFilter(KEY_PRIMARY_SORT_COLUMN, this.column);
		}

		if (primarySortColumn.equals(this.column)) {
			// Just reverse sort direction if submitted column equals
			// primary sort.
			setFilter(KEY_PRIMARY_SORT_DESCENDING, !primarySortDescending);
		} else {
			// If not equal and there was a primary sort then push that to
			// secondary level and
			// set the new column as primary sort column.
			setFilter(KEY_PRIMARY_SORT_COLUMN, this.column);
			setFilter(KEY_PRIMARY_SORT_DESCENDING, true);
			setFilter(KEY_SECONDARY_SORT_COLUMN, primarySortColumn);
			setFilter(KEY_SECONDARY_SORT_DESCENDING, primarySortDescending);
		}

		return SUCCESS;
	}

	/**
	 * Push the user input back for fixing and resubmission. This is not
	 * currently not implemented in a meaningful way for sorting.
	 */
	@Override
	protected void pushBackUserInput() {
		// noop
	}

	/**
	 * Validate the user submitted information. Currently this just checks that
	 * you have a sort column set in your request.
	 */
	@Override
	protected void validateUserInput() {
		if (this.column == null) {
			addError("error.noSort"); //$NON-NLS-1$
		}
	}

	/**
	 * Get the rendering class name, this is used as the base key for managing
	 * sort values in the session
	 */
	protected abstract String getRenderClassName();

	/**
	 * Default sort column name
	 */
	protected abstract String getDefaultSortColumn();

	/**
	 * Whether default sort column should be ascending or descending
	 */
	protected abstract Boolean getDefaultSortDescending();

}
