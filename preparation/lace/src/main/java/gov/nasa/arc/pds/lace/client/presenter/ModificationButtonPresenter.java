package gov.nasa.arc.pds.lace.client.presenter;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

/**
 * Implements a presenter for a widget that displays a modification button for label elements.
 */
public class ModificationButtonPresenter extends Presenter<ModificationButtonPresenter.Display> {
	
	/**
	 * Defines an interface that the view must implement.
	 */
	public interface Display extends Presenter.Display<ModificationButtonPresenter>{
		
		/**
		 * Adds an event handler.
		 * 
		 * @param handler a click handler
		 * @return <code>HandlerRegistration</code> used to remove the handler
		 */
		HandlerRegistration addEventHandler(ClickHandler handler);
	}
	
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
	public HandlerRegistration addEventHandler(ClickHandler handler) {
		return getView().addEventHandler(handler);
	}
}
