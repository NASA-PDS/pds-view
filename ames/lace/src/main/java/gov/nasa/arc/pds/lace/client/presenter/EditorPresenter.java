package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppInjector;
import gov.nasa.arc.pds.lace.client.event.EditorContentChangingEvent;
import gov.nasa.arc.pds.lace.client.event.FieldValueChangedEvent;
import gov.nasa.arc.pds.lace.client.event.TreeItemSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.TreeItemSelectionEventHandler;
import gov.nasa.arc.pds.lace.client.event.ValidationEvent;
import gov.nasa.arc.pds.lace.client.resources.IconLookup;
import gov.nasa.arc.pds.lace.client.util.ContainerNameHelper;
import gov.nasa.arc.pds.lace.client.view.EditorView;
import gov.nasa.arc.pds.lace.shared.Container;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays the
 * editor (edit view) which consists of a title and a content.
 */
public class EditorPresenter extends Presenter<EditorPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(EditorView.class)
	public interface Display extends Presenter.Display<EditorPresenter> {

		/**
		 * Sets the editor's title.
		 *
		 * @param title the name of the container being loaded in the editor.
		 */
		void setTitle(String title);

		/**
		 * Sets an icon representing the container type.
		 *
		 * @param className the CSS class name that represents an icon for this container type
		 */
		void setIcon(String className);

		/**
		 * Sets the error messages associated with the top-level container for this
		 * editor view.
		 *
		 * @param messages an array of error messages
		 */
		void setErrors(String[] messages);

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
	private IconLookup lookup;
	private Container container;

	/**
	 * Creates an instance of the editor presenter.
	 *
	 * @param view the display interface for the editor view
	 * @param lookup an icon lookup service to display the right icon for the container
	 * @param injector
	 */
	@Inject
	public EditorPresenter(
			Display view,
			IconLookup lookup,
			AppInjector injector
	) {
		super(view);
		view.setPresenter(this);
		this.injector = injector;
		this.lookup = lookup;
	}

	@Override
	protected void addEventHandlers() {
		addEventHandler(TreeItemSelectionEvent.TYPE, new TreeItemSelectionEventHandler() {

			@Override
			public void onEvent(Container container) {
				// Alert any existing presenters that the editor content is about to change.
				fireEvent(new EditorContentChangingEvent());
				load(container);
			}

		});

		addEventHandler(ValidationEvent.TYPE, new ValidationEvent.Handler() {
			@Override
			public void onEvent(Void data) {
				updateErrors();
			}
		});

		addEventHandler(FieldValueChangedEvent.TYPE, new FieldValueChangedEvent.Handler() {
			@Override
			public void onEvent(FieldValueChangedEvent.EventDetails details) {
				if (details.getContainerPresenter().equals(containerPresenter)) {
					getView().setTitle(ContainerNameHelper.getContainerElementName(details.getContainer()));
				}
			}
		});
	}

	/**
	 * Loads the specified container in the editor.
	 *
	 * @param theContainer the container to load in the editor
	 */
	private void load(Container theContainer) {
		this.container = theContainer;

		containerPresenter = injector.getContainerPresenter();
		containerPresenter.display(theContainer, null, true);

		Display view = getView();
		view.setTitle(ContainerNameHelper.getContainerElementName(theContainer));
		view.setIcon(lookup.getIconClassName(container.getType().getElementName()));
		view.show(containerPresenter.asWidget());
		updateErrors();
	}

	private void updateErrors() {
		if (container != null) {
			getView().setErrors(container.getErrorMessages());
		}
	}
}
