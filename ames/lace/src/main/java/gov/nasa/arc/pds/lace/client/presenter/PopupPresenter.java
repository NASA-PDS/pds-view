package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.view.PopupView;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays a dialog box (popup).
 */
public class PopupPresenter extends Presenter<PopupPresenter.Display> {
	
	@ImplementedBy(PopupView.class)
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
		 * Enables or disables the autoHide feature. When enabled, the popup will be
		 * automatically hidden when the user clicks outside of it.
		 * 
		 * @param autoHide <code>true</code> if the dialog should be automatically
		 * 			hidden when the user clicks outside of it
		 */
		void setAutoHide(boolean autoHide);
		
		/**
		 * Displays the popup as a confirmation box. The confirmation popup has two 
		 * buttons labeled 'Yes' and 'No'.
		 * 
		 * @param title the confirmation box title
		 * @param message the message to display
		 * @param yesButtonHandler click handler for the 'yes' button
		 * @param noButtonHandler click handler for the 'no' button
		 */
		void showConfirmationBox(String title, String message, ClickHandler yesButtonHandler, ClickHandler noButtonHandler);
		
		/**
		 * Displays the popup as an error box with an OK button.
		 * 
		 * @param title the alert box title
		 * @param message the message to display
		 */
		void showErrorBox(String title, String message);
	}
		
	@Inject
	public PopupPresenter(Display view) {
		super(view);
		view.setPresenter(this);
	}
	
	/**
	 * Enables or disables the autoHide feature.
	 * 
	 * @param autoHide true, if the dialog should be automatically
	 * 			hidden when the user clicks outside of it
	 */
	public void setAutoHide(boolean autoHide) {
		getView().setAutoHide(autoHide);
	}
	
	/**
	 * Handles a click of the close button.
	 */
	public void onClose(ClickEvent event) {
		if (event != null) {
			event.stopPropagation();
		}	
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
		getView().hide();
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
	 * Displays a confirmation box with Yes and No buttons.
	 *
	 * @param yesButtonHandler
	 * @param noButtonHandler
	 */
	public void showConfirmationBox(String title, String message, ClickHandler yesButtonHandler, ClickHandler noButtonHandler) {
		getView().showConfirmationBox(title, message, yesButtonHandler, noButtonHandler);		
	}
	
	/**
	 * Displays an error box with an OK button which hides the box when pressed.
	 * 
	 * @param message
	 */
	public void showErrorBox(String message) {
		showErrorBox(null, message);
	}

	/**
	 * Displays an error box with an OK button which hides the box when pressed. 
	 * 
	 * @param title
	 * @param message
	 */
	public void showErrorBox(String title, String message) {
		getView().showErrorBox(title, message);
	}
}
