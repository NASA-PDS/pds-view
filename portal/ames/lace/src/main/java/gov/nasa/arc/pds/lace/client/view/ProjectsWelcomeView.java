package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ProjectsWelcomePresenter;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectsWelcomeView extends Composite implements ProjectsWelcomePresenter.Display {

	interface Binder extends UiBinder<Widget, ProjectsWelcomeView> { /*empty*/ }
	
	private static final Binder BINDER = GWT.create(Binder.class);
	
	@SuppressWarnings("unused")
	private ProjectsWelcomePresenter presenter;
	
	@UiField
	HTMLPanel closeBtn;
	
	@UiField
	HTMLPanel welcomeBox;
	
	/**
	 * Creates a new instance of the view.
	 */
	@Inject
	public ProjectsWelcomeView() {
		initWidget(BINDER.createAndBindUi(this));
		
		closeBtn.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onRequestClose();
			}
		}, ClickEvent.getType());
	}

	@Override
	public void setPresenter(ProjectsWelcomePresenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void closeBox() {
		welcomeBox.setVisible(false);
	}
}
