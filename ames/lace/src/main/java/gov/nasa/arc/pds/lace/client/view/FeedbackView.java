package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.Feedback;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Implements the view for the display of a feedback message to the user.
 * The widget removes itself after a few milliseconds if it's a non-modal
 * feedback. 
 */
public class FeedbackView extends Composite implements Feedback.Display {

	private FlowPanel panel;
	
	@Override
	public void setPresenter(Feedback presenter) {
		// Not needed
	}
	
	/**
	 * Creates an instance of <code>FeedbackView</code>
	 */
	public FeedbackView() {
		panel = new FlowPanel();
		initWidget(panel);
	}

	@Override
	public void show(String message, boolean isModal) {
		if (isModal) {
			panel.addStyleName("globalMessage");
			String contentHtml =
					"<div class='feedback modal'>" + 
						"<div class='font extraLarge message'>" + message + "</div>" +						
						"<div class='font large'>One moment please...</div>" +
						"<div class='wait-spinner'></div>" +
					"</div>";
			showFeedback(contentHtml);
		} else {
			panel.removeStyleName("globalMessage");
			String contentHtml =
					"<div class='feedback passive'>" +
						"<span class='message dropShadow'>" + message + "</span>" +
					"</div>";
			showFeedback(contentHtml);
			onShowAfter();
		}
	}
	
	@Override
	public void hide() {
		RootPanel.get().remove(this);
	}
	
	private void showFeedback(String contentHtml) {
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.append(SafeHtmlUtils.fromTrustedString(contentHtml));
		getElement().setInnerHTML(sb.toSafeHtml().asString());
		RootPanel.get().add(this);
	}
	
	private void onShowAfter() {
		Timer t = new Timer() {
			public void run() {
				hide();
			}
		};
		t.schedule(4000);
	}
}
