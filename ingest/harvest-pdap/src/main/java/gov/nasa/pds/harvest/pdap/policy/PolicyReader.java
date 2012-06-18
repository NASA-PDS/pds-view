// Copyright 2006-2012, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$

package gov.nasa.pds.harvest.pdap.policy;

import gov.nasa.pds.harvest.pdap.util.XMLValidationEventHandler;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PolicyReader {
    public final static String POLICY_PACKAGE = "gov.nasa.pds.harvest.pdap.policy";
    public final static String POLICY_SCHEMA = "harvest-pdap-policy.xsd";

    private Schema schema;

    private Unmarshaller unmarshaller;

    public PolicyReader() throws JAXBException, SAXException, SAXParseException {
      JAXBContext jc = JAXBContext.newInstance(POLICY_PACKAGE);
      unmarshaller = jc.createUnmarshaller();
      SchemaFactory sf = SchemaFactory.newInstance(
              javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
      unmarshaller.setEventHandler(new XMLValidationEventHandler());
      schema = null;
      try {
          schema = sf.newSchema(
                  PolicyReader.class.getResource(POLICY_SCHEMA));
      } catch (SAXException se) {
          throw new SAXException("Problems parsing harvest-pdap policy schema: "
                  + se.getMessage());
      }
    }

    public Policy unmarshall(InputStream policyXML, boolean validate) throws JAXBException {
        return unmarshall(new StreamSource(policyXML), validate);
    }

    public Policy unmarshall(File policyXML, boolean validate) throws JAXBException {
        return unmarshall(new StreamSource(policyXML), validate);
    }

    public Policy unmarshall(StreamSource policyXML, boolean validate) throws JAXBException {

      if (validate) {
        unmarshaller.setSchema(schema);
      } else {
        unmarshaller.setSchema(null);
      }
      JAXBElement<Policy> policy = unmarshaller.unmarshal(policyXML, Policy.class);
      return policy.getValue();
    }
}
