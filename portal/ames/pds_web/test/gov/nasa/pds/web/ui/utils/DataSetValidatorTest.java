package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.web.BaseTestCase;
import gov.nasa.pds.web.ui.actions.dataManagement.PostValidationBucket;
import gov.nasa.pds.web.ui.containers.LabelContainer;
import gov.nasa.pds.web.ui.containers.dataSet.Bucket;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.SimpleProblem;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import org.junit.Test;

// TODO: test complete error
// TODO: test every error situation
// TODO: integration into lib should pull some of these out
// TODO: for each negative case, have a positive to make sure not just always
// erroring out
@SuppressWarnings("nls")
public class DataSetValidatorTest extends BaseTestCase {

	private static final File LOCAL_TEST_DIR = new File(TEST_DIR,
			"featureSpecific");

	private static final DataSetValidator DSE = new DataSetValidator("1",
			LOCAL_TEST_DIR);

	static {
		try {
			DataSetValidator.initMasterDictionary(new File(new File("src"),
					"masterdd.full").toURI().toURL());
			DSE.initDictionary();
			DSE.setVolume(LOCAL_TEST_DIR);
			DSE.results.setRootNode(LOCAL_TEST_DIR);
			DSE.addObserver(new StorageObserver());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	static class StorageObserver implements Observer {

		@Override
		public void update(Observable validator, Object bucket) {
			// TODO Auto-generated method stub
			if (bucket instanceof Bucket) {
				try {
					PostValidationBucket.storeBucket("1", (Bucket) bucket);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// test that granular error found in array type value in tab file
	@Test
	public void testSimple() {
		final LabelContainer label = DSE.validateLabel(new File(LOCAL_TEST_DIR,
				"simpleValid.lbl"), true);

		assertEquals(0, label.getProblems().size());
		assertEquals(3, label.getLabelObj().getStatements().size());
	}

	// test that granular error found in array type value in tab file
	@Test
	public void testColumnsWithArray() {
		DSE.validateLabel(new File(LOCAL_TEST_DIR, "COL_ARRAY_TAB.LBL"), true);
		DSE.sendBucket();

		final SimpleProblem sp = assertHasProblem(DSE.getResults(),
				ProblemType.INVALID_VALUE_FOR_COLUMN);
		assertProblemEquals(sp, null, "validation.error.columnTypeMismatch",
				ProblemType.INVALID_VALUE_FOR_COLUMN, "Phi Values",
				"ASCII_REAL", " 3a9.06");
	}

	// quoted string is never closed, also causes missing END
	@Test
	public void testMissingEndQuote() throws Exception {
		final LabelContainer label = DSE.validateLabel(new File(LOCAL_TEST_DIR,
				"missingEndQuote.lbl"), true);
		assertNotNull(label);
		assertEquals(
				"parser.error.missingEndQuote", label.getProblems().get(0).getKey()); //$NON-NLS-1$
		assertEquals(2, label.getProblems().size());
	}

	// label has no END
	@Test
	public void testMissingEndStatement() throws Exception {
		final LabelContainer label = DSE.validateLabel(new File(LOCAL_TEST_DIR,
				"missingEndStatement.lbl"), true);
		assertNotNull(label);
		assertEquals(
				"parser.error.missingEndStatement", label.getProblems().get(0).getKey()); //$NON-NLS-1$
		assertEquals(1, label.getProblems().size());
	}

	// object is never closed
	@Test
	public void testMissingEndObject() throws Exception {
		final LabelContainer label = DSE.validateLabel(new File(LOCAL_TEST_DIR,
				"missingEndObject.lbl"), true);
		assertNotNull(label);
		assertEquals("parser.error.missingEndObject", label.getProblems()
				.get(0).getKey()); //$NON-NLS-1$
		assertEquals(1, label.getProblems().size());
	}

	// group is never closed
	@Test
	public void testMissingEndGroup() throws Exception {
		final LabelContainer label = DSE.validateLabel(new File(LOCAL_TEST_DIR,
				"missingEndGroup.lbl"), true);
		assertNotNull(label);
		assertEquals("parser.error.missingEndGroup", label.getProblems().get(0)
				.getKey()); //$NON-NLS-1$
		assertEquals(1, label.getProblems().size());
	}

	// missing close comment token
	@Test
	public void testMissingEndComment() throws Exception {
		/*
		 * final LabelContainer label = DSE.validateLabel(new
		 * File(LOCAL_TEST_DIR, "missingEndComment.lbl"), true);
		 * assertNotNull(label); assertEquals("parser.error.missingEndComment",
		 * label.getProblems().get( 0).getKey()); //$NON-NLS-1$ assertEquals(1,
		 * label.getProblems().size());
		 */
	}

	// not able to evaluate file as label - just missing version
	@Test
	public void testInvalidLabel() throws Exception {
		// stub
	}

	// key not in dictionary
	@Test
	public void testUnknownKey() throws Exception {
		// stub
	}

	// value not in dictionary but possibly to be added
	@Test
	public void testUnknownValue() throws Exception {
		// stub
	}

	// value not in dictionary and given that type is static, unlikely to be
	// added
	// TODO: lots of ways this can manifest, see checkers and have assert for
	// each
	@Test
	public void testInvalidValue() throws Exception {
		// stub
	}

	// value cannot be cast to expected type
	// TODO: have assert for each type mismatch
	@Test
	public void testInvalidType() throws Exception {
		// stub
	}

	// statement not allowed in object
	@Test
	public void testInvalidMember() throws Exception {
		// stub
	}

	// missing a required child object in object
	@Test
	public void testMissingMember() throws Exception {
		// stub
	}

	// missing a required child object in object
	@Test
	public void testMissingProperty() throws Exception {
		// stub
	}

	// pointer name does not match convention and may be in error
	@Test
	public void testPotentialPointerProblem() throws Exception {
		// stub
	}

	// line too long - dependent on a variety of factors but usually more than
	// 80 chars including line termination chars
	@Test
	public void testExcessiveLineLength() throws Exception {
		// stub
	}

	// lines appear to be equal length but this does not match
	@Test
	public void testWrongLineLength() throws Exception {
		// stub
	}

	// line does not end in CRLF
	// TODO: test all types of line endings
	@Test
	public void testIllegalLineEnding() throws Exception {
		// stub
	}

	// found a statement with no ID
	@Test
	public void testMissingID() throws Exception {
		// stub
	}

	// namespace too long
	@Test
	public void testExcessiveNamespaceLength() throws Exception {
		// stub
	}

	// id too long
	@Test
	public void testExcessiveIdentifierLength() throws Exception {
		// stub
	}

	// value missing in assignment statement
	@Test
	public void testMissingValue() throws Exception {
		// stub
	}

	// value too short for definition
	@Test
	public void testValueTooShort() throws Exception {
		// stub
	}

	// value somehow malformed (ex. too many tokens)
	@Test
	public void testBadValue() throws Exception {
		// stub
	}

	// value too long
	@Test
	public void testExcessiveValueLength() throws Exception {
		// stub
	}

	// less than min or greater than max
	// TODO: assert for both
	@Test
	public void testOutOfRange() throws Exception {
		// stub
	}

	// unclear what exception is for, does not appear to be used
	@Test
	public void testInvalidDescription() throws Exception {
		// stub
	}

	// does not match date format rules
	// TODO: very granular messages exist for this, do assert for each
	@Test
	public void testInvalidDate() throws Exception {
		// stub
	}

	// include pointers result in infinite include loop
	@Test
	public void testCircularPointer() throws Exception {
		// stub
	}

	// columns defined and columns found do not match
	// TODO: since now creating columns by byte index, is there a way to even
	// tell if there's a problem here? won't it always match? Perhaps all that
	// we can do is see if there are extra bytes at the end... see the spec
	@Test
	public void testColumnNumberMismatch() throws Exception {
		// stub
	}

	// TODO: same issue as above
	@Test
	public void testColumnLengthMismatch() throws Exception {
		// stub
	}

	// table value or item cannot be cast to defined type
	// TODO: add assert for each type
	@Test
	public void testInvalidValueForColumn() throws Exception {
		// stub
	}

	// pointed to file does not exist
	@Test
	public void testMissingResource() throws Exception {
		// stub
	}

	// file defined in index table does not exist
	@Test
	public void testMissingIndexResource() throws Exception {
		// stub
	}

	// required file not found
	@Test
	public void testMissingRequiredResource() throws Exception {
		// stub
	}

	// file that should be labeled or special file or otherwise known was not
	@Test
	public void testUnknownFile() throws Exception {
		// stub
	}

	// label that should be indexed was not
	@Test
	public void testLabelNotIndexed() throws Exception {
		// stub
	}

	// unexpected root folder
	// TODO: really an error according to spec? If not, kill the error type
	@Test
	public void testUnknownFolder() throws Exception {
		// stub
	}

	// file length is zero
	@Test
	public void testEmptyFile() throws Exception {
		// stub
	}

	// folder contains no files
	@Test
	public void testEmptyFolder() throws Exception {
		// stub
	}

	// file found but real path and pointer or index path do not agree
	@Test
	public void testMismatchedCase() throws Exception {
		// stub
	}

	// file format does not appear to match defined format (ex. fits file unable
	// to be interpreted as fits file...)
	@Test
	public void testBadFormat() throws Exception {
		// stub
	}

	// no indexes were found
	@Test
	public void testNoIndexes() throws Exception {
		// stub
	}

	// version line found but not as first line (after optional old pds id)
	@Test
	public void testMisallocatedVersion() throws Exception {
		// stub
	}

	// unknown value found but in cat file so should be ingested and not
	// complain about in labels
	@Test
	public void testNewValue() throws Exception {
		// stub
	}

	@Test
	public void testBadColumnLength() throws Exception {
		final LabelContainer label = DSE.validateLabel(new File(LOCAL_TEST_DIR,
				"columnLengthMismatch.lbl"), true);
		assertNotNull(label);
		assertEquals("validation.error.columnLengthMismatch", label
				.getProblems().get(0).getKey());
		assertEquals(1, label.getProblems().size());
	}

	@Test
	public void testBadColumnStartByte() throws Exception {
		final LabelContainer label = DSE.validateLabel(new File(LOCAL_TEST_DIR,
				"columnStartOOR.lbl"), true);
		assertNotNull(label);
		assertEquals("validation.error.columnOutOfRange", label.getProblems()
				.get(0).getKey());
		assertEquals(1, label.getProblems().size());
	}

	/*
	 * Tests That May Or May Not Belong Here: has attached content (goes with
	 * something being labelled) start byte stuff do all checker type stuff...
	 * but may want to put into separate classes associated with checker... test
	 * local dd supressing issues, issues in local dd surfacing, etc
	 */

}
