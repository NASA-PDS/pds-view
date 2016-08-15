package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;

/**
 * Post a file for previewing
 * 
 * @author jagander
 */
public class PostFilePreview extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * File node for preview
	 */
	private String node;

	/**
	 * Set the node for previewing
	 * 
	 * @param node
	 *            the node to preview
	 */
	public void setNode(final String node) {
		this.node = node;
	}

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// get the request
		final HttpServletRequest request = ServletActionContext.getRequest();

		// get the input stream from the request
		InputStream in = request.getInputStream();

		// get temp dir to write file to
		final File targetFile = HTTPUtils.getProcessTempFile(getProcId(), Integer.toString(this.node.hashCode()));
		targetFile.createNewFile();

		// create output stream for writing
		FileOutputStream out = new FileOutputStream(targetFile);

		// copy the posted file to cache file
		IOUtils.copy(in, out);

		return JSON;
	}

	/**
	 * Push back user input, nothing to see here
	 */
	@Override
	protected void pushBackUserInput() {
		// TODO Auto-generated method stub
	}

	/**
	 * Validate user input. Not implemented at the moment but there may be
	 * possible things to validate such as file permissions
	 */
	@Override
	protected void validateUserInput() {
		// TODO Auto-generated method stub
	}

}
