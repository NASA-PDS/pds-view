package gov.nasa.pds.tools.label;

/**
 * Implements an object that stores a line and column location
 * in a source document.
 */
public final class SourceLocation {

	private int lineNumber;
	private int columnNumber;

	/**
	 * Creates a new instance with a given line and column number.
	 *
	 * @param line the source line number, or -1 if not available
	 * @param column the source column number, or -1 if not available
	 */
	public SourceLocation(int line, int column) {
		lineNumber = line;
		columnNumber = column;
	}

	/**
	 * Gets the source line number.
	 *
	 * @return the source line number, or -1 if not available
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Gets the source column number.
	 *
	 * @return the source column number, or -1 if not available.
	 */
	public int getColumnNumber() {
		return columnNumber;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null || !(obj instanceof SourceLocation)) {
			return false;
		}

		SourceLocation other = (SourceLocation) obj;
		return lineNumber==other.lineNumber && columnNumber==other.columnNumber;
	}

	@Override
	public int hashCode() {
		return lineNumber*17 + columnNumber;
	}

}
