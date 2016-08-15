package gov.nasa.pds.web.ui.utils;

import gov.nasa.arc.pds.tools.util.LocaleUtils;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.web.ui.actions.BaseAction;
import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.DataSetProblem;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.ProblemCluster;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.ProblemGroup;
import gov.nasa.pds.web.ui.containers.Option;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.NewValue;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.SimpleProblem;
import gov.nasa.pds.web.ui.containers.tabularData.Column;

import java.io.File;
import java.util.Locale;

import org.junit.Test;

@SuppressWarnings("nls")
public class ComparatorsTest extends BaseTestAction {

	@Test
	public void testCASE_INSENSITIVE_COMPARATOR() {
		// test normal strings, just a pass through
		assertNegative(Comparators.CASE_INSENSITIVE_COMPARATOR
				.compare("A", "c"));
		assertZero(Comparators.CASE_INSENSITIVE_COMPARATOR.compare("a", "a"));
		assertPositive(Comparators.CASE_INSENSITIVE_COMPARATOR
				.compare("b", "a"));

		// test nulls
		assertPositive(Comparators.CASE_INSENSITIVE_COMPARATOR.compare(null,
				"b"));
		assertNegative(Comparators.CASE_INSENSITIVE_COMPARATOR.compare("a",
				null));
		assertZero(Comparators.CASE_INSENSITIVE_COMPARATOR.compare(null, null));

		// test numeric strings
		assertNegative(Comparators.CASE_INSENSITIVE_COMPARATOR
				.compare("1", "2"));
		assertPositive(Comparators.CASE_INSENSITIVE_COMPARATOR.compare("100",
				"60"));
		assertNegative(Comparators.CASE_INSENSITIVE_COMPARATOR.compare(
				"101dalmations", "60"));
	}

	@Test
	public void testNUMERIC_COMPARATOR() {
		assertNegative(Comparators.NUMERIC_COMPARATOR.compare(1, 3));
		assertZero(Comparators.NUMERIC_COMPARATOR.compare(1, 1));
		assertPositive(Comparators.NUMERIC_COMPARATOR.compare(2342, 3));

		// test nulls
		assertNegative(Comparators.NUMERIC_COMPARATOR.compare(3, null));
		assertZero(Comparators.NUMERIC_COMPARATOR.compare(null, null));
		assertPositive(Comparators.NUMERIC_COMPARATOR.compare(null, 12312));
	}

	@Test
	public void testOPTION_NAME_COMPARATOR() {
		final Option none = new Option(BaseAction.NONE_OPTION, "YYYY");
		final Option text = new Option("asdf", "asdf");
		final Option number = new Option(123, 123);

		assertNegative(Comparators.OPTION_NAME_COMPARATOR.compare(none, number));
		assertZero(Comparators.OPTION_NAME_COMPARATOR.compare(text, text));
		assertPositive(Comparators.OPTION_NAME_COMPARATOR.compare(text, number));
		assertPositive(Comparators.OPTION_NAME_COMPARATOR.compare(number, none));
	}

	@Test
	public void testOPTION_NAME_NUMERIC_COMPARATOR() {
		final Option one = new Option(1, 1);
		final Option hundred = new Option(100, 100);

		assertNegative(Comparators.OPTION_NAME_NUMERIC_COMPARATOR.compare(one,
				hundred));
		assertZero(Comparators.OPTION_NAME_NUMERIC_COMPARATOR.compare(one, one));
		assertPositive(Comparators.OPTION_NAME_NUMERIC_COMPARATOR.compare(
				hundred, one));
	}

