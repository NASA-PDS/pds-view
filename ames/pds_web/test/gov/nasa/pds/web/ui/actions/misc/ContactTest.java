package gov.nasa.pds.web.ui.actions.misc;

import com.opensymphony.xwork2.Action;

import gov.nasa.pds.web.ui.actions.BaseTestAction;

public class ContactTest extends BaseTestAction {

	private Contact contact;

	@Override
	protected void clearAction() {
		this.contact = null;

	}

	@SuppressWarnings("nls")
	public void testDefault() throws Exception {
		this.contact = createAction(Contact.class);
		final String results = this.contact.execute();
		assertEquals(Action.SUCCESS, results);
		assertEquals("Contact", this.contact.getTitle());
	}
}
