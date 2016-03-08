package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppInjector;
import gov.nasa.arc.pds.lace.client.event.TreeItemSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.TreeItemSelectionEventHandler;
import gov.nasa.arc.pds.lace.shared.Container;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implements the presenter for a widget that displays the
 * editor (edit view) which consists of a title and a content.
 */
public class EditorPresenter extends Presenter<EditorPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	public interface Display extends Presenter.Display<EditorPresenter> {

		/**
		 * Sets the editor's title.
		 *
		 * @param title the name of the container being loaded in the editor.
		 */
		void setTitle(String title);

		/**
		 * Clears the editor's content.
		 */
		void clearContent();
		
		/**
		 * Shows the specified widget in the editor.
		 * 
		 * @param widget the container widget 
		 */
		void show(Widget widget);
	}
	
	private AppInjector injector;
	private ContainerPresenter containerPresenter;

	/**
	 * Creates an instance of the editor presenter.
	 *
	 * @param view the display interface for the editor view
	 * @param bus the event bus to use for firing and receiving events
	 * @param injector
	 */
	@Inject
	public EditorPresenter(
			Display view,
			EventBus bus,
			AppInjector injector			
	) {
		super(view);
		view.setPresenter(this);
		this.injector = injector;

		bus.addHandler(TreeItemSelectionEvent.TYPE, new TreeItemSelectionEventHandler() {
			
			@Override
			public void onEvent(Container container) {
				load(container);
			}
			
		});
	}
	
	/**
	 * Loads the specified container in the editor.
	 *
	 * @param container the container to load in the editor
	 */
	private void load(Container container) {
		if (containerPresenter != null) {
			containerPresenter.removeHandlers();										
		}
		containerPresenter = injector.getContainerPresenter();
		containerPresenter.display(container, true);
		
		Display view = getView();
		view.setTitle(container.getType().getElementName());
		view.show(containerPresenter.asWidget());
	}
}
