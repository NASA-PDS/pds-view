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

package gov.nasa.pds.registry.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A class to wrap list responses from the registry with.
 * 
 * @author pramirez
 * 
 */
@XmlRootElement(name = "response", namespace = "http://registry.pds.nasa.gov")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryResponse {

  // Where in the set of results to start
  @XmlAttribute
  private Integer start;

  // How many items were found
  @XmlAttribute
  private Long numFound;

  @XmlElementWrapper(name = "results", namespace = "http://registry.pds.nasa.gov")
  @XmlElementRef
  private List<? extends RegistryObject> results;

  public RegistryResponse() {
    this(null, null, null);
  }

  public RegistryResponse(Integer start, Long numFound) {
    this(start, numFound, null);
  }

  public RegistryResponse(List<? extends RegistryObject> results) {
    this(null, null, results);
  }

  public RegistryResponse(Integer start, Long numFound,
      List<? extends RegistryObject> results) {
    this.start = start;
    this.numFound = numFound;
    this.results = results;
  }

  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public Long getNumFound() {
    return numFound;
  }

  public void setNumFound(Long numFound) {
    this.numFound = numFound;
  }

  public List<? extends RegistryObject> getResults() {
    return this.results;
  }

  public void setResults(List<? extends RegistryObject> results) {
    this.results = new ArrayList<RegistryObject>(results);
  }
}
