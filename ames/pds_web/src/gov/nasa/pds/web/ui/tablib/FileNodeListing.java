package gov.nasa.pds.web.ui.tablib;

import gov.nasa.pds.tools.constants.Constants.Severity;
import gov.nasa.pds.web.ui.actions.dataManagement.ValidationProcess;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.SetNode;
import gov.nasa.pds.web.ui.annotations.WidgetTagAttribute;
import gov.nasa.pds.web.ui.constants.DataSetConstants.DataType;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

public class FileNodeListing extends WidgetBodyTagSupport {

	private SetNode rootNode;

	private static final long serialVersionUID = 1L;

	private String selectPath;

	private String procId;

	private final Map<Integer, Boolean> expandStates = new HashMap<Integer, Boolean>();

	@SuppressWarnings("nls")
	public static enum Icon {
		FILE("file.gif"), //
		FILE_WARNING("fileWarning.gif"), //
		FILE_ERROR("fileError.gif"), //
		FOLDER("folder.gif"), //
		FOLDER_WARNING("folderWarning.gif"), //
		FOLDER_ERROR("folderError.gif");

		private final String fileName;

		private Icon(final String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return this.fileName;
		}

		public static Icon fromNode(final SetNode node) {
			DataType type = node.getType();
			Severity severity = node.getSeverity();
			if (type.equals(DataType.DIRECTORY)) {
				if (severity.equals(Severity.ERROR)) {
					return FOLDER_ERROR;
				}
				if (severity.equals(Severity.WARNING)) {
					return FOLDER_WARNING;
				}
				return FOLDER;
			}
			if (severity.equals(Severity.ERROR)) {
				return FILE_ERROR;
			}
			if (severity.equals(Severity.WARNING)) {
				return FILE_WARNING;
			}
			return FILE;
		}

	}

	@WidgetTagAttribute(description = "Method name to look up in value stack to find root node", required = true)
	public void setNode(final String methodName) {
		this.rootNode = (SetNode) findValue(methodName);
	}

	@WidgetTagAttribute(description = "Process id", required = true)
	public void setProcId(final String methodName) {
		this.procId = (String) findValue(methodName);
	}

	@SuppressWarnings("nls")
	private void addElement(SetNode node) {
		DataType nodeType = node.getType();
		final boolean isDir = nodeType.equals(DataType.DIRECTORY);

		// if it's not a directory and there are no problems, don't render
		// if (!isDir && node.getSeverity().getValue() ==
		// Severity.NONE.getValue()) {
		// return;
		// }
		final String iconPath = getBasePath() + "/web/images/icons/";
		out("<div class=\"treeNode");
		if (node.isSelected()) {
			out(" selected");
		}
		out("\">");

		final String relativeFileName = node.getRelativePath();
		final Integer fileKey = relativeFileName.hashCode();
		Boolean open;
		if (this.expandStates.containsKey(fileKey)) {
			open = this.expandStates.get(fileKey);
		} else {
			open = isDir
					&& node.getSeverity().getValue() < Severity.NONE.getValue();
		}

		if (isDir) {
			out("<a href=\"" + this.selectPath + "?file="
					+ relativeFileName.hashCode() + "&amp;state="
					+ Boolean.toString(!open) + "&amp;procId=" + this.procId
					+ "\">");
			if (open) {
				out("<img src=\"" + iconPath + "minus.gif\" />");
			} else {
				out("<img src=\"" + iconPath + "plus.gif\" />");
			}
			out("</a>");
		}

		Icon icon = Icon.fromNode(node);
		final String path = iconPath + icon.getFileName();
		out("<img src=\"" + path + "\" />");

		out("<a href=\"" + this.selectPath + "?node=" + relativeFileName
				+ "&amp;procId=" + this.procId + "\">");
		out(node.getName() + "</a>");
		out("</div>\n");
		if (node.getHasChildren()) {
			// only add children if open
			if (open) {
				out("<div class=\"treeNodeList\">\n");
				for (SetNode child : node.getChildren()) {
					addElement(child);
				}
				out("</div>\n");
			}
		}

	}

	@SuppressWarnings("nls")
	@Override
	public int doEndTag() {
		// path for updates
		this.selectPath = getBasePath() + "/UpdateValidateView.action";

		// get overriding expand states from process
		final ValidationProcess process = (ValidationProcess) HTTPUtils
				.getProcess(this.procId);
		this.expandStates.putAll(process.getFileNodeStates());

		out("<div class=\"treeNodeList\">\n");
		addElement(this.rootNode);
		out("</div>\n");
		try {
			writeContents();
		} catch (JspException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

}
