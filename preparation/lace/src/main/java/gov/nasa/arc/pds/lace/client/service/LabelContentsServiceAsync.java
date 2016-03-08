package gov.nasa.arc.pds.lace.client.service;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.ResultType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LabelContentsServiceAsync {

	void getRootContainer(String elementName, AsyncCallback<Container> callback);

	void updateContainer(Container container, InsertionPoint insPoint,
			int alternativeIndex, int typeIndex, AsyncCallback<ResultType> callback);

	void getContainerForFile(String filePath, AsyncCallback<Container> callback);

	void saveSimplelItem(Container container, SimpleItem item, String value,
			AsyncCallback<Container> callback);

	void writeModel(Container container, AsyncCallback<String> callback);

}
