package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.DataSetProblem;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.ProblemCluster;
import gov.nasa.pds.web.ui.actions.dataManagement.ShowResults.ProblemGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.Action;

/*
 * This is a smoke test of expected problems for a deliberately bad test volume
 */
@SuppressWarnings("nls")
public class ShowResultsTest extends BaseTestAction {

	private ShowResults showResults;

	private String showResultsResult;

	private ValidateVolume validateVolume;

	private String validateVolumeResult;

	private static List<ProblemGroup> GROUPS;

	private static final List<DataSetProblem> PROBLEMS = new ArrayList<DataSetProblem>();

	// private static final List<DataSetProblem> CHECKED_PROBLEMS = new
	// ArrayList<DataSetProblem>();

	private static List<ProblemType> CHECKED_TYPES = new ArrayList<ProblemType>();

	// problem types not currently used or supported
	private static List<ProblemType> IGNORED_TYPES = new ArrayList<ProblemType>();

	static {
		// to be part of dictionary check
		IGNORED_TYPES.add(ProblemType.EXCESSIVE_IDENTIFIER_LENGTH);
		IGNORED_TYPES.add(ProblemType.EXCESSIVE_NAMESPACE_LENGTH);
		IGNORED_TYPES.add(ProblemType.INVALID_DEFINITION);
		// IGNORED_TYPES.add(ProblemType.INVALID_LABEL_WARNING);
		IGNORED_TYPES.add(ProblemType.COLUMN_NUMBER_MISMATCH);
		IGNORED_TYPES.add(ProblemType.COLUMN_LENGTH_MISMATCH);
		IGNORED_TYPES.add(ProblemType.UNKNOWN_FOLDER);
		IGNORED_TYPES.add(ProblemType.BAD_FORMAT);
		IGNORED_TYPES.add(ProblemType.INVALID_LABEL_FRAGMENT);

		// determined later in the game when no prob type necessary
		IGNORED_TYPES.add(ProblemType.NEW_VALUE);

		// not yet supported
		IGNORED_TYPES.add(ProblemType.DUPLICATE_IDENTIFIER);

		// not part of my tests
		IGNORED_TYPES.add(ProblemType.BAD_CATALOG_NAME);

		// ???
		IGNORED_TYPES.add(ProblemType.INVALID_DESCRIPTION);

		// currently orphaned
		IGNORED_TYPES.add(ProblemType.INVALID_LABEL_WARNING);
	}

	private File getDataSetVolumePath() {
		return new File(getTestDataDirectory(), "volumes");//$NON-NLS-1$ 
	}

	private void init() {
		if (GROUPS == null) {

			// CHECKED_PROBLEMS.clear();
			CHECKED_TYPES.clear();
			PROBLEMS.clear();

			initAction();
			GROUPS = this.showResults.getProblemGroups();
			for (final ProblemGroup group : GROUPS) {
				final List<ProblemCluster> clusters = group.getClusters();
				for (final ProblemCluster cluster : clusters) {
					PROBLEMS.addAll(cluster.getProblems());
				}
			}

		}
	}

