package gov.nasa.arc.pds.lace.client;

import gov.nasa.arc.pds.lace.client.resources.HtmlResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Implements the EntryPoint interface to act as a module entry point.
 * 
 */
public class Main implements EntryPoint {
	
	private final AppInjector injector = GWT.create(AppInjector.class);
	
	private DockLayoutPanel mainPanel;

	@Override	
	public void onModuleLoad() {		
		HTML footer = new HTML();
		footer.setHTML(HtmlResources.INSTANCE.getFooterHtml().getText());
		footer.getElement().setId("footer");
		
		FlowPanel treePanel = new FlowPanel();
		treePanel.getElement().setId("treePanel");		
		treePanel.add(injector.getTreePresenter().asWidget());
		
		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		splitPanel.getElement().setId("splitPanel");
		splitPanel.addWest(treePanel, 300.0D);
		splitPanel.setWidgetMinSize(treePanel, 100);
		splitPanel.add(injector.getEditorPresenter().asWidget());
				
		mainPanel = new DockLayoutPanel(Style.Unit.PX);
		mainPanel.addNorth(injector.getPageHeaderPresenter().asWidget(), 60.0D);
		mainPanel.addSouth(footer, 25.0D);
		mainPanel.add(splitPanel);
		
		RootLayoutPanel.get().add(mainPanel);
	}
	
}
