package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.pds.web.ui.actions.BaseTestAction;

import com.opensymphony.xwork2.Action;

public class PrivacyTest extends BaseTestAction {

	private Privacy privacy;

	@Override
	protected void clearAction() {
		this.privacy = null;

	}

	@SuppressWarnings("nls")
	public void testDefault() throws Exception {
		this.privacy = createAction(Privacy.class);
		final String results = this.privacy.execute();
		assertEquals(Action.SUCCESS, results);
		assertEquals("Privacy", this.privacy.getTitle());
	}
}