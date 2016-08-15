package gov.nasa.pds.web;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.web.ui.containers.LabelContainer;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.SimpleProblem;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

@SuppressWarnings({ "nls", "unused" })
public abstract class BaseTestCase extends TestCase {

	public static final File TEST_DIR = new File("test-data"); //$NON-NLS-1$

	public static void assertNegative(Number number) {
		assertTrue(number != null && number.doubleValue() < 0);
	}

	public static void assertPositive(Number number) {
		assertTrue(number != null && number.doubleValue() > 0);
	}

	public static void assertZero(Number number) {
		assertTrue(number != null && number.doubleValue() == 0);
	}

	public static LabelParserException assertHasProblem(
			final LabelContainer label, final ProblemType type) {
		if (label == null) {
			fail("Label is null");
			return null;
		}
		return assertHasProblem(label.getLabelObj(), type);
	}

	// TODO: determine how to test for problem if problem type overflows
	public static SimpleProblem assertHasProblem(
			final ValidationResults results, final ProblemType type) {
		return assertHasSimpleProblem(results.getPreviewProblems(), type);
	}

	public static LabelParserException assertHasProblem(final Label label,
			final ProblemType type) {
		if (label == null) {
			fail("Label is null");
			return null;
		}
		return assertHasParseProblem(label.getProblems(), type);
	}

	public static LabelParserException assertHasParseProblem(
			final List<LabelParserException> problems, final ProblemType type) {
		for (final LabelParserException problem : problems) {
			if (problem.getType().equals(type)) {
				return problem;
			}
		}
		fail("Label did not contain a problem of type \"" + type.toString()
				+ "\".");
		return null;
	}

	public static SimpleProblem assertHasSimpleProblem(
			final List<SimpleProblem> problems, final ProblemType type) {
		for (final SimpleProblem problem : problems) {
			if (problem.getType().equals(type)) {
				return problem;
			}
		}
		fail("Label did not contain a problem of type \"" + type.toString()
				+ "\".");
		return null;
	}

	public static void assertDoesntHaveProblem(final LabelContainer label,
			final ProblemType type) {
		assertDoesntHaveProblem(label.getLabelObj(), type);
	}

	public static void assertDoesntHaveProblem(Label label, ProblemType type) {
		List<LabelParserException> problems = label.getProblems();
		for (final LabelParserException problem : problems) {
			problem.getType().equals(type);
			fail("Label contains a problem of type \"" + type.toString()
					+ "\" when it should not.");
		}
	}

	public void assertProblemEquals(final LabelParserException problem,
			Integer lineNumber, Integer column, String key, ProblemType type,
			Object... arguments) {
		assertEquals("Lines do not match: ", lineNumber,
				problem.getLineNumber());
		assertEquals("Columns do not match: ", column, problem.getColumn());
		assertEquals("Keys do not match: ", key, problem.getKey());
		assertEquals("Problem types do not match: ", type, problem.getType());
		final Object[] probArgs = problem.getArguments();
		assertEquals(
				StrUtils.toSeparatedString(probArgs) + " vs "
						+ StrUtils.toSeparatedString(arguments),
				arguments.length, probArgs.length);
		for (int i = 0; i < arguments.length; i++) {
			assertEquals(arguments[i].toString(), probArgs[i].toString());
		}
	}

	public void assertProblemEquals(final SimpleProblem problem,
			Integer lineNumber, String key, ProblemType type,
			Object... arguments) {
		assertEquals("Lines do not match: ", lineNumber,
				problem.getLineNumber());
		assertEquals("Keys do not match: ", key, problem.getKey());
		assertEquals("Problem types do not match: ", type, problem.getType());
		final Object[] probArgs = problem.getArguments();
		assertEquals(
				StrUtils.toSeparatedString(probArgs) + " vs "
						+ StrUtils.toSeparatedString(arguments),
				arguments.length, probArgs.length);
		for (int i = 0; i < arguments.length; i++) {
			assertEquals(arguments[i].toString(), probArgs[i].toString());
		}
	}
}
