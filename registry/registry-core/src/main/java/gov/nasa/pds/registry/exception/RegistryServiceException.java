//	Copyright 2009-2014, by the California Institute of Technology.
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

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author pramirez
 *
 */
public class RegistryServiceException extends Exception {
  private static final long serialVersionUID = -3676574693217741086L;
  private final ExceptionType exceptionType;
  private final String messageKey;
  private final Object[] arguments;
  private final ClientResponse.Status status;

  public RegistryServiceException(final String messageKey,
      final ExceptionType exceptionType, final Object... arguments) {
    this.messageKey = messageKey;
    this.exceptionType = exceptionType;
    this.arguments = arguments;
    this.status = null;
  }

  public RegistryServiceException(final String messageKey, final Response.Status status) {
    this.messageKey = messageKey;
    this.status = ClientResponse.Status.fromStatusCode(status.getStatusCode());
    this.arguments = null;
    this.exceptionType = null;
  }

  public RegistryServiceException(final String messageKey, final ClientResponse.Status status) {
    this.messageKey = messageKey;
    this.status = status;
    this.arguments = null;
    this.exceptionType = null;
  }

  public ExceptionType getExceptionType() {
    return this.exceptionType;
  }

  public String getMessageKey() {
    return this.messageKey;
  }

  public Object[] getArguments() {
    return this.arguments;
  }

  @Override
  public String getMessage() {
    String message = "";
    //TODO fix to generate message from resource bundle
    ClientResponse.Status status = getStatus();
    message = "[" + status.getStatusCode() + " " + status.name() + "]";
    if ( (messageKey != null) && !(messageKey.isEmpty()) ) {
      message = message + "   " + messageKey;
    }

    return message;
  }

  public ClientResponse.Status getStatus () {
    return (exceptionType == null) ? status : ClientResponse.Status.fromStatusCode(exceptionType.getStatus().getStatusCode());
  }
}
