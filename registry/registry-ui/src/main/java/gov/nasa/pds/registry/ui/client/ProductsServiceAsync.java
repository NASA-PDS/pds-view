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

import gov.nasa.pds.registry.ui.shared.ViewProduct;

import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Asynchronous interface to the products service
 * 
 * @author jagander
 */
public interface ProductsServiceAsync {

	void getProduct(final String guid,
			AsyncCallback<SerializableResponse<ViewProduct>> callback);
	
	//Request getProduct(final String guid, 
	//      AsyncCallback<SerializableResponse<ViewProduct>> callback);
	
	// or
	
	// Method returns the underlying HTTP RequestBuilder instance
	//RequestBuilder getProduct(final String guid,
	 //      AsyncCallback<SerializableResponse<ViewProduct>> callback);
	
	void requestRows(Request request, Map<String, String> filters,
			AsyncCallback<SerializableResponse<ViewProduct>> callback);
	
	void updateProduct(final ViewProduct product, AsyncCallback<Boolean> callback);

	void deleteProduct(ViewProduct product, AsyncCallback<Boolean> callback);
}
