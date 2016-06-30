package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.arc.pds.tools.container.FileMirror;
import gov.nasa.arc.pds.tools.util.FileUtils;
import gov.nasa.arc.pds.tools.util.LocaleUtils;
import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.constants.Constants.Severity;
import gov.nasa.pds.tools.containers.SimpleDictionaryChange;
import gov.nasa.pds.web.AppVersionInfo;
import gov.nasa.pds.web.ui.UIManager;
import gov.nasa.pds.web.ui.actions.BaseViewAction;
import gov.nasa.pds.web.ui.constants.ApplicationConstants;
import gov.nasa.pds.web.ui.constants.DataSetConstants.DataType;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.NewValue;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.SimpleProblem;
import gov.nasa.pds.web.ui.utils.Comparators;
import gov.nasa.pds.web.ui.utils.DateUtils;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Display validation results to user
 * 
 * @author jagander
 */
@SuppressWarnings("nls")
public class ShowResults extends BaseViewAction {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Root node of file browser, used to display tree
	 */
	private SetNode rootNode;

	/**
	 * Selected node for filtering, defaults to root node
	 */
	private SelectedNode selectedNode;

	/**
	 * String representation of selected node, used for node lookup
	 */
	private String selectedNodeString;

	/**
	 * Problem groupingings
	 */
	private List<ProblemGroup> problemGroups = new ArrayList<ProblemGroup>();

	/**
	 * A lookup map for the nodes so that problems may be associated with them
	 */
	private Map<String, SetNode> nodeLookupMap = new HashMap<String, SetNode>();

	/**
	 * New values found in the volume
	 */
	private List<NewValue> newValues = new ArrayList<NewValue>();

	/**
	 * Changes found from local data dictionaries
	 */
	private final List<DictionaryChange> dictionaryChanges = new ArrayList<DictionaryChange>();

	/**
	 * Path to the volume
	 */
	private String volumePath;

	/**
	 * Javascript safe version of the volume path
	 */
	private String volumePathJS;

	/**
	 * Path to volume on remote system for after-the-fact retrieval of relative
	 * paths
	 */
	private String fullPath;

	/**
	 * Flag for whether validation is occurring locally
	 */
	private boolean remote = false;

	/**
	 * Flag for whether the root node is selected or defaulted to
	 */
	private boolean isRootNode = true;

	/**
	 * Flag for whether all labels were parseable
	 */
	private boolean labelsParseable = true;

	/**
	 * Flag for whether all label syntax was valid
	 */
	private boolean labelSyntaxValid = true;

	/**
	 * Flag for whether all local data dictionaries are valid
	 */
	private boolean dictionaryValid = true;

	/**
	 * Flag for whether all label values were valid
	 */
	private boolean valuesValid = true;

	/**
	 * Flag for whether all label keys were valid
	 */
	private boolean keysValid = true;

	/**
	 * Flag for whether all label objects were valid
	 */
	private boolean objectsValid = true;

	/**
	 * Flag for whether all pointers were valid
	 */
	private boolean pointersValid = true;

	/**
	 * Flag for whether all line lengths were valid
	 */
	private boolean lineLengthsValid = true;

	/**
	 * Flag for whether all line endings were valid
	 */
	private boolean lineEndingsValid = true;

	/**
	 * Flag for whether index was valid
	 */
	private boolean indexValid = true;

	/**
	 * Flag for whether all products were listed in the index
	 */
	private boolean productsCovered = true;

	/**
	 * Flag for whether all tables were valid
	 * 
	 * NOTE: very limited tests are conducted on tables
	 */
	private boolean tablesValid = true;

	/**
	 * Flag for whether required files are present in the volume
	 */
	private boolean requiredResourcesPresent = true;

	/**
	 * Flag for whether empty files or folders were found
	 */
	private boolean noEmptyResources = true;

	/**
	 * Id of the volume being validated
	 */
	private String volumeId;

	/**
	 * Flag for whether it is necessary to show generate report button
	 */
	private boolean showGenerateReport = true;

	/**
	 * Size of the volume on disc
	 */
	private String volumeSpace;

	/**
	 * Number of files in the volume
	 */
	private long volumeFiles = 0;

	/**
	 * Number of folders in the volume
	 */
	private long volumeFolders = 0;

	/**
	 * Duration of time it took to validate the volume
	 */
	private String duration;

	/**
	 * File separator used on machine that did the original validation, retained
	 * so that can work with file paths with differing file separators
	 */
	private String originalSeparator;

	/**
	 * Get the nodes for representing the node tree
	 * 
	 * @return nodes for tree rendering
	 */
	public SetNode getNodes() {
		return this.rootNode;
	}

	/**
	 * Get all problems for display
	 * 
	 * @return problems, grouped by type
	 */
	public List<ProblemGroup> getProblemGroups() {
		return this.problemGroups;
	}

	/**
	 * Get selected node
	 * 
	 * @return selected node
	 */
	public SelectedNode getSelectedNode() {
		return this.selectedNode;
	}

	/**
	 * Get path to selected node
	 * 
	 * @return selected node string
	 */
	public String getNodePath() {
		return this.selectedNodeString;
	}

	/**
	 * Get path to volume
	 * 
	 * @return path to volume
	 */
	public String getVolumePath() {
		return this.volumePath;
	}

	/**
	 * Get javascript safe volume path
	 * 
	 * @return javascript safe volume path
	 */
	public String getVolumePathJS() {
		return this.volumePathJS;
	}

	/**
	 * Get new values found in the volume
	 * 
	 * @return new values
	 */
	public List<NewValue> getNewValues() {
		return this.newValues;
	}

	/**
	 * Get dictionary changes from master dictionary found in local dictionaries
	 * 
	 * @return dictionary changes
	 */
	public List<DictionaryChange> getDictionaryChanges() {
		return this.dictionaryChanges;
	}

	/**
	 * Get flag for whether validation is remote
	 */
	public boolean isRemote() {
		return this.remote;
	}

	/**
	 * Get flag for whether all labels were parseable
	 * 
	 * @return flag for all labels being parseable
	 */
	public boolean isLabelsParseable() {
		return this.labelsParseable;
	}

	/**
	 * Get flag for whether all syntax was valid in labels
	 * 
	 * @return flag for syntax being valid
	 */
	public boolean isLabelSyntaxValid() {
		return this.labelSyntaxValid;
	}