	// problems are only sorted withing groups so type and severity same
	@Test
	public void testPROBLEM_COMPARATOR() {
		final File root = getTestDataDirectory();

		final File lowFile = new File(root, "AAREADME.TXT");
		final File highFile = new File(root, "DATASET.CAT");

		LocaleUtils localeUtils = new LocaleUtils(new Locale("en_us"), false);

		final SimpleProblem lowPathSimple = new SimpleProblem(lowFile, root,
				"foo.key", ProblemType.LABEL_NOT_INDEXED, 1);
		final DataSetProblem lowPath = new DataSetProblem(lowPathSimple,
				localeUtils);

		final SimpleProblem highPathL1Simple = new SimpleProblem(highFile,
				root, "foo.key", ProblemType.BAD_FORMAT, 1);
		final DataSetProblem highPathL1 = new DataSetProblem(highPathL1Simple,
				localeUtils);

		final SimpleProblem highPathL5Simple = new SimpleProblem(highFile,
				root, "foo.key", ProblemType.BAD_FORMAT, 90);
		final DataSetProblem highPathL5 = new DataSetProblem(highPathL5Simple,
				localeUtils);

		// compare by path
		assertNegative(Comparators.PROBLEM_COMPARATOR.compare(lowPath,
				highPathL1));
		assertZero(Comparators.PROBLEM_COMPARATOR.compare(highPathL1,
				highPathL1));
		assertPositive(Comparators.PROBLEM_COMPARATOR.compare(highPathL1,
				lowPath));

		// paths same, compare by line number
		assertNegative(Comparators.PROBLEM_COMPARATOR.compare(highPathL1,
				highPathL5));
		assertZero(Comparators.PROBLEM_COMPARATOR.compare(highPathL5,
				highPathL5));
		assertPositive(Comparators.PROBLEM_COMPARATOR.compare(highPathL5,
				highPathL1));
	}

	@Test
	public void testTABULAR_COLUMN_COMPARATOR() {
		Column col1 = new Column(1);
		Column col5 = new Column(5);

		assertNegative(Comparators.TABULAR_COLUMN_COMPARATOR
				.compare(col1, col5));
		assertZero(Comparators.TABULAR_COLUMN_COMPARATOR.compare(col5, col5));
		assertPositive(Comparators.TABULAR_COLUMN_COMPARATOR
				.compare(col5, col1));
	}

	// just a pass through to case insensitive compare, just do a head check
	@Test
	public void testTABULAR_COLUMN_DATATYPE_COMPARATOR() {
		final Column high = new Column(1);
		high.setDataType("z");
		final Column low = new Column(2);
		low.setDataType("a");
		final Column lowAlt = new Column(3);
		lowAlt.setDataType("a");

		assertNegative(Comparators.TABULAR_COLUMN_DATATYPE_COMPARATOR.compare(
				low, high));
		assertZero(Comparators.TABULAR_COLUMN_DATATYPE_COMPARATOR.compare(
				lowAlt, low));
		assertPositive(Comparators.TABULAR_COLUMN_DATATYPE_COMPARATOR.compare(
				high, low));
	}

	// just a pass through to case insensitive compare, just do a head check
	@Test
	public void testTABULAR_COLUMN_NAME_COMPARATOR() {
		final Column high = new Column(1);
		high.setName("z");
		final Column low = new Column(2);
		low.setName("a");
		final Column lowAlt = new Column(3);
		lowAlt.setName("a");

		assertNegative(Comparators.TABULAR_COLUMN_NAME_COMPARATOR.compare(low,
				high));
		assertZero(Comparators.TABULAR_COLUMN_NAME_COMPARATOR.compare(lowAlt,
				low));
		assertPositive(Comparators.TABULAR_COLUMN_NAME_COMPARATOR.compare(high,
				low));
	}

