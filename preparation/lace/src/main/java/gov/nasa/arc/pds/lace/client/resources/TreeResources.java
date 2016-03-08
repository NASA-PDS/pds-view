package gov.nasa.arc.pds.lace.client.resources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;

/**
 * Implements the resources used by the tree widget. 
 */
public class TreeResources implements Tree.Resources {
	
	public ImageResource treeClosed() {
		return Resources.INSTANCE.treeClosed();
	}
		
	public ImageResource treeOpen() {
		return Resources.INSTANCE.treeOpen();
	}
  	
	public ImageResource treeLeaf() {
		return Resources.INSTANCE.treeLeaf();
	}
	
}
