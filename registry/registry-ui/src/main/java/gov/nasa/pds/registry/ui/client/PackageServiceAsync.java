// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations 
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
// is subject to U.S. export control laws and regulations, the recipient has 
// the responsibility to obtain export licenses or other export authority as 
// may be required before exporting such information to foreign countries or 
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.ViewRegistryPackage;

import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

import javax.servlet.http.HttpServletRequest;

/**
 * Asynchronous interface to the packages service
 * 
 * @author hyunlee
 */
public interface PackageServiceAsync {

    void requestRows(String serverUrl, Request request, Map<String, String> filters,
			AsyncCallback<SerializableResponse<ViewRegistryPackage>> callback);
	
	void updatePackage(String serverUrl, final ViewRegistryPackage registryPackage, AsyncCallback<Boolean> callback);
	
	void deletePackage(String serverUrl, ViewRegistryPackage registryPackage, AsyncCallback<Boolean> callback);
	
	void getRemoteUser(AsyncCallback<String> callback);
	
	void getRemotePassword(AsyncCallback<String> callback);
	
	void getSessionId(AsyncCallback<String> callback);

}
