//	Copyright 2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//
package gov.nasa.pds.report.manager.rules;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * 
 * @author jpadams
 * @version $Revision$
 * 
 */
@Ignore
public class PDSTest {

	/**
	 * JUnit Test Rules to print headers for each test
	 */
	@Rule
	public MethodRule watchman = new TestWatchman() {
		public void starting(FrameworkMethod method) {
			System.err.println("\n\n------------ Running test: "
					+ method.getName() + " ------------");
		}
	};

	/**
	 * Custom rule to allow for performing one unit test at a time. Helpful when
	 * a test is failing.
	 * 
	 * @author jpadams
	 * 
	 */
	public class SingleTestRule implements MethodRule {
		private String applyMethod;

		public SingleTestRule(String applyMethod) {
			this.applyMethod = applyMethod;
		}

		@Override
		public Statement apply(final Statement statement,
				final FrameworkMethod method, final Object target) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					if (applyMethod.equals(method.getName())) {
						statement.evaluate();
					} else if (applyMethod.equals("")) {
						statement.evaluate();
					}
				}
			};
		}
	}
}
