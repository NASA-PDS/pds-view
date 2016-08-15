package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.tools.dict.Dictionary;
import gov.nasa.pds.web.AppVersionInfo;
import gov.nasa.pds.web.ui.actions.BaseViewAction;
import gov.nasa.pds.web.ui.utils.DataSetValidator;

/**
 * Action to display current state of the validation being performed. This is
 * only used in the installed/developer configuration as the applet version
 * displays the status in-place.
 * 
 * @author jagander
 */
public class ValidationProgress extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Process container for the validation
	 */
	private ValidationProcess process;

	/**
	 * Master dictionary used in validation. Used to get version info.
	 */
	private Dictionary dictionary = DataSetValidator.getMasterDictionary();

	/**
	 * Get volume path. Used for letting the user know what is being validated,
	 * critical in long running validation processes.
	 * 
	 * @return the volume path
	 */
	public String getVolumePath() {
		return this.process.getVolumePath();
	}

	/**
	 * Get the application version info. Lets the user know what versions of
	 * things influence the validation mechanics.
	 * 
	 * @return application version info
	 */
	public AppVersionInfo getAppVersionInfo() {
		return new AppVersionInfo();
	}

	/**
	 * Get the dictionary version info. Lets the user know what version of the
	 * data dictionary is used in validation.
	 * 
	 * @return the current dictionary version
	 */
	public String getDictionaryVersion() {
		return this.dictionary.getVersion();
	}

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// get the process and cast it to the appropriate type
		this.process = (ValidationProcess) getProcess();

		return SUCCESS;
	}

}
