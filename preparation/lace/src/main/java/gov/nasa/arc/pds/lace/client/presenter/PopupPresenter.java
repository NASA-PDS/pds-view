package gov.nasa.arc.pds.lace.client.presenter;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implements the presenter for a widget that displays a dialog box (popup).
 */
public class PopupPresenter extends Presenter<PopupPresenter.Display> {
	
	public interface Display extends Presenter.Display<PopupPresenter> {
		
		/**
		 * Displays the popup.
		 */
		void display();
		
		/**
		 * Hides the popup.
		 */
		void hide();
		
		/**
		 * Sets the text inside the caption.
		 * 
		 * @param text the popup's new text inside the caption
		 */
		void setText(String text);
		
		/**
		 * Sets the content of the popup to the given widget.
		 * 
		 * @param widget the widget to show in the popup
		 */
		void setContent(Widget widget);
		
		/**
		 * Sets the content of the popup to the specified text;
		 * 
		 * @param text the text to show in the popup
		 */
		void setContent(String text);
		
		/**
		 * Clears the content panel.
		 */
		void clear();
		
		/**
		 * Sets the popup as a confirmation popup. The confirmation popup has two 
		 * buttons labeled 'Yes' and 'No'.
		 * 
		 * @param yesButtonHandler click handler for the 'yes' button
		 * @param noButtonHandler click handler for the 'no' button
		 */
		void setConfirmation(ClickHandler yesButtonHandler, ClickHandler noButtonHandler);
	}
	
	@Inject
	public PopupPresenter(Display view) {
		super(view);
		view.setPresenter(this);
	}
	
	/**
	 * Handles a click of the close button.
	 */
	public void onClose() {
		hide();
	}
	
	/**
	 * Sets the view's text inside the caption.
	 * 
	 * @param text the new text inside the caption
	 */
	public void setText(String text) {
		getView().setText(text);
	}
	
	/**
	 * Displays the view.
	 */
	public void display() {
		getView().display();
	}
	
	/**
	 * Hides the view.
	 */
	public void hide() {
		Display view = getView();
		view.clear();
		view.hide();
	}
	
	/**
	 * Sets the content panel in the view to the given widget.
	 * 
	 * @param widget
	 */
	public void setContent(Widget widget) {
		getView().setContent(widget);
	}
	
	/**
	 * 
	 * @param text
	 */
	public void setContent(String text) {
		getView().setContent(text);
	}
	
	/**
	 * Sets the popup as a confirmation popup.
	 *
	 * @param yesButtonHandler
	 * @param noButtonHandler
	 */
	public void setConfirmation(ClickHandler yesButtonHandler, ClickHandler noButtonHandler) {
		getView().setConfirmation(yesButtonHandler, noButtonHandler);		
	}
}
