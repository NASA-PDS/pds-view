package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.TreePresenter;
import gov.nasa.arc.pds.lace.client.resources.Resources;
import gov.nasa.arc.pds.lace.client.resources.TreeResources;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * A view class responsible for displaying the tree view.
 */
public class TreeView extends Composite implements TreePresenter.Display {

	private static final String TREE_WIDGET_ID = "treeWidget";

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
	public void addRootItem(TreeItem root, String name) {
		root.setHTML(createTreeNodeHtml(Resources.INSTANCE.getObjectIcon(), name));
		tree.addItem(root);
	}

	@Override
	public TreeItem addChildItem(TreeItem parent, String name) {
		return parent.addItem(createTreeNodeHtml(Resources.INSTANCE.getObjectIcon(), name));
	}

	@Override
	public TreeItem insertItem(TreeItem parent, String name, int index) {
		return parent.insertItem(index, createTreeNodeHtml(Resources.INSTANCE.getObjectIcon(), name));
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
	
	private SafeHtml createTreeNodeHtml(ImageResource image, String label) {
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.append(SafeHtmlUtils.fromTrustedString(AbstractImagePrototype.create(image).getHTML()));
		sb.append(SafeHtmlUtils.fromTrustedString("<span>"));
		sb.appendEscaped(label);
		sb.append(SafeHtmlUtils.fromTrustedString("</span>"));
		return sb.toSafeHtml();
	}
}
