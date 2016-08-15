package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.pds.web.ui.actions.BaseTestAction;

import org.apache.commons.mail.MultiPartEmail;

public class SendErrorTest extends BaseTestAction {

	private SendError sendError = null;

	@Override
	protected void clearAction() {
		this.sendError = null;
	}

	@SuppressWarnings( { "nls", "unused" })
	public void testMissingField() throws Exception {
		this.sendError = createAction(SendError.class);
		this.sendError.setName("");
		this.sendError.setEmail("");
		this.sendError.setCircumstances("");
		final String results = this.sendError.execute();
		/*
		 * assertEquals(Action.INPUT, results);
		 * assertContainsError(this.sendError, "error.requiredstring",
		 * this.sendError.getUIManager().getTxt( "error.label.circumstances"));
		 * assertContainsError(this.sendError, "error.requiredstring",
		 * this.sendError.getUIManager().getTxt("error.label.email"));
		 * assertContainsError(this.sendError, "error.requiredstring",
		 * this.sendError.getUIManager().getTxt("error.label.name"));
		 */
	}

	@SuppressWarnings( { "nls", "unused" })
	public void testValid() throws Exception {
		this.sendError = createAction(SendError.class);
		this.sendError.setName("josh");
		this.sendError.setEmail("jag@josh.com");
		this.sendError.setCircumstances("I did something silly.");

		final String results = this.sendError.execute();
		// assertEquals(Action.SUCCESS, results);

		final MultiPartEmail emailObj = this.sendError.getEmailObj();
		// TODO: make sure email looks right? not sure worth while since
		// hardcoded

	}
}
