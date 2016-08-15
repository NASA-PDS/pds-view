package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.applets.CancelledException;
import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.constants.ApplicationConstants;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.dataSet.Bucket;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.DataSetValidator;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Validation action used for "installed" copies of the tool. This is primarily
 * used for development purposes as the applet validation approach is the
 * primary validation path. However, there are a few parties that may require a
 * local validation instance. This will be their path to validation.
 * 
 * @author jagander
 */
public class ValidateVolume extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Path to the volume to validate
	 */
	private String volumePath;

	/**
	 * Cache of status objects, used to update the user with current status or
	 * terminate the progress check when the validation is cancelled or
	 * completed
	 */
	protected final static Map<String, StatusContainer> statusCache = new HashMap<String, StatusContainer>();

	/**
	 * Status of this instance of validation
	 */
	private final StatusContainer status = new StatusContainer();

	/**
	 * Whether this process is blocking
	 * 
	 * NOTE: only used for testing purposes, defeats the purpose of spawning a
	 * new thread when enabled
	 */
	protected boolean blocking = false;

	/**
	 * Set path to volume to validate
	 * 
	 * @param volumePath
	 *            path to volume on the file system
	 */
	public void setVolumePath(final String volumePath) {
		this.volumePath = volumePath;
	}

	/**
	 * Main method of the action.
	 */
	// TODO: have results action remove process from cache if present
	// TODO: figure out how to do a cancel
	@Override
	protected String executeInner() throws Exception {

		// generate a new process instance
		final ValidationProcess process = new ValidationProcess();

		// set the path to the volume in the process
		process.setVolumePath(this.volumePath);

		// set a flag for not being a remote validation, thus not using applets
		// for validation interaction
		process.setRemote(false);

		// set the process id
		setProcId(process.getID());

		// store the process in the session
		HTTPUtils.storeProcess(process);

		// get the volume folder you want to validate
		final File file = new File(getDataSetDirectory(), this.volumePath);

		// create a validator instance
		DataSetValidator validator = new DataSetValidator(getProcId(), file,
				this.status);

		// get the result id
		// NOTE: results are created with the validator but won't be populated
		// with content until validated, id is valid however
		final String resultID = validator.getResults().getId();

		// set the result id in the process
		process.setResultID(resultID);

		// put status in cache
		ValidateVolume.statusCache.put(process.getID(), this.status);

		// get the session temp dir to store results in
		final File sessionTempDir = HTTPUtils.getSessionTempDir();

		// create a new validation thread
		ValidationThread thread = new ValidationThread(validator,
				sessionTempDir, getProcId());

		// either spawn process in the thread or run it in place
		// NOTE: running in place is just a hack for testing purposes and should
		// not be used in production
		if (this.blocking) {
			thread.run();
		} else {
			thread.start();
		}

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateUserInput() {
		// TODO Auto-generated method stub

	}

	/**
	 * The thread that does the actual validation
	 */
	class ValidationThread extends Thread implements Observer {

		/**
		 * The volume validation instance
		 */
		private final DataSetValidator validator;

		/**
		 * Temp dir to write out serialized results
		 */
		private final File sessionTempDir;

		/**
		 * Process id associated with this validation
		 */
		private final String procId;

		/**
		 * Status container for the state of the validation
		 */
		@SuppressWarnings("hiding")
		private StatusContainer status;

		/**
		 * Constructor for the thread
		 * 
		 * @param validator
		 *            the validator instance that does the validation
		 * @param sessionTempDir
		 *            temp dir to write out serialized results
		 * @param procId
		 *            the process id associated with this validation
		 */
		public ValidationThread(final DataSetValidator validator,
				final File sessionTempDir, final String procId) {
			this.validator = validator;
			// make this thread an observer of the validator
			validator.addObserver(this);
			this.sessionTempDir = sessionTempDir;
			this.procId = procId;
		}

		/**
		 * Main method of the thread
		 */
		@Override
		public void run() {
			try {
				// get the status object from the validator
				this.status = this.validator.getStatus();

				// block the status until the results have been written
				this.status.setBlocked(true);

				// do validation
				this.validator.validate();

				// get results from validator
				final ValidationResults results = this.validator.getResults();

				// get result id for storing file in temp directory
				final String resultID = results.getId();

				// CACHE RESULTS

				// get target file
				final File targetFile = HTTPUtils.getProcessTempFile(getProcId(), ApplicationConstants.VALIDATION_RESULT_FILE);

				// it should not already exist so create it
				targetFile.createNewFile();

				// write the results
				final FileOutputStream fileOut = new FileOutputStream(
						targetFile);
				final ObjectOutputStream objOut = new ObjectOutputStream(
						fileOut);
				objOut.writeObject(results);

				// contents written, release the block
				this.status.setBlocked(false);

				// flush and close streams
				objOut.flush();
				objOut.close();
				fileOut.flush();
				fileOut.close();

				// log results
				LogManager.logValidation(results);
			} catch (final CancelledException e) {
				// TODO: redirect to manage data sets
			} catch (final Exception e) {
				// TODO: redirect to error page
				setException(e);
			}
		}

		/**
		 * Catch updates from validator instance, currently only handles the
		 * batch storage of problems
		 */
		@Override
		public void update(@SuppressWarnings("hiding") Observable validator,
				Object object) {

			// only continue if object is a posted bucket of problems
			if (object instanceof Bucket) {
				this.status.setBlocked(true);
				try {
					PostValidationBucket.storeBucket(this.procId,
							(Bucket) object);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.status.setBlocked(false);
			}

		}
	}

}
