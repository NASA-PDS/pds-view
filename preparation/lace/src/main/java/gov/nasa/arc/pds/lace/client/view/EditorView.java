package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.EditorPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A view class responsible for displaying the edit view (editor).
 */
public class EditorView extends Composite implements EditorPresenter.Display {

	interface EditorViewUiBinder extends UiBinder<Widget, EditorView> { /*empty*/ }
	
	private static EditorViewUiBinder uiBinder = GWT.create(EditorViewUiBinder.class);
	
	private EditorPresenter presenter;

	@UiField
	DockLayoutPanel editorPanel;
	
	@UiField
	Element title;
	
	@UiField
	FlowPanel content;
	
	/**
	 * Creates an instance of <code>EditorView</code>.
	 */
	public EditorView() {
		initWidget(uiBinder.createAndBindUi(this));
		editorPanel.getElement().setId("editorPanel");
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
}
