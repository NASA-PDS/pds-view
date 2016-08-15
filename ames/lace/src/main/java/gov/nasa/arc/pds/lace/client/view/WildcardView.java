package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.WildcardPresenter;

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view for the display of a wildcard item selection.
 */
public class WildcardView extends Composite implements WildcardPresenter.Display{
	
	interface WildcardViewUiBinder extends UiBinder<Widget, WildcardView> { /*empty*/ }

	private static WildcardViewUiBinder uiBinder = GWT.create(WildcardViewUiBinder.class);

	private static final String LABEL_CELL_WIDTH = "100em";
	private static final String DROPDOWN_CELL_WIDTH = "300em";
	private static final String ELEMENT_DROPDOWN_DEFAULT_VALUE = "-- Select an element --";
	
	private WildcardPresenter presenter;

	@UiField
	ListBox nsDropdown;
	
	@UiField
	ListBox elementDropdown;
	
	@UiField
	HorizontalPanel nsPanel;
	
	@UiField
	HorizontalPanel elementPanel;
	
	@UiField
	InlineLabel nsLabel;
	
	@UiField
	InlineLabel elementLabel;
	
	@UiField
	FormPanel form;

	@UiField
	Button insertButton;

	@UiField
	Button cancelButton;
	
	/**
	 * Creates a new instance of the view.
	 */
	public WildcardView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		nsPanel.setCellWidth(nsLabel, LABEL_CELL_WIDTH);
		nsPanel.setCellWidth(nsDropdown, DROPDOWN_CELL_WIDTH);
		elementPanel.setCellWidth(elementLabel, LABEL_CELL_WIDTH);
		elementPanel.setCellWidth(elementDropdown, DROPDOWN_CELL_WIDTH);
		form.setMethod(FormPanel.METHOD_GET);
		
		nsDropdown.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {		
				loadElementNames();
			}
			
		});
	}

	@Override
	public void setPresenter(WildcardPresenter presenter) {
		this.presenter = presenter;		
	}

	@Override
	public void setNamespaces(List<String> namespaces) {
		for (String ns : namespaces) {
			nsDropdown.addItem(ns);
		}
		
		loadElementNames();
	}	
	
	@Override
	public void setElementNames(Collection<String> elementNames) {
		// Set the first option to a default value and disable it.
		elementDropdown.addItem(ELEMENT_DROPDOWN_DEFAULT_VALUE);
		elementDropdown.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
		
		for (String name : elementNames) {
			elementDropdown.addItem(name);
		}
	}
		
	@Override
	public String getDefaultValue() {
		return ELEMENT_DROPDOWN_DEFAULT_VALUE;		
	}
	
	private void loadElementNames() {
		elementDropdown.clear();
		presenter.loadTopLevelElementNames(getSelectedNS());
	}
	
	private String getSelectedNS() {
		return nsDropdown.getItemText(nsDropdown.getSelectedIndex());
	}
	
	@UiHandler("cancelButton")
	public void onCancel(ClickEvent event) {
		presenter.cancel();
	}

	@UiHandler("insertButton")
	public void onInsert(ClickEvent event) {
		event.stopPropagation();
		form.submit();
	}

	@UiHandler("form")
	public void onSubmit(SubmitEvent event) {
		presenter.insertElement(event, getSelectedNS(), elementDropdown.getItemText(elementDropdown.getSelectedIndex()));
	}
}
