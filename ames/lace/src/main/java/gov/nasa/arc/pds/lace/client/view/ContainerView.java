package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.event.ModificationEvent;
import gov.nasa.arc.pds.lace.client.event.ModificationEvent.ModificationType;
import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.presenter.ModificationButtonPresenter;
import gov.nasa.arc.pds.lace.client.resources.Resources;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a widget for showing an expand/collapse frame,
 * with a title and icon, which contains other UI elements.
 */
public class ContainerView extends Composite implements ContainerPresenter.Display {

	interface ContainerViewUiBinder extends UiBinder<Widget, ContainerView> { /*empty*/ }

	/** A style name used to hide components. */
	private static final String HIDDEN_STYLE = "hidden";

	/** A style to indicate when the documentation is being shown. */
	private static final String OPEN_STYLE = "open";

	private static final ImageResource EDIT_ARROW_COLLAPSED = Resources.INSTANCE.editArrowCollapsed();
	private static final ImageResource EDIT_ARROW_EXPANDED = Resources.INSTANCE.editArrowExpanded();
	private static final String ALERT_CLASSNAME = "alert";
	private static final String MIDDLE_CLASSNAME = "middle";

	private static ContainerViewUiBinder uiBinder = GWT.create(ContainerViewUiBinder.class);

	private ContainerPresenter presenter;
	private boolean deleteEventsAttached = false;

	private HandlerRegistration modificationButtonHandler;
	private HandlerRegistration mouseOutHandler;
	private HandlerRegistration mouseMoveHandler;
	private HandlerRegistration mouseOverHandler;

	private boolean hasErrors = false;

	@UiField
	InlineLabel title;

	@UiField
	Image arrowIcon;

	@UiField
	HTMLPanel alertIcon;

	@UiField
	HTMLPanel itemErrors;

	@UiField
	HTML itemDocumentation;

	@UiField
	HTMLPanel typeIcon;

	@UiField
	FlowPanel attributePanel;

	@UiField
	FlowPanel contentPanel;

	@UiField
	FocusPanel titlePanel;

	@UiField
	HTMLPanel container;

	@UiField(provided=true)
	ModificationButtonPresenter modificationButton;

	@UiField
	InlineLabel infoIcon;

