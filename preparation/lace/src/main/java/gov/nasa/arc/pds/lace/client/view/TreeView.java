package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.TreePresenter;
import gov.nasa.arc.pds.lace.client.resources.TreeResources;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * A view class responsible for displaying the tree view.
 */
public class TreeView extends Composite implements TreePresenter.Display {

	private static final String TREE_WIDGET_ID = "treeWidget";
	private static final String SMALL_ICON_CLASSNAME = "small icon";
	private static final String ALERT_ICON_CLASSNAME= "alertIcon";
	private static final String ALERT_CLASSNAME= "alert";
	private static final String MIDDLE_CLASSNAME= "middle";
	
	private Tree tree;
	private TreePresenter presenter;

	/**
	 * Creates a new tree view.
	 */
	public TreeView() {
		TreeResources resources = new TreeResources();
		tree = new Tree(resources);
		initWidget(tree);
		
		tree.getElement().setId(TREE_WIDGET_ID);
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				presenter.handleItemSelection(event.getSelectedItem());
			}
			
		});
	}

	@Override
	public void setPresenter(TreePresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void removeAllItems() {
		tree.clear();
	}

	@Override
	public void addRootItem(TreeItem root, String name, String icon, boolean isComplete) {
		root.setHTML(createTreeNodeHtml(name, icon, isComplete));
		tree.addItem(root);
	}

	@Override
	public TreeItem addChildItem(TreeItem parent, String name, String icon, boolean isComplete) {
		return parent.addItem(createTreeNodeHtml(name, icon, isComplete));
	}

	@Override
	public TreeItem insertItem(TreeItem parent, String name, String icon, boolean isComplete, int index) {
		return parent.insertItem(index, createTreeNodeHtml(name, icon, isComplete));
	}
	
	@Override
	public void setState(TreeItem item, boolean open) {
		item.setState(open);
	}

	@Override
	public void setSelectedItem(TreeItem item) {
		tree.setSelectedItem(item, true);
	}
	
	@Override
	public TreeItem getSelectedItem() {
		return tree.getSelectedItem();
	}
	
	@Override
	public void updateCompleteState(TreeItem item, boolean complete) {		
		
		String html = item.getHTML();
		int iconIdx = html.indexOf(SMALL_ICON_CLASSNAME);
		int alertIdx = html.indexOf(ALERT_CLASSNAME);		
		int alertIconIdx = html.indexOf(ALERT_ICON_CLASSNAME);
		
		// If the item is complete and has the 'alert' style, remove it.
		if (complete && (alertIdx != alertIconIdx)) {					
			html = html.replaceFirst(ALERT_CLASSNAME, "");
			item.setHTML(SafeHtmlUtils.fromTrustedString(html));			
		}
		
		// If the item is incomplete and doesn't have the 'alert' style, add it.
		if (!complete && (alertIdx == alertIconIdx)) {
			String sub1 = html.substring(0, iconIdx);
			String sub2 = html.substring(iconIdx);
			html = sub1 + " " + ALERT_CLASSNAME + " " + sub2;
			item.setHTML(SafeHtmlUtils.fromTrustedString(html));
		}
	}
	
	private SafeHtml createTreeNodeHtml(String label, String icon, boolean complete) {
		String className = SMALL_ICON_CLASSNAME + " " + MIDDLE_CLASSNAME + " " + icon;
		if (!complete) {
			className = className + " " + ALERT_CLASSNAME;
		}
		
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.append(SafeHtmlUtils.fromTrustedString("<div class='" + className + "'>"));		
		sb.append(SafeHtmlUtils.fromTrustedString("<div class='" + ALERT_ICON_CLASSNAME + "'></div>"));
		sb.append(SafeHtmlUtils.fromTrustedString("</div>"));
		sb.append(SafeHtmlUtils.fromTrustedString("<span class='" + MIDDLE_CLASSNAME + "'>"));
		sb.appendEscaped(label);
		sb.append(SafeHtmlUtils.fromTrustedString("</span>"));
		
		return sb.toSafeHtml();
	}
}
