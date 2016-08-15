package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.arc.pds.tools.util.LocaleUtils;
import gov.nasa.pds.tools.constants.Constants.Severity;
import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.constants.ApplicationConstants;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.SimpleProblem;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Preview of a cached file from a validation process
 */
public class PreviewFile extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Collection of line objects used in rendering the textual file with
	 * decorations
	 */
	private List<Line> lines = new ArrayList<Line>();

	/**
	 * Relative path to file from volume root
	 */
	private String path;

	/**
	 * The cached file on the server
	 */
	private File node;

	/**
	 * The size of the file on disk
	 */
	private String size;

	/**
	 * Number of error level problems in the file
	 */
	private int numErrors;

	/**
	 * Number of warning level problems in the file
	 */
	private int numWarnings;

	/**
	 * Number of info level problems in the file
	 */
	private int numInfo;

	/**
	 * Set the path to the cached file on the server
	 * 
	 * @param path
	 *            the path to the cached file
	 */
	public void setNode(final String path) {
		this.path = path;
	}

	/**
	 * Get the collection of decorated lines in the file
	 * 
	 * @return the collection of lines
	 */
	public List<Line> getLines() {
		return this.lines;
	}

	/**
	 * Get the line object for a given line number
	 * 
	 * @param lineNumber
	 *            the line number of the line object to return
	 * 
	 * @return the line object matching the line number
	 */
	private Line getLine(int lineNumber) {
		int line = Math.max(1, lineNumber);
		return this.lines.get(line - 1);
	}

	/**
	 * Get the path to the cached preview file
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Get the size of the preview file
	 */
	public String getSize() {
		return this.size;
	}

	/**
	 * Get the number of error level problems in the file
	 */
	public int getNumErrors() {
		return this.numErrors;
	}

	/**
	 * Get the number of warning level problems in the file
	 */
	public int getNumWarnings() {
		return this.numWarnings;
	}

	/**
	 * Get the number of info level problems in the file
	 */
	public int getNumInfo() {
		return this.numInfo;
	}

	/**
	 * The main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// get the active validation process
		ValidationProcess process = (ValidationProcess) getProcess();

		// if the process is not found, assume a bad request
		if (process == null) {
			return MISSING_INFORMATION;
		}

		// get cached results
		final File resultFile = HTTPUtils.getProcessTempFile(getProcId(), ApplicationConstants.VALIDATION_RESULT_FILE);
		FileInputStream in = new FileInputStream(resultFile);

		// create results from id
		ObjectInputStream ois = new ObjectInputStream(in);

		// read the serialized data object
		final ValidationResults results = (ValidationResults) ois.readObject();
		in.close();
		ois.close();

		if (process.isRemote()) {
			// get cached remote file
			this.node = HTTPUtils.getProcessTempFile(getProcId(), Integer.toString(this.path.hashCode()));
		} else {
			// get local file
			File rootFile = new File(getDataSetDirectory(), process
					.getVolumePath());

			this.node = new File(rootFile, this.path);
		}

		// get reader for file
		final FileReader reader = new FileReader(this.node);
		final BufferedReader br = new BufferedReader(reader);

		// read lines into Line objects
		String lineContents;
		int lineNumber = 1;
		while ((lineContents = br.readLine()) != null) {
			this.lines.add(new Line(lineNumber, lineContents, getUIManager()
					.getLocaleUtils()));
			lineNumber++;
		}

		// Close the input stream
		br.close();

		// get the problems for this file
		List<SimpleProblem> probs = results.getProblems(this.path);

		// iterate across problems, adding them to lines and incrementing counts
		for (SimpleProblem simpleProb : probs) {

			// if the problem matches the path, integrate into lines
			if (simpleProb.getFile().getRelativePath().equals(this.path)) {

				// get line number of problem
				Integer lineNum = simpleProb.getLineNumber();

				// if there is a line number, add the problem to the found line
				if (lineNum != null) {

					// get line object for the given line number
					Line line = getLine(lineNum);

					// add the problem to the line
					line.addProblem(simpleProb);
				}

				// get the severity of the problem
				Severity severity = simpleProb.getType().getSeverity();

				// increment problem counts
				if (severity.equals(Severity.ERROR)) {
					this.numErrors++;
				} else if (severity.equals(Severity.WARNING)) {
					this.numWarnings++;
				} else if (severity.equals(Severity.INFO)) {
					this.numInfo++;
				}
			}
		}

		// get size of file
		this.size = getUIManager().getLocaleUtils().formatBytes(
				this.node.length());

		return SUCCESS;
	}

	/**
	 * Push back user input, nothing to see here
	 */
	@Override
	protected void pushBackUserInput() {
		// noop

	}

	/**
	 * Validate user input. Not currently implemented but validating that the
	 * node exists, the line number is in range, etc may be worthwhile
	 */
	@Override
	protected void validateUserInput() {
		// TODO make sure set and node exist

	}

	/**
	 * Line object for display
	 */
	public class Line {

		/**
		 * Line number of object
		 */
		private final int lineNumber;

		/**
		 * Textual contents of the line
		 */
		private final String contents;

		/**
		 * Locale utilities used for displaying problem messages associated with
		 * the line
		 */
		private final LocaleUtils localeUtils;

		/**
		 * Problems associated with the line
		 */
		private List<SimpleProblem> problems = new ArrayList<SimpleProblem>();

		/**
		 * Worst severity of the line
		 */
		private Severity severity = Severity.NONE;

		/**
		 * Problem string, a union of problem messages on the line. Used for
		 * display
		 */
		private String problemString;

		/**
		 * Line constructor
		 * 
		 * @param lineNumber
		 *            line number of the line
		 * @param contents
		 *            textual line contents
		 * @param localeUtils
		 *            locale utilities for message generation
		 */
		Line(int lineNumber, String contents, final LocaleUtils localeUtils) {
			this.lineNumber = lineNumber;
			this.contents = contents;
			this.localeUtils = localeUtils;
		}

		/**
		 * Get icon for severity of the line
		 * 
		 * @return path to icon image
		 */
		public String getSeverityIcon() {
			return ShowResults.getImageForSeverity(this.severity);
		}

		/**
		 * Get current severity of the line
		 * 
		 * @param severity
		 *            of th eline
		 */
		public Severity getSeverity() {
			return this.severity;
		}

		/**
		 * Get the line number of the line
		 * 
		 * @return line number of the line
		 */
		public int getLineNumber() {
			return this.lineNumber;
		}

		/**
		 * Get the textual contents of the line
		 * 
		 * @return textual contents of the line
		 */
		public String getContents() {
			return this.contents;
		}

		/**
		 * Add a problem to line
		 * 
		 * @param problem
		 *            problem to add to line
		 */
		public void addProblem(SimpleProblem problem) {
			Severity problemSeverity = problem.getType().getSeverity();
			if (problemSeverity.getValue() < this.severity.getValue()) {
				this.severity = problemSeverity;
			}
			this.problems.add(problem);
		}

		/**
		 * Get css classname for line color decoration. If no problems, just
		 * alternate colors. If has problems, display color appropriate to
		 * severity.
		 * 
		 * @return class name for decoration
		 */
		public String getCssClass() {

			// if there are no problems, get classname by whether line number is
			// even or odd
			if (this.severity.equals(Severity.NONE)) {
				if (this.lineNumber % 2 == 0) {
					return "even"; //$NON-NLS-1$
				}
				return "odd"; //$NON-NLS-1$
			}

			// there were problems so return the severity as the class name
			return this.severity.toString();
		}

		/**
		 * Get the joined messages of all problems associated with the line.
		 * 
		 * @return joined message string
		 */
		public String getProblemString() {

			// only generate string if not already set
			if (this.problemString == null) {
				this.problemString = ""; //$NON-NLS-1$

				// iterate across problems
				for (SimpleProblem problem : this.problems) {

					// add start indicator for each message
					// TODO: find better way to display messages for line
					String message = "* "; //$NON-NLS-1$

					try {
						// generate message from props file
						message = this.localeUtils.getText(problem.getKey(),
								problem.getArguments());
					} catch (Exception e) {

						// no property was found for the given key, possibly
						// unknown parser error
						message = problem.getKey();
						System.out.println("Make key for message \"" //$NON-NLS-1$
								+ problem.getKey() + "\"."); //$NON-NLS-1$
					}

					// add message to joined string
					this.problemString += message;
				}
			}
			return this.problemString;

		}
	}

}
