package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.InsertionPointPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 *
 */
public class InsertionPointView extends Composite implements InsertionPointPresenter.Display {

	private static InsertionPointUiBinder uiBinder = GWT.create(InsertionPointUiBinder.class);

	interface InsertionPointUiBinder extends UiBinder<Widget, InsertionPointView> {

	}

	private InsertionPointPresenter presenter;

	@UiField
	FlowPanel requiredPanel;

	@UiField
	FlowPanel optionalPanel;

	@UiField
	Label plusButton;

	@UiField
	InlineLabel optionalTitle;

	@UiField
	InlineLabel requiredLabel;

	@UiField
	InlineHyperlink choiceLink;

	@UiField
	InlineHyperlink optionalLink;

	/**
	 *
	 */
	public InsertionPointView() {
		initWidget(uiBinder.createAndBindUi(this));
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
	public void setOptionalTitle(String title) {
		optionalTitle.setText(title);
	}

	@UiHandler("plusButton")
	public void onPlusButtonClick(ClickEvent event) {
		event.stopPropagation();
		presenter.handlePlusButtonClickEvent(event);
	}

	@UiHandler("choiceLink")
	public void onRequiredPanelClick(ClickEvent event) {
		event.stopPropagation();
		presenter.handleRequiredPanelClickEvent(event);
	}

	@UiHandler("optionalLink")
	public void onOptionalPanelClick(ClickEvent event) {
		event.stopPropagation();
		presenter.handleOptionalPanelClickEvent(event);
	}
}
