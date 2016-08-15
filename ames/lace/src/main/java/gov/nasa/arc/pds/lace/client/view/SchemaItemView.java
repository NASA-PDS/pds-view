package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.SchemaItemPresenter;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SchemaItemView extends Composite implements SchemaItemPresenter.Display {

	interface Binder extends UiBinder<Widget, SchemaItemView> { /*empty*/ }
	
	private static final Binder BINDER = GWT.create(Binder.class);
	
	@UiField
	Label itemName;
	
	@UiField
	Label deleteButton;
	
	private SchemaItemPresenter presenter;
	
	/**
	 * Creates a new instance of the view.
	 */
	@Inject
	public SchemaItemView() {
		initWidget(BINDER.createAndBindUi(this));
	}

	@Override
	public void setPresenter(SchemaItemPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setItemName(String name) {
		itemName.setText(name);
	}
	
	@UiHandler("deleteButton")
	void onRequestDelete(ClickEvent event) {
		presenter.onRequestDelete();
	}
	
}
