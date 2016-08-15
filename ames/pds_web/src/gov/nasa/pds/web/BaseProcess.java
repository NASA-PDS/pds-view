package gov.nasa.pds.web;

import java.io.Serializable;
import java.util.UUID;

/**
 * Base class for all process containers. Useful for wizard type processes.
 * 
 * @author jagander
 */
public abstract class BaseProcess implements Serializable {

	private static final long serialVersionUID = 3814660161042545589L;

	/**
	 * The process id
	 */
	protected String id;

	/**
	 * Constructor for the process. Creates a random id.
	 */
	public BaseProcess() {
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * Constructor for the process that allows you to define the id rather than
	 * have it assigned for you.
	 * 
	 * Useful for applet work where we need to have the prodc id for redirect
	 * and the proc is generated in a separate post. Easier to give id to post
	 * rather than get results containing new id.
	 */
	public BaseProcess(final String id) {
		this.id = id;
	}

	/**
	 * Get the id for the process.
	 */
	public String getID() {
		return this.id;
	}
}
