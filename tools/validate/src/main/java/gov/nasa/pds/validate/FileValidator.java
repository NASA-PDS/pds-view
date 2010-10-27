package gov.nasa.pds.validate;

import gov.nasa.pds.tools.label.ExceptionContainer;
import gov.nasa.pds.tools.label.LabelValidator;
import gov.nasa.pds.validate.report.Report;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

public class FileValidator extends Validator{
    public FileValidator(Report report) {
        super(report);
    }

    public void validate(File file)
    throws SAXException, IOException {
        LabelValidator lv = new LabelValidator();
        ExceptionContainer exceptionContainer = new ExceptionContainer();
        if(schema != null) {
            lv.validate(exceptionContainer, file, schema);
        } else {
            lv.validate(exceptionContainer, file);
        }
        report.record(file.toURI(), exceptionContainer.getExceptions());
    }
}
