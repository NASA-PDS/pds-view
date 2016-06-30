package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ElementChooserPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SchemaItemPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SchemaItemPresenter.Handler;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view for the display of a chooser dialog for
 * choosing a top-level or other element.
 */
public class ElementChooserView extends Composite implements ElementChooserPresenter.Display {
	
	interface Binder extends UiBinder<Widget, ElementChooserView> { /*empty*/ }

	private static final Binder BINDER = GWT.create(Binder.class);

	private static final String LABEL_CELL_WIDTH = "100em";
	private static final String DROPDOWN_CELL_WIDTH = "300em";
	private static final String ELEMENT_DROPDOWN_DEFAULT_VALUE = "-- Select an element --";
	
	private ElementChooserPresenter presenter;
	private Provider<SchemaItemPresenter> itemProvider;

	@UiField
	ListBox nsDropdown;
	
	@UiField
	ListBox elementDropdown;
	
	@UiField
	InlineLabel nsLabel;
	
	@UiField
	InlineLabel elementLabel;
	
	@UiField
	HorizontalPanel nsPanel;
	
	@UiField
	HorizontalPanel elementPanel;
	
	@UiField
	FlowPanel localSchemas;
	
	@UiField
	FormPanel uploadForm;
	
	@UiField
	FileUpload upload;
	
	@UiField
	Button okButton;

	@UiField
	Button cancelButton;

	private Handler schemaFileDeleteHandler;
	
	/**
	 * Creates a new instance of the view.
	 * 
	 * @param itemProvider a provider of the component for each local schema file item
	 */
	@Inject
	public ElementChooserView(
			Provider<SchemaItemPresenter> itemProvider
	) {
		this.itemProvider = itemProvider;
		initWidget(BINDER.createAndBindUi(this));

		nsPanel.setCellWidth(nsLabel, LABEL_CELL_WIDTH);
		nsPanel.setCellWidth(nsDropdown, DROPDOWN_CELL_WIDTH);
		elementPanel.setCellWidth(elementLabel, LABEL_CELL_WIDTH);
		elementPanel.setCellWidth(elementDropdown, DROPDOWN_CELL_WIDTH);
		
		// Point the form at a service.
		uploadForm.setAction(GWT.getModuleBaseURL()  + "fileUpload");

	    // Because the form has a FileUpload widget, we'll need to set the
	    // form to use the POST method, and multipart MIME encoding.
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		
		nsDropdown.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (!getSelectedNS().equals(ELEMENT_DROPDOWN_DEFAULT_VALUE)) {
					loadElementNames();
				}
			}
		});
		
		SchemaItemPresenter.Handler handler = new SchemaItemPresenter.Handler() {
			@Override
			public void onRequestDelete(String name) {
				presenter.onRequestDeleteSchemaFile(name);
			}
		};
		this.schemaFileDeleteHandler = handler;
	}

	@Override
	public void setPresenter(ElementChooserPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setNamespaces(Collection<String> namespaces) {
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
	
	private String getSelectedElement() {
		return elementDropdown.getItemText(elementDropdown.getSelectedIndex());
	}
	
	@UiHandler("cancelButton")
	public void onCancel(ClickEvent event) {
		presenter.cancel();
	}

	@UiHandler("okButton")
	public void onChoose(ClickEvent event) {
		presenter.onChooseElement(getSelectedNS(), getSelectedElement());
	}

	@Override
	public void setSchemaFiles(String[] result) {
		localSchemas.clear();
		for (String s : result) {
			SchemaItemPresenter item = itemProvider.get();
			item.setItemName(s);
			item.setHandler(schemaFileDeleteHandler);
			localSchemas.add(item);
		}
	}
	
	@UiHandler("uploadForm")
	void onRequestUpload(SubmitEvent event) {
		if (!presenter.isUploadValid()) {
			event.cancel();
			presenter.onInvalidUploadRequest();
		}
	}
	
	@Override
	public String getUploadFilename() {
		return upload.getFilename();
	}
	
	@UiHandler("uploadForm")
	void onUploadComplete(SubmitCompleteEvent event) {
		presenter.onUploadComplete();
	}
	
}
