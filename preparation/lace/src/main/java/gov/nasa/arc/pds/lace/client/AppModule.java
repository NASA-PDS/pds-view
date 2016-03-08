package gov.nasa.arc.pds.lace.client;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.presenter.EditorPresenter;
import gov.nasa.arc.pds.lace.client.presenter.InsertionPointPresenter;
import gov.nasa.arc.pds.lace.client.presenter.LabelFileUploadPresenter;
import gov.nasa.arc.pds.lace.client.presenter.ModificationButtonPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PageHeaderPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SimpleItemPresenter;
import gov.nasa.arc.pds.lace.client.presenter.TreePresenter;
import gov.nasa.arc.pds.lace.client.util.InsertOptionMap;
import gov.nasa.arc.pds.lace.client.view.ContainerView;
import gov.nasa.arc.pds.lace.client.view.EditorView;
import gov.nasa.arc.pds.lace.client.view.InsertionPointView;
import gov.nasa.arc.pds.lace.client.view.LabelFileUploadView;
import gov.nasa.arc.pds.lace.client.view.ModificationButtonView;
import gov.nasa.arc.pds.lace.client.view.PageHeaderView;
import gov.nasa.arc.pds.lace.client.view.PopupView;
import gov.nasa.arc.pds.lace.client.view.SimpleItemView;
import gov.nasa.arc.pds.lace.client.view.TreeView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * Implements the Gin module configuration for
 * the application as a collection of bindings.
 */
public class AppModule extends AbstractGinModule {

	@Override
	protected void configure() {
		/*
		 * The singleton event bus for the application.
		 *
		 * The binding tells Gin that whenever it sees a dependency on an EventBus,
		 * it should satisfy the dependency using a SimpleEventBus.
		 */
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);		
		
		// The singleton insert option map. 		 
		bind(InsertOptionMap.class).in(Singleton.class);
		
		// Bind views to their presenter displays.
		bind(TreePresenter.Display.class).to(TreeView.class);
		bind(ContainerPresenter.Display.class).to(ContainerView.class);
		bind(EditorPresenter.Display.class).to(EditorView.class);
		bind(SimpleItemPresenter.Display.class).to(SimpleItemView.class);
		bind(InsertionPointPresenter.Display.class).to(InsertionPointView.class);
		bind(PopupPresenter.Display.class).to(PopupView.class);
		bind(LabelFileUploadPresenter.Display.class).to(LabelFileUploadView.class);
		bind(PageHeaderPresenter.Display.class).to(PageHeaderView.class);
		bind(ModificationButtonPresenter.Display.class).to(ModificationButtonView.class);
	}
}

