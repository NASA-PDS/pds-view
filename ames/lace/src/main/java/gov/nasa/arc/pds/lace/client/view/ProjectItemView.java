package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ProjectItemPresenter;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem.Type;

import java.util.Date;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectItemView extends Composite implements ProjectItemPresenter.Display {

	interface Binder extends UiBinder<Widget, ProjectItemView> { /*empty*/ }
	
	private static final Binder BINDER = GWT.create(Binder.class);
	
	@UiField
	FocusPanel projectItem;
	
	@UiField
	InlineLabel itemIcon;
	
	@UiField
	InlineLabel itemLink;
	
	@UiField
	InlineLabel lastUpdated;
	
	@UiField
	InlineLabel deleteButton;
	
	private ProjectItemPresenter presenter;
	
	/**
	 * Creates a new instance of the view.
	 */
	@Inject
	public ProjectItemView() {
		initWidget(BINDER.createAndBindUi(this));
		
		deleteButton.setTitle("Click to delete the label");
		projectItem.setTitle("Click to load the label");
	}

	@Override
	public void setPresenter(ProjectItemPresenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("deleteButton")
	void onRequestDelete(ClickEvent event) {
		event.stopPropagation();
		presenter.onRequestDelete();
	}
	
	@UiHandler("projectItem")
	void onRequestNavigate(ClickEvent event) {
		event.stopPropagation();
		presenter.onRequestNavigate();
	}

	@Override
	public void setItemName(String name) {
		itemLink.setText(name);
	}

	@Override
	public void setItemType(Type type) {
		itemIcon.getElement().addClassName("itemType" + type.toString());
	}

	@Override
	public void setLastUpdated(Date date) {
		DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);
		lastUpdated.setText(formatter.format(date));
	}
	
}
