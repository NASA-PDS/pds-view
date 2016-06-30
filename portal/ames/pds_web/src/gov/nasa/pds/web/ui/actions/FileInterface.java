package gov.nasa.pds.web.ui.actions;

/**
 * Interface for actions that return files for download. Currently unnused as
 * output is large enough that it must be streamed.
 * 
 * @author jagander
 */
public interface FileInterface {

	/**
	 * Get content type of output file
	 */
	public String getContentType();

	/**
	 * Get output file name
	 */
	public String getFileName();

}
