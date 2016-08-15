package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.arc.pds.tools.util.FileUtils;
import gov.nasa.pds.web.ui.actions.BaseStreamAction;
import gov.nasa.pds.web.ui.constants.ApplicationConstants;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Download validation report action. Report should have been generated prior to
 * calling this action.
 * 
 * @see gov.nasa.pds.web.ui.actions.dataManagement.ValidationReport
 * 
 * @author jagander
 */
public class DownloadReport extends BaseStreamAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Content type fixed at text/plain
	 */
	private final static String CONTENT_TYPE = "text/plain"; //$NON-NLS-1$

	/**
	 * Report file name
	 */
	private String fileName;

	/**
	 * Get content type
	 * 
	 * @return content type
	 */
	public String getContentType() {
		return CONTENT_TYPE;
	}

	/**
	 * Get file name
	 * 
	 * @param file
	 *            name
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Main method of action
	 */
	@Override
	public String executeInner() {
		try {

			// get current validation process
			final ValidationProcess process = (ValidationProcess) getProcess();

			// if no process, not enough information to proceed
			if (process == null) {
				return MISSING_INFORMATION;
			}

			// get cached results
			final File sessionTempDir = HTTPUtils.getSessionTempDir();
			final File resultFile = HTTPUtils.getProcessTempFile(getProcId(), ApplicationConstants.VALIDATION_RESULT_FILE);
			FileInputStream in = new FileInputStream(resultFile);

			// create results
			ObjectInputStream ois = new ObjectInputStream(in);

			// read the serialized data object
			ValidationResults results = (ValidationResults) ois.readObject();
			in.close();
			ois.close();

			// create the output file
			final String sourceFilename = results.getTableName();
			final File reportFile = new File(sessionTempDir, sourceFilename);

			this.fileName = FileUtils.getSafeName(results.getVolumeId())
					+ "_report.txt"; //$NON-NLS-1$

			this.inputStream = new FileInputStream(reportFile);

		} catch (Exception e) {
			setException(e);
			return ERROR;
		}
		return SUCCESS;
	}
}
