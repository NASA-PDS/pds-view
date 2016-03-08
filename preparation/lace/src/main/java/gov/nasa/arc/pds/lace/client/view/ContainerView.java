package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.presenter.ModificationButtonPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;
import gov.nasa.arc.pds.lace.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implements a widget for showing an expand/collapse frame,
 * with a title and icon, which contains other UI elements.
 */
public class ContainerView extends Composite implements ContainerPresenter.Display { 

	interface ContainerViewUiBinder extends UiBinder<Widget, ContainerView> { /*empty*/ }
	
	private static final ImageResource EDIT_ARROW_COLLAPSED = Resources.INSTANCE.editArrowCollapsed();
	private static final ImageResource EDIT_ARROW_EXPANDED = Resources.INSTANCE.editArrowExpanded();
	private static final String ALERT_CLASSNAME = "alert";
	private static final String MIDDLE_CLASSNAME = "middle";
	
	private static ContainerViewUiBinder uiBinder = GWT.create(ContainerViewUiBinder.class);	
		 	
	private ContainerPresenter presenter;
	
	@UiField
	InlineLabel title;
	
	@UiField
	Image arrowIcon;
	
	@UiField
	HTMLPanel typeIcon;
	
	@UiField
	FlowPanel contentPanel;
	
	@UiField
	FocusPanel titlePanel;
	
	@UiField
	HTMLPanel container;
	
	@UiField(provided=true)
	ModificationButtonPresenter modificationButton;
	
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
	public void add(Widget widget) {
		contentPanel.add(widget);
	}
	
	@Override
	public void insert(Widget widget, int index) {
		contentPanel.insert(widget, index);
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
			arrowIcon.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent e) {		
					e.stopPropagation();
					presenter.handleContainerClickEvent();		
				}
				
			});
		}		
	}

	@Override
	public void remove(int index) {
		try {
			contentPanel.remove(index);
		} catch(IndexOutOfBoundsException ex) {
			GWT.log("Index out of bound: " + ex.getMessage());
			return;
		}
	}

	@Override
	public void addEventListeners(boolean isDeletable) {
		if (isDeletable) {
			titlePanel.addMouseMoveHandler(new MouseMoveHandler() {
				
				@Override
				public void onMouseMove(MouseMoveEvent event) {
					event.preventDefault();
					event.stopPropagation();
					presenter.handleMouseEvent(true);
				}
				
			});
			
			titlePanel.addMouseOverHandler(new MouseOverHandler() {
				
				@Override
				public void onMouseOver(MouseOverEvent event) {
					event.preventDefault();
					event.stopPropagation();
					presenter.handleMouseEvent(true);
				}
				
			});
			
			titlePanel.addMouseOutHandler(new MouseOutHandler() {
				
				@Override
				public void onMouseOut(MouseOutEvent event) {
					event.preventDefault();
					event.stopPropagation();
					presenter.handleMouseEvent(false);
				}
				
			});		
				
			modificationButton.addEventHandler(new ClickHandler() {
	
				@Override
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					presenter.modifyElement();
				}
				
			});
		}
		
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
	public void handleDeleteAction(String name, PopupPresenter popup) {
		String msg = "This action will permanently delete the '" + name 
				+ "' element. Do you want to continue?";
		enableModification(false);
		showConfirmationPopup("Delete", msg, popup);
	}
	
	private void showConfirmationPopup(String title, String msg, final PopupPresenter popup) {		
		ClickHandler yesBtnHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				presenter.deleteElement();
			}
			
		};
		
		ClickHandler noBtnHandler = new ClickHandler() {			
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				presenter.cancelDelete();
			}
			
		};
		
		popup.setText(title);
		popup.setContent(msg);
		popup.setConfirmation(yesBtnHandler, noBtnHandler);
		popup.display();	
	}
}