	private void initAction() {
		try {
			this.validateVolume = createAction(ValidateVolume.class);
			this.validateVolume.blocking = true;
			this.validateVolume
					.overrideDataSetDirectory(getDataSetVolumePath());
			this.validateVolume.setVolumePath("bad"); //$NON-NLS-1$
			this.validateVolumeResult = this.validateVolume.execute();
			final String procId = this.validateVolume.getProcId();

			this.showResults = createAction(ShowResults.class);
			this.showResults.setProcId(procId);

			this.showResultsResult = this.showResults.execute();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void assertHasProblem(final ProblemType type,
			final Integer lineNumber, final String key, final String path,
			final String resource, final Object... arguments) {

		// make sure things were initialized, inefficient but should work ok
		init();

		if (!CHECKED_TYPES.contains(type)) {
			CHECKED_TYPES.add(type);
		}

		// check type
		List<DataSetProblem> possibleMatches = new ArrayList<DataSetProblem>();
		for (final DataSetProblem problem : PROBLEMS) {
			if (problem.getType().equals(type)) {
				possibleMatches.add(problem);
			}
		}
		if (possibleMatches.size() == 0) {
			fail("No problems of the type \"" + type.toString()
					+ "\" were found.");
		}

		// check keys
		final List<String> badKeys = new ArrayList<String>();
		final List<DataSetProblem> keyMatches = new ArrayList<DataSetProblem>();
		for (final DataSetProblem problem : possibleMatches) {
			if (problem.getKey().equals(key)) {
				keyMatches.add(problem);
			} else {
				badKeys.add(problem.getKey());
			}
		}
		if (keyMatches.size() == 0) {
			String keys = "";
			if (badKeys.size() > 0) {
				keys = " Found keys were "
						+ StrUtils.toSeparatedString(badKeys);
			}
			fail("No problems of the type \"" + type.toString()
					+ "\" with key " + key + " were found." + keys);
		}
		possibleMatches = keyMatches;

		// check path
		final List<String> badPaths = new ArrayList<String>();
		final List<DataSetProblem> pathMatches = new ArrayList<DataSetProblem>();
		for (final DataSetProblem problem : possibleMatches) {
			if (problem.getPath().equals(path)) {
				pathMatches.add(problem);
			} else {
				badPaths.add(problem.getPath());
			}
		}
		if (pathMatches.size() == 0) {
			String paths = "";
			if (badPaths.size() > 0) {
				paths = " Found paths were "
						+ StrUtils.toSeparatedString(badPaths);
			}
			fail("No possible matches of the type \"" + type.toString()
					+ "\" with path " + path + " were found." + paths);
		}
		possibleMatches = pathMatches;

		// check resource
		final List<String> badResources = new ArrayList<String>();
		final List<DataSetProblem> resourceMatches = new ArrayList<DataSetProblem>();
		for (final DataSetProblem problem : possibleMatches) {
			if (problem.getResource().equals(resource)) {
				resourceMatches.add(problem);
			} else {
				badResources.add(problem.getResource());
			}
		}
		if (resourceMatches.size() == 0) {
			String resources = "";
			if (badResources.size() > 0) {
				resources = " Found resources were "
						+ StrUtils.toSeparatedString(badResources);
			}
			fail("No possible matches of the type \"" + type.toString()
					+ "\" with resource " + resource + " were found."
					+ resources);
		}
		possibleMatches = resourceMatches;

		// check arguments
		DataSetProblem bestMatch = null;
		int numArgsMatch = 0;
		final List<DataSetProblem> argumentMatches = new ArrayList<DataSetProblem>();
		for (final DataSetProblem problem : possibleMatches) {
			boolean argsMatch = true;

			Object[] currArgs = problem.getArguments();

			// avoid index out of range issues, only test things that can match
			int argTestLength = Math.min(currArgs.length, arguments.length);
			for (int i = 0; i < argTestLength; i++) {
				if (!arguments[i].toString().equals(currArgs[i].toString())) {
					int num = i + 1;
					if (num > numArgsMatch) {
						numArgsMatch = num;
						bestMatch = problem;
					}
					argsMatch = false;
				}
			}
			// can't match if one prob has more args than other
			if (currArgs.length != arguments.length) {
				argsMatch = false;
				continue;
			}
			if (argsMatch) {
				argumentMatches.add(problem);
			}
		}
		if (argumentMatches.size() == 0) {
			String foundArgMessage = "";
			if (bestMatch != null) {
				foundArgMessage = " Found args in possible match were: "
						+ StrUtils.toSeparatedString(bestMatch.getArguments());
			} else if (possibleMatches.size() == 1) {
				foundArgMessage = " Found args in possible match were: "
						+ StrUtils.toSeparatedString(possibleMatches.get(0)
								.getArguments());
			}
			fail("No possible matches of the type \"" + type.toString()
					+ "\" with the arguments "
					+ StrUtils.toSeparatedString(arguments) + " were found."
					+ foundArgMessage);
		}
		possibleMatches = argumentMatches;

		// check line number
		final List<String> badLines = new ArrayList<String>();
		if (lineNumber != null) {
			final List<DataSetProblem> lineMatches = new ArrayList<DataSetProblem>();
			for (final DataSetProblem problem : possibleMatches) {
				if (lineNumber.equals(problem.getLineNumber())) {
					lineMatches.add(problem);
				} else {
					if (problem.getLineNumber() != null) {
						badLines.add(problem.getLineNumber().toString());
					}
				}
			}
			if (lineMatches.size() == 0) {
				String lineList = "";
				if (badLines.size() > 0) {
					lineList = " Found lines were "
							+ StrUtils.toSeparatedString(badLines);
				}
				fail("No possible matches of the type \"" + type.toString()
						+ "\" on line " + lineNumber + " were found."
						+ lineList);
			}
			possibleMatches = lineMatches;
		} else {
			final List<DataSetProblem> lineMatches = new ArrayList<DataSetProblem>();
			for (final DataSetProblem problem : possibleMatches) {
				if (problem.getLineNumber() == null) {
					lineMatches.add(problem);
				}
			}
			if (lineMatches.size() == 0) {
				fail("No possible matches of the type \"" + type.toString()
						+ "\" with no line were found.");
			}
			possibleMatches = lineMatches;
		}

		if (possibleMatches.size() != 1) {
			System.out
					.println("Too many possible matches of the type \""
							+ type.toString()
							+ "\" with the provided arguments to determine a match. More specificity is required.");
		}

		PROBLEMS.remove(possibleMatches.get(0));

		// since problem found, add to list of checked problems
		// CHECKED_PROBLEMS.add(possibleMatches.get(0));
	}

	public void testValidationChain() throws Exception {
		initAction();
		assertEquals(Action.SUCCESS, this.validateVolumeResult);
		assertEquals(Action.SUCCESS, this.showResultsResult);
	}

	// test invalid volume

	// test invalid process

	// TODO: make sure line 45 of data/BAD.lbl has a complaint since no longer
	// treated as extra token
	public void testExtraTokens() {
		assertHasProblem(ProblemType.BAD_VALUE, 36,
				"parser.error.tooManyTokens", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "va, l, ue", "IDENT");

		assertHasProblem(ProblemType.BAD_VALUE, 41,
				"parser.error.tooManyTokens", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "123123, extraToken", "IDENT");

		assertHasProblem(ProblemType.BAD_VALUE, 42,
				"parser.error.tooManyTokens", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "comment, without, start, *",
				"IDENT");

		assertHasProblem(ProblemType.BAD_VALUE, 43,
				"parser.error.tooManyTokens", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "3, ,, 4, ,, 5, }", "IDENT");
	}

	public void testBadValue() {
		assertHasProblem(ProblemType.BAD_VALUE, 45, "parser.error.badValue",
				"data" + File.separatorChar + "BAD.lbl", "BAD.lbl", "ident",
				"foo.html");
	}

	public void testCircularPointer() {
		assertHasProblem(ProblemType.CIRCULAR_POINTER_REF, 8,
				"parser.error.circularReference", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "data" + File.separatorChar
						+ "BAD.lbl", "data" + File.separatorChar + "BAD.lbl");
	}

	public void testColumnNumberMismatch() {
		assertHasProblem(ProblemType.COLUMN_NUMBER_MISMATCH, null,
				"validation.error.columnNumberMismatch", "index"
						+ File.separatorChar + "fakindex.TAB", "fakindex.TAB",
				"8", "9");
	}

	public void testExcessiveLineLength() {
		assertHasProblem(ProblemType.EXCESSIVE_LINE_LENGTH, 9,
				"parser.error.lineTooLong", "voldesc.cat", "voldesc.cat", 83);

		assertHasProblem(ProblemType.EXCESSIVE_LINE_LENGTH, 42,
				"parser.error.lineTooLong", "voldesc.cat", "voldesc.cat", 83);

		assertHasProblem(ProblemType.EXCESSIVE_LINE_LENGTH, 3,
				"parser.error.lineTooLong", "data" + File.separatorChar
						+ "attachedStream.lbl", "attachedStream.lbl", 79);

		assertHasProblem(ProblemType.EXCESSIVE_LINE_LENGTH, 3,
				"parser.error.lineTooLong", "data" + File.separatorChar
						+ "attachedBlankFllNoPointer.lbl",
				"attachedBlankFllNoPointer.lbl", 79);

		assertHasProblem(ProblemType.EXCESSIVE_LINE_LENGTH, 3,
				"parser.error.lineTooLong", "data" + File.separatorChar
						+ "attachedBlankFillWWhite.lbl",
				"attachedBlankFillWWhite.lbl", 79);

		assertHasProblem(ProblemType.EXCESSIVE_LINE_LENGTH, 3,
				"parser.error.lineTooLong", "data" + File.separatorChar
						+ "attachedBlankFill.lbl", "attachedBlankFill.lbl", 79);
	}

	public void testExcessiveValueLength() {
		assertHasProblem(
				ProblemType.EXCESSIVE_VALUE_LENGTH,
				11,
				"parser.error.tooLong",
				"catalog" + File.separatorChar + "dataset.cat",
				"dataset.cat",
				"a                                    a a                        ...",
				"111", "60", "DATA_SET_NAME", "CHARACTER");
		// [a a a ..., 111, 60, DATA_SET_NAME, CHARACTER]
	}

	public void testInvalidDate() {
		assertHasProblem(ProblemType.INVALID_DATE, 28,
				"parser.error.dateOutOfRange", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "2008-07-99");
	}

	public void testInvalidLabel() {
		assertHasProblem(ProblemType.INVALID_LABEL, null,
				"parser.error.missingVersion", "data" + File.separatorChar
						+ "INVALID_LABEL.lbl", "INVALID_LABEL.lbl",
				"INVALID_LABEL.lbl");
	}

	public void testInvalidMember() {
		assertHasProblem(ProblemType.INVALID_MEMBER, 13,
				"parser.error.invalidObject", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "SPECTRUM", "BIT_ELEMENT");
	}

	public void testInvalidValue() {
		assertHasProblem(ProblemType.INVALID_VALUE, 48,
				"parser.error.invalidValue", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "asdf", "VAR_DATA_TYPE", "asdf");
	}

	public void testInvalidValueForColumn() {
		assertHasProblem(ProblemType.INVALID_VALUE_FOR_COLUMN, null,
				"validation.error.badDate", "index" + File.separatorChar
						+ "fakindex.TAB", "fakindex.TAB",
				"PRODUCT_CREATION_TIME", "DATE", "2007-06-13Q10:00:00");

		assertHasProblem(ProblemType.INVALID_VALUE_FOR_COLUMN, null,
				"validation.error.badDate", "index" + File.separatorChar
						+ "fakindex.TAB", "fakindex.TAB", "START_TIME", "DATE",
				"1996-10-14K05:23:33.9");
	}

	public void testMissingID() {
		assertHasProblem(ProblemType.MISSING_ID, 35, "parser.error.missingId",
				"data" + File.separatorChar + "BAD.lbl", "BAD.lbl", "");
	}

	public void testMissingIndexResource() {
		assertHasProblem(ProblemType.MISSING_INDEX_RESOURCE, 1,
				"validation.error.missingLabel", "index" + File.separatorChar
						+ "fakindex.TAB", "fakindex.TAB", "MISSING.LBL");
	}

	public void testMissingMember() {
		assertHasProblem(ProblemType.MISSING_MEMBER, 13,
				"parser.error.missingRequiredObject", "data"
						+ File.separatorChar + "BAD.lbl", "BAD.lbl", "SPECTRUM",
				"COLUMN");
	}

	public void testMissingProperty() {
		assertHasProblem(ProblemType.MISSING_PROPERTY, 13,
				"parser.error.missingRequiredElement", "data"
						+ File.separatorChar + "BAD.lbl", "BAD.lbl", "SPECTRUM",
				"COLUMNS");
	}

	public void testMissingRequiredResource() {
		assertHasProblem(ProblemType.MISSING_REQUIRED_RESOURCE, null,
				"validation.error.missingRequiredChild", "browse", "browse",
				"BROWINFO.TXT", "BROWSE");
	}

	public void testMissingResource() {
		assertHasProblem(ProblemType.MISSING_RESOURCE, 45,
				"validation.error.missingTargetFile", "data"
						+ File.separatorChar + "BAD.lbl", "BAD.lbl", "data"
						+ File.separatorChar + "foo.html");

		assertHasProblem(ProblemType.MISSING_RESOURCE, 46,
				"validation.error.missingTargetFile", "data"
						+ File.separatorChar + "BAD.lbl", "BAD.lbl", "data"
						+ File.separatorChar + "foo.bar");
	}

	public void testMissingEndStatement() {
		assertHasProblem(ProblemType.PARSE_ERROR, 1,
				"parser.error.missingEndStatement", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl");

		assertHasProblem(ProblemType.PARSE_ERROR, 1,
				"parser.error.missingEndStatement", "data" + File.separatorChar
						+ "INVALID_LABEL.lbl", "INVALID_LABEL.lbl");
	}

	public void testGenericParseError() {
		assertHasProblem(ProblemType.PARSE_ERROR, 44,
				"parser.error.noViableAlternative", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "{");

		assertHasProblem(ProblemType.PARSE_ERROR, 44,
				"parser.error.noViableAlternative", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "{");

		assertHasProblem(ProblemType.PARSE_ERROR, 44,
				"parser.error.noViableAlternative", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "{");
	}

	public void testIllegalCharacters() {
		assertHasProblem(ProblemType.PARSE_ERROR, 42,
				"parser.error.illegalCharacter", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "\\xd");

		assertHasProblem(ProblemType.PARSE_ERROR, 54,
				"parser.error.illegalCharacter", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "%");
	}

	public void testShortValue() {
		assertHasProblem(ProblemType.SHORT_VALUE, 31, "parser.error.tooShort",
				"data" + File.separatorChar + "BAD.lbl", "BAD.lbl", "a", "1",
				"3", "UNCOMPRESSED_FILE_NAME", "CHARACTER");
	}

	public void testTypeMismatch() {
		assertHasProblem(ProblemType.TYPE_MISMATCH, 30,
				"parser.error.typeMismatch", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "A_AXIS_RADIUS", "REAL",
				"TextString", "should be real not text");
	}

	public void testUnknownKey() {
		assertHasProblem(ProblemType.UNKNOWN_KEY, 33,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 36,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 37,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 38,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 39,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 41,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 42,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 43,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 54,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 55,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "IDENT");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 11,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "VOYAGER:FILE_RECORDS");

		assertHasProblem(ProblemType.UNKNOWN_KEY, 26,
				"parser.error.definitionNotFound", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "badkey");
	}

	public void testEmptyFile() {
		assertHasProblem(ProblemType.EMPTY_FILE, null,
				"validation.error.emptyFile", "data" + File.separatorChar
						+ "badName.txt", "badName.txt", "badName.txt");

		assertHasProblem(ProblemType.EMPTY_FILE, null,
				"validation.error.emptyFile", "data" + File.separatorChar
						+ "INVALID_LABEL.lbl", "INVALID_LABEL.lbl",
				"INVALID_LABEL.lbl");

		assertHasProblem(ProblemType.EMPTY_FILE, null,
				"validation.error.emptyFile", "data" + File.separatorChar
						+ "unknownFile.txt", "unknownFile.txt",
				"unknownFile.txt");
	}

	public void testEmptyFolder() {
		assertHasProblem(ProblemType.EMPTY_FOLDER, null,
				"validation.error.emptyDirectory", "empty", "empty", "empty");
	}

	public void testLabelNotIndexed() {
		assertHasProblem(ProblemType.LABEL_NOT_INDEXED, null,
				"validation.error.unIndexedLabel", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "BAD.lbl");

		assertHasProblem(ProblemType.LABEL_NOT_INDEXED, null,
				"validation.error.unIndexedLabel", "data" + File.separatorChar
						+ "INVALID_LABEL.lbl", "INVALID_LABEL.lbl",
				"INVALID_LABEL.lbl");
	}

	public void testLabelNotToBeIndexed() {
		assertHasProblem(ProblemType.LABEL_NOT_TO_BE_INDEXED, null,
				"validation.error.illegalIndexedLabel", "catalog"
						+ File.separatorChar + "dataset.cat", "dataset.cat",
				"dataset.cat");
	}

	public void testMissingValue() {
		assertHasProblem(ProblemType.MISSING_VALUE, 25,
				"parser.error.missingValue", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "METEORITE_TYPE");
	}

	public void testPotentialPointerProblem() {
		assertHasProblem(ProblemType.POTENTIAL_POINTER_PROBLEM, 7,
				"validation.error.badPointerName", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "BAD.lbl", "badName.txt",
				"BAD.txt");
	}

	public void testUnknownFile() {
		assertHasProblem(ProblemType.UNKNOWN_FILE, null,
				"validation.error.unknownFile", "data" + File.separatorChar
						+ "unknownFile.txt", "unknownFile.txt",
				"unknownFile.txt");
	}

	public void testUnkownValue() {
		// TODO: Re-enable this test after adding test cases.
//		assertHasProblem(ProblemType.UNKNOWN_VALUE, 24,
//				"parser.warning.unknownValue", "data" + File.separatorChar
//						+ "BAD.lbl", "BAD.lbl", "non_existant_value",
//				"DATA_SET_ID", "non_existant_value");
//
//		assertHasProblem(ProblemType.UNKNOWN_VALUE, 10,
//				"parser.warning.unknownValue", "document" + File.separatorChar
//						+ "ads.lbl", "ads.lbl", "BIBLIOGRAPHIC INFORMATION",
//				"DOCUMENT_TOPIC_TYPE", "BIBLIOGRAPHIC INFORMATION");
//
//		assertHasProblem(ProblemType.UNKNOWN_VALUE, 8,
//				"parser.warning.unknownValue", "index" + File.separatorChar
//						+ "fakindex.LBL", "fakindex.LBL",
//				"HST-S/SA-WFPC2-4-ASTROM2005-V1.X", "DATA_SET_ID",
//				"HST-S/SA-WFPC2-4-ASTROM2005-V1.X");
	}

	public void testPlaceHolder() {
		assertHasProblem(ProblemType.PLACEHOLDER_VALUE, 49,
				"parser.info.placeholderValue", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "LABEL_REVISION_NOTE", "NULL");
	}

	public void testManipulatedValue() {
		assertHasProblem(ProblemType.MANIPULATED_VALUE, 50,
				"parser.warning.manipulatedValue", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "yes", "YES",
				"MISSING_PACKET_FLAG");
	}

	public void testUnknownUnits() {
		assertHasProblem(ProblemType.UNKNOWN_VALUE_TYPE, 51,
				"parser.warning.unknownUnits", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "EXPOSURE_DURATION", "seconds");
	}

	public void testInvalidValueType() {
		assertHasProblem(ProblemType.INVALID_TYPE, 52,
				"parser.error.badInteger", "data" + File.separatorChar
						+ "BAD.lbl", "BAD.lbl", "43.3");
	}

	public void testExceedsMaxValue() {
		assertHasProblem(ProblemType.OOR, 53, "parser.error.exceedsMax", "data"
				+ File.separatorChar + "BAD.lbl", "BAD.lbl", "91.0", "90.0",
				"DECLINATION", "REAL");
	}

	public void testMismatchedCase() {
		assertHasProblem(ProblemType.MISMATCHED_CASE, 2,
				"validation.error.mismatchedCase", "index" + File.separatorChar
						+ "index.tab", "index.tab", "catalog"
						+ File.separatorChar + "dataset.cat");
	}

	public void testPossibleStartByteMismatch() {
		assertHasProblem(ProblemType.START_BYTE_POSSIBLE_MISMATCH, 6,
				"parser.warning.startBytePossibleMismatch", "data"
						+ File.separatorChar + "attachedBlankFillWWhite.lbl",
				"attachedBlankFillWWhite.lbl", "573", "568");

		assertHasProblem(ProblemType.START_BYTE_POSSIBLE_MISMATCH, 6,
				"parser.warning.startBytePossibleMismatch", "data"
						+ File.separatorChar + "attachedPaddedWWhite.lbl",
				"attachedPaddedWWhite.lbl", "573", "568");
	}

	public void testStartByteMismatch() {
		assertHasProblem(ProblemType.ATTACHED_START_BYTE_MISMATCH, 6,
				"parser.error.startByteMismatch", "data" + File.separatorChar
						+ "attachedBlankFillInvalid.lbl",
				"attachedBlankFillInvalid.lbl", "568", "569");

		assertHasProblem(ProblemType.ATTACHED_START_BYTE_MISMATCH, 6,
				"parser.error.startByteMismatch", "data" + File.separatorChar
						+ "attachedPaddedInvalid.lbl",
				"attachedPaddedInvalid.lbl", "568", "569");
	}

	public void testWrongLineLength() {
		assertHasProblem(ProblemType.WRONG_LINE_LENGTH, 4,
				"parser.error.wrongLineLength", "data" + File.separatorChar
						+ "attachedPadded.lbl", "attachedPadded.lbl", 79, 80);

		assertHasProblem(ProblemType.WRONG_LINE_LENGTH, 5,
				"parser.error.wrongLineLength", "data" + File.separatorChar
						+ "attachedPadded.lbl", "attachedPadded.lbl", 79, 78);

		assertHasProblem(ProblemType.WRONG_LINE_LENGTH, 4,
				"parser.error.wrongLineLength", "data" + File.separatorChar
						+ "attachedPaddedWWhite.lbl",
				"attachedPaddedWWhite.lbl", 79, 80);

		assertHasProblem(ProblemType.WRONG_LINE_LENGTH, 5,
				"parser.error.wrongLineLength", "data" + File.separatorChar
						+ "attachedPaddedWWhite.lbl",
				"attachedPaddedWWhite.lbl", 79, 78);
	}

	public void testBadLineEndings() {
		assertHasProblem(ProblemType.ILLEGAL_LINE_ENDING, 1,
				"parser.error.badLineEnding", "data" + File.separatorChar
						+ "badLineEnding.lbl", "badLineEnding.lbl");

		assertHasProblem(ProblemType.ILLEGAL_LINE_ENDING, 2,
				"parser.error.badLineEnding", "data" + File.separatorChar
						+ "badLineEnding.lbl", "badLineEnding.lbl");
	}

	public void testFragHasVersion() {
		assertHasProblem(ProblemType.FRAGMENT_HAS_VERSION, null,
				"parser.warning.versionPresent", "data" + File.separatorChar
						+ "versionFrag.fmt", "versionFrag.fmt", "data"
						+ File.separatorChar + "versionFrag.fmt");
	}

	public void testFragHasSFDU() {
		assertHasProblem(ProblemType.FRAGMENT_HAS_SFDU, null,
				"parser.warning.sfduPresent", "data" + File.separatorChar
						+ "sfduFrag.fmt", "sfduFrag.fmt", "data"
						+ File.separatorChar + "sfduFrag.fmt");
	}

	public void testColumnDefOOR() {
		assertHasProblem(ProblemType.COLUMN_DEF_OOR, 120,
				"validation.error.columnOutOfRange", "index"
						+ File.separatorChar + "fakindex.LBL", "fakindex.LBL",
				"218", "216");
	}

	public void testAllProblemsChecked() {
		// TODO: Re-enable this test after adding test cases.
//		init();
//		/*
//		 * final List<DataSetProblem> unchecked = new
//		 * ArrayList<DataSetProblem>(); for (final DataSetProblem problem :
//		 * PROBLEMS) { if (!CHECKED_PROBLEMS.contains(problem)) {
//		 * unchecked.add(problem); } }
//		 */
//
//		assertTrue("Found " + PROBLEMS.size() + " problems not covered.",
//				PROBLEMS.size() == 0);

	}

	public void testAllTypesCovered() {
		// TODO: Re-enable this test after adding test cases.
//		init();
//		final List<String> missingTypes = new ArrayList<String>();
//		for (final ProblemType type : ProblemType.values()) {
//			if (!CHECKED_TYPES.contains(type) && !IGNORED_TYPES.contains(type)) {
//				missingTypes.add(type.toString());
//			}
//		}
//		assertTrue(missingTypes.size() + " types not covered in tests: "
//				+ StrUtils.toSeparatedString(missingTypes),
//				missingTypes.size() == 0);
	}

	// TODO: make sure every [parser,validation].[error,warning,info].* is
	// covered, get resources.properties and make list from keys

	// TODO: test group and cluster organization

	// TODO: test error overflow (too many bad line endings or similar)

	// TODO: test new value sorting

	// TODO: test node filter

	// TODO: test expanded tree

	// TODO: test expanded problem cluster

	@Override
	protected void clearAction() {
		this.showResults = null;
		this.validateVolume = null;
	}

}