	/**
	 * Get flag for whether local data dictionaries were valid
	 * 
	 * @return local data dictionary validity
	 */
	public boolean isDictionaryValid() {
		return this.dictionaryValid;
	}

	/**
	 * Get flag for all label values being valid
	 * 
	 * @return label value validity
	 */
	public boolean isValuesValid() {
		return this.valuesValid;
	}

	/**
	 * Get flag for all label keys being valid
	 * 
	 * @return label key validity
	 */
	public boolean isKeysValid() {
		return this.keysValid;
	}

	/**
	 * Get flag for all label objects being valid
	 * 
	 * @return label object validity
	 */
	public boolean isObjectsValid() {
		return this.objectsValid;
	}

	/**
	 * Get flag for all label pointers being valid
	 * 
	 * @return pointer validity
	 */
	public boolean isPointersValid() {
		return this.pointersValid;
	}

	/**
	 * Get flag for all line lengths being valid in labels
	 * 
	 * @return line length validity
	 */
	public boolean isLineLengthsValid() {
		return this.lineLengthsValid;
	}

	/**
	 * Get flag for all line endings being validin labels
	 * 
	 * @return line ending validity
	 */
	public boolean isLineEndingsValid() {
		return this.lineEndingsValid;
	}

	/**
	 * Get flag for the index being valid
	 * 
	 * @return index validity
	 */
	public boolean isIndexValid() {
		return this.indexValid;
	}

	/**
	 * Get flag for all products being listed in the index
	 * 
	 * @return whether all products are in index
	 */
	public boolean isProductsCovered() {
		return this.productsCovered;
	}

	/**
	 * Get flag for all tables being valid
	 * 
	 * @return table validity
	 */
	public boolean isTablesValid() {
		return this.tablesValid;
	}

	/**
	 * Get flag for whether all required resources are present in the volume
	 * 
	 * @return whether required resources were found
	 */
	public boolean isRequiredResourcesPresent() {
		return this.requiredResourcesPresent;
	}

	/**
	 * Get flag for whether any empty files or folders were found
	 * 
	 * @return whether empty resources were found
	 */
	public boolean isNoEmptyResources() {
		return this.noEmptyResources;
	}

	/**
	 * Get flag for whether the root node is selected or defaulted to
	 * 
	 * @return whether root node selected
	 */
	public boolean isRootNode() {
		return this.isRootNode;
	}

	/**
	 * Get volume id
	 * 
	 * @return volume id
	 */
	public String getVolumeId() {
		return this.volumeId;
	}

	/**
	 * Get application version information
	 * 
	 * @return application version information
	 */
	public AppVersionInfo getAppVersionInfo() {
		return new AppVersionInfo();
	}

	/**
	 * Get flag for whether to show the generate report button
	 * 
	 * @return whether to show generate report
	 */
	public boolean showGenerateReport() {
		return this.showGenerateReport;
	}

	/**
	 * Get the size of the volume on disc
	 * 
	 * @return volume size
	 */
	public String getVolumeSpace() {
		return this.volumeSpace;
	}

	/**
	 * Get number files in the volume
	 * 
	 * @return number of files in volume
	 */
	public long getVolumeFiles() {
		return this.volumeFiles;
	}

	/**
	 * Get number of folders in the volume
	 * 
	 * @return number of folders in the volume
	 */
	public long getVolumeFolders() {
		return this.volumeFolders;
	}

	/**
	 * Get duration of validation
	 * 
	 * @return duration of validation
	 */
	public String getDuration() {
		return this.duration;
	}

