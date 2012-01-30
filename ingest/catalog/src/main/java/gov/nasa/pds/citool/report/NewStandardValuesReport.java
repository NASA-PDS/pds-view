package gov.nasa.pds.citool.report;

import gov.nasa.pds.citool.status.Status;
import gov.nasa.pds.tools.LabelParserException;

import java.io.PrintWriter;
import java.net.URI;
import java.util.List;

public class NewStandardValuesReport extends Report {
    private int numStandardValues;

    public NewStandardValuesReport() {
        numStandardValues = 0;
    }

    @Override
    protected void printHeader(PrintWriter writer) {
        // TODO Auto-generated method stub
        writer.println();
        writer.println("New Standard Values:");
        writer.println();
    }

    public void printStandardValue(String identifier, String value) {
        getOutput().print("    ");
        getOutput().println(identifier + " = " + value);
        ++numStandardValues;
    }

    @Override
    protected void printRecordMessages(PrintWriter writer, Status status,
            URI sourceUri, List<LabelParserException> problems) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void printFooter(PrintWriter writer) {
        writer.println();
        writer.println("New Standard Values Summary:");
        writer.println();
        writer.print("  ");
        writer.println(numStandardValues + " new standard value(s) found");
    }

    @Override
    protected void printRecordMessages(PrintWriter writer, Status status,
            List<String> sourceUris, List<LabelParserException> problems) {
        // TODO Auto-generated method stub

    }

}
