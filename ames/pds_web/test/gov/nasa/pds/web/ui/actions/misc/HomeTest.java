package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.pds.web.ui.actions.BaseTestAction;

import com.opensymphony.xwork2.Action;

public class HomeTest extends BaseTestAction {

	private Home home;

	@Override
	protected void clearAction() {
		this.home = null;

	}

	@SuppressWarnings("nls")
	public void testDefault() throws Exception {
		this.home = createAction(Home.class);
		final String results = this.home.execute();
		assertEquals(Action.SUCCESS, results);
		assertEquals("Home", this.home.getTitle());
	}

}