	/**
	 * Main method of action
	 */
	@Override
	protected String executeInner() throws Exception {

		// get active validation process
		final ValidationProcess process = (ValidationProcess) getProcess();

		// if no process is found, assume invalid url used to access action
		if (process == null) {
			System.out.println("Process is null in ShowResults.executeInner().");
			return MISSING_INFORMATION;
		}

		// get expand/collapse states of problem groups from process
		final Map<Integer, Boolean> problemStates = process.getProblemStates();

		// get remote state from process
		this.remote = process.isRemote();

		// get volume path from process
		this.volumePath = process.getVolumePath();

		// convert volume path to a JS safe string
		this.volumePathJS = StrUtils.safeJS(this.volumePath);

		// get string representation of selected node from process
		this.selectedNodeString = process.getSelectedNode();

		// get serialized validation results from id and session id
		final File resultFile = HTTPUtils.getProcessTempFile(getProcId(), ApplicationConstants.VALIDATION_RESULT_FILE);
		FileInputStream in = new FileInputStream(resultFile);

		// create results
		ObjectInputStream ois = new ObjectInputStream(in);

		// read the serialized data object
		ValidationResults results = (ValidationResults) ois.readObject();
		in.close();
		ois.close();

		// get file separator used on remote system for file comparison
		this.originalSeparator = results.getOriginalSeparator();

		// get path on remote system to get relative paths where necessary
		this.fullPath = results.getPath();

		// get duration validation took
		this.duration = DateUtils.getMillisecondsToDuration(results
				.getDuration());

		// determine if report file already generated
		final File reportFile = HTTPUtils.getProcessTempFile(getProcId(), ApplicationConstants.VALIDATION_REPORT_FILE);
		this.showGenerateReport = !reportFile.exists();

		// get problems from database
		final List<SimpleProblem> foundProbs = results.getPreviewProblems();

		// create root node as the path generation is different
		// convert to safe path before making file instance
		final String unixPath = results.getPath().replaceAll(
				FileUtils.OPPOSITE_SEP_CHAR, FileUtils.REGEX_SAFE_SEP);
		final String rootNodeFileName = (new File(unixPath)).getName();
		this.rootNode = new SetNode("", rootNodeFileName, false);
		this.nodeLookupMap.put("", this.rootNode);

		// set flag for root node
		this.isRootNode = StrUtils.nullOrEmpty(this.selectedNodeString);

		// set volume size
		this.volumeSpace = getUIManager().getLocaleUtils().formatBytes(
				results.getVolumeSpace());
		this.volumeFiles = results.getNumFiles();
		this.volumeFolders = results.getNumFolders();

		// assemble node tree and add problems to groups and clusters
		while (foundProbs.size() > 0) {

			// get first node in stack
			final SimpleProblem problem = foundProbs.remove(0);

			// add problem to groups and clusters
			addProblem(problem);

			// get representation of file associated with issue
			final FileMirror file = problem.getFile();

			// get path from file representation
			final String path = file.getRelativePath();

			// get node associated with path
			SetNode node = findNode(path);

			// create or update the node from the file associated with the issue
			if (node == null) {

				// create a new node
				node = new SetNode(file);

				// add to lookup map
				this.nodeLookupMap.put(path.toUpperCase(), node);
			}

			// update the severity of a given node
			node.updateSeverity(problem.getType().getSeverity(), this);
		}

		// pass new values through
		List<NewValue> allNewValues = results.getNewValues();

		// associate new values with nodes
		for (final NewValue newVal : allNewValues) {

			// if the new value is associated with a file at or below the
			// selected node, add it to the list for display
			if (isBelowSelectedNode(newVal.getFile())
					&& !this.newValues.contains(newVal)) {
				this.newValues.add(newVal);
			}

			// add new value file nodes so that is present and selectable in the
			// file tree
			FileMirror file = newVal.getFile();
			final String path = file.getRelativePath();
			addManualNode(path);
		}

		// sort the new values
		Collections.sort(this.newValues, Comparators.NEW_VALUE_COMPARATOR);

		// show any dictionary files in the navigator
		for (SimpleDictionaryChange change : results.getDictionaryChganges()) {
			final String path = FileUtils.getRelativePath(this.fullPath, change
					.getSourceString());
			addManualNode(path);
		}

		// attach nodes
		// NOTE: couldn't do node linkage in first iteration as order of entry
		// in lookup map not guaranteed
		final Collection<SetNode> explicitNodes = new ArrayList<SetNode>();

		// add all nodes already known
		explicitNodes.addAll(this.nodeLookupMap.values());

		// iterate across nodes and fill in any missing connections
		for (final SetNode node : explicitNodes) {

			// get path of expected parent node
			final String parentPath = node.getParent();

			// if there is a parent path try to find the parent node, or create
			// it
			if (parentPath != null) {

				// get the parent node
				SetNode parent = findNode(parentPath);

				// node is null, create it
				if (parent == null) {
					// parent does not exist, no problems associated with
					// that directory so create it as filler now
					parent = createFillerNode(parentPath);
				}

				// attach the current node to the parent
				parent.attachChild(node, this);
			}
		}

		// now that the tree has been filled, clear out the node listing to
		// recover memory, the collection is potentially very large
		explicitNodes.clear();

		// set selected node if it exists
		SetNode foundNode = null;
		if (this.selectedNodeString != null) {
			foundNode = findNode(this.selectedNodeString);

		}

		// if selected node was not set or found, set to root
		if (foundNode == null) {
			foundNode = this.rootNode;
		}

		// set the selected state of the selected node
		foundNode.setSelected(true);

		// wrap selected node in selected node object
		this.selectedNode = new SelectedNode(getUIManager(), foundNode);

		// convert problems into groups and clusters
		while (foundProbs.size() > 0) {
			addProblem(foundProbs.remove(0));
		}

		// retrieve problem count from results object
		final Map<ProblemType, Integer> problemCount = results.getGroupCount();

		// sort problems, update expand state, update num errors
		for (ProblemGroup problemGroup : this.problemGroups) {

			// if root node, get total number of problems for display.
			if (this.isRootNode) {
				Integer totalProblems = problemCount
						.get(problemGroup.getType());
				problemGroup.setNumProblems(totalProblems);
			}

			// set expanded state for group
			if (problemStates.containsKey(problemGroup.hashCode())) {
				Boolean expandState = problemStates
						.get(problemGroup.hashCode());
				problemGroup.setExpanded(expandState);
			}

			// get problem clusters within a group
			final List<ProblemCluster> clusters = problemGroup.getClusters();

			// update cluster display
			Collections.sort(clusters, Comparators.PROBLEM_CLUSTER_COMPARATOR);
			for (ProblemCluster cluster : clusters) {

				// update expanded state for clusters
				if (problemStates.containsKey(cluster.hashCode())) {
					Boolean expandState = problemStates.get(cluster.hashCode());
					cluster.setExpanded(expandState);
				}

				// sort problems within clusters
				Collections.sort(cluster.getProblems(),
						Comparators.PROBLEM_COMPARATOR);
			}
		}

		// sort groups
		Collections.sort(this.problemGroups,
				Comparators.PROBLEM_GROUP_COMPARATOR);

		// sort nodes
		this.rootNode.sort();

		// update selected node with problem info
		long numErrors = 0;
		long numWarnings = 0;
		long numInfo = 0;
		for (ProblemGroup problemGroup : this.problemGroups) {

			final Severity severity = problemGroup.getType().getSeverity();
			if (severity.equals(Severity.ERROR)) {
				numErrors += problemGroup.getSize();
			} else if (severity.equals(Severity.WARNING)) {
				numWarnings += problemGroup.getSize();
			} else if (severity.equals(Severity.INFO)) {
				numInfo += problemGroup.getSize();
			}

		}
		this.selectedNode.setNumErrors(numErrors);
		this.selectedNode.setNumWarnings(numWarnings);
		this.selectedNode.setNumInfo(numInfo);

		// pass through dictionary changes if any
		final List<SimpleDictionaryChange> changes = results
				.getDictionaryChganges();
		final LocaleUtils localeUtils = getUIManager().getLocaleUtils();
		for (final SimpleDictionaryChange simpleChange : changes) {
			final DictionaryChange change = new DictionaryChange(simpleChange,
					localeUtils, this.fullPath);
			this.dictionaryChanges.add(change);
		}

		// update tests passed or failed
		updateTests();

		// get volume ID
		this.volumeId = results.getVolumeId();

		// set title
		setTitle("validationResults.title", this.volumeId);

		return SUCCESS;
	}

	/**
	 * Add a node to the tree manually. Used with new values and dictionary
	 * changes.
	 * 
	 * @param path
	 *            path to node
	 */
	private void addManualNode(final String path) {

		// find node if it exists
		SetNode node = findNode(path);

		// create or update the node from the file associated with the path
		if (node == null) {

			// create a new node with no explicit name and force to be file
			node = new SetNode(path, null, true);

			// add to lookup map
			this.nodeLookupMap.put(path.toUpperCase(), node);
		}
	}

