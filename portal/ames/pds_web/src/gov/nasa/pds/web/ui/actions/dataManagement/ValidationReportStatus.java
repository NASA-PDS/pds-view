package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.ui.actions.BaseJSONAction;
import gov.nasa.pds.web.ui.containers.StatusContainer;

/**
 * Async status check on validation report generation
 * 
 * @author jagander
 */
public class ValidationReportStatus extends BaseJSONAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Whether the report generation has been canceled
	 */
	private boolean cancel = false;

	/**
	 * Setter for whether report generation is canceled
	 * 
	 * @param cancel
	 *            cancel state
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	/**
	 * Main method of the action
	 */
	@Override
	protected void executeInner() throws Exception {

		// create status container as null as it may not exist
		StatusContainer status = null;

		// create message as null as it may not be set
		String message = null;

		// get and cast the process
		final ValidationProcess process = (ValidationProcess) getProcess();

		// if no process was found, cannot continue
		// NOTE: probably a server error due to something crashing or expiring,
		// _possibly_ user error in directly accessing url but not likely
		if (process == null) {
			// TODO: do error checking
			// return MISSING_INFORMATION;
			return;
		}

		// check that status exists in cache
		if (ValidationReport.statusCache.containsKey(process.getID())) {

			// get status from cache
			status = ValidationReport.statusCache.get(process.getID());

			// if cancel state set to true, set the status to canceled so that
			// it will cancel the report generation
			if (this.cancel) {
				status.setCancelled();
				// TODO: redirect to manage data sets page
			} else {
				// pass the message through, currently not using key->value
				// lookups
				message = status.getMessageKey();
			}
		} else {
			// couldn't find the status, give a generic error
			message = "Unable to retrieve report status."; //$NON-NLS-1$
		}

		// pass the message through
		this.jsonContainer.put("status", message); //$NON-NLS-1$

		// get flag for whether validation is complete, triggers an end to the
		// status check and forwards to results
		boolean isDone = status == null ? false : status.isDone();

		// set done flag
		this.jsonContainer.put("done", isDone); //$NON-NLS-1$

		// set canceled flag
		// TODO: misspelled, update spelling across the board
		this.jsonContainer.put("cancelled", this.cancel); //$NON-NLS-1$

		// remove status if done
		if (isDone) {
			ValidationReport.statusCache.remove(this.getProcId());
		}
	}

}
