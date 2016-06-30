package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;

/**
 * Update the view of a given data set for the duration of a session. This
 * information should be stored in the session for the context of the displaying
 * class.
 * 
 * @author jagander
 * 
 */
public class UpdateValidateView extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Currently selected node in the file explorer tree
	 */
	private String selectedNode;

	/**
	 * Node to expand or collapse in the file explorer tree
	 */
	private Integer fileNode;

	/**
	 * Problem group to expand or contract
	 */
	private Integer problemNode;

	/**
	 * State to set the node to
	 */
	private Boolean state;

	/**
	 * Set the selected node
	 * 
	 * @param selectedNode
	 *            node to select
	 */
	public void setNode(final String selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * Set expand/collapse state of problem group/cluster or node in the tree
	 * 
	 * @param state
	 *            expand/collapse state
	 */
	public void setState(final Boolean state) {
		this.state = state;
	}

	/**
	 * Set the file to expand or collapse
	 * 
	 * @param fileNode
	 *            node to expand or collapse
	 */
	public void setFile(final Integer fileNode) {
		this.fileNode = fileNode;
	}

	/**
	 * Problem group to expand or collapse
	 * 
	 * @param problemNode
	 *            problem group to expand or collapse
	 */
	public void setProblemGroup(final Integer problemNode) {
		this.problemNode = problemNode;
	}

	/**
	 * Getter for the anchor of the clicked group, allows the resulting forward
	 * to scroll to the clicked location. See the struts action definition for
	 * this class.
	 * 
	 * @return html id for the clicked element
	 */
	public String getNamedAnchor() {
		return "problemGroup" + this.problemNode; //$NON-NLS-1$
	}

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {
		// get process
		ValidationProcess process = (ValidationProcess) getProcess();

		// if a node was clicked, select it
		if (this.selectedNode != null) {
			process.setSelectedNode(this.selectedNode);
		}

		// if a node was expanded or collapsed, update the node construct
		if (this.fileNode != null) {
			process.updateFileTree(this.fileNode, this.state);
		}

		// if a group was expanded or collapsed, update the problems grouping
		// construct
		if (this.problemNode != null) {
			process.updateProblemNodes(this.problemNode, this.state);

			// return a special "success" that includes a named anchor to force
			// a scroll to the clicked location
			return "namedSuccess"; //$NON-NLS-1$
		}

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateUserInput() {
		// TODO: check if all 3 options are null and say no input provided
	}

}
