package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a widget for showing an expand/collapse frame, with a title
 * and default cube icon, which contains other UI elements.
 */
public class ContainerView extends Composite implements ContainerPresenter.Display { 

	interface ContainerViewUiBinder extends UiBinder<Widget, ContainerView> { /*empty*/ }
	
	private static final ImageResource EDIT_ARROW_COLLAPSED = Resources.INSTANCE.editArrowCollapsed();
	private static final ImageResource EDIT_ARROW_EXPANDED = Resources.INSTANCE.editArrowExpanded();
	
	private static ContainerViewUiBinder uiBinder = GWT.create(ContainerViewUiBinder.class);	
		
	// Flag indicating whether the container is expanded or not.
	private boolean isExpanded = false; 	
	private ContainerPresenter presenter;
	
	@UiField
	Element title;
	
	@UiField
	Image expandIcon;
	
	@UiField
	ImageElement typeIcon;
	
	@UiField
	FlowPanel contentPanel;
	
	@UiField
	HTMLPanel container;
	
	/**
	 * Creates an instance of <code>ContainerView</code>.
	 */
	public ContainerView() {
		initWidget(uiBinder.createAndBindUi(this));		
		expandIcon.setResource(EDIT_ARROW_COLLAPSED);
		expandIcon.setTitle("Open");
	}

	@Override
	public void setPresenter(ContainerPresenter presenter) {
		this.presenter = presenter;		
	}

	@Override
	public void toggleContainer() {		
		if (contentPanel.getStyleName().contains("hide")) {			
			contentPanel.removeStyleName("hide");
			contentPanel.addStyleName("display");
			expandIcon.setResource(EDIT_ARROW_EXPANDED);	
			expandIcon.setTitle("Close");
			isExpanded = true;
		} else {
			contentPanel.removeStyleName("display");
			contentPanel.addStyleName("hide");
			expandIcon.setResource(EDIT_ARROW_COLLAPSED);
			expandIcon.setTitle("Open");
			isExpanded = false;
		}
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
	public void setTypeIcon(ImageResource image) {
		typeIcon.setSrc(image.getSafeUri().asString());
	}

	@Override
	public ImageElement getTypeIcon() {
		// TODO: should it return ImageSource?
		return typeIcon;
	}
	
	@Override
	public void addContent(Widget widget) {		
		contentPanel.add(widget);
	}
	
	@Override
	public void clearContent() {
		contentPanel.clear();
	}
	
	@Override
	public Widget getContent() {
		contentPanel.removeStyleName("hide");
		contentPanel.removeStyleName("content");
		contentPanel.removeStyleName("editorContentPanel");
		return contentPanel.asWidget();		
	}
	
	@UiHandler("expandIcon")
	void onClick(ClickEvent e) {		
		e.stopPropagation();
		presenter.handleContainerClickEvent(isExpanded);		
	}
}
