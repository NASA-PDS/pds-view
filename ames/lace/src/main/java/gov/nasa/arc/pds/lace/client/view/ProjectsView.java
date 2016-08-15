package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ProjectItemPresenter;
import gov.nasa.arc.pds.lace.client.presenter.ProjectsPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A view class responsible for displaying the edit view (editor).
 */
public class ProjectsView extends Composite implements ProjectsPresenter.Display {

	interface Binder extends UiBinder<Widget, ProjectsView> { /*empty*/ }
	
	private static final Binder BINDER = GWT.create(Binder.class);
	private static final String NO_LABEL_STYLE_NAME = "noLabel";
	
	private ProjectsPresenter presenter;

	@UiField
	FlowPanel projectsPanel;
	
	@UiField
	FlowPanel content;
	
	/**
	 * Creates an instance of <code>EditorView</code>.
	 */
	public ProjectsView() {
		initWidget(BINDER.createAndBindUi(this));
		projectsPanel.getElement().setId("projectsPanel");
	}

	@Override
	public void setPresenter(ProjectsPresenter presenter) {
		this.presenter = presenter;		
	}
	
	@Override
	public void clearItems() {
		content.clear();		
	}
	
	@Override
	public void addItem(ProjectItemPresenter itemPresenter) {
		content.add(itemPresenter);
	}

	@UiHandler("projectsPanel")
	void onAttach(AttachEvent event) {
		presenter.onRequestRefresh();
	}

	@Override
	public void add(IsWidget widget) {
		content.add(widget);
	}

	@Override
	public void showEmptyItemsList() {
		HTMLPanel panel = new HTMLPanel(SafeHtmlUtils.fromTrustedString("<span>No Labels.</span>"));
		panel.addStyleName(NO_LABEL_STYLE_NAME);
		content.add(panel);
	}
}