	@Test
	public void testPROBLEM_CLUSTER_COMPARATOR() {

		final File root = getTestDataDirectory();

		final File lowFile = new File(root, "AAREADME.TXT");
		final File highFile = new File(root, "DATASET.CAT");

		LocaleUtils localeUtils = new LocaleUtils(new Locale("en_us"), false);

		final SimpleProblem lowPathSimple = new SimpleProblem(lowFile, root,
				"foo.key", ProblemType.LABEL_NOT_INDEXED, 1);
		final DataSetProblem lowPath = new DataSetProblem(lowPathSimple,
				localeUtils);

		final SimpleProblem highPathL1Simple = new SimpleProblem(highFile,
				root, "foo.key", ProblemType.BAD_FORMAT, 1);
		final DataSetProblem highPathL1 = new DataSetProblem(highPathL1Simple,
				localeUtils);

		final SimpleProblem highPathL5Simple = new SimpleProblem(highFile,
				root, "foo.key", ProblemType.BAD_FORMAT, 90);
		final DataSetProblem highPathL5 = new DataSetProblem(highPathL5Simple,
				localeUtils);

		final ProblemCluster lowPathCluster = new ProblemCluster(
				"B description");
		lowPathCluster.addProblem(lowPath);

		final ProblemCluster highPathClusterL1 = new ProblemCluster(
				"Z description");
		highPathClusterL1.addProblem(highPathL1);

		final ProblemCluster highPathClusterL2 = new ProblemCluster(
				"1 description");
		highPathClusterL2.addProblem(highPathL5);

		final ProblemCluster lowDescClusterMulti = new ProblemCluster(
				"A description");
		lowDescClusterMulti.addProblem(lowPath);
		lowDescClusterMulti.addProblem(highPathL1);

		final ProblemCluster highDescClusterMulti = new ProblemCluster(
				"Z description");
		highDescClusterMulti.addProblem(lowPath);
		highDescClusterMulti.addProblem(highPathL1);

		// if both groups only have one, this is the same as prob comparator
		// compare by path
		assertNegative(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				lowPathCluster, highPathClusterL1));
		assertZero(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highPathClusterL1, highPathClusterL1));
		assertPositive(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highPathClusterL1, lowPathCluster));

		// paths same, compare by line number
		assertNegative(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highPathClusterL1, highPathClusterL2));
		assertZero(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highPathClusterL2, highPathClusterL2));
		assertPositive(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highPathClusterL2, highPathClusterL1));

		// if left has more than one prob and right has only one bubble group
		assertNegative(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highDescClusterMulti, highPathClusterL2));
		assertPositive(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highPathClusterL2, highDescClusterMulti));

		// if both have multiple, sort by description
		assertNegative(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				lowDescClusterMulti, highDescClusterMulti));
		assertZero(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highDescClusterMulti, highDescClusterMulti));
		assertPositive(Comparators.PROBLEM_CLUSTER_COMPARATOR.compare(
				highDescClusterMulti, lowDescClusterMulti));
	}

	@Test
	public void testPROBLEM_GROUP_COMPARATOR() {
		ProblemGroup warning = new ProblemGroup(ProblemType.LABEL_NOT_INDEXED);
		ProblemGroup errorHighDesc = new ProblemGroup(
				ProblemType.MISSING_MEMBER);
		ProblemGroup errorLowDesc = new ProblemGroup(ProblemType.BAD_FORMAT);

		// compare by severity
		assertNegative(Comparators.PROBLEM_GROUP_COMPARATOR.compare(
				errorLowDesc, warning));
		assertZero(Comparators.PROBLEM_GROUP_COMPARATOR.compare(errorLowDesc,
				errorLowDesc));
		assertPositive(Comparators.PROBLEM_GROUP_COMPARATOR.compare(warning,
				errorLowDesc));

		// compare by description when severity same
		assertNegative(Comparators.PROBLEM_GROUP_COMPARATOR.compare(
				errorLowDesc, errorHighDesc));
		assertPositive(Comparators.PROBLEM_GROUP_COMPARATOR.compare(
				errorHighDesc, errorLowDesc));
	}

	@Test
	public void testNEW_VALUE_COMPARATOR() {
		final File fileObj = new File("C:/dev/path/name.lbl");
		LabelParserException lowValueException = new LabelParserException(
				fileObj, 1, 19, "error.something", ProblemType.UNKNOWN_VALUE,
				"z new value truncated", "A_lowkey", "z new value normalized");
		File root = new File("C:/dev/");
		NewValue low = new NewValue(fileObj, lowValueException, root);

		LabelParserException highValueException = new LabelParserException(
				fileObj, 1, 19, "error.something", ProblemType.UNKNOWN_VALUE,
				"a new value truncated", "Z_highkey", "a new value normalized");
		NewValue high = new NewValue(fileObj, highValueException, root);

		assertNegative(Comparators.NEW_VALUE_COMPARATOR.compare(low, high));
		assertZero(Comparators.NEW_VALUE_COMPARATOR.compare(low, low));
		assertPositive(Comparators.NEW_VALUE_COMPARATOR.compare(high, low));
	}

	@Override
	protected void clearAction() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testConstructor() {
		new Comparators();
	}
}
