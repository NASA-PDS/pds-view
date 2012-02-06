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

import java.io.IOException;

import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;

/**
 * @author pramirez
 * 
 */
public class XMLCatalogResolver extends
    org.apache.xerces.util.XMLCatalogResolver {

  public String resolveIdentifier(XMLResourceIdentifier resourceIdentifier)
      throws XNIException, IOException {
    String resolvedId = null;

    Boolean xsdInclude = false;
    // Don't resolve by URI if this is a schema include
    if (resourceIdentifier instanceof XSDDescription) {
      XSDDescription xsdDescription = (XSDDescription) resourceIdentifier;
      if (xsdDescription.getContextType() == XSDDescription.CONTEXT_INCLUDE) {
        xsdInclude = true;
      }
    }

    // The namespace is useful for resolving namespace aware
    // grammars such as XML schema. Let it take precedence over
    // the external identifier if one exists.
    String namespace = resourceIdentifier.getNamespace();
    if (namespace != null && !xsdInclude) {
      resolvedId = resolveURI(namespace);
    }

    // Resolve against an external identifier if one exists. This
    // is useful for resolving DTD external subsets and other
    // external entities. For XML schemas if there was no namespace
    // mapping we might be able to resolve a system identifier
    // specified as a location hint.
    if (resolvedId == null) {
      String publicId = resourceIdentifier.getPublicId();
      String systemId = getUseLiteralSystemId() ? resourceIdentifier
          .getLiteralSystemId() : resourceIdentifier.getExpandedSystemId();
      if (publicId != null && systemId != null) {
        resolvedId = resolvePublic(publicId, systemId);
      } else if (systemId != null) {
        resolvedId = resolveSystem(systemId);
      }
    }
    return resolvedId;
  }
  

}
