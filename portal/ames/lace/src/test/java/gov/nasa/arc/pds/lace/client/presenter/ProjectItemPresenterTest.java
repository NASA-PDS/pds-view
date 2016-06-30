package gov.nasa.arc.pds.lace.client.presenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem;

import java.util.Date;

import javax.inject.Inject;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.AbstractModule;

@Guice(modules={ProjectItemPresenterTest.Module.class})
public class ProjectItemPresenterTest {

	@Inject
	private ProjectItemPresenter presenter;

	@Inject
	private ProjectItemPresenter.Display view;

	@Test
	public void testInitialization() {
		verify(view).setPresenter(presenter);
	}

	@Test
	public void testSetLocation() {
		presenter.setItemLocation("theLocation");
		assertEquals(presenter.getItemLocation(), "theLocation");
	}
	@Test
	public void testSetName() {
		presenter.setItemName("theName");
		verify(view).setItemName("theName");
	}

	@Test
	public void testSetType() {
		presenter.setItemType(ProjectItem.Type.LABEL);
		verify(view).setItemType(ProjectItem.Type.LABEL);
	}

	@Test
	public void testSetLastUpdated() {
		Date now = new Date();
		presenter.setItemLastUpdated(now);
		verify(view).setLastUpdated(now);
	}

	public static class Module extends AbstractModule {

		private ProjectItemPresenter.Display view;

		@Override
		protected void configure() {
			view = mock(ProjectItemPresenter.Display.class);

			bind(ProjectItemPresenter.Display.class).toInstance(view);
			bind(EventBus.class).toInstance(new SimpleEventBus());
		}

	}

}
