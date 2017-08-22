// Copyright 2006-2017, by the California Institute of Technology.
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
package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.label.CachedEntityResolver;
import gov.nasa.pds.tools.label.ExceptionContainer;
import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.LabelException;
import gov.nasa.pds.tools.label.LabelValidator;
import gov.nasa.pds.tools.label.MissingLabelSchemaException;
import gov.nasa.pds.tools.label.SchematronTransformer;
import gov.nasa.pds.tools.util.Utility;
import gov.nasa.pds.tools.util.XMLExtractor;
import gov.nasa.pds.tools.validate.ValidationResourceManager;
import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.GenericProblems;
import gov.nasa.pds.tools.validate.rule.ValidationTest;
import net.sf.saxon.tinytree.TinyNodeImpl;
import net.sf.saxon.trans.XPathException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implements a validation chain that validates PDS4 bundles. It is applicable
 * if there is a bundle label in the root directory.
 */
public class LabelValidationRule extends AbstractValidationRule {

	private static final Pattern LABEL_PATTERN = Pattern.compile(".*\\.xml", Pattern.CASE_INSENSITIVE);

	private static final String XML_SUFFIX = ".xml";
	
	private SchemaValidator schemaValidator;
	private SchematronTransformer schematronTransformer;
  private Map<URL, ExceptionContainer> labelSchemaResults;
  private Map<URL, ExceptionContainer> labelSchematronResults;
  private Map<URL, Transformer> labelSchematrons;
	
  public LabelValidationRule() throws TransformerConfigurationException {
    schemaValidator = new SchemaValidator();
    schematronTransformer = new SchematronTransformer();
    labelSchemaResults = new HashMap<URL, ExceptionContainer>();
    labelSchematronResults = new HashMap<URL, ExceptionContainer>();
    labelSchematrons = new HashMap<URL, Transformer>();
  }
  
	@Override
	public boolean isApplicable(String location) {
    if (Utility.isDir(location)) {
      return false;
    } else {
      return true;
    }
	}

	/**
	 * Implements a rule that checks the label file extension.
	 */
	@ValidationTest
	public void checkLabelExtension() {
	  if (!FilenameUtils.getName(getTarget().toString()).endsWith(XML_SUFFIX)) {
		  reportError(PDS4Problems.INVALID_LABEL_EXTENSION, getTarget(), -1, -1);
    }
	}

	/**
	 * Parses the label and records any errors resulting from the parse,
	 * including schema and schematron errors.
	 */
	@ValidationTest
	public void validateLabel() {
		ExceptionProcessor processor = new ExceptionProcessor(getListener(), getTarget());

    LabelValidator validator = ValidationResourceManager.INSTANCE.getResource(LabelValidator.class);
		try {
      Document document = null;
      if (getContext().isForceLabelSchemaValidation()) {
        ExceptionContainer exceptionContainer = new ExceptionContainer();
        // Validate the label's schema and schematron first before doing
        // label validation
        boolean hasValidSchemas = validateLabelSchemas(getTarget(),
            exceptionContainer);

        Map<String, Transformer> labelSchematrons = validateLabelSchematrons(
            getTarget(), exceptionContainer);

        if (hasValidSchemas && !labelSchematrons.isEmpty()) {
          CachedEntityResolver resolver = new CachedEntityResolver();
          resolver.addCachedEntities(schemaValidator.getCachedLSResolver()
              .getCachedEntities());
          validator.setCachedEntityResolver(resolver);
          validator.setCachedLSResourceResolver(
              schemaValidator.getCachedLSResolver());
          validator.setLabelSchematrons(labelSchematrons);
          // By logging this INFO message, it will allow us to be able to report if a file passed validation.
          getListener().addProblem(new LabelException(ExceptionType.INFO, "Processing target", getTarget().toString()));
          document = validator.parseAndValidate(processor, getTarget());
        } else {
          // Print any label problems that occurred during schema and schematron
          // validation.
          if (exceptionContainer.getExceptions().size() != 0) {
            for (LabelException le : exceptionContainer.getExceptions()) {
              le.setSource(getTarget().toString());
              getListener().addProblem(le);
            }
          }
        }
      } else {
        // By logging this INFO message, it will allow us to be able to report if a file passed validation.
        getListener().addProblem(new LabelException(ExceptionType.INFO, "Processing target", getTarget().toString()));
        document = validator.parseAndValidate(processor, getTarget());
      }
      if (document != null) {
        getContext().put(PDS4Context.LABEL_DOCUMENT, document);
      }
		} catch (SAXException | IOException | ParserConfigurationException
				| TransformerException | MissingLabelSchemaException e) {
		  if (e instanceof XPathException) {
		    XPathException xe = (XPathException) e;
		    if (!xe.hasBeenReported()) {
          reportError(GenericProblems.UNCAUGHT_EXCEPTION, getTarget(), -1, -1, e.getMessage());		      
		    }
		  } else {
  		  // Don't need to report SAXParseException messages as they have already 
  		  // been reported by the LabelValidator's error handler
  		  if (!(e instanceof SAXParseException)) {
  		    reportError(GenericProblems.UNCAUGHT_EXCEPTION, getTarget(), -1, -1, e.getMessage());
  		  }
		  }
		}
	}
	
