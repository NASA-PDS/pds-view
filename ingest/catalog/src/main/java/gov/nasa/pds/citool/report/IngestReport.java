//Copyright 2006-2009, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//Any commercial use must be negotiated with the Office of Technology Transfer
//at the California Institute of Technology.
//
//This software is subject to U. S. export control laws and regulations
//(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
//is subject to U.S. export control laws and regulations, the recipient has
//the responsibility to obtain export licenses or other export authority as
//may be required before exporting such information to foreign countries or
//providing access to foreign nationals.

// $Id$
//

package gov.nasa.pds.citool.report;

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.Severity;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.label.validate.Validator;
import gov.nasa.pds.tools.util.MessageUtils;
import gov.nasa.pds.citool.status.Status;
//import gov.nasa.pds.citool.util.Utility;

import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

/**
 * This class represents a full report for the Vtool command line. This is the
 * standard report that will display all problems generated for every file that
 * was inspected. Messages are grouped at the file level and then summarized at
 * the end.
 *
 * @author pramirez
 *
 */
public class IngestReport extends Report {
	private boolean printDetails = true;
	
	public void setPrintDetails(boolean print) {
		printDetails = print;
	}

  @Override
  protected void printHeader(PrintWriter writer) {
      writer.println("\nPDS Catalog Ingest Tool Report");
      writer.println();
      writer.println("Configuration:");
      for (String configuration : configurations) {
        writer.println(configuration);
      }
      writer.println();
      writer.println("Parameters:");
      for (String parameter : parameters) {
        writer.println(parameter);
      }
      writer.println();
    writer.println("Ingest Details:");
  }

  @Override
  protected void printRecordMessages(PrintWriter writer, Status status,
      URI sourceUri, List<LabelParserException> problems) {
    Map<URI, List<LabelParserException>> externalProblems = new HashMap<URI, List<LabelParserException>>();
    writer.println();
    writer.print("  ");
    writer.print(status.getName());
    writer.print(": ");
    writer.println(sourceUri.toString());

    // Print all the sources problems and gather all external problems
    for (LabelParserException problem : problems) {
      if (sourceUri.equals(problem.getSourceURI())) {
        printProblem(writer, problem);        
      } else {
        List<LabelParserException> extProbs = externalProblems.get(problem
            .getSourceURI());
        if (extProbs == null) {
          extProbs = new ArrayList<LabelParserException>();
        }
        extProbs.add(problem);
        externalProblems.put(problem.getSourceURI(), extProbs);
      }
    }
    
    for (URI extUri : externalProblems.keySet()) {
      writer.print("    Begin Fragment: ");
      writer.println(extUri.toString());
      for (LabelParserException problem : externalProblems.get(extUri)) {
        printProblem(writer, problem);
      }
      writer.print("    End Fragment: ");
      writer.println(extUri.toString());
    }
  }

  private void printProblem(PrintWriter writer,
      final LabelParserException problem) {
    writer.print("      ");
    writer.print(problem.getType().getSeverity().getName());
    writer.print(":  ");
    if (problem.getLineNumber() != null) {
      writer.print("line ");
      writer.print(problem.getLineNumber().toString());
      if (problem.getColumn() != null) {
        writer.print(", ");
        writer.print(problem.getColumn().toString());
      }
      writer.print(": ");
    }

    try {
      writer.println(MessageUtils.getProblemMessage(problem));      
    } catch (RuntimeException re) {
      // For now the MessageUtils class seems to be throwing this exception
      // when it can't find a key. In these cases the actual message seems to
      // be the key itself.
      writer.println(problem.getKey());
      re.printStackTrace();
    }
  }

  @Override
  protected void printFooter(PrintWriter writer) {
      int totalFiles = getNumPassed() + getNumFailed() + getNumSkipped();
      int totalValidated = getNumPassed() + getNumFailed();
      writer.println();
      writer.println("Summary:");
      writer.println();
      writer.println("  " + gov.nasa.pds.citool.ingestor.CatalogRegistryIngester.fileObjCount + " of " 
    		  + totalFiles + " file(s) ingested, "
              + this.getNumSkipped() + " skipped");
      //writer.println("  " + this.getNumPassed() + " of " + totalValidated
      //        + " passed");
      writer.println();  
      if (printDetails) {
    	  writer.println("  Number of successful file object ingestion: " + gov.nasa.pds.citool.ingestor.CatalogRegistryIngester.fileObjCount);
    	  writer.println("  Number of successful storage service ingestion: " + gov.nasa.pds.citool.ingestor.CatalogRegistryIngester.storageCount);
    	  writer.println("  Number of successful registry ingestion: " + gov.nasa.pds.citool.ingestor.CatalogRegistryIngester.registryCount);
    	  writer.println("  Name of the registry package: " + gov.nasa.pds.citool.ingestor.CatalogRegistryIngester.registryPackageName);
    	  writer.println();
      }
      writer.println("End of Report\n");
      writer.flush();
  }

  @Override
  public void printRecordSkip(PrintWriter writer, final URI sourceUri,
      final Exception exception) {
    writer.println();
    writer.print("  ");
    writer.print(Status.SKIP.getName());
    writer.print(": ");
    writer.println(sourceUri.toString());

    if (exception instanceof LabelParserException) {
      LabelParserException problem = (LabelParserException) exception;
      writer.print("      ");
      writer.print(problem.getType().getSeverity().getName());
      writer.print(":  ");
      if (problem.getLineNumber() != null) {
        writer.print("line ");
        writer.print(problem.getLineNumber().toString());
        if (problem.getColumn() != null) {
          writer.print(", ");
          writer.print(problem.getColumn().toString());
        }
        writer.print(": ");
      }
      try {
        writer.println(MessageUtils.getProblemMessage(problem));
      } catch (RuntimeException re) {
        // For now the MessageUtils class seems to be throwing this exception
        // when it can't find a key. In these cases the actual message seems to
        // be the key itself.
        writer.println(problem.getKey());
      }
    } else {
      writer.print("      ");
      writer.print(Severity.ERROR.getName());
      writer.print("  ");
      writer.println(exception.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    ConsoleAppender console = new ConsoleAppender(
        new PatternLayout("%-5p %m%n"));
    console.setThreshold(Level.FATAL);
    BasicConfigurator.configure(console);
    Report report = new ValidateReport();
    report.printHeader();
    ManualPathResolver resolver = new ManualPathResolver();
    DefaultLabelParser parser = new DefaultLabelParser(true, true, true,
        resolver);
    URL labelURL = new URL(args[0]);
    resolver.setBaseURI(ManualPathResolver.getBaseURI(labelURL.toURI()));
    Label label = parser.parseLabel(labelURL, true);
    Validator validator = new Validator();
    validator.validate(label);
    report.record(label.getLabelURI(), label.getProblems());
    report.printFooter();
  }

  @Override
  protected void printRecordMessages(PrintWriter writer, Status status,
        List<String> sourceUris, List<LabelParserException> problems) {
    // TODO Auto-generated method stub

  }
}
