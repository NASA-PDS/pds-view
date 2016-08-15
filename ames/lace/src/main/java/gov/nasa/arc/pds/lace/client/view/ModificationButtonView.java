package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ModificationButtonPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view for the display of a modification button for label elements.
 */
public class ModificationButtonView extends Composite implements ModificationButtonPresenter.Display {

	interface ModificationButtonUiBinder extends UiBinder<Widget, ModificationButtonView> { /*empty*/ }

	private static ModificationButtonUiBinder uiBinder = GWT.create(ModificationButtonUiBinder.class);

	private ModificationButtonPresenter presenter;

	@UiField
	Label cutButton;

	@UiField
	Label copyButton;

	@UiField
	Label deleteButton;

	/**
	 * Creates a new instance of <code>ModificationButtonView</code>
	 */
	public ModificationButtonView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(ModificationButtonPresenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * Sets whether this object is visible.
	 *
	 * @param visible true to show the object, false to hide it
	 */
	@Override
	public void setVisible(boolean visible) {
		cutButton.setVisible(visible);
		copyButton.setVisible(visible);
		deleteButton.setVisible(visible);
	}

	/**
	 * Tests whether or not this object is visible.
	 *
	 * @return true if the object is visible
	 */
	@Override
	public boolean isVisible() {
		return deleteButton.isVisible();
	}

	@UiHandler("cutButton")
	void onRequestCut(ClickEvent event) {
		event.stopPropagation();
		presenter.onRequestCut();
	}

	@UiHandler("copyButton")
	void onRequestCopy(ClickEvent event) {
		event.stopPropagation();
		presenter.onRequestCopy();
	}

	@UiHandler("deleteButton")
	void onRequestDelete(ClickEvent event) {
		event.stopPropagation();
		presenter.onRequestDelete();
	}

}