	/**
	 * Creates an instance of <code>ContainerView</code>.
	 */
	@Inject
	public ContainerView(ModificationButtonPresenter modButton) {
		modificationButton = modButton;

		initWidget(uiBinder.createAndBindUi(this));

		arrowIcon.setResource(EDIT_ARROW_COLLAPSED);
		arrowIcon.setTitle("Open");
		arrowIcon.setStyleName(MIDDLE_CLASSNAME);

		alertIcon.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (hasErrors) {
					itemErrors.removeStyleName("hidden");
				}
			}
		}, MouseOverEvent.getType());

		alertIcon.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				itemErrors.addStyleName("hidden");
			}
		}, MouseOutEvent.getType());
	}

	@Override
	public void setPresenter(ContainerPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setContainerTitle(String title) {
		this.title.setText(title);
	}

	@Override
	public String getContainerTitle() {
		return this.title.getText();
	}

	@Override
	public void setIcon(String className, boolean isComplete) {
		typeIcon.addStyleName(className);
		if (!isComplete) {
			typeIcon.addStyleName(ALERT_CLASSNAME);
		}
	}

	@Override
	public void setState(boolean complete) {
		if (complete) {
			typeIcon.removeStyleName(ALERT_CLASSNAME);
		} else {
			typeIcon.addStyleName(ALERT_CLASSNAME);
		}
	}

	@Override
	public void addAttribute(Widget widget) {
		attributePanel.add(widget);
	}

	@Override
	public void add(Widget widget) {
		contentPanel.add(widget);
	}

	@Override
	public void insert(Widget widget, int index) throws IndexOutOfBoundsException {
		try {
			contentPanel.insert(widget, index);
		} catch(IndexOutOfBoundsException ex) {
			GWT.log("[View] Index is out of bound: " + ex.getMessage());
			throw ex;
		}
	}

	@Override
	public void clearContent() {
		contentPanel.clear();
	}

	@Override
	public void toggleContainer() {
		if (contentPanel.getStyleName().contains("hide")) {
			contentPanel.removeStyleName("hide");
			contentPanel.addStyleName("display");
			arrowIcon.setResource(EDIT_ARROW_EXPANDED);
			arrowIcon.setTitle("Close");
		} else {
			contentPanel.removeStyleName("display");
			contentPanel.addStyleName("hide");
			arrowIcon.setResource(EDIT_ARROW_COLLAPSED);
			arrowIcon.setTitle("Open");
		}
	}

	@Override
	public void show(boolean contentOnly) {
		if (contentOnly) {
			contentPanel.removeStyleName("hide");
			contentPanel.removeStyleName("content");
			contentPanel.removeStyleName("editorContentPanel");
			enableModification(false);
		} else {
			titlePanel.removeStyleName("hide");
			container.addStyleName("container");
		}
	}

	@Override
	public void remove(int index) throws IndexOutOfBoundsException {
		try {
			contentPanel.remove(index);
		} catch(IndexOutOfBoundsException ex) {
			GWT.log("[View] Index is out of bound: " + ex.getMessage());
			throw ex;
		}
	}

	@Override
	public void addDeleteEventListeners() {
		if (!deleteEventsAttached) {
			mouseMoveHandler = titlePanel.addMouseMoveHandler(new MouseMoveHandler() {

				@Override
				public void onMouseMove(MouseMoveEvent event) {
					event.preventDefault();
					event.stopPropagation();
					presenter.handleMouseEvent(true);
				}

			});

			mouseOverHandler = titlePanel.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					event.preventDefault();
					event.stopPropagation();
					presenter.handleMouseEvent(true);
				}

			});

			mouseOutHandler = titlePanel.addMouseOutHandler(new MouseOutHandler() {

				@Override
				public void onMouseOut(MouseOutEvent event) {
					event.preventDefault();
					event.stopPropagation();
					presenter.handleMouseEvent(false);
				}

			});

			modificationButtonHandler = modificationButton.addEventHandler(new ModificationEvent.Handler() {
				@Override
				public void onEvent(ModificationType eventType) {
					switch (eventType) {
					case CUT:
						presenter.onCut();
						break;
					case COPY:
						presenter.onCopy();
						break;
					default: //DELETE
						presenter.onDelete();
						break;
					}
				}
			});

			deleteEventsAttached = true;
		}
	}

	@Override
	public void removeDeleteEventListeners() {
		if (deleteEventsAttached) {
			mouseOverHandler.removeHandler();
			mouseOutHandler.removeHandler();
			mouseMoveHandler.removeHandler();
			modificationButtonHandler.removeHandler();
			deleteEventsAttached = false;
			enableModification(false);
		}
	}

	@Override
	public void addEventListeners() {
		titlePanel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent e) {
				e.stopPropagation();
				presenter.handleContainerClickEvent();
			}

		});
	}

	@Override
	public void enableModification(boolean show) {
		modificationButton.setVisible(show);
	}

	@Override
	public boolean isDeleteEventsAttached() {
		return deleteEventsAttached;
	}

	@Override
	public void setErrors(String[] messages) {
		itemErrors.getElement().setInnerHTML("");
		hasErrors = (messages.length > 0);

		if (hasErrors) {
			Document d = Document.get();
			UListElement list = d.createULElement();

			for (String msg : messages) {
				LIElement li = d.createLIElement();
				li.setInnerText(msg);
				list.appendChild(li);
			}

			itemErrors.getElement().appendChild(list);
		}
	}

	@Override
	public void setDocumentation(String documentation) {
		itemDocumentation.setHTML(SafeHtmlUtils.fromTrustedString(documentation));
		infoIcon.removeStyleName(HIDDEN_STYLE);
	}

	@UiHandler("infoIcon")
	void onInfoIconClick(ClickEvent event) {
		event.stopPropagation();
		presenter.onRequestDocumentation();
	}

	@Override
	public void showDocumentation(boolean show) {
		if (show) {
			infoIcon.addStyleName(OPEN_STYLE);
			itemDocumentation.removeStyleName(HIDDEN_STYLE);
		} else {
			infoIcon.removeStyleName(OPEN_STYLE);
			itemDocumentation.addStyleName(HIDDEN_STYLE);
		}
	}

}
