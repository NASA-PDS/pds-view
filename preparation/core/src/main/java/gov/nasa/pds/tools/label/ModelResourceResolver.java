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

package gov.nasa.pds.tools.label;

import java.io.InputStream;

import gov.nasa.pds.tools.util.VersionInfo;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * @author pramirez
 * 
 */
public class ModelResourceResolver implements LSResourceResolver {
  private String modelVersion;

  public ModelResourceResolver(String modelVersion) {
    this.modelVersion = modelVersion;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public LSInput resolveResource(String type, String namespaceURI,
      String publicId, String systemId, String baseURI) {
    LSInput lsin = new LSInputImpl();
    InputStream input = ModelResourceResolver.class.getResourceAsStream("/"
        + VersionInfo.SCHEMA_DIR + "/" + modelVersion + "/" + systemId);
    lsin.setSystemId(systemId);
    lsin.setByteStream(input);
    return lsin;
  }

}
