/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.nasa.arc.pds.visualizer.client;

import gov.nasa.arc.pds.visualizer.client.event.ErrorReportEvent;
import gov.nasa.arc.pds.visualizer.client.event.PresentDataEvent;
import gov.nasa.arc.pds.visualizer.shared.Constants;
import gov.nasa.pds.domain.Array2DImageProduct;
import gov.nasa.pds.domain.Bundle;
import gov.nasa.pds.domain.DocumentProduct;
import gov.nasa.pds.domain.HasChildren;
import gov.nasa.pds.domain.HasParsableChildren;
import gov.nasa.pds.domain.PDSObject;
import gov.nasa.pds.domain.TableCharacterProduct;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;


public class ProductView extends Composite {


	/**
	 * Specifies the images that will be bundled for this Composite and specify
	 * that tree's images should also be included in the same bundle.
	 * @see http://www.iconshub.com/category/alphabet-icons/224/multipurpose-alphabet-icons-set1/3658/letter-c-dg-icon-png.html
	 */
	public interface Images extends ClientBundle, Tree. Resources {
		@Source("images/letterAdgiconiconshub.png") ImageResource letterAdgiconiconshub();
		@Source("images/letterBdgiconiconshub.png") ImageResource letterBdgiconiconshub();
		@Source("images/letterC.png") ImageResource letterC();
		@Source("images/letterDdgiconiconshub.png") ImageResource letterDdgiconiconshub();
		@Source("images/letterIdgiconiconshub.png") ImageResource letterIdgiconiconshub();
		@Source("images/letterPdgiconiconshub.png") ImageResource letterPdgiconiconshub();
		@Source("images/letterTdgiconiconshub.png") ImageResource letterTdgiconiconshub();
		@Source("images/letterXdgiconiconshub.png") ImageResource letterXdgiconiconshub();
	}

	final private Images images = GWT.create(Images.class);
	private EventBus eventBus;
	private DataServiceAsync rpcService;
	private TreeItem openedTreeID = null;
	private static final String DUMMY_TREEITEM = ""; 
	final Tree tree = new Tree(images);

