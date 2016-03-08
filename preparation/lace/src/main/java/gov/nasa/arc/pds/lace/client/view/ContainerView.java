package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

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
	Element title;
	
	@UiField
	Image arrowIcon;
	
	@UiField
	Element typeIcon;
	
	@UiField
	FlowPanel contentPanel;
	
	@UiField
	HTMLPanel titlePanel;
	
	@UiField
	HTMLPanel container;
	
	/**
	 * Creates an instance of <code>ContainerView</code>.
	 */
	public ContainerView() {
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
		this.title.setInnerText(title);
	}

	@Override
	public String getContainerTitle() {
		return this.title.getInnerText();
	}
	
	@Override
	public void setIcon(String className, boolean isComplete) {
		typeIcon.addClassName(className);
		if (!isComplete) {
			typeIcon.addClassName(ALERT_CLASSNAME);
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
}
