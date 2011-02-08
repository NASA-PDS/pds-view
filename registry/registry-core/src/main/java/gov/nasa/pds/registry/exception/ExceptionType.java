//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.registry.exception;

import javax.ws.rs.core.Response;

/**
 * @author pramirez
 * 
 */
public enum ExceptionType {
  OBJECT_NOT_FOUND(Response.Status.NOT_FOUND), INVALID_REQUEST(Response.Status.BAD_REQUEST), EXISTING_OBJECT(Response.Status.CONFLICT);

  private Response.Status status;

  private ExceptionType(Response.Status status) {
    this.status = status;
	}
  
  public Response.Status getStatus() {
    return this.status;
  }
}
