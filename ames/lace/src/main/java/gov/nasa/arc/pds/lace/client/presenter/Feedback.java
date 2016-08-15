package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.view.FeedbackView;

import javax.inject.Inject;

import com.google.inject.ImplementedBy;

/**
 * Implements a presenter for a widget that displays feedback to the user
 * either for passive status messaging or status messaging requiring a modal
 * interruption.
 */
public class Feedback extends Presenter<Feedback.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(FeedbackView.class)
	public interface Display extends Presenter.Display<Feedback> {
		
		/**
		 * Shows the widget with the specified message.
		 * 
		 * @param message the message to show
		 * @param isModal true, if the message should preempt user interaction
		 */
		void show(String message, boolean isModal);
		
		/**
		 * Hides the widget.
		 */
		void hide();
	}
	
	/**
	 * Creates a new instance of the feedback presenter.
	 *
	 * @param view the display interface for the feedback view
	 */
	@Inject
	public Feedback(Display view) {
		super(view);	
		view.setPresenter(this);
	}

	/**
	 * Displays the specified message as a feedback to the user.
	 * 
	 * @param message the feedback message
	 * @param modal true for modal feedback, false for passive feedback 
	 */
	public void display(String message, boolean modal) {
		getView().show(message, modal);
	}
	
	/**
	 * Hides the widget.
	 */
	public void hide() {
		getView().hide();
	}
}
