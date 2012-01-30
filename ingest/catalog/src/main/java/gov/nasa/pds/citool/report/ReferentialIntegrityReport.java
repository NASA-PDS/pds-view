package gov.nasa.pds.citool.report;

import gov.nasa.pds.citool.diff.DiffException;
import gov.nasa.pds.citool.status.Status;
import gov.nasa.pds.citool.util.Utility;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.Severity;
import gov.nasa.pds.tools.util.MessageUtils;

import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public class ReferentialIntegrityReport extends Report {
    private String riCheckHeader;

    public ReferentialIntegrityReport() {
        riCheckHeader = "";
    }

    public void setIntegrityCheckHeader(String name) {
        this.riCheckHeader = name;
    }

    @Override
    protected void printHeader(PrintWriter writer) {
        // TODO Auto-generated method stub
        writer.println();
        writer.println("Referential Integrity Details:");
    }

    @Override
    protected void printRecordMessages(PrintWriter writer, Status status,
            List<String> sourceUris, List<LabelParserException> problems) {
        Map<URI, List<LabelParserException>> externalProblems = new HashMap<URI, List<LabelParserException>>();
        // TODO Auto-generated method stub
        writer.println();
        writer.print("  ");
        writer.print(status.getName());
        writer.print(": ");
        writer.println(riCheckHeader);
        writer.print("    Parent File(s): ");
        // Just get the file name of the URI
        writer.println(sourceUris.toString());
        // Print all the sources problems and gather all external problems
        for (LabelParserException problem : problems) {
            boolean printedProblem = false;
            for (String source : sourceUris) {
                if (source.equals(FilenameUtils.getName(problem.getSourceURI().getPath()))) {
                   printProblem(writer, problem);
                   printedProblem = true;
                   break;
                }
            }
            if (!printedProblem) {
                List<LabelParserException> extProbs = externalProblems.get(problem
                .getSourceURI());
                if (extProbs == null) {
                    extProbs = new ArrayList<LabelParserException>();
                }
                extProbs.add(problem);
                externalProblems.put(problem.getSourceURI(), extProbs);
            }
        }
        writer.println("    Begin checking children");
        for (URI extUri : externalProblems.keySet()) {
            for (LabelParserException problem : externalProblems.get(extUri)) {
                printProblem(writer, problem);
            }
        }
        writer.println("    End checking children");
    }

    @Override
    protected void printRecordSkip(PrintWriter writer,
            final List<String> sourceUris, final Exception exception) {
        writer.println();
        writer.print("  ");
        writer.print("SKIP");
        writer.print(": ");
        writer.println(riCheckHeader);
        writer.print("    Parent File(s): ");
        // Just get the file name of the URI
        writer.println(sourceUris.toString());
        writer.print("      ");
        writer.println(exception.getMessage());
    }

    private void printProblem(PrintWriter writer,
          final LabelParserException problem) {
        writer.print("      ");
//        writer.print(problem.getType().getSeverity().getName());
//        writer.print("  ");
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
            writer.print(FilenameUtils.getName(problem.getSourceURI().toString()) + ": ");
            writer.println(MessageUtils.getProblemMessage(problem));
        } catch (RuntimeException re) {
            // For now the MessageUtils class seems to be throwing this exception
            // when it can't find a key. In these cases the actual message seems to
            // be the key itself.
            writer.println(problem.getKey());
        }
        if (problem instanceof DiffException) {
            DiffException diffProblem = (DiffException) problem;
            writer.print("      ");
            writer.print("Source: ");
            writer.println(diffProblem.getSourceFile().getName());
            writer.println(Utility.printDiff("      ", diffProblem.getDiffs()));
        }
    }

    @Override
    protected void printFooter(PrintWriter writer) {
        int totalFiles = getNumPassed() + getNumFailed() + getNumSkipped();
        int totalValidated = getNumPassed() + getNumFailed();
        writer.println();
        writer.println("Referential Integrity Summary:");
        writer.println();
        writer.println("  " + totalValidated + " of " + totalFiles + " referential integrity check(s) made, "
                + this.getNumSkipped() + " skipped");
        writer.println("  " + this.getNumPassed() + " of " + totalValidated
                + " passed");
        writer.println();
    }

    @Override
    protected void printRecordMessages(PrintWriter writer, Status status,
            URI sourceUri, List<LabelParserException> problems) {
        // TODO Auto-generated method stub
    }

}
