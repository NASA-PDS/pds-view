package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseJSONAction;
import gov.nasa.pds.web.ui.containers.StatusContainer;

/*
 * Manages status messages in LoadData.statusCache. Used by JavaScript calls
 * from loadProgress.jsp
 * 
 * @author Laura Baalman
 */
public class LoadStatus extends BaseJSONAction {

	private static final long serialVersionUID = 1L;

	private boolean cancel = false;

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	@SuppressWarnings("nls")
	@Override
	protected void executeInner() throws Exception {
		StatusContainer status = null;
		String message = null;

		if (LoadData.statusCache.containsKey(this.getProcId())) {
			status = LoadData.statusCache.get(this.getProcId());
			if (this.cancel) {
				status.setCancelled();
				// TODO: redirect
			} else {
				message = status.getMessageKey() + " : step "
						+ status.getStep() + " of " + status.getNumSteps();
			}
		} else {
			message = "Unable to retrieve status.";
			this.setCancel(true);
		}

		// if found, return formatted message string
		this.jsonContainer.put("status", message);

		boolean isDone = status == null ? false : status.isDone();
		this.jsonContainer.put("done", isDone);
		this.jsonContainer.put("cancelled", this.cancel);

		// remove status if done
		if (isDone) {
			LoadData.statusCache.remove(this.getProcId());
		}
	}

}
