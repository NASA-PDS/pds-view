package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.arc.pds.tools.util.LocaleUtils;
import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.constants.Constants.Severity;
import gov.nasa.pds.tools.containers.SimpleDictionaryChange;
import gov.nasa.pds.web.ui.UIManager;
import gov.nasa.pds.web.ui.actions.BaseJSONAction;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.DataSetProblem;
import gov.nasa.pds.web.ui.constants.ApplicationConstants;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.NewValue;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.SimpleProblem;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Action that generates validation report for download. Report is downloaded in
 * a separate action once this completes.
 * 
 * @see gov.nasa.pds.web.ui.actions.dataManagement.DownloadReport
 * 
 * @author jagander
 */
public class ValidationReport extends BaseJSONAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Newline format to use
	 */
	public static final String NEWLINE = "\r\n"; //$NON-NLS-1$

	/**
	 * ASCII section separator char sequence
	 */
	public static final String SECTION_SEPARATOR = "============================================================" + NEWLINE; //$NON-NLS-1$

	/**
	 * A status cache to use for checking the status of a given report
	 * generation
	 */
	protected final static Map<String, StatusContainer> statusCache = new HashMap<String, StatusContainer>();

	/**
	 * Status container for this report generation. Instantiate with 1 step.
	 */
	private final StatusContainer status = new StatusContainer(1);

	/**
	 * Main method of action
	 */
	@Override
	public void executeInner() throws Exception {

		// get active validation process
		final ValidationProcess process = (ValidationProcess) getProcess();

		if (process == null) {
			// TODO: do error checking
			return;
		}

		// put status container in cache so you can check status of report
		// generation
		ValidationReport.statusCache.put(process.getID(), this.status);

		// get the temp dir that report will be written to
		final File sessionTempDir = HTTPUtils.getSessionTempDir();
		final File resultFile = HTTPUtils.getProcessTempFile(getProcId(), ApplicationConstants.VALIDATION_RESULT_FILE);

		// do report in thread so it's not blocking
		ReportThread thread = new ReportThread(resultFile, this.status, sessionTempDir);

		// start the thread
		thread.start();
	}

	/**
	 * Report worker thread
	 */
	class ReportThread extends Thread {

		/** The result file. */
		private final File resultFile;

		/**
		 * Status container for this report generation.
		 */
		@SuppressWarnings("hiding")
		private final StatusContainer status;

		/**
		 * Temp dir that report will be written to
		 */
		private final File sessionTempDir;

		/**
		 * Constructor for thread.
		 * 
		 * @param resultFile
		 *            the result file
		 * @param status
		 *            container to store status in
		 * @param sessionTempDir
		 *            directory to write the file to
		 */
		public ReportThread(final File resultFile,
				final StatusContainer status, final File sessionTempDir) {
			this.resultFile = resultFile;
			this.status = status;
			this.sessionTempDir = sessionTempDir;
		}

		/**
		 * Main method of thread
		 */
		@SuppressWarnings("nls")
		@Override
		public void run() {
			try {

				// buffer to use for looped string concatenation
				StringBuilder buf = new StringBuilder();

				// get file from id and session id
				this.status.setStatus("Getting cached result info.");

				// create input stream
				FileInputStream in = new FileInputStream(resultFile);

				// create results
				ObjectInputStream ois = new ObjectInputStream(in);

				// read the serialized data object
				ValidationResults results = (ValidationResults) ois
						.readObject();
				in.close();
				ois.close();

				// create the output file
				final String filename = results.getTableName();
				final File outFile = new File(this.sessionTempDir, filename);
				// clear out old file if it currently exists
				if (outFile.exists()) {
					outFile.delete();
					outFile.createNewFile();
				} else {
					outFile.createNewFile();
				}

				final FileOutputStream os = new FileOutputStream(outFile);
				final OutputStreamWriter writer = new OutputStreamWriter(os);

				// get groups you will need to retrieve problems on
				Map<ProblemType, Integer> groupCount = results.getGroupCount();

				// generate problem count
				this.status.setStatus("Generating problem counts.");
				int numErrors = 0;
				int numWarnings = 0;
				int numInfo = 0;
				for (Iterator<Entry<ProblemType, Integer>> it = groupCount
						.entrySet().iterator(); it.hasNext();) {
					Entry<ProblemType, Integer> entry = it.next();
					Integer count = entry.getValue();
					ProblemType type = entry.getKey();
					if (count != null) {
						if (type.getSeverity().equals(Severity.ERROR)) {
							numErrors += count;
						} else if (type.getSeverity().equals(Severity.WARNING)) {
							numWarnings += count;
						} else if (type.getSeverity().equals(Severity.INFO)) {
							numInfo += count;
						}
					}
				}

				final UIManager uiManager = getUIManager();

				// add in the header content
				this.status.setStatus("Writing general info.");
				writer.write(SECTION_SEPARATOR);
				writer.write(uiManager
						.getTxt("validationResults.title.elementDetails")
						+ NEWLINE);
				writer.write(SECTION_SEPARATOR);

				writer.write(uiManager.getTxt("validationResults.label.name")
						+ ": " + results.getPath() + NEWLINE);
				writer.write(NEWLINE);

				writer.write(uiManager
						.getTxt("validationResults.label.problems")
						+ ": "
						+ uiManager.getTxt("previewFile.text.numErrors",
								numErrors)
						+ ", "
						+ uiManager.getTxt("previewFile.text.numWarnings",
								numWarnings)
						+ ", "
						+ uiManager.getTxt("previewFile.text.numInfo", numInfo)
						+ NEWLINE);
				writer.write(NEWLINE);

				writer.write(uiManager.getTxt("validationResults.label.size")
						+ ": "
						+ uiManager.getTxt("validationResults.text.volumeSize",
								results.getNumFiles(), results.getNumFolders())
						+ NEWLINE);
				writer.write(NEWLINE);

				if (results.getNewValues().size() > 0) {
					this.status.setStatus("Writing new values.");
					writer.write(SECTION_SEPARATOR);
					writer.write(uiManager
							.getTxt("validationResults.title.newValues")
							+ NEWLINE);
					writer.write(SECTION_SEPARATOR);
					writer
							.write(uiManager
									.getTxt("validationResults.table.newValue.column.key")
									+ "\t"
									+ uiManager
											.getTxt("validationResults.table.newValue.column.value")
									+ "\t"
									+ uiManager
											.getTxt("validationResults.table.newValue.column.resource")
									+ "\t"
									+ uiManager
											.getTxt("validationResults.table.newValue.column.path")
									+ "\t"
									+ uiManager
											.getTxt("validationResults.table.newValue.column.lineNumber")
									+ NEWLINE);

					// print out new values
					for (final NewValue newVal : results.getNewValues()) {
						buf.setLength(0);
						buf.append(newVal.getKey());
						buf.append("\t");
						buf.append(newVal.getValue());
						buf.append("\t");
						buf.append(newVal.getFile().getName());
						buf.append("\t");
						buf.append(newVal.getFile().getRelativePath());
						buf.append("\t");
						buf.append(newVal.getLineNumber());
						buf.append(NEWLINE);
						writer.write(buf.toString());
					}
					writer.write(NEWLINE);
				}

				if (results.getDictionaryChganges().size() > 0) {
					this.status.setStatus("Writing dictionary changes.");
					writer.write(SECTION_SEPARATOR);
					writer
							.write(uiManager
									.getTxt("validationResults.title.dictionaryChanges")
									+ NEWLINE);
					writer.write(SECTION_SEPARATOR);
					writer
							.write(uiManager
									.getTxt("validationResults.table.dictionaryChanges.column.key")
									+ "\t"
									+ uiManager
											.getTxt("validationResults.table.dictionaryChanges.column.message")
									+ "\t"
									+ uiManager
											.getTxt("validationResults.table.dictionaryChanges.column.lineNumber")
									+ NEWLINE);

					// print out new values
					for (final SimpleDictionaryChange change : results
							.getDictionaryChganges()) {
						buf.setLength(0);
						String message = getUIManager().getTxt(
								change.getMessageKey(), change.getArguments());
						buf.append(change.getId().toString());
						buf.append("\t");
						buf.append(message);
						buf.append("\t");
						buf.append(change.getLineNumber());
						buf.append(NEWLINE);
						writer.write(buf.toString());
					}
					writer.write(NEWLINE);
				}

				writer.write(SECTION_SEPARATOR);
				writer.write(uiManager
						.getTxt("validationResults.title.problems")
						+ NEWLINE);
				writer.write(SECTION_SEPARATOR);
				writer
						.write(uiManager
								.getTxt("validationResults.table.problems.column.description")
								+ "\t"
								+ uiManager
										.getTxt("validationResults.table.problems.column.resource")
								+ "\t"
								+ uiManager
										.getTxt("validationResults.table.problems.column.path")
								+ "\t"
								+ uiManager
										.getTxt("validationResults.table.problems.column.lineNumber")
								+ NEWLINE);

				final LocaleUtils localeUtils = getUIManager().getLocaleUtils();
				// get problems and append to end of file in batches
				// TODO: indicate current problem being written after each batch
				this.status.setStatus("Beginning to write problems.");
				long writtenProbs = 0;
				for (Iterator<Entry<ProblemType, Integer>> it = groupCount
						.entrySet().iterator(); it.hasNext();) {
					final Entry<ProblemType, Integer> entry = it.next();
					final ProblemType type = entry.getKey();
					final Integer numProbs = entry.getValue();

					if (numProbs > 0) {
						// get the first bucket to prime the loop
						List<SimpleProblem> groupProbs = results
								.getNextBucket(type);
						final SimpleProblem sampleProblem = groupProbs.get(0);
						final String severity = sampleProblem.getType()
								.getSeverity().getName();
						final String description = sampleProblem.getType()
								.toString();

						// write the group line
						writer.write("[" + severity + "] " + description + " ("
								+ numProbs + ")");

						// TODO: determine how to do clustering when you have
						// buckets, or if necessary at all
						while (groupProbs != null && groupProbs.size() > 0) {
							// clear buffer for new iteration
							buf.setLength(0);
							DataSetProblem problem = null;
							for (final SimpleProblem prob : groupProbs) {
								// create full prob from simple
								problem = new DataSetProblem(prob, localeUtils);
								// write to buffer
								buf.append("\t");
								buf.append(problem.getMessage().replaceAll(
										"\n", ""));
								buf.append("\t");
								buf.append(problem.getResource());
								buf.append("\t");
								buf.append(problem.getPath());
								buf.append("\t");
								buf.append(problem.getLineNumber());
								buf.append(NEWLINE);
								writtenProbs++;
							}

							// dump contents of buffer to file
							this.status.setStatus("Writing problem "
									+ uiManager.getLocaleUtils().getNumber(
											writtenProbs));
							writer.write(buf.toString());

							// get next bucket if there is any left in group
							if (groupProbs.size() == ValidationResults.BUCKET_LIMIT) {
								groupProbs = results.getNextBucket(type);
							} else {
								break;
							}
						}
					}
					// reset offset to zero since using a new type
					results.resetProblemPosition();
				}
				writer.flush();
				writer.close();
				this.status.setDone();
			} catch (final Exception e) {
				this.status.setStatus(StrUtils.toString(e, true, 1));
				// TODO: redirect to error page
				setException(e);
			}
		}
	}
}
