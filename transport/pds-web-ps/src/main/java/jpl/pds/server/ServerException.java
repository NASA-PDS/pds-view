// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.

package jpl.pds.server;

/**
 * Exception in a PDS server.
 *
 * @author Kelly
 * @version $Revision$
 */
public class ServerException extends Exception {
	/**
	 * Creates a new <code>ServerException</code> instance.
	 */
	public ServerException() {}

	/**
	 * Creates a new <code>ServerException</code> instance.
	 *
	 * @param msg Detail message.
	 */
	public ServerException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new <code>ServerException</code> instance.
	 *
	 * @param cause Chained exception.
	 */
	public ServerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new <code>ServerException</code> instance.
	 *
	 * @param msg Detail message.
	 * @param cause Chained exception.
	 */
	public ServerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
