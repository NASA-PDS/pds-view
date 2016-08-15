package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.EditorPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A view class responsible for displaying the edit view (editor).
 */
public class EditorView extends Composite implements EditorPresenter.Display {

	private static final String ALERT_CLASSNAME = "alert";
	private static final String ICON_MIDDLE_CLASSNAME = "icon middle";

	interface EditorViewUiBinder extends UiBinder<Widget, EditorView> { /*empty*/ }
	
	private static EditorViewUiBinder uiBinder = GWT.create(EditorViewUiBinder.class);
	
	@SuppressWarnings("unused")
	private EditorPresenter presenter;
	private boolean hasErrors = false;

	@UiField
	DockLayoutPanel editorPanel;
	
	@UiField
	HTMLPanel typeIcon;
	
	@UiField
	HTMLPanel alertIcon;
	
	@UiField
	Element title;
	
	@UiField
	HTMLPanel itemErrors;
	
	@UiField
	FlowPanel content;
	
	/**
	 * Creates an instance of <code>EditorView</code>.
	 */
	public EditorView() {
		initWidget(uiBinder.createAndBindUi(this));
		editorPanel.getElement().setId("editorPanel");
		
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
	public void setPresenter(EditorPresenter presenter) {
		this.presenter = presenter;		
	}

	@Override
	public void setTitle(String title) {
		this.title.setInnerText(title);
	}
	
	@Override
	public void clearContent() {
		content.clear();		
	}
	
	@Override
	public void show(Widget widget) {
		content.clear();
		content.add(widget);		
	}

	@Override
	public void setIcon(String className) {
		String styleName = typeIcon.getStyleName(); 
		if (styleName != null) {
			typeIcon.removeStyleName(styleName);
		}	
		typeIcon.addStyleName(ICON_MIDDLE_CLASSNAME + " " + className);
	}

	@Override
	public void setErrors(String[] messages) {
		itemErrors.getElement().setInnerHTML("");
		hasErrors = (messages.length > 0);
		
		if (!hasErrors) {
			typeIcon.removeStyleName(ALERT_CLASSNAME);
		} else {
			typeIcon.addStyleName(ALERT_CLASSNAME);
			
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
}
