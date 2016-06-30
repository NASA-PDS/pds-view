package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.InsertionPointPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view for the display of an insertion point as either a plus button
 * or a grayed-out box.
 */
public class InsertionPointView extends Composite implements InsertionPointPresenter.Display {

	interface InsertionPointUiBinder extends UiBinder<Widget, InsertionPointView> { /*empty*/ }

	private static final InsertionPointUiBinder uiBinder = GWT.create(InsertionPointUiBinder.class);

	private static final String PASTE_ALLOWED_CLASS = "pasteAllowed";

	private InsertionPointPresenter presenter;

	@UiField
	FocusPanel wrapperPanel;

	@UiField
	FlowPanel insertionPointPanel;

	@UiField
	Label dropTarget;

	@UiField
	Label pasteButton;

	@UiField
	FlowPanel requiredPanel;

	@UiField
	FlowPanel optionalPanel;

	@UiField
	FlowPanel anyPanel;

	@UiField
	Label plusButton;

	@UiField
	InlineLabel optionalTitle;

	/**
	 * Creates a new instance of the view.
	 */
	public InsertionPointView() {
		initWidget(uiBinder.createAndBindUi(this));

		addHandlers();
	}

	@Override
	public void setPresenter(InsertionPointPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setPlusButtonPosition(int top, int right) {
		Element element = plusButton.getElement();
		DOM.setStyleAttribute(element, "top", top + "px");
		DOM.setStyleAttribute(element, "right", right + "px");
	}

	@Override
	public void showPlusButton(boolean show) {
		plusButton.setVisible(show);
	}

	@Override
	public void showRequiredPanel(boolean show) {
		requiredPanel.setVisible(show);
	}

	@Override
	public void showOptionalPanel(boolean show) {
		optionalPanel.setVisible(show);
	}

	@Override
	public void showAnyPanel(boolean show) {
		anyPanel.setVisible(show);
	}

	@Override
	public void setOptionalTitle(String title) {
		optionalTitle.setText(title);
	}

	@UiHandler("plusButton")
	public void onPlusButtonClick(ClickEvent event) {
		event.stopPropagation();
		presenter.handlePlusButtonClickEvent(event);
	}

	private void addHandlers() {

		// Add click handler on required panel
		requiredPanel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				presenter.handleRequiredPanel(event);
			}
		}, ClickEvent.getType());

		// Add click handler on optional panel
		optionalPanel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				presenter.handleOptionalPanel(event);
			}
		}, ClickEvent.getType());

		// Add click handler for 'any' panel
		anyPanel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				presenter.handleAnyPanel(event);
			}
		}, ClickEvent.getType());
	}

	@UiHandler("pasteButton")
	void onRequestPaste(ClickEvent event) {
		presenter.onRequestPaste();
	}

	@UiHandler("pasteButton")
	void onMouseOverPaste(MouseOverEvent event) {
		if (presenter.canPaste()) {
			dropTarget.addStyleName(PASTE_ALLOWED_CLASS);
			pasteButton.addStyleName(PASTE_ALLOWED_CLASS);
		}
	}

	@UiHandler("pasteButton")
	void onMouseOutPaste(MouseOutEvent event) {
		dropTarget.removeStyleName(PASTE_ALLOWED_CLASS);
		pasteButton.removeStyleName(PASTE_ALLOWED_CLASS);
	}

	@UiHandler("dropTarget")
	void onMouseOverDrop(MouseOverEvent event) {
		if (presenter.canPaste()) {
			dropTarget.addStyleName(PASTE_ALLOWED_CLASS);
			pasteButton.addStyleName(PASTE_ALLOWED_CLASS);
		}
	}

	@UiHandler("dropTarget")
	void onMouseOutDrop(MouseOutEvent event) {
		dropTarget.removeStyleName(PASTE_ALLOWED_CLASS);
		pasteButton.removeStyleName(PASTE_ALLOWED_CLASS);
	}

	@UiHandler("wrapperPanel")
	void onMouseOverPanel(MouseOverEvent event) {
		if (presenter.canDropOnto() && presenter.canPaste()) {
			pasteButton.addStyleName(PASTE_ALLOWED_CLASS);
		}
	}

	@UiHandler("wrapperPanel")
	void onMouseOutPanel(MouseOutEvent event) {
		pasteButton.removeStyleName(PASTE_ALLOWED_CLASS);
	}

	@Override
	public void setInsertionType(String insertionType) {
		insertionPointPanel.addStyleName(insertionType);
	}

}
