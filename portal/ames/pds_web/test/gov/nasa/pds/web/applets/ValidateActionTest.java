package gov.nasa.pds.web.applets;

import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.containers.dataSet.Bucket;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.SimpleProblem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ValidateActionTest extends BaseTestAction {
	@SuppressWarnings("nls")
	public void testScratch() {
		final List<Bucket> buckets = new ArrayList<Bucket>();

		Bucket currentBucket = new Bucket();
		File file = getTestDataDirectory();
		SimpleProblem problem1 = new SimpleProblem(file, file, "key1",
				ProblemType.BAD_VALUE, 1, new Object[0]);
		currentBucket.addProblem(problem1);
		buckets.add(currentBucket);

		currentBucket = new Bucket();

		assertEquals("key1", buckets.get(0).getProblems().get(0).getKey());
	}

	@Override
	protected void clearAction() {
		// TODO Auto-generated method stub

	}
}
