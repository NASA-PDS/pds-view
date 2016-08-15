package gov.nasa.arc.pds.lace.client.presenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Defines an interface that all presenters must implement.
 *
 * @param <D> the type for the display
 */
public class Presenter<D extends Presenter.Display<?>> implements IsWidget, HasVisibility {

	/**
	 * Defines an interface that all views must implement.
	 *
	 * @param <T> the presenter type
	 */
	public interface Display<T> extends IsWidget, HasVisibility {
				
		/**
		 * Sets the presenter instance for this view.
		 *
		 * @param presenter the presenter
		 */
		void setPresenter(T presenter);
	}
	
	private D view;
	private EventBus bus;
	private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();
	
	/**
	 * Creates a new instance of the presenter.
	 *
	 * @param view the view to use for user interaction and display
	 */
	public Presenter(D view) {
		this.view = view;
		
		Widget w = view.asWidget();
		if (w != null) {
			w.addAttachHandler(new AttachEvent.Handler() {
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					if (event.isAttached()) {
						addEventHandlers();
					} else {
						removeEventHandlers();
					}
				}
			});
		}
	}
	
	/**
	 * Sets the event bus to use for handler registration.
	 * 
	 * @param b the event bus
	 */
	@Inject
	void setEventBus(EventBus b) {
		this.bus = b;
	}

	protected D getView() {
		return view;
	}
	
	@Override
	public boolean isVisible() {
		return view.isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		view.setVisible(visible);		
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
	
	/**
	 * Add event handlers as needed. This is called when
	 * the view is attached to the DOM. Subclasses must
	 * override this method if they want to install
	 * event handlers for events received through the
	 * event bus.
	 */
	protected void addEventHandlers() {
		// nothing to do
	}

	/**
	 * Adds an event handler. Subclasses should use this method
	 * to install event handlers for events received through the
	 * event bus. The handlers will be unregistered automatically
	 * when the view is detached from the DOM.
	 * 
	 * @param type the event type
	 * @param handler the event handler
	 * @return the handler registration
	 */
	protected <Handler> HandlerRegistration addEventHandler(Type<Handler> type, Handler handler) {
		HandlerRegistration reg = bus.addHandler(type, handler);
		handlers.add(reg);
		return reg;
	}
	
	private void removeEventHandlers() {
		for (HandlerRegistration reg : handlers) {
			reg.removeHandler();
		}
		handlers.clear();
	}
	
	/**
	 * Fires an event through the event bus.
	 * 
	 * @param event the event to fire
	 */
	protected void fireEvent(Event<?> event) {
		bus.fireEvent(event);
	}
	
}
