package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.BaseProcess;

import java.util.HashMap;
import java.util.Map;

/**
 * Process container for everything surrounding the validation of a volume.
 * Primarily contains meta info about the validation and current state of the UI
 * view.
 * 
 * @author jagander
 */
public class ValidationProcess extends BaseProcess {

	private static final long serialVersionUID = -4637168420047734466L;

	/**
	 * String val of the uuid for validation results
	 */
	private String resultID;

	/**
	 * Relative path to node
	 */
	private String selectedNode;

	/**
	 * Full path locally or remote
	 */
	private String volumePath;

	/**
	 * Whether or not doing remote validation
	 */
	private boolean remote = false;

	/**
	 * Expand collapse states of file node listing on left
	 */
	private Map<Integer, Boolean> fileNodeStates = new HashMap<Integer, Boolean>();

	/**
	 * Expand collapse states of problem groups and clusters
	 */
	private Map<Integer, Boolean> problemStates = new HashMap<Integer, Boolean>();

	/**
	 * Constructor that creates random id
	 */
	public ValidationProcess() {
		super();
	}

	/**
	 * Constructor that creates process with known id
	 */
	public ValidationProcess(final String id) {
		super(id);
	}

	/**
	 * Get the validation results id
	 * 
	 * @return the validation results id
	 */
	public String getResultID() {
		return this.resultID;
	}

	/**
	 * Set the validation results id
	 * 
	 * @param resultID
	 *            the validation results id
	 */
	public void setResultID(String resultID) {
		this.resultID = resultID;
	}

	/**
	 * Get the selected node or null if not set
	 * 
	 * @return the selected node
	 */
	public String getSelectedNode() {
		return this.selectedNode;
	}

	/**
	 * Set the selected node
	 * 
	 * @param selectedNode
	 *            the string representation for the node to set as selected
	 */
	public void setSelectedNode(String selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * Get the path to the volume being validated
	 * 
	 * @param the
	 *            path to the volume being validated
	 */
	public String getVolumePath() {
		return this.volumePath;
	}

	/**
	 * Flag for whether this validation is occurring on the server or on the
	 * user's machine through an applet
	 * 
	 * @return flag indicating whether validation is being performed remotely
	 */
	public boolean isRemote() {
		return this.remote;
	}

	/**
	 * Get expand/collapse states of the file explorer
	 * 
	 * @return the expand states of the file explorer nodes
	 */
	public Map<Integer, Boolean> getFileNodeStates() {
		return this.fileNodeStates;
	}

	/**
	 * Get expand/collapse states of problem groupings
	 * 
	 * @return the expand states of problem groupings
	 */
	public Map<Integer, Boolean> getProblemStates() {
		return this.problemStates;
	}

	/**
	 * Set the path to the volume being validated
	 * 
	 * @param volumePath
	 *            the path to the volume being validated
	 */
	public void setVolumePath(String volumePath) {
		this.volumePath = volumePath;
	}

	/**
	 * Set whether the validation is being completed remotely
	 * 
	 * @param remote
	 *            whether the validation is being completed remotely
	 */
	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	/**
	 * Update the file explorer, setting a specific node as expanded or
	 * collapsed
	 * 
	 * @param key
	 *            key of node to update state
	 * @param state
	 *            expanded?
	 */
	public void updateFileTree(final Integer key, final Boolean state) {
		this.fileNodeStates.put(key, state);
	}

	/**
	 * Update problem groupings, setting a specific group or cluster as expanded
	 * or collapsed
	 * 
	 * @param key
	 *            key of grouping to update state
	 * @param state
	 *            expanded?
	 */
	public void updateProblemNodes(final Integer key, final Boolean state) {
		this.problemStates.put(key, state);
	}

}
