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

package gov.nasa.pds.registry.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author pramirez
 * 
 */
public class DateParam {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss");
  private final Date date;
  private final String originalValue;
  
  static {
    dateFormat.setLenient(false);
  }

  public DateParam(String date) throws WebApplicationException {
    this.originalValue = date;
    try {
      this.date = dateFormat.parse(date);
    } catch (ParseException e) {
      throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
          .entity("Couldn't parse date \"" + date + "\"")
          .build());
    }
  }
  
  public Date getDate() {
    return date;
  }
  
  public String getOriginalValue() {
    return originalValue;
  }

}
