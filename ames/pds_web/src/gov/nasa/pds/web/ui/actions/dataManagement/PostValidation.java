package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.constants.ApplicationConstants;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.managers.DBManager;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Post validation results
 * 
 * @author jagander
 */
public class PostValidation extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// get input stream containing posted file
		final InputStream in = HTTPUtils.getRequest().getInputStream();

		// create results
		ObjectInputStream ois = new ObjectInputStream(in);

		// read the serialized data object
		ValidationResults results = (ValidationResults) ois.readObject();

		// copy results to file
		final File targetFile = HTTPUtils.getProcessTempFile(getProcId(), ApplicationConstants.VALIDATION_RESULT_FILE);
		System.out.println("Creating target file: " + targetFile.getAbsolutePath());
		targetFile.createNewFile();
		final FileOutputStream fileOut = new FileOutputStream(targetFile);
		final ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(results);
		objOut.flush();
		objOut.close();
		ois.close();
		in.close();

		// store meta info in process
		System.out.println("Creating new validation process with procId=" + getProcId() + ".");
		final ValidationProcess process = new ValidationProcess(getProcId());
		process.setResultID(results.getId());
		process.setVolumePath(results.getPath());
		process.setRemote(true);
		HTTPUtils.storeProcess(process);

		// log results
		LogManager.logValidation(results);

		// clear out old data
		DBManager.clearOldValidationData();

		return JSON;
	}

	/**
	 * Push back user input. Nothing to see here
	 */
	@Override
	protected void pushBackUserInput() {
		// TODO Auto-generated method stub

	}

	/**
	 * Validate user input. Currently not aware of anything worth checking here.
	 */
	@Override
	protected void validateUserInput() {
		// TODO Auto-generated method stub

	}

}
