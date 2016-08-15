package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.ui.actions.BaseJSONAction;
import gov.nasa.pds.web.ui.containers.StatusContainer;

/**
 * Async status for getting the state of the validation being performed
 * 
 * @author jagander
 */
public class ValidationStatus extends BaseJSONAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Flag for validation being canceled
	 */
	private boolean cancel = false;

	/**
	 * Set the validation as canceled
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	/**
	 * Main method of the action
	 */
	@Override
	protected void executeInner() throws Exception {
		StatusContainer status = null;
		String message = null;

		// check if status exists in cache, if not, it's a catastrophic failure
		if (ValidateVolume.statusCache.containsKey(this.getProcId())) {

			// get status from cache
			status = ValidateVolume.statusCache.get(this.getProcId());

			if (this.cancel) {

				// if user canceled, set status to canceled so thread can end
				// the process
				status.setCancelled();
				// TODO: redirect to manage data sets page
			} else {

				// not canceled, get the current state including what step of
				// the process it's on
				// TODO: use props file to assemble message
				message = status.getMessageKey() + " (step " + status.getStep() //$NON-NLS-1$
						+ ")"; //$NON-NLS-1$
			}
		} else {

			// status not found, some unknown error occurred, just report
			// failure to user
			message = "Unable to retrieve status of validation."; //$NON-NLS-1$
		}

		// pass message through
		this.jsonContainer.put("status", message); //$NON-NLS-1$

		// check if process is done
		boolean isDone = status == null ? false : status.isDone();

		// pass through done state so page's javascript can kill the update
		// check and forward
		this.jsonContainer.put("done", isDone); //$NON-NLS-1$

		// pass through the canceled state so the page's javascript can kill the
		// update check and return to start
		// TODO: fix spelling error
		this.jsonContainer.put("cancelled", this.cancel); //$NON-NLS-1$

		// remove status if done
		if (isDone) {
			ValidateVolume.statusCache.remove(this.getProcId());
		}
	}

}
