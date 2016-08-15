package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ModificationEvent;
import gov.nasa.arc.pds.lace.client.event.ModificationEvent.ModificationType;
import gov.nasa.arc.pds.lace.client.view.ModificationButtonView;

import javax.inject.Inject;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.ImplementedBy;

/**
 * Implements a presenter for a widget that displays a modification button for label elements.
 */
public class ModificationButtonPresenter extends Presenter<ModificationButtonPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(ModificationButtonView.class)
	public interface Display extends Presenter.Display<ModificationButtonPresenter>{ }

	private HandlerManager handlerManager = new HandlerManager(this);;

	/**
	 * Creates a new instance.
	 *
	 * @param view the view to use for user interaction and display
	 */
	@Inject
	public ModificationButtonPresenter(Display view) {
		super(view);
		view.setPresenter(this);
	}

	/**
	 * Adds an event handler.
	 *
	 * @param handler a click handler used for handling the click event on the button
	 * @return <code>HandlerRegistration</code> used to remove the handler
	 */
	public HandlerRegistration addEventHandler(ModificationEvent.Handler handler) {
		return handlerManager.addHandler(ModificationEvent.TYPE, handler);
	}

	public void onRequestCut() {
		handlerManager.fireEvent(new ModificationEvent(ModificationType.CUT));
	}

	public void onRequestCopy() {
		handlerManager.fireEvent(new ModificationEvent(ModificationType.COPY));
	}

	public void onRequestDelete() {
		handlerManager.fireEvent(new ModificationEvent(ModificationType.DELETE));
	}

}