	/**
	 * Create filler nodes from the given path to a valid ancestor node
	 * 
	 * @param path
	 *            path to the node that needs to be created
	 * 
	 * @return newly created node
	 */
	private SetNode createFillerNode(final String path) {

		// create a new node with the given path
		final SetNode node = new SetNode(path);

		// put the node in the lookup map
		this.nodeLookupMap.put(path.toUpperCase(), node);

		// if another broken link in the chain above it, create that as well
		String parentPath = node.getParent();

		// not dealing with real files, null parent may mean parent is root, but
		// make sure THIS isn't root
		if (parentPath == null && path != "") {
			parentPath = "";
		}

		// if a parent path exists, try to find the node
		if (parentPath != null) {

			// get the parent
			SetNode parent = findNode(parentPath);

			// if the parent is null, create a filler
			if (parent == null) {

				// do filler from parent to valid ancestor and return as current
				// parent
				parent = createFillerNode(parentPath);

				// NOTE: if the map already contains the node, it already has or
				// will be attached elsewhere
			}

			// attach the node to its parent
			parent.attachChild(node, this);
		}

		return node;
	}

	/**
	 * Update test states. Checks if all of a given type of test has passed.
	 * 
	 * Sparsely commented as checks and updates are fairly self explanatory
	 */
	private void updateTests() {
		this.labelsParseable = !problemGroupExists(ProblemType.INVALID_LABEL);

		this.labelSyntaxValid = !problemGroupExists(ProblemType.PARSE_ERROR);

		List<ProblemType> valuesProblems = Arrays.asList(new ProblemType[] {
				ProblemType.UNKNOWN_VALUE_TYPE, ProblemType.UNKNOWN_VALUE,
				ProblemType.INVALID_VALUE, ProblemType.TYPE_MISMATCH,
				ProblemType.EXCESSIVE_VALUE_LENGTH, ProblemType.INVALID_DATE });
		this.valuesValid = !problemGroupsExist(valuesProblems);

		List<ProblemType> keysProblems = Arrays.asList(new ProblemType[] {
				ProblemType.UNKNOWN_KEY, ProblemType.MISSING_ID });
		this.keysValid = !problemGroupsExist(keysProblems);

		// TODO: why no invalid property error?
		List<ProblemType> objectProblems = Arrays.asList(new ProblemType[] {
				ProblemType.INVALID_MEMBER, ProblemType.MISSING_MEMBER,
				ProblemType.MISSING_PROPERTY });
		this.objectsValid = !problemGroupsExist(objectProblems);

		this.dictionaryValid = this.valuesValid && this.keysValid
				&& this.objectsValid;

		List<ProblemType> pointerProblems = Arrays
				.asList(new ProblemType[] {
						ProblemType.POTENTIAL_POINTER_PROBLEM,
						ProblemType.CIRCULAR_POINTER_REF,
						ProblemType.MISSING_RESOURCE });
		this.pointersValid = !problemGroupsExist(pointerProblems);

		List<ProblemType> lineLengthProblems = Arrays.asList(new ProblemType[] {
				ProblemType.EXCESSIVE_LINE_LENGTH,
				ProblemType.WRONG_LINE_LENGTH });
		this.lineLengthsValid = !problemGroupsExist(lineLengthProblems);

		this.lineEndingsValid = !problemGroupExists(ProblemType.ILLEGAL_LINE_ENDING);

		List<ProblemType> indexProblems = Arrays.asList(new ProblemType[] {
				ProblemType.LABEL_NOT_INDEXED,
				ProblemType.LABEL_NOT_TO_BE_INDEXED,
				ProblemType.MISSING_INDEX_RESOURCE });
		this.indexValid = !problemGroupsExist(indexProblems);

		this.productsCovered = !problemGroupExists(ProblemType.UNKNOWN_FILE);

		List<ProblemType> tableProblems = Arrays.asList(new ProblemType[] {
				ProblemType.COLUMN_NUMBER_MISMATCH,
				ProblemType.COLUMN_LENGTH_MISMATCH,
				ProblemType.INVALID_VALUE_FOR_COLUMN });
		this.tablesValid = !problemGroupsExist(tableProblems);

		this.requiredResourcesPresent = !problemGroupExists(ProblemType.MISSING_REQUIRED_RESOURCE);

		List<ProblemType> emptyProblems = Arrays.asList(new ProblemType[] {
				ProblemType.EMPTY_FILE, ProblemType.EMPTY_FOLDER });
		this.noEmptyResources = !problemGroupsExist(emptyProblems);
	}

