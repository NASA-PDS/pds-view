package gov.nasa.pds.citool.diff;

import java.net.URI;
import java.util.List;

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;

public class DiffException extends LabelParserException {
    private static final long serialVersionUID = 4633039751184604784L;
    private List<DiffRecord> diffs;
    private Integer sourceLineNumber;
    private Integer targetLineNumber;

    public DiffException(final URI sourceUri,
            final Integer sourceLineNumber, final Integer targetLineNumber,
            final String key, final List<DiffRecord> diffs,
            final Object... arguments) {
        super(sourceUri, targetLineNumber, null, key,
                ProblemType.TYPE_MISMATCH, arguments);
        this.diffs = diffs;
        this.sourceLineNumber = sourceLineNumber;
        this.targetLineNumber = targetLineNumber;
    }

    public List<DiffRecord> getDiffs() {
        return diffs;
    }

    public Integer getSourceLineNumber() {
        return sourceLineNumber;
    }

    public Integer getTargetLineNumber() {
        return targetLineNumber;
    }
}