	public ProductView(DataServiceAsync datasvc, SimpleEventBus eventbus2) {
	
		init(datasvc, eventbus2);
		final TreeItem rootTreeItem = new TreeItem(imageItemHTML(selectIcon(new Bundle()), Constants.ALL_BUNDLES)); 
		PDSObject allBundlesDI = new Bundle(null, Constants.ALL_BUNDLES);
		allBundlesDI.setTitle(Constants.ALL_BUNDLES);
		rootTreeItem.setUserObject(new TreeItemUserObject(allBundlesDI, false));
		rootTreeItem.addItem(DUMMY_TREEITEM);
		tree.addItem(rootTreeItem);	
		rootTreeItem.setState(false);
		initWidget(tree);

		// User selects data to be presented
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event)  {
				TreeItem treeItem = (TreeItem) event.getSelectedItem();
				if (treeItem.getParentItem() == null) {
					return;// if select top node
				}				
				eventBus.fireEvent(new PresentDataEvent(((TreeItemUserObject)treeItem.getUserObject()).pdsObject));
			}
		});

		// user opens a product
		tree.addOpenHandler(new OpenHandler<TreeItem>() {
			@Override
			public void onOpen(OpenEvent<TreeItem> event) {
				TreeItem openedTreeItem = (TreeItem) event.getTarget(); 
				openedTreeID  = openedTreeItem; // save 				
				PDSObject dataOfOpened = ((TreeItemUserObject)openedTreeItem.getUserObject()).pdsObject;
				assert dataOfOpened != null : "Null data associated with tree node";

				// if previously fetched
				if (((TreeItemUserObject)openedTreeItem.getUserObject()).fetchedState == true) {
					openedTreeItem.setState(true);
					return;
				}
				
				if (openedTreeItem.getParentItem() == null) { 
					
					fetchTopLevelChildren(dataOfOpened, rootTreeItem);					
				} else {
					openedTreeItem.removeItems();
					openedTreeItem.addItem(new TreeItem(Constants.waitingString)); 	
					fetchChildren(dataOfOpened.getArchiveRoot(), dataOfOpened.getThis_file_name());
				}
			}
		});
	}

	public void init(DataServiceAsync svc, EventBus bus) {
		this.rpcService = svc;
		this.eventBus = bus;
	}

	/**
	 * Generates HTML for a tree item with an attached icon.
	 * 
	 * @param imageProto the image prototype to use
	 * @param title the title of the item
	 * @return the HTML
	 */
	private String imageItemHTML(ImageResource imageProto, String title) {
		return AbstractImagePrototype.create(imageProto).getHTML() + " " + title; //+ "(" + count + ")";
	}

	/**
	 * Gets top level children
	 * @param allBundlesDI data item corresponding to root 
	 * @param rootTreeItem tree item visual component corresponding to root
	 */
	public void fetchTopLevelChildren(final HasChildren allBundlesDI, final TreeItem rootTreeItem) {

		assert ((TreeItemUserObject)rootTreeItem.getUserObject()).fetchedState == false;
		
		AsyncCallback <PDSObject[]> callbackData = new AsyncCallback<PDSObject[]>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				String s = "Shortcuts fetch top level.onFailure: " + ((caught.getCause() != null) ? caught.getCause() : "") + " "+ caught.getMessage();
				eventBus.fireEvent(new ErrorReportEvent(s));
			}

			public void onSuccess(PDSObject[] allTopLevel) {
				System.err.println("Shortcuts fetch top level RPC onsuccess" );

				try {
					rootTreeItem.removeItems();
					String itemWithCount = appendChildCountIfNeeded(rootTreeItem.getHTML(), allTopLevel.length);					
					rootTreeItem.setHTML(itemWithCount);	
					((TreeItemUserObject)rootTreeItem.getUserObject()).fetchedState = true;
					List<PDSObject> topList = Arrays.asList(allTopLevel);
					Collections.sort(topList);

					for (PDSObject topChild : topList) {			
						allBundlesDI.clearAllFilenames();
						allBundlesDI.addFilename(topChild.getThis_file_name());		
						topChild.setTitle(topChild.getThis_file_name());
						TreeItem topTreeItem = new TreeItem(imageItemHTML(selectIcon(topChild), topChild.getTitle()));
						topTreeItem.setUserObject(new TreeItemUserObject(topChild, false));
						rootTreeItem.addItem(topTreeItem);
						if (topChild instanceof HasParsableChildren) {
							topTreeItem.addItem(new TreeItem(DUMMY_TREEITEM));
						} else {
							TreeItem fullyOpenedTreeItem = topTreeItem;
							HasChildren fullyOpenedProduct = topChild;
							int productChildCount = getViewableChildCount(fullyOpenedProduct);
							String fullyOpenedLabelWithCount = appendChildCountIfNeeded(fullyOpenedTreeItem.getHTML(), productChildCount);					
							fullyOpenedTreeItem.setHTML(fullyOpenedLabelWithCount);
						}
					}
				} catch (Exception e) {
					System.err.println("Shortcuts fetch all top level onsuccess content failed: " +e.getMessage());
					e.printStackTrace();
				}
			}	
		};

		rpcService.fetchToplevel(callbackData);

	}
	
	void fetchChildren(String archiveRoot, String fileToRead)  {

		AsyncCallback<PDSObject[]> callbackData = new AsyncCallback<PDSObject[]>() {

			TreeItem foundOpenedTreeItem = null;

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				String s = "Shortcuts.fetchChildren.onFailure: " + ((caught.getCause() != null) ? caught.getCause() : "") + " "+ caught.getMessage();
				eventBus.fireEvent(new ErrorReportEvent(s));
			}

			public void onSuccess(PDSObject[] children) {
				System.err.println("Short cuts fetch children RPC onsuccess" );

				try {
					foundOpenedTreeItem = null;
					findOpenedItem(tree.getItem(0));
					assert foundOpenedTreeItem != null : "find tree item returned null node";
					foundOpenedTreeItem.removeItems();					
					String itemWithCount = appendChildCountIfNeeded(foundOpenedTreeItem.getHTML(), children.length);					
					foundOpenedTreeItem.setHTML(itemWithCount);
					((TreeItemUserObject)foundOpenedTreeItem.getUserObject()).fetchedState = true;

					for (int i=0; i < children.length; i++) {	
						TreeItem childTreeItem = new TreeItem(imageItemHTML(selectIcon(children[i]), children[i].getTitle()));
						childTreeItem.setUserObject(new TreeItemUserObject(children[i],false));
						foundOpenedTreeItem.addItem(childTreeItem);

						if (children[i] instanceof HasParsableChildren) {
							childTreeItem.addItem(new TreeItem(DUMMY_TREEITEM));
						} else {
							TreeItem fullyOpenedTreeItem = childTreeItem;
							HasChildren fullyOpenedProduct = children[i];
							int productChildCount = getViewableChildCount(fullyOpenedProduct);
							String fullyOpenedLabelWithCount = appendChildCountIfNeeded(fullyOpenedTreeItem.getHTML(), productChildCount);					
							fullyOpenedTreeItem.setHTML(fullyOpenedLabelWithCount);
						}
					}
				} catch (Exception e) {
					System.err.println("Short cuts fetch children RPC onsuccess contents failed: " +e.getMessage());
					e.printStackTrace();
				}
			}

			/** 
			 * Traverse tree to find the node that was opened.
			 * @param item tree item where traversal begins
			 * @param idOpenedNode ID of the node that was opened
			 * @return node that was opened
			 */
			void findOpenedItem(TreeItem item) {

				for (int i = 0; i < item.getChildCount(); i++) {    
					TreeItem child = item.getChild(i); 
					if (child == openedTreeID) {
						foundOpenedTreeItem = child;
						return;
					}			
					findOpenedItem(child); 
				}
			}
		};

		rpcService.parseCollection(archiveRoot, fileToRead, callbackData);

	}

	/** Returns a count suitable for viewing. */
	private int getViewableChildCount(HasChildren fullyOpenedProduct) {	
		//PTOOL-51 if fullyOpenedProduct instanceof DocumentProduct, getPrimaryDocumentCount();
		return fullyOpenedProduct.getFilenamesCount();
	}

	private static String appendChildCountIfNeeded(String html, int childCount) {
		boolean isAlreadyAppended = false;
		int length = html.length();
		int lastIndexOf = html.lastIndexOf(')');
		if (lastIndexOf == length - 1) {
			isAlreadyAppended = true;
		}
		return isAlreadyAppended ? html : html + " (" +childCount+ ")";
	}
	
	private ImageResource selectIcon(PDSObject pdsObject) {
		if (pdsObject instanceof gov.nasa.pds.domain.Bundle) {
			return images.letterBdgiconiconshub();
		} else {
			if (pdsObject instanceof gov.nasa.pds.domain.Collection) {
				return images.letterC();
			} else {
				if (pdsObject instanceof DocumentProduct) {
					return images.letterDdgiconiconshub();
				} else {
					if (pdsObject instanceof TableCharacterProduct) {
						return images.letterTdgiconiconshub();
					} else {
						if (pdsObject instanceof Array2DImageProduct) {
							return images.letterIdgiconiconshub();
						} else {
							return images.letterXdgiconiconshub();
						}
					}
				}
			}
		}
	}
	
	
	class TreeItemUserObject {
		PDSObject pdsObject;
		boolean fetchedState;
		
	    TreeItemUserObject(PDSObject pdsObject, boolean fetchedState) {
			this.pdsObject = pdsObject;
			this.fetchedState = fetchedState;
		}
	}
}