package gov.nasa.pds.web.ui.actions;

import gov.nasa.pds.web.ui.actions.misc.Home;

import java.util.List;

@SuppressWarnings("nls")
public class BaseActionTest extends BaseTestAction {

	private BaseAction action;

	private void init() throws Exception {
		this.action = createAction(Home.class);
	}

	@Override
	protected void clearAction() {
		// TODO Auto-generated method stub

	}

	public void testAddError() throws Exception {
		init();
		// clear errors in case somehow hanging around from another test
		this.action.clearErrorsAndMessages();
		// make sure starting clean
		assertFalse(this.action.hasErrors());
		// add error
		this.action.addError("error.requiredstring", "asdf");
		// make sure error was added
		assertTrue(this.action.hasErrors());
		// retrieve errors from action
		final List<String> errors = this.action.getErrorMessages();
		// confirm messages now cleared
		assertFalse(this.action.hasErrors());
		// make sure only one error
		assertEquals(1, errors.size());
		// make sure message is retrieving correctly
		assertEquals("0001: \"asdf\" is a required field.", errors.get(0));
	}

	// TODO: test in dev mode and not in dev mode
	public void testAddErrorBad() throws Exception {
		init();
		try {
			this.action.addError("asdf");
			fail("There should have been an error about not finding the key");
		} catch (final Exception e) {
			// noop
		}
	}

	public void testAddNotice() throws Exception {
		init();
		// clear errors in case somehow hanging around from another test
		this.action.clearErrorsAndMessages();
		// make sure starting clean
		assertFalse(this.action.hasNotices());
		// add error
		this.action.addNotice("submitError.notice.messageSent");
		// make sure error was added
		assertTrue(this.action.hasNotices());
		// retrieve errors from action
		final List<String> notices = this.action.getNoticeMessages();
		// confirm messages now cleared
		assertFalse(this.action.hasNotices());
		// make sure only one error
		assertEquals(1, notices.size());
		// make sure message is retrieving correctly
		assertEquals(
				"An email containing the details of your issue has been sent. These submissions are handled individually by our development staff and may take some time to be reviewed. However, if you have not heard from us within a few days, you may want to contact us directly.",
				notices.get(0));
	}

	public void testGetErrorMessagesSimple() throws Exception {
		init();
		// clear errors in case somehow hanging around from another test
		this.action.clearErrorsAndMessages();
		// make sure starting clean
		assertFalse(this.action.hasErrors());
		// add error
		this.action.addError("error.requiredstring", "asdf");
		// make sure error was added
		assertTrue(this.action.hasErrors());
		// retrieve errors from action
		final List<String> errors = this.action.getErrorMessagesSimple();
		// confirm messages are still there
		assertTrue(this.action.hasErrors());
		// make sure only one error
		assertEquals(1, errors.size());
	}

	public void testSetException() throws Exception {
		init();
		Exception e = this.action.getException();
		this.action.setException(new RuntimeException("foo"));
		e = this.action.getException();
		assertNotNull(e);
	}

	public void testGetExceptionMessage() throws Exception {
		init();
		this.action.setException(new RuntimeException("foo"));
		assertEquals("foo", this.action.getExceptionMessage());
	}

	public void testGetExceptionStackString() throws Exception {
		init();
		final String exceptionStart = "foo\ngov.nasa.pds.web.ui.actions.BaseActionTest.testGetExceptionStackString(BaseActionTest.java";
		this.action.setException(new RuntimeException("foo"));
		final String result = this.action.getExceptionStackString();
		// assertEquals(exceptionStart, result);
		assertTrue(result, result.startsWith(exceptionStart));
	}

	public void testSessionValueManipulation() throws Exception {
		init();
		// test roundtrip of a simple value
		final String classname = Home.class.toString();
		this.action.setSessionValue(classname, "context", "key", "value");
		final String value = (String) this.action.getSessionValue(classname,
				"context", "key");
		assertEquals("value", value);

		// test asking for missing value
		assertNull(this.action.getSessionValue(classname, "context", "badKey"));

		// test blank value removing entry
		this.action.setSessionValue(classname, "context", "key", "");
		assertNull(this.action.getSessionValue(classname, "context", "key"));
	}
}
