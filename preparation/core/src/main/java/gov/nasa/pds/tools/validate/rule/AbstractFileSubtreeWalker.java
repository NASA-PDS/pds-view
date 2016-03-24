package gov.nasa.pds.tools.validate.rule;

import java.io.File;

/**
 * Implements a class that walks a file system subtree and performs
 * an operation on each file or directory found.
 *
 * @param <T> the type used by the concrete subclass to keep track of state
 */
public abstract class AbstractFileSubtreeWalker<T> {

	/**
	 * Walks a subtree starting from a specified file or directory.
	 *
	 * @param f a file or directory
	 * @param state the state managed by the caller
	 */
	public void walkSubtree(File f, T state) {
		if (!f.isDirectory()) {
			try {
				handleFile(f, state);
			} catch (Exception e) {
				// TODO Log the exception
				e.printStackTrace();
			}
		} else {
			T childState;
			try {
				childState = handleDirectory(f, state);
				for (File child : f.listFiles()) {
					walkSubtree(child, childState);
				}
			} catch (Exception e) {
				// TODO log the exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles encountering a directory. Subclasses should
	 * implement the processing for directories in this method.
	 *
	 * @param dir the directory encountered
	 * @param state the current walking state
     * @throws Exception if there is an error processing the directory
	 * @return the new state for walking children of this directory
	 */
	protected abstract T handleDirectory(File dir, T state) throws Exception;

	/**
	 * Handles encountering a file. Subclasses should
	 * implement the processing for files in this method.
	 *
	 * @param f the file encountered
	 * @param state the current walking state
	 * @throws Exception if there is an error processing the file
	 */
	protected abstract void handleFile(File f, T state) throws Exception;

}
