//  Copyright 2009-2014, by the California Institute of Technology.
//  ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//  Any commercial use must be negotiated with the Office of Technology
//  Transfer at the California Institute of Technology.
//
//  This software is subject to U. S. export control laws and regulations
//  (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
//  is subject to U.S. export control laws and regulations, the recipient has
//  the responsibility to obtain export licenses or other export authority as
//  may be required before exporting such information to foreign countries or
//  providing access to foreign nationals.
//
//  $Id$
package gov.nasa.pds.tools.label;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.pds.tools.util.Utility;
import gov.nasa.pds.tools.util.XslURIResolver;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

/**
 * A class that transforms Schematron files based on the isoSchematron
 * stylesheet.
 *
 * @author mcayanan
 *
 */
public class SchematronTransformer {
  private Transformer isoTransformer;
  private TransformerFactory transformerFactory;
  private Map<String, Transformer> cachedTransformers;

  /**
   * Constructor.
   *
   * @throws TransformerConfigurationException A transformer configuration
   *  error occurred.
   */
  public SchematronTransformer() throws TransformerConfigurationException {
    // Use saxon for schematron (i.e. the XSLT generation).
    System.setProperty("javax.xml.transform.TransformerFactory",
        "net.sf.saxon.TransformerFactoryImpl");
    TransformerFactory isoFactory = TransformerFactory.newInstance();
    // Set the resolver that will look in the jar for imports
    isoFactory.setURIResolver(new XslURIResolver());
    // Load the isoSchematron stylesheet that will be used to transform each
    // schematron file
    Source isoSchematron = new StreamSource(LabelValidator.class
        .getResourceAsStream("/schematron/iso_svrl_for_xslt2.xsl"));
    isoTransformer = isoFactory.newTransformer(isoSchematron);
    transformerFactory = TransformerFactory.newInstance();
    cachedTransformers = new HashMap<String, Transformer>();
  }

  /**
   * Transform the given schematron source.
   *
   * @param source The schematron source.
   *
   * @return A transformed schematron.
   *
   * @throws TransformerException If an error occurred during the transform
   * process.
   */
  public Transformer transform(Source source) throws TransformerException {
    return transform(source, null);
  }

  /**
   * Transform the given schematron source.
   *
   * @param source The schematron source.
   * @param container Container to hold problems that occurred during the
   * transform process.
   *
   * @return A transformed schematron.
   *
   * @throws TransformerException If an error occurred during the transform
   * process.
   */
  public Transformer transform(Source source, ExceptionContainer container)
      throws TransformerException {
    Transformer transformer = null;
    if (cachedTransformers.containsKey(source.getSystemId())) {
      transformer = cachedTransformers.get(source.getSystemId());
    } else {
      if (container != null) {
        isoTransformer.setErrorListener(
            new TransformerErrorListener(container));
      }
      StringWriter schematronStyleSheet = new StringWriter();
      isoTransformer.transform(source, new StreamResult(
          schematronStyleSheet));
      transformer = transformerFactory.newTransformer(
          new StreamSource(new StringReader(schematronStyleSheet.toString())));
    }
    return transformer;
  }

  /**
   * Transform the given schematron.
   *
   * @param schematron The URL to the schematron.
   *
   * @return A transformed schematron.
   *
   * @throws TransformerException If an error occurred during the transform
   * process.
   */
  public Transformer transform(URL schematron) throws TransformerException {
    return transform(schematron, null);
  }

  /**
   * Transform the given schematron.
   *
   * @param schematron The URL to the schematron.
   * @param container An ExceptionContainer to capture problems.
   *
   * @return A transformed schematron.
   *
   * @throws TransformerException If an error occurred during the transform
   * process.
   */
  public Transformer transform(URL schematron, ExceptionContainer container)
      throws TransformerException {
    Transformer transformer = null;

    if (cachedTransformers.containsKey(schematron.toString())) {
      transformer = cachedTransformers.get(schematron.toString());
    } else {
      if (container != null) {
        isoTransformer.setErrorListener(
            new TransformerErrorListener(container));
      }
      StringWriter schematronStyleSheet = new StringWriter();
      InputStream in = null;
      URLConnection conn = null;
      try {
        conn = schematron.openConnection();
        in = Utility.openConnection(conn);
        StreamSource source = new StreamSource(in);
        source.setSystemId(schematron.toString());
        isoTransformer.transform(source, new StreamResult(
          schematronStyleSheet));
        transformer = transformerFactory.newTransformer(
            new StreamSource(new StringReader(schematronStyleSheet.toString())));
      } catch (TransformerException te) {
        // Only throw exception if a container was not set.
        if (container == null) {
          throw te;
        }
      } catch (IOException io) {
        String message = "";
        if (io instanceof FileNotFoundException) {
          message = "Cannot read schematron as URL cannot be found: "
            + io.getMessage();
        } else {
          message = "Error occurred while reading schematron '"
              + schematron.toString() + "': " + io.getMessage();
        }
        if (container != null) {
          container.addException(new LabelException(ExceptionType.FATAL,
            message, schematron.toString()));
        } else {
          throw new TransformerException(message);
        }
      } finally {
        IOUtils.closeQuietly(in);
        IOUtils.close(conn);
      }
    }
    return transformer;
  }

}
