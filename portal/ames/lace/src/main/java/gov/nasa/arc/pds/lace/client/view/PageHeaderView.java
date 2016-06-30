package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.PageHeaderPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view of the page header component.
 */
public class PageHeaderView extends Composite implements PageHeaderPresenter.Display {

	interface PageHeaderViewUiBinder extends UiBinder<Widget, PageHeaderView> { /*empty*/ }

	private static final int MAX_DISPLAY_CHARACTER = 30;

	private static PageHeaderViewUiBinder uiBinder = GWT.create(PageHeaderViewUiBinder.class);

	private PageHeaderPresenter presenter;
	private String labelName;
	private boolean isTextChanged = false;
	private boolean allowTitleEditing = true;

	@UiField
	Label returnButton;

	@UiField
	TextBox textBox;

	@UiField
	HTMLPanel returnLink;

	@UiField
	InlineLabel pageTitle;

	@UiField
	Button importButton;

	@UiField
	Button exportButton;

	@UiField
	Button previewButton;

	@UiField
	Button logoutButton;

	@UiField
	Button settingsButton;

	@UiField
	Button createButton;

	@UiField
	FlowPanel headerPanel;

	@UiField
	FormPanel form;

	@UiField
	Hidden fileName;

	@UiField
	Hidden desiredName;

	@UiField
	HTML logoHolder;

	/**
	 * Creates a new view instance.
	 */
	public PageHeaderView() {
		initWidget(uiBinder.createAndBindUi(this));

		String html = "<img src='images/logo-nasa.png' class='logo nasa' />"
					+ "<img src='images/logo-lace.png' class='logo lace' />";
		logoHolder.setHTML(html);
		headerPanel.getElement().setId("header");

		returnLink.setVisible(false);

		textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				presenter.saveLabelName(event.getValue());
				isTextChanged = false;
				textBox.setVisible(false);
				pageTitle.setText(event.getValue());
				pageTitle.setVisible(true);
			}
		});

		textBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				String value = textBox.getValue();
				if (isTextChanged) {
					value = textBox.getValue().substring(0, MAX_DISPLAY_CHARACTER) + "...";
					isTextChanged = false;
				}
				textBox.setVisible(false);
				pageTitle.setText(value);
				pageTitle.setVisible(true);
			}

		});

		textBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				 if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					 textBox.setFocus(false);
				 }
			}
		});
	}

	@Override
	public void setPresenter(PageHeaderPresenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("importButton")
	void onImport(ClickEvent event) {
		if (presenter != null) {
			presenter.onImport();
		}
	}

	@UiHandler("exportButton")
	void onExport(ClickEvent event) {
		if (presenter != null) {
			presenter.onExport();
		}
	}

	@UiHandler("previewButton")
	void onPreview(ClickEvent event) {
		if (presenter != null) {
			presenter.onPreview();
		}
	}

	@UiHandler("logoutButton")
	void onLogout(ClickEvent event) {
		if (presenter != null) {
			presenter.onLogout();
		}
	}

	@UiHandler("settingsButton")
	void onSettings(ClickEvent event) {
		settingsButton.setFocus(false);
		if (presenter != null) {
			presenter.onSettings();
		}
	}

	@UiHandler("createButton")
	void onCreate(ClickEvent event) {
		if (presenter != null) {
			presenter.onNewLabel();
		}
	}

	@UiHandler("pageTitle")
	void onTextBoxClick(ClickEvent event) {
		if (!allowTitleEditing) {
			// Ignore the event.
			return;
		}

		String value = pageTitle.getText();
		if (labelName.length() > MAX_DISPLAY_CHARACTER) {
			value = labelName;
			isTextChanged = true;
		}
		pageTitle.setVisible(false);
		textBox.setValue(value);
		textBox.setVisible(true);
		textBox.setFocus(true);
	}

	@Override
	public void setLabelName(String name) {
		labelName = name;
		pageTitle.setTitle(name);
		if (name.length() > MAX_DISPLAY_CHARACTER) {
			name = name.substring(0, MAX_DISPLAY_CHARACTER) + "...";
		}
		setPageTitle(name);
	}

	@Override
	public void setDownloadFileName(String name) {
		this.fileName.setValue(name);
	}

	@Override
	public void setDownloadDesiredName(String name) {
		this.desiredName.setValue(name);
	}

	@Override
	public void performDownload() {
		String action = GWT.getHostPageBaseURL() + "designer/fileDownload";

		GWT.log("Setting upload form action to '" + action + "'.");
		form.setAction(action);
		form.setMethod(FormPanel.METHOD_GET);
		form.submit();
	}

	@UiHandler("returnButton")
	void onRequestReturn(ClickEvent event) {
		presenter.onRequestReturn();
	}

	@Override
	public void setPageTitle(String title) {
		pageTitle.setText(title);
		//pageTitle.setVisible(true);
	}

	@Override
	public void enableCreateButton() {
		createButton.setVisible(true);
	}

	@Override
	public void enableImportButton() {
		importButton.setVisible(true);
	}

	@Override
	public void enableExportButton() {
		exportButton.setVisible(true);
	}

	@Override
	public void enablePreviewButton() {
		previewButton.setVisible(true);
	}

	@Override
	public void enableLogoutButton() {
		logoutButton.setVisible(true);
	}

	@Override
	public void allowTitleEditing(boolean allow) {
		this.allowTitleEditing = allow;
	}

	@Override
	public void enableSettingsButton() {
		settingsButton.setVisible(true);
	}

	@Override
	public void enableReturnLink(boolean enable) {
		returnLink.setVisible(enable);
	}

}
