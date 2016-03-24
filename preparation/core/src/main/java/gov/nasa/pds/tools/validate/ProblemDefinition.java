package gov.nasa.pds.tools.validate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * Defines a specific problem uncovered by a validation rule.
 */
public class ProblemDefinition {

    private static final AtomicInteger keyGenerator = new AtomicInteger();
    private static final Map<Integer, ProblemDefinition> PROBLEMS = new ConcurrentHashMap<Integer, ProblemDefinition>();

	/**
	 * Defines the severities of validation rule violations. These
	 * are defined in order of increasing severity, so that their
	 * {@link Enum#ordinal()} values are increasing integers.
	 */
	public static enum Severity {
		NONE, INFO, WARNING, ERROR
	}

	private final int id;
	private final Severity severity;
	private final ProblemType type;
	private final String message;
	private final String standardsDocument;
	private final String standardsSection;
	private int knownHashCode;

	public ProblemDefinition(
			Severity severity,
			ProblemType type,
			String message,
			String standardsDocument,
			String standardsSection
	) {
	    this.id = keyGenerator.incrementAndGet();
		this.severity = severity;
		this.type = type;
		this.message = message;
		this.standardsDocument = standardsDocument;
		this.standardsSection = standardsSection;

		PROBLEMS.put(this.id, this);
	}

	public int getID() {
	    return id;
	}

	public Severity getSeverity() {
		return severity;
	}

	public ProblemType getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public String getStandardsDocument() {
		return standardsDocument;
	}

	public String getStandardsSection() {
		return standardsSection;
	}

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProblemDefinition)) {
            return false;
        }

        ProblemDefinition other = (ProblemDefinition) obj;
        return message.equals(other.message)
                && severity==other.severity
                && type==other.type
                && standardsDocument.equals(other.standardsDocument)
                && standardsSection.equals(other.standardsSection);
    }

    @Override
    public int hashCode() {
        if (knownHashCode == 0) {
            String combined = message + severity.toString() + standardsDocument + standardsSection + type.toString();
            knownHashCode = combined.hashCode();
        }

        return knownHashCode;
    }

    public static ProblemDefinition findByID(int id) {
        return PROBLEMS.get(id);
    }

}
