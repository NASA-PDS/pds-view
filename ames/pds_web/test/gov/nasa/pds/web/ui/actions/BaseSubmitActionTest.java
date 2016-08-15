package gov.nasa.pds.web.ui.actions;

import gov.nasa.pds.web.ui.actions.dataManagement.PostFilePreview;
import gov.nasa.pds.web.ui.actions.misc.SendError;

import com.opensymphony.xwork2.Action;

@SuppressWarnings("nls")
public class BaseSubmitActionTest extends BaseTestAction {

	private SendError action;

	@Override
	protected void clearAction() {
		// TODO Auto-generated method stub

	}

	private void init() throws Exception {
		this.action = createAction(SendError.class);
	}

	public void testReferrer() throws Exception {
		init();
		this.action.setReferrer("foo");
		assertEquals("foo", this.action.getReferrer());
	}

	public void testSetSubmit() throws Exception {
		init();
		this.action.setSubmit("asdf");
		assertEquals("submit", this.action.actionString);
	}

	public void testSetCancel() throws Exception {
		init();
		this.action.setCancel("asdf");
		assertEquals("cancel", this.action.actionString);
	}

	public void testSetUpdate() throws Exception {
		init();
		this.action.setUpdate("asdf");
		assertEquals("update", this.action.actionString);
	}

	public void testSetDelete() throws Exception {
		init();
		this.action.setDelete("asdf");
		assertEquals("delete", this.action.actionString);
	}

	public void testSetEdit() throws Exception {
		init();
		this.action.setEdit("asdf");
		assertEquals("edit", this.action.actionString);
	}

	public void testSetSave() throws Exception {
		init();
		this.action.setSave("asdf");
		assertEquals("save", this.action.actionString);
	}

	public void testSetNext() throws Exception {
		init();
		this.action.setNext("asdf");
		assertEquals("next", this.action.actionString);
	}

	public void testSetBack() throws Exception {
		init();
		this.action.setBack("asdf");
		assertEquals("back", this.action.actionString);
	}

	public void testSetLast() throws Exception {
		init();
		this.action.setLast("asdf");
		assertEquals("last", this.action.actionString);
	}

	public void testExecuteNormal() throws Exception {
		init();
		this.action.setName("name");
		this.action.setCircumstances("circumstances");
		this.action.setStack("stack");
		this.action.setEmail("email");
		this.action.execute();
		// assertEquals(Action.SUCCESS, result);
	}

	public void testExecuteError() throws Exception {
		final PostFilePreview postFilePreview = createAction(PostFilePreview.class);
		final String result = postFilePreview.execute();
		assertEquals(Action.ERROR, result);
	}

	public void testExecuteInput() throws Exception {
		init();
		final String result = this.action.execute();
		assertEquals(Action.INPUT, result);
	}

}
