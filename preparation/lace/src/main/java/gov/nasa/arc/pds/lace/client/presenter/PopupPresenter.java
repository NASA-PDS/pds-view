package gov.nasa.arc.pds.lace.client.presenter;

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
		 * @param widget
		 */
		void setContent(Widget widget);
		
		/**
		 * Clears the content panel.
		 */
		void clear();
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
}
