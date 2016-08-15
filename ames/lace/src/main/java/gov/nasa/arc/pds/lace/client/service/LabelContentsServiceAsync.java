package gov.nasa.arc.pds.lace.client.service;

import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.ResultType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.ValidationResult;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LabelContentsServiceAsync {

	void getUser(AsyncCallback<String> callback);

	void setUser(String userID, AsyncCallback<Void> callback);

	void getRootContainer(String elementName, String elementNS, AsyncCallback<Container> callback);

	void updateContainer(Container container, InsertionPoint insPoint,
			int alternativeIndex, LabelItemType type, AsyncCallback<ResultType> callback);

	void pasteElement(Container container, LabelElement element,
			InsertionPoint insPoint, AsyncCallback<ResultType> callback);

	void getContainerForFile(String filePath, AsyncCallback<Container> callback);

	void saveAttribute(LabelItem parentItem, AttributeItem attribute, String value, AsyncCallback<Void> callback);

	void saveSimplelItem(Container container, SimpleItem item, String value,
			AsyncCallback<Container> callback);

	void writeModel(Container container, AsyncCallback<String> callback);

	void deleteElement(Container container, LabelElement element,
			AsyncCallback<ResultType> callback);

	void validateModel(Container root, AsyncCallback<ValidationResult> callback);

	void getNamespaces(AsyncCallback<Collection<String>> callback);

	void getElementNamesByNamespace(String namespace, AsyncCallback<Collection<String>> callback);

	void getTypeForElement(String elementName, String elementNS, AsyncCallback<LabelItemType> callback);

	void getProjectsItems(String location, AsyncCallback<ProjectItem[]> callback);

	void getContainerForLocation(String location,
			AsyncCallback<Container> callback);

	void deleteProjectItem(String location, AsyncCallback<Void> callback);

	void getDefaultSchemaFiles(AsyncCallback<String[]> callback);

	void addDefaultSchemaFile(String filename, AsyncCallback<Void> callback);

	void removeDefaultSchemaFile(String filename, AsyncCallback<Void> callback);

	void setLabelName(String newName, AsyncCallback<Void> callback);

	void getItemAttribute(String key, AsyncCallback<String> callback);

	void setItemAttribute(String key, String value, AsyncCallback<Void> callback);

}