  private boolean validateLabelSchemas(URL label,
      ExceptionContainer labelProblems) {
    boolean passFlag = true;
    List<URL> schemaUrls = new ArrayList<URL>();
    List<StreamSource> sources = new ArrayList<StreamSource>();
    String schemaLocations = "";
    try {
      XMLExtractor extractor = new XMLExtractor(label);
      schemaLocations = extractor.getSchemaLocation();
    } catch (Exception e) {
      labelProblems.addException(new LabelException(ExceptionType.FATAL,
          "Error occurred while attempting to find schemas using the XPath '"
          + XMLExtractor.SCHEMA_LOCATION_XPATH + "': " + e.getMessage(),
          label.toString()));
      return false;
    }
    if (schemaLocations == null ||
        (schemaLocations != null && schemaLocations.isEmpty())) {
      labelProblems.addException(new LabelException(ExceptionType.ERROR,
          "No schema(s) found in the label.", label.toString()));
      return false;
    } else {
      StringTokenizer tokenizer = new StringTokenizer(schemaLocations);
      if ((tokenizer.countTokens() % 2) != 0) {
        labelProblems.addException(new LabelException(ExceptionType.ERROR,
            "schemaLocation value does not appear to have matching sets of "
            + "namespaces to uris: '" + schemaLocations + "'",
            label.toString()));
        return false;
      } else {
        // While loop that will grab the schema URIs
        while (tokenizer.hasMoreTokens()) {
          // First token assumed to be the namespace
          String namespace = tokenizer.nextToken();
          // Second token assumed to be the URI
          String uri = tokenizer.nextToken();
          URL schemaUrl = null;
          try {
            schemaUrl = new URL(uri);
            schemaUrls.add(schemaUrl);
          } catch (MalformedURLException mu) {
            // The schema specification value does not appear to be
            // a URL. Assume a local reference to the schematron and
            // attempt to resolve it.
            try {
              URL parent = label.toURI().resolve(".").toURL();
              schemaUrl = new URL(parent, schemaLocations);
              schemaUrls.add(schemaUrl);
            } catch (MalformedURLException mue) {
              labelProblems.addException(new LabelException(ExceptionType.ERROR,
                  "Cannot resolve schema specification '"
                      + schemaLocations + "': " + mue.getMessage(),
                      label.toString()));
              passFlag = false;
            } catch (URISyntaxException e) {
              //Ignore
            }
          }
        }
      }
    }
    if (labelProblems.getExceptions().size() != 0) {
      passFlag = false;
    } else {
      try {
        schemaValidator.setExternalLocations(schemaLocations);
      } catch (Exception ignore) {
        //Should not throw an exception
      }
      for (URL schemaUrl : schemaUrls) {
        ExceptionContainer container = new ExceptionContainer();
        schemaValidator.getCachedLSResolver().setExceptionHandler(container);
        LSInput input = schemaValidator.getCachedLSResolver()
            .resolveResource("", "", "", schemaUrl.toString(),
                schemaUrl.toString());
        boolean addSource = true;
        if (container.getExceptions().size() != 0) {
          try {
            for (LabelException le : container.getExceptions()) {
              le.setSource(schemaUrl.toURI().toString());
              getListener().addProblem(le);
            }
            if (container.hasError() || container.hasFatal()) {
              passFlag = false;
              addSource = false;
            }
          } catch (URISyntaxException u) {
            labelProblems.addException(new LabelException(ExceptionType.FATAL,
                "URI syntax exception occurred for schema '"
                + schemaUrl.toString() + "': " + u.getMessage(),
                label.toString()));
          }
        }
        if (addSource) {
          StreamSource streamSource = new StreamSource(
              input.getByteStream());
          streamSource.setSystemId(schemaUrl.toString());
          sources.add(streamSource);
        }
      }
      if (passFlag) {
        for (StreamSource source : sources) {
          try {
            URL schemaUrl = null;
            try {
              schemaUrl = new URL(source.getSystemId());
            } catch(MalformedURLException ignore) {
              //Should never throw an exception
            }
            ExceptionContainer container = new ExceptionContainer();
            if (labelSchemaResults.containsKey(schemaUrl)) {
              container = labelSchemaResults.get(schemaUrl);
              if (container.getExceptions().size() != 0) {
                for (LabelException le : container.getExceptions()) {
                  le.setSource(schemaUrl.toURI().toString());
                  getListener().addProblem(le);
                }
                if (container.hasError() || container.hasFatal()) {
                  passFlag = false;
                }
              }
            } else {
              try {
                container = schemaValidator.validate(source);
              } catch (Exception e) {
                container.addException(new LabelException(ExceptionType.ERROR,
                    "Error reading schema: " + e.getMessage(),
                    schemaUrl.toString()));
              }
              if (container.getExceptions().size() != 0) {
                for (LabelException le : container.getExceptions()) {
                  le.setSource(schemaUrl.toURI().toString());
                  getListener().addProblem(le);
                }
                if (container.hasError() || container.hasFatal()) {
                  passFlag = false;
                }
              }
              labelSchemaResults.put(schemaUrl, container);
            }
          } catch (URISyntaxException u) {
            labelProblems.addException(new LabelException(ExceptionType.FATAL,
                "URI syntax exception occurred for schema '"
                + source.getSystemId() + "': " + u.getMessage(),
                label.toString()));
          }
        }
      }
    }
    return passFlag;
  }