	/**
	 * Check that a problem group exists with a given type
	 * 
	 * @param problemType
	 *            problem type to check against existing problem groups
	 * 
	 * @return flag for a problem group existing with the given type
	 */
	private boolean problemGroupExists(ProblemType problemType) {
		for (ProblemGroup group : this.problemGroups) {
			if (group.getType().equals(problemType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check that a problem group exists with one of the given types
	 * 
	 * @param types
	 *            problem types to check against existing problem groups
	 * 
	 * @return flag for a problem group existing with one of the given types
	 */
	private boolean problemGroupsExist(List<ProblemType> types) {
		for (ProblemGroup group : this.problemGroups) {
			ProblemType type = group.getType();
			if (types.contains(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a problem into problem groups surfaced to user
	 * 
	 * @param simpleProblem
	 *            simple problem container to add to problem groups
	 */
	private void addProblem(final SimpleProblem simpleProblem) {

		// check that the problem is below the selected node in the tree before
		// adding
		if (isBelowSelectedNode(simpleProblem.getFile())) {

			// create a problem from the simple problem
			DataSetProblem problem = new DataSetProblem(simpleProblem,
					getUIManager().getLocaleUtils());

			// create a problem group from the type
			ProblemGroup problemGroup = new ProblemGroup(simpleProblem
					.getType());

			// get the index of the group within the groups
			int index = this.problemGroups.indexOf(problemGroup);

			if (index != -1) {

				// the group already existed to just add the problem
				this.problemGroups.get(index).addProblem(problem);
			} else {

				// add the problem to the group
				problemGroup.addProblem(problem);

				// the problem group didn't exist, add it
				this.problemGroups.add(problemGroup);
			}
		}
	}

	/**
	 * Check to see if the target file is below the selected node
	 * 
	 * @param targetFile
	 *            file to check for being below selected node
	 * 
	 * @return flag for whether the target file is below the selected node
	 * 
	 *         TODO this dupes isParent stuff in FileUtils but can't quite merge
	 */
	private boolean isBelowSelectedNode(final FileMirror targetFile) {

		// if root node is selected, just return true
		if (this.isRootNode) { //$NON-NLS-1$
			return true;
		}

		// get the selected path and upper case it for ease of comparison
		final String selectedPath = this.selectedNodeString.toUpperCase()
				+ this.originalSeparator;

		// get the target path and upper case it for ease of comparison
		final String searchPath = targetFile.getRelativePath().toUpperCase()
				+ this.originalSeparator;

		// if the target path starts with the selected node path, it's a child
		return searchPath.startsWith(selectedPath);
	}

	/**
	 * Find a node with the given path
	 * 
	 * @param searchPath
	 *            path of node to look for
	 * 
	 * @return node matching path, if it exists
	 */
	SetNode findNode(final String searchPath) {

		// upper case path for simplicity of comparison
		final String key = searchPath.toUpperCase();

		// find the node in the lookup map
		if (this.nodeLookupMap.containsKey(key)) {
			return this.nodeLookupMap.get(key);
		}

		// not found, return null
		return null;
	}

	/**
	 * Get the image file name for severity
	 * 
	 * @param severity
	 *            severity to retrieve an image for
	 * 
	 * @return image file name associated with severity
	 */
	public static String getImageForSeverity(final Severity severity) {
		if (severity.equals(Severity.ERROR)) {
			return "error.png";
		} else if (severity.equals(Severity.WARNING)) {
			return "warning.png";
		} else if (severity.equals(Severity.INFO)) {
			return "info.png";
		} else if (severity.equals(Severity.NONE)) {
			return null;
		}
		return null;
	}

	/**
	 * Wrapper for nodes in a file tree
	 */
	public static class SetNode {

		/**
		 * Display name for the node
		 */
		final String name;

		/**
		 * File vs folder
		 */
		final DataType type;

		/**
		 * Flag for whether the node is a directory
		 */
		protected final Boolean isDirectory;

		/**
		 * Relative path to node from volume root
		 */
		protected final String relativePath;

		/**
		 * Parent node path
		 */
		protected final String parent;

		/**
		 * File size, assuming it is a file
		 */
		protected final long length;

		/**
		 * Severity of problems associated with the node
		 */
		protected Severity severity;

		/**
		 * Flag for whether this is the currently selected node
		 */
		boolean selected = false;

		/**
		 * Child nodes
		 */
		private final List<SetNode> children = new ArrayList<SetNode>();

		/**
		 * Get the relative path from node to volume root
		 * 
		 * @return relative path
		 */
		public String getRelativePath() {
			return this.relativePath;
		}

		/**
		 * Get the parent path
		 * 
		 * @return get parent path
		 */
		public String getParent() {
			return this.parent;
		}

		/**
		 * Get the file size
		 * 
		 * @return file size
		 */
		public long getLength() {
			return this.length;
		}

		/**
		 * Minimal constructor for the node. For use in filler node generation
		 * when a node is missing connections between itself and the next valid
		 * ancestor node.
		 */
		public SetNode(final String path) {
			this(path, null, false);
		}

		/**
		 * Manual constructor for a tree node.
		 * 
		 * @param path
		 *            path to node
		 * @param display
		 *            name for node
		 * @param isFile
		 *            whether node is a file or not
		 */
		public SetNode(final String path, final String name,
				final boolean isFile) {
			this.type = isFile ? DataType.fromFileName(path)
					: DataType.DIRECTORY;
			this.isDirectory = true;
			this.relativePath = path;
			this.length = 0;
			final File fileObj = new File(path);
			this.parent = fileObj.getParent() == null && !path.equals("") ? ""
					: fileObj.getParent();
			this.name = name == null ? fileObj.getName() : name;
			this.severity = Severity.NONE;
		}

		/**
		 * Normal constructor for a tree node.
		 * 
		 * @param file
		 *            file representation for node
		 */
		public SetNode(final FileMirror file) {

			// display name is just file name
			this.name = file.getName();

			// pass through directory flag from file representation
			this.isDirectory = file.isDirectory();

			if (file.isDirectory()) {
				// if directory, type is directory...
				this.type = DataType.DIRECTORY;
			} else {
				// if not directory, infer type from file name
				this.type = DataType.fromFileName(this.name);
			}

			// get size from file representation
			this.length = file.length();

			// get parent if it exists, defaulting to an empty string if parent
			// is root
			this.parent = file.getParent() == null
					&& !file.getRelativePath().equals("") ? "" : file
					.getParent();

			// pass through relative path
			this.relativePath = file.getRelativePath();

			// default severity to none
			this.severity = Severity.NONE;
		}

		/**
		 * Attach a child node
		 * 
		 * @param node
		 *            node to attach
		 * @param action
		 *            action context, required for updating severity
		 */
		public void attachChild(final SetNode node, ShowResults action) {

			// only add if isn't already attached
			if (!this.children.contains(node)) {

				// add to children
				this.children.add(node);

				// update severity if necessary
				updateSeverity(node.getSeverity(), action);
			}
		}

		/**
		 * Sort children for display, should be called once on the root node
		 * once all have been added
		 */
		public void sort() {

			// sort children
			Collections.sort(this.children, Comparators.SET_NODE_COMPARATOR);

			// for each child, if they are directories, sort their children
			for (final SetNode node : this.children) {
				if (node.isDirectory) {
					node.sort();
				}
			}
		}

		/**
		 * Get child nodes
		 * 
		 * @return child nodes
		 */
		public List<SetNode> getChildren() {
			return this.children;
		}

		/**
		 * Whether the node has children
		 * 
		 * @return flag for whether there are children
		 */
		public boolean getHasChildren() {
			return this.children.size() > 0;
		}

		/**
		 * Get name of node
		 * 
		 * @return node name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Set whether this node is the selected node
		 * 
		 * @param selected
		 *            whether this node is the selected node
		 */
		public void setSelected(final boolean selected) {
			this.selected = selected;
		}

		/**
		 * Get node type
		 * 
		 * @return node type
		 */
		public DataType getType() {
			return this.type;
		}

		/**
		 * Flag for whether this node is a directory
		 * 
		 * @return flag for whether node is a directory
		 */
		public boolean isDirectory() {
			return this.isDirectory;
		}

		/**
		 * Update severity setting as worst level from this and all children
		 * 
		 * @param testSeverity
		 *            severity to check against for updating to
		 * @param action
		 *            action context used for doing node lookup
		 */
		public void updateSeverity(final Severity testSeverity,
				ShowResults action) {

			// if severity is null or test severity is worse than current
			// severity, set to test severity
			// NOTE: no need to check children if test is not worse, it will
			// already be at the high water mark
			if (this.severity == null
					|| this.severity.getValue() > testSeverity.getValue()) {

				// set severity
				this.severity = testSeverity;

				// if the parent isn't null, cascade the severity up
				if (this.parent != null) {

					// get the parent node
					final SetNode parentNode = action.findNode(this.parent);

					// if the parent node exists, update the severity
					if (parentNode != null) {
						parentNode.updateSeverity(testSeverity, action);
					}
				}
			}
		}

		/**
		 * Get severity of problems associated with node
		 * 
		 * @return severity of node problems
		 */
		public Severity getSeverity() {
			return this.severity;
		}

		/**
		 * Flag for node being the selected node
		 * 
		 * @return is selected node
		 */
		public boolean isSelected() {
			return this.selected;
		}

		/**
		 * Equivalence test
		 * 
		 * @param obj
		 *            object to check equivalence against
		 * 
		 * @return is equal
		 */
		@Override
		public boolean equals(Object obj) {

			// if same object, euqal
			if (this == obj) {
				return true;
			}

			// if object is null or classes differ, not equal
			if ((obj == null) || (obj.getClass() != this.getClass())) {
				return false;
			}

			// already know it's a set node, cast it
			SetNode node = (SetNode) obj;

			// equal if the paths are equal
			return node.getRelativePath().equals(this.getRelativePath());
		}
	}

	/**
	 * Problem container
	 */
	public static class DataSetProblem {

		/**
		 * Problem type
		 */
		private final ProblemType problemType;

		/**
		 * Problem message
		 */
		private String message;

		/**
		 * File name or folder name problem occurred in
		 */
		private String resource;

		/**
		 * Path to problem location
		 */
		private String path;

		/**
		 * Line number in file that a problem occurred in
		 */
		private Integer location;

		/**
		 * Instance of locale utilities for message resolution
		 */
		private LocaleUtils localeUtils;

		/**
		 * Arguments for message resolution and testing similarity to other
		 * problems
		 */
		private Object[] arguments;

		/**
		 * Message key
		 */
		private String key;

		/**
		 * Constructor
		 * 
		 * @param problem
		 *            simple problem this is built from
		 * @param localeUtils
		 *            locale utilities used for message resolution
		 */
		public DataSetProblem(final SimpleProblem problem,
				final LocaleUtils localeUtils) {
			this.resource = problem.getFile().getName();
			this.location = problem.getLineNumber();
			this.path = problem.getFile().getRelativePath();
			this.key = problem.getKey();
			this.localeUtils = localeUtils;
			this.arguments = problem.getArguments();
			this.problemType = problem.getType();
		}

		/**
		 * Get message key. Only used for testing.
		 * 
		 * @return message key
		 */
		public String getKey() {
			return this.key;
		}

		/**
		 * Get message arguments. Only used for testing.
		 */
		public Object[] getArguments() {
			return this.arguments;
		}

		/**
		 * Get problem message
		 * 
		 * @return problem message
		 */
		public String getMessage() {
			if (this.message == null) {
				// TODO: determine how to work around lack of key for message
				try {
					this.message = this.localeUtils.getText(this.key,
							this.arguments);
				} catch (Exception e) {
					this.message = this.key;
					System.out.println("Make key for message \"" + this.key
							+ "\".");
				}
			}
			return this.message;
		}

		/**
		 * Get problem type
		 * 
		 * @return problem type
		 */
		public ProblemType getType() {
			return this.problemType;
		}

		/**
		 * Get resource problem occurred in, name only
		 * 
		 * @return resource problem occurred in
		 */
		public String getResource() {
			return this.resource;
		}

		/**
		 * Get location problem occurred in
		 * 
		 * @return location problem occurred in
		 */
		public String getPath() {
			return this.path;
		}

		/**
		 * Get location problem occurred in, javascript safe
		 * 
		 * @returned js safe problem location
		 */
		public String getPathJS() {
			return StrUtils.safeJS(this.path);
		}

		/**
		 * Get line number problem occurred on
		 * 
		 * @return line number that the problem occurred on
		 */
		public Integer getLineNumber() {
			return this.location;
		}

		/**
		 * Get line number to scroll to for preview, drops back 5 if possible
		 * for better presentation
		 * 
		 * @return line to scroll to on file preview
		 */
		public Integer getScrollLineNumber() {
			return this.location > 5 ? this.location - 5 : 0;
		}

		/**
		 * To string method, used primarily in debugging
		 * 
		 * @return string representation of object
		 */
		@Override
		public String toString() {
			return this.problemType.toString() + " : " + this.getMessage();
		}

		/**
		 * Whether this problem is similar to a given problem
		 * 
		 * @param problem
		 *            problem to compare with
		 * 
		 * @return whether the test problem is similar to this problem
		 */
		public boolean isSimiliarTo(final DataSetProblem problem) {

			// keys don't match, fail
			if (!problem.key.equals(this.key)) {
				return false;
			}

			// TODO: find or create array equivelency test that meets needs
			// args of unequal length, can't be equivalent
			if (problem.arguments.length != this.arguments.length) {
				return false;
			}

			// match each argument
			int i = 0;
			for (final Object arg : this.arguments) {
				if (!arg.equals(problem.arguments[i])) {
					return false;
				}
			}

			// no failures, return true
			return true;
		}
	}

	/**
	 * Container for the selected node, should only be one instance of this.
	 * Contains more meta info than FileNode.
	 * 
	 * TODO add date created info
	 */
	public static class SelectedNode {

		/**
		 * Node name
		 */
		private final String name;

		/**
		 * Node type
		 */
		final DataType type;

		/**
		 * Number of files
		 */
		long numFilesRaw;

		/**
		 * Nice numeric presentation of number of files
		 */
		String numFiles;

		/**
		 * File size, including children if folder
		 */
		long sizeRaw;

		/**
		 * Nice presentation of file size or folder size
		 */
		String size;

		/**
		 * Number of errors in this node and below
		 */
		long numErrors;

		/**
		 * Number of warnings in this node or below
		 */
		long numWarnings;

		/**
		 * Number of info in this node or below
		 */
		long numInfo;

		/**
		 * Instance of locale utils for number and date formatting
		 */
		final LocaleUtils localeUtils;

		/**
		 * Path to node
		 */
		final String path;

		/**
		 * Whether the node is a directory
		 */
		final boolean isDirectory;

		/**
		 * Constructor for node
		 */
		public SelectedNode(final UIManager uiManager,
				final SetNode selectedNode) {
			this.localeUtils = uiManager.getLocaleUtils();
			this.name = selectedNode.getName();
			this.type = selectedNode.getType();

			// initialize node characteristics dependent on children
			initFiles(selectedNode);

			this.size = this.localeUtils.formatBytes(this.sizeRaw);
			this.numFiles = this.localeUtils.getText(
					"validation.selectedNode.text.numFiles", this.numFilesRaw);
			this.path = selectedNode.getRelativePath();
			this.isDirectory = selectedNode.isDirectory();
		}

		/**
		 * Initialize properties related to children
		 * 
		 * @param file
		 *            node to retrieve children through
		 */
		private void initFiles(final SetNode file) {

			// create a stack for nodes to walk
			final Stack<SetNode> dirStack = new Stack<SetNode>();

			// init stack with current node
			dirStack.push(file);

			// set size if selected file is not directory
			if (!file.isDirectory()) {
				this.sizeRaw = file.getLength();
			}

			// walk stack
			while (!dirStack.empty()) {

				// get current node
				final SetNode curDir = dirStack.pop();

				// get node children
				final List<SetNode> tempFiles = curDir.getChildren();

				// if there are children, update with their properties
				if (tempFiles != null) {
					for (final SetNode curFile : tempFiles) {

						if (curFile.isDirectory()) {
							// directory, push into stack
							dirStack.push(curFile);
						} else {

							// file, increment num files and size
							this.numFilesRaw++;
							this.sizeRaw += curFile.getLength();
						}
					}
				}
			}
		}

		/**
		 * Get whether is directory
		 * 
		 * @return whether is directory
		 */
		public boolean isDirectory() {
			return this.isDirectory;
		}

		/**
		 * Get number of files below node
		 * 
		 * @return num files
		 */
		public String getNumFiles() {
			return this.numFiles;
		}

		/**
		 * Get representation for node
		 * 
		 * @return node name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Get node type
		 * 
		 * @return node type
		 */
		public DataType getType() {
			return this.type;
		}

		/**
		 * Get size of this file or sum of children
		 * 
		 * @return size of node
		 */
		public String getSize() {
			return this.size;
		}

		/**
		 * Get number of errors
		 * 
		 * @return number of errors
		 */
		public long getNumErrors() {
			return this.numErrors;
		}

		/**
		 * Setter for num errors, calculated in external process
		 * 
		 * @param numErrors
		 *            number of errors
		 */
		public void setNumErrors(long numErrors) {
			this.numErrors = numErrors;
		}

		/**
		 * Get number of warnings
		 * 
		 * @return number of warnings
		 */
		public long getNumWarnings() {
			return this.numWarnings;
		}

		/**
		 * Set number of warnings, calculated in an external process
		 * 
		 * @return numWarnings number of warnings
		 */
		public void setNumWarnings(long numWarnings) {
			this.numWarnings = numWarnings;
		}

		/**
		 * Get number of info
		 * 
		 * @return number of info
		 */
		public long getNumInfo() {
			return this.numInfo;
		}

		/**
		 * Set number of info, calculated in an external process
		 * 
		 * @param numInfo
		 *            number of info
		 */
		public void setNumInfo(long numInfo) {
			this.numInfo = numInfo;
		}

		/**
		 * Get path to node
		 * 
		 * @return path to node
		 */
		public String getPath() {
			return this.path;
		}

		/**
		 * Get javascript safe path to node
		 * 
		 * @return js safe node path
		 */
		public String getPathJS() {
			return StrUtils.safeJS(this.path);
		}
	}

	/**
	 * A group of similar problems. Grouped by problem type.
	 */
	public static class ProblemGroup {

		/**
		 * Problems in group
		 */
		final List<ProblemCluster> groupProblems = new ArrayList<ProblemCluster>();

		/**
		 * Description for group
		 */
		final String description;

		/**
		 * Problem type that identifies group
		 */
		final ProblemType type;

		/**
		 * Number of files in group
		 */
		int size = 0;

		/**
		 * Number of problems in excess of what can be displayed
		 */
		int excessProblems = 0;

		/**
		 * Whether group is expanded, defaults to true
		 */
		Boolean expanded = true;

		/**
		 * Constructor for group
		 * 
		 * @param type
		 *            problem type that identifies the group
		 */
		public ProblemGroup(ProblemType type) {
			this.type = type;

			// TODO: make more human readable description
			this.description = type.toString();
		}

		/**
		 * Equality test for group, beyond the basics, just tests that types are
		 * equal
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (obj.getClass() != this.getClass())) {
				return false;
			}
			ProblemGroup group = (ProblemGroup) obj;
			return group.getType().equals(this.type);
		}

		@Override
		public int hashCode() {
			return this.description.hashCode();
		}

		/**
		 * Get group type
		 * 
		 * @return group type
		 */
		public ProblemType getType() {
			return this.type;
		}

		/**
		 * Get filename for severity icon
		 * 
		 * @return filename for severity icon
		 */
		public String getSeverityIcon() {
			return ShowResults.getImageForSeverity(this.type.getSeverity());
		}

		/**
		 * Add problem to group. Adds to or creates a problem to a cluster of
		 * similar problems.
		 * 
		 * @param problem
		 *            problem to add
		 */
		public void addProblem(DataSetProblem problem) {

			// get cluster that has same message as this problem
			ProblemCluster cluster = new ProblemCluster(problem.getMessage());

			// find cluster
			int index = this.groupProblems.indexOf(cluster);
			if (index != -1) {

				// cluster found, add to cluster
				this.groupProblems.get(index).addProblem(problem);
			} else {

				// cluster not found, create it and add problem
				cluster.addProblem(problem);
				this.groupProblems.add(cluster);
			}
			this.size++;
		}

		/**
		 * Set the number of problems, calculated externally and, in this case,
		 * just calculates the number of problems that exist in the group but
		 * could not be displayed
		 * 
		 * @param numProblems
		 *            number of problems known to exist in the group
		 */
		public void setNumProblems(final int numProblems) {
			this.excessProblems = numProblems - this.size;
		}

		/**
		 * Get group description
		 * 
		 * @return group description
		 */
		public String getDescription() {
			return this.description;
		}

		/**
		 * Get clusters associated with this group
		 * 
		 * @return clusters in the group
		 */
		public List<ProblemCluster> getClusters() {
			return this.groupProblems;
		}

		/**
		 * Get number of problems including ones unable to be displayed
		 * 
		 * @return number of problems in group
		 */
		public int getSize() {
			return this.size + this.excessProblems;
		}

		/**
		 * Get excess number of problems beyond those displayed
		 * 
		 * @return number of problems in excess of limit
		 */
		public int getExcessProblems() {
			return this.excessProblems;
		}

		/**
		 * Is node expanded
		 * 
		 * @return flag for whether node is expanded
		 */
		public boolean isExpanded() {
			return this.expanded;
		}

		/**
		 * Set expanded state
		 * 
		 * @param expanded
		 *            expand state for node
		 */
		public void setExpanded(final boolean expanded) {
			this.expanded = expanded;
		}

		/**
		 * String representation of group
		 * 
		 * @return string representation of group
		 */
		@Override
		public String toString() {
			return this.type.toString() + " " + this.description;
		}
	}

	/**
	 * Problem cluster, a grouping of very similar problems
	 */
	public static class ProblemCluster {

		/**
		 * Problems in cluster
		 */
		final List<DataSetProblem> clusterProblems = new ArrayList<DataSetProblem>();

		/**
		 * Problem cluster descriptions
		 */
		final String description;

		/**
		 * Whether cluster is expanded
		 */
		Boolean expanded = false;

		/**
		 * Constructor for cluster, clustered by problem message, used as
		 * description
		 * 
		 * @param description
		 */
		public ProblemCluster(String description) {
			this.description = description;
		}

		/**
		 * Equivalency test, beyond basics, just tests description
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (obj.getClass() != this.getClass())) {
				return false;
			}
			ProblemCluster cluster = (ProblemCluster) obj;
			return cluster.getDescription().equals(this.description);
		}

		@Override
		public int hashCode() {
			return this.description.hashCode();
		}

		/**
		 * Add problem to cluster
		 * 
		 * @param problem
		 *            problem to add to cluster
		 */
		public void addProblem(DataSetProblem problem) {
			this.clusterProblems.add(problem);
		}

		/**
		 * Get description for cluster
		 * 
		 * @return cluster description
		 */
		public String getDescription() {
			return this.description;
		}

		/**
		 * Get problems in cluster
		 * 
		 * @return cluster problems
		 */
		public List<DataSetProblem> getProblems() {
			return this.clusterProblems;
		}

		/**
		 * Flag for whether node is expanded
		 * 
		 * @return flag for expand state
		 */
		public boolean isExpanded() {
			return this.expanded;
		}

		/**
		 * Set expand state for node
		 * 
		 * @param expanded
		 *            expand state to set
		 */
		public void setExpanded(final boolean expanded) {
			this.expanded = expanded;
		}

		/**
		 * Get string representation of object
		 * 
		 * @return string representation of object
		 */
		@Override
		public String toString() {
			return this.description + "(" + this.clusterProblems.size() + ")";
		}
	}

	/**
	 * Wrapper for one change from local dictionary to master dictionary
	 */
	public static class DictionaryChange {

		/**
		 * Line number in local data dictionary that change occurred in
		 */
		private final Integer lineNumber;

		/**
		 * String version of dictionary identifier
		 */
		private final String id;

		/**
		 * Message to describe the change that occurred
		 */
		private final String message;

		/**
		 * Path to file that initiated the change
		 */
		private final String path;

		/**
		 * Constructor for change. Primarily a wrapper for a simple change
		 * previously recorded.
		 * 
		 * @param change
		 *            change that occurred
		 * @param localeUtils
		 *            locale utils used to render message
		 * @param volumePath
		 *            path to volume to use in getting relative path in change
		 * 
		 *            TODO send in change type or just have it be part of the
		 *            message
		 */
		// send in type? or just have different messages
		public DictionaryChange(final SimpleDictionaryChange change,
				final LocaleUtils localeUtils, final String volumePath) {
			this.lineNumber = change.getLineNumber();
			this.id = change.getId().toString();
			this.message = localeUtils.getText(change.getMessageKey(), change
					.getArguments());
			this.path = FileUtils.getRelativePath(volumePath, change
					.getSourceString());
		}

		/**
		 * Get line number change occurred on
		 * 
		 * @return line number change occurred on
		 */
		public Integer getLineNumber() {
			return this.lineNumber;
		}

		/**
		 * Get line number to scroll to when previewing the change
		 * 
		 * @param line
		 *            number to scroll to in preview
		 */
		public Integer getScrollLineNumber() {
			return this.lineNumber > 5 ? this.lineNumber - 5 : 0;
		}

		/**
		 * Get definition id
		 * 
		 * @return definition id for change
		 */
		public String getId() {
			return this.id;
		}

		/**
		 * Get message describing change
		 * 
		 * @return message describing change
		 */
		public String getMessage() {
			return this.message;
		}

		/**
		 * Get path to file change occurred in
		 * 
		 * @return path to file change occurred in
		 */
		public String getPath() {
			return this.path;
		}

		/**
		 * Get javascript safe path to file change occurred in
		 * 
		 * @return js safe path to change location
		 */
		public String getPathJS() {
			return StrUtils.safeJS(this.path);
		}

		/**
		 * Get string representation of object
		 * 
		 * @return string representation of object
		 */
		@Override
		public String toString() {
			return this.message;
		}
	}

}
