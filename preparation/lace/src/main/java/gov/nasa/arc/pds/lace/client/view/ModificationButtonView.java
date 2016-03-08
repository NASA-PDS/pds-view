package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ModificationButtonPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view for the display of a modification button for label elements.
 */
public class ModificationButtonView extends Composite implements ModificationButtonPresenter.Display, HasClickHandlers {

	interface ModificationButtonUiBinder extends UiBinder<Widget, ModificationButtonView> { /*empty*/ } 
	
	private static ModificationButtonUiBinder uiBinder = GWT.create(ModificationButtonUiBinder.class);

	private ModificationButtonPresenter presenter;

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
	public void setVisible(boolean visible) {
		deleteButton.setVisible(visible);		
	}
	
	/**
	 * Tests whether or not this object is visible.
	 * 
	 * @return true if the object is visible
	 */
	public boolean isVisible() {
		return deleteButton.isVisible();
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
	
	@Override
	public HandlerRegistration addEventHandler(ClickHandler handler) {
		return addClickHandler(handler);	
	}
}
