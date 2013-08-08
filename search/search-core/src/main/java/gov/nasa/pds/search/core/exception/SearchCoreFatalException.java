//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id: InvalidProductClassException.java 10821 2012-07-26 23:57:12Z jpadams $ 
//

package gov.nasa.pds.search.core.exception;

/**
 * @author pramirez
 * @version $Revision: 10821 $
 * 
 */
public class SearchCoreFatalException extends Exception {
	private static final long serialVersionUID = 5947032886827182399L;

	public SearchCoreFatalException(String message) {
		super(message);
		
		System.exit(1);
	}

}