  private Map<String, Transformer> validateLabelSchematrons(URL label,
      ExceptionContainer labelProblems) {
    boolean passFlag = true;
    Map<String, Transformer> results = new HashMap<String, Transformer>();
    List<TinyNodeImpl> xmlModels = new ArrayList<TinyNodeImpl>();
    try {
      XMLExtractor extractor = new XMLExtractor(label);
      xmlModels = extractor.getNodesFromDoc(XMLExtractor.XML_MODEL_XPATH);
    } catch (Exception e) {
      labelProblems.addException(new LabelException(ExceptionType.FATAL,
          "Error occurred while attempting to find schematrons using the XPath '"
          + XMLExtractor.XML_MODEL_XPATH + "': " + e.getMessage(),
          label.toString()));
      // results should be empty
      return results;
    }
    Pattern pattern = Pattern.compile(
        "href=\\\"([^=]*)\\\"( schematypens=\\\"([^=]*)\\\")?"
        );
    List<URL> schematronRefs = new ArrayList<URL>();
    for (TinyNodeImpl xmlModel : xmlModels) {
      String filteredData = xmlModel.getStringValue().replaceAll("\\s+", " ");
      filteredData = filteredData.trim();
      Matcher matcher = pattern.matcher(filteredData);
      if (matcher.matches()) {
        String value = matcher.group(1).trim();
        URL schematronRef = null;
        try {
          schematronRef = new URL(value);
        } catch (MalformedURLException ue) {
          // The schematron specification value does not appear to be
          // a URL. Assume a local reference to the schematron and
          // attempt to resolve it.
          try {
            URL parent = label.toURI().resolve(".").toURL();
            schematronRef = new URL(parent, value);
          } catch (MalformedURLException mue) {
            labelProblems.addException(new LabelException(ExceptionType.ERROR,
                "Cannot resolve schematron specification '"
                    + value + "': " + mue.getMessage(),
                    label.toString(),
                    label.toString(),
                    new Integer(xmlModel.getLineNumber()),
                    null));
            passFlag = false;
            continue;
          } catch (URISyntaxException e) {
            //Ignore
          }
        }
        schematronRefs.add(schematronRef);
      }
    }
    if (schematronRefs.isEmpty()) {
      labelProblems.addException(new LabelException(ExceptionType.ERROR,
          "No schematrons specified in the label", label.toString()));
    }

    //Now validate the schematrons
    for (URL schematronRef : schematronRefs) {
      try {
        ExceptionContainer container = null;
        if (labelSchematrons.containsKey(schematronRef)) {
          container = labelSchematronResults.get(schematronRef);
          if (container.getExceptions().size() != 0) {
            for (LabelException le : container.getExceptions()) {
              le.setSource(schematronRef.toURI().toString());
              getListener().addProblem(le);
            }
            if (container.hasError() || container.hasFatal()) {
              passFlag = false;
            }
          } else {
            results.put(schematronRef.toString(),
                labelSchematrons.get(schematronRef));
          }
        } else {
          container = new ExceptionContainer();
          try {
            Transformer transformer = schematronTransformer.transform(
                schematronRef, container);
            labelSchematrons.put(schematronRef, transformer);
            results.put(schematronRef.toString(), transformer);
          } catch (TransformerException te) {
            //Ignore as the listener handles the exceptions and puts it into
            //the container
          } catch (Exception e) {
            container.addException(new LabelException(ExceptionType.FATAL,
                "Error occurred while attempting to read schematron: "
                + e.getMessage(), schematronRef.toString()));
          }
          if (container.getExceptions().size() != 0) {
            for (LabelException le : container.getExceptions()) {
              le.setSource(schematronRef.toURI().toString());
              getListener().addProblem(le);
            }
            if (container.hasError() || container.hasFatal()) {
              passFlag = false;
            }
          }
          labelSchematronResults.put(schematronRef, container);
        }
      } catch (URISyntaxException u) {
        labelProblems.addException(new LabelException(ExceptionType.FATAL,
            "URI syntax exception occurred for schematron '"
            + schematronRef.toString() + "': " + u.getMessage(),
            label.toString()));
      }
    }
    if (!passFlag) {
      results.clear();
    }
    return results;
  }
}